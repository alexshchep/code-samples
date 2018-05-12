# Solution to solve for implied volatility of an option via
# Crank-Nicolson method 
# by Alex Shchepetkin	
#

import os
import sys
import math
import unittest
import logging
import random
import numpy as np
from scipy.stats import norm


# TODO - make it into option class


def fun1(x):
    return x ** 2 - 612


def fun2(x):
    return 2 * x


def newton_method(f0, f0_d, x0, tol, niter=100, random_guess_range=(0, 1), f0_kwargs={}, f0_d_kwargs={}):
    """
    Implementation of a Newton-Raphson method

    parameters:
        f0 - the function to solve
        f0_d - the derivative of the function
        x0 - initial guess
        tol - tolerance between guess and solution
        niter - maximum number of iterations (default = 100)
        random_guess_range - if function diverges, try a random value in this range
        f0_kwargs - arguments for the main function
        f0_d_kwargs - arguments for the derivative function
    returns:
        root solution
    """
    log = logging.getLogger('newton_method')
    for i in range(niter):
        f0_val = float(f0(x0, **f0_kwargs))  # output of the function
        f0_d_val = f0_d(x0, **f0_d_kwargs)  # output of the derivative function
        # log.debug('function returns {}, derivative returns {}'.format( f0_val,f0_d_val))
        if math.fabs(f0_val) < tol:
            return x0
        # avoid division by zero, and guess randomly instead
        if f0_d_val == 0:
            x0 = random.uniform(random_guess_range[0], random_guess_range[1])
            continue
        else:
            x1 = x0 - f0_val / f0_d_val

        log.debug('i is {}, x0 is {}, x1 is {}'.format(i, x0, x1))
        x0 = x1

    return x1


def TDMA(a, b, c, f):
    """
        Thomas algorithm implementation from
        https://en.wikibooks.org/wiki/Algorithm_Implementation/Linear_Algebra/Tridiagonal_matrix_algorithm
    """
    a, b, c, f = map(lambda k_list: map(float, k_list), (a, b, c, f))

    alpha = [0]
    beta = [0]
    n = len(f)
    x = [0] * n

    for i in range(n - 1):
        alpha.append(-b[i] / (a[i] * alpha[i] + c[i]))
        beta.append((f[i] - a[i] * beta[i]) / (a[i] * alpha[i] + c[i]))

    x[n - 1] = (f[n - 1] - a[n - 2] * beta[n - 1]) / (c[n - 1] + a[n - 2] * alpha[n - 1])

    for i in reversed(range(n - 1)):
        x[i] = alpha[i + 1] * x[i + 1] + beta[i + 1]

    return x


def thomas(leftmat, rightmat):
    """
        https://en.wikipedia.org/wiki/Tridiagonal_matrix_algorithm (switch a and c diagonals)
        variables are not the same with wiki to stay consistent with previous declarations in this code
        Thomas algorithm
        a - upper right diagonal
        d - main diagonal
        c - lower left diagonal
    Parameters:
        leftmat - tridiagonal matrix to solve (numpy matrix)
        rightmat - solution to the equation (numpy array or list)
    Returns:
        x - solution to the Thomas algorithm (numpy array)
    """
    log = logging.getLogger('Thomas')
    # get diagonals

    a = np.concatenate([np.diagonal(leftmat, offset=1).copy(), [0]])
    #a = np.concatenate([[0], np.diagonal(leftmat, offset=1).copy()])
    #a = np.diag(leftmat, 1).copy()
    d = np.diagonal(leftmat).copy()
    c = np.concatenate([[0], np.diagonal(leftmat, offset=-1).copy()])
    #c = np.diag(leftmat, -1).copy()
    # forward sweeps
    #a[0] = a[0] / d[0]
    for i in range(1, len(a) - 1):
        a[i] = a[i] / (d[i] - c[i] * a[i - 1])
    xprime = np.array(rightmat)  # copy solution array, xprime = dprime on wiki
    xprime[0] = rightmat[0] / d[0]
    log.debug(xprime)
    for i in range(1, len(rightmat)):
        xprime[i] = (rightmat[i] - c[i] * xprime[i - 1]) / (d[i] - c[i] * a[i - 1])
    x = xprime.copy()
    for i in range(len(rightmat) - 2, -1, -1):  # backwards
        x[i] = x[i] - a[i] * x[i + 1]
    return x


class Option(object):
    """
    A financial option, either a call or a put.
    Attributes:
        s - price of the stock
        k - strike price of the option
        r - risk free rate (percentage points)
        q - continuously compounded dividend yield (percentage points)
        t - time to expiration (years)
        option_type - type of an option (put or call)
        sigma - stock volatility
        v - option price
    """

    def __init__(self, s, k, r, q, t, option_type, sigma, v):
        """
        initialize object type Option
        """
        option_type = option_type.lower()
        assert option_type in ['call', 'put'], 'option_type needs to be either a put or a call'
        self.s = float(s)
        self.k = float(k)
        self.r = float(r)
        self.q = float(q)
        self.t = float(t)
        self.option_type = option_type
        try:
            self.sigma = float(sigma)
        except TypeError:
            self.sigma = sigma  # empty
        try:
            self.v = float(v)
        except TypeError:
            self.v = v  # empty

    def crank_nicolson(self, smax, smin, tmax, nsteps, ntimes):
        """
        Crank-Nicolson algorithm to solve the American option for implied volatility

        parameters:
            smax - maximum stock price
            smin - minimum stock price
            tmax - maximum time
            nsteps - number of stock price steps
            ntimes - number of stock time steps

        returns:
            implied volatility
        """
        stockindex = None
        hz = float(smax - smin) / nsteps  # stock stepsize
        tz = self.t / float(ntimes)  # time stepsize
        # set up the matrix
        mat = np.zeros(shape=(nsteps+1, ntimes+1))
        umat = np.zeros(shape=(nsteps+1, ntimes+1))  # the value of the option price
        diaglength = len(mat.diagonal())

        payoffs = [self.option_payoff(x) for x in np.arange(smin, smax+1, hz)]
        umat[:, 0] = payoffs  # fill with payoffs
        astarvec = np.zeros(diaglength)
        dstarvec = np.zeros(diaglength)
        cstarvec = np.zeros(diaglength)

        # if self.option_type == 'put':
        #     umat[0, :] = self.k  # the value of a put is strike price when stock price is 0
        #     umat[-1, :] = 0  # the value of a put is 0 when stock price is the max
        # elif self.option_type == 'call':
        #     umat[-1, :] = self.k  # the value of a put is strike price when stock price is the max
        #     umat[0, :] = 0  # the value of a put is 0 when stock price is 0
        determineindex = False
        # loop through time
        timesteps = np.arange(0, tmax+1, tz)
        stocksteps = np.arange(0, smax+1, hz)
        rightside = np.zeros(nsteps+1)
        # loop through time
        for i in range(1,tmax+1):
            umat[0, i] = self.k   # value is strike price when stock price is zero
            umat[-1, i] = 0  # value is zero when stock price is maximum
            # loop through price
            for j in range(1, smax+1):
                if j*hz > self.s and not determineindex:
                    stockindex = j
                    determineindex = True
                dstarvec[j] = 1.0 + 1.0/2 * (self.r + self.sigma ** 2 * j ** 2) * tz
                cstarvec[j] = -1.0 / 4 * (self.r * j + self.sigma ** 2 * j ** 2) * tz
                astarvec[j] = 1.0/4 * (float(self.r) * j - self.sigma ** 2 * j ** 2) * tz
                rightside[j] = -astarvec[j] * umat[j-1, i-1] +\
                            (1 - 1.0/2 * tz * (self.r + self.sigma ** 2 * j ** 2)) * umat[j, i-1]
                if j < nsteps:
                    rightside[j] = rightside[j] - cstarvec[j] * umat[j+1, i-1]
            mat = mat + np.diag(cstarvec[:-1], 1)  # c is upper right diagonal
            np.fill_diagonal(mat, dstarvec)  # d is the main diagonal
            mat = mat + np.diag(astarvec[1:], -1)  # a is the lower left diagonal
            thomasresult = thomas(mat[1:,1:], rightside[1:])  # solve Thomas algorithm

            for j in range(1, nsteps):
                umat[j, i] = max(self.k - j*hz, thomasresult[j-1])
        impvol = umat[stockindex - 1, -1] +\
                 (self.s - (stockindex - 1) * hz) *\
                 (umat[stockindex, -1] -
                  umat[stockindex - 1, -1]) / hz


        # aval = 1.0 / 4 * ((self.sigma ** 2) / float(hz) ** 2 + (self.r - self.sigma ** 2 / 2.0) / hz)
        # avec = np.repeat(aval, diaglength - 1)
        # astarval = -aval
        # astarvec = -avec
        #
        # dval = 1.0 / tz - 1.0 / 2 * self.sigma ** 2 / hz ** 2
        # dvec = np.repeat(dval, diaglength)
        # dstarval = 1.0 / tz + self.r + 1.0 / 2 * self.sigma ** 2 / hz ** 2
        # dstarvec = np.repeat(dstarval, diaglength)
        #
        # cval = 1.0 / 4 * (self.sigma ** 2 / hz ** 2 - (self.r - self.sigma ** 2 / 2.0) / hz)
        # cvec = np.repeat(cval, diaglength - 1)
        # cstarval = -cval
        # cstarvec = -cvec
        #
        # mat = mat + np.diag(astarvec, 1)  # a is upper right diagonal
        # np.fill_diagonal(mat, dstarvec)  # d is the main diagonal
        # mat = mat + np.diag(cstarvec, -1)  # c is the lower left diagonal
        # umat = np.zeros(shape=(nsteps, ntimes))  # option value matrix
        # if self.option_type == 'put':
        #     # value is maximum of strike minus stock value or 0
        #     umat[:, 0] = np.array([self.k - float(h) * smax / nsteps for h in nsteps])
        #     umat[0, :] = self.k  # value is strike when stock is zero
        #     umat[nsteps - 1, :] = 0  # value is zero when price is max
        #
        # zvector = np.repeat(aval + dval + cval, diaglength)  # resulting vector
        # zvector[0] = zvector[0] - cstarval  # boundaries ( top row)
        # zvector[-1] = zvector[-1] - astarval  # boundaries ( bottom row)
        #
        # # perform thomas algorithm over the iteration of time
        # for j in range(tmax):
        #     matsolve = mat[:,j]
        #     zsolve = umat[:, j] * zvector
        #     thomas_result = thomas(mat, zsolve)
        # impvol = thomas(mat, zvector)
        # impvol = impvol[-1]  # time t = 0
        return impvol

    def cn_implied_vol(self, x0, tol, niter):
        """
        function to solve for implied volatility using the crank nicolson method

        parameters:
            x0 - initial guess
            tol - tolerance
            niter - number of iterations
        returns:
            the implied volatility of the passed Option
        """
        sigmatemp = newton_method(f0=self.bs_implied_vol, f0_d=self.get_vega, x0=0.01,
                                  tol=0.001, niter=100)
        cn_est = 0  # initial estimation
        # crank nicolson price
        self.c = self.crank_nicolson(smax=100, smin=0, tmax=100, nsteps=100, ntimes=100)
        while abs(sigmatemp - cn_est) > tol:
            cn_est = sigmatemp

            self.c = self.crank_nicolson(smax=100, smin=0, tmax=100, nsteps=100, ntimes=100)
            sigmatemp = newton_method(f0=self.bs_implied_vol, f0_d=self.get_vega, x0=sigmatemp,
                                      tol=0.001, niter=100)
        return cn_est

    def bs_implied_vol(self, sigma):
        """
        equation to solve for the implied volatility using the newton method

        parameters:
            sigma - volatility of the option
            p - price of the option
            kwargs - other arguments for the blackscholes model
        returns:
            difference between the model prediction and the price
        """
        self.sigma = sigma
        return self.blackscholes() - self.v

    def get_vega(self, sigma):
        """
        method to get vega and burn the sigma parameter
        """
        return self.vega()

    def blackscholes(self):
        """
        c_price = N(d1)*s - N(d2)*k*e^(-r*t)
        p_price = N(-d2)*k*e^(-r*t) - N(-d1)*s

        parameters:
            s - stock price,
            k - strike price,
            r - risk free rate (percentage points),
            sigma - volatility of the stock (percentage points),
            q - continuosly compounded dividend yield (percentage points),
            t - time to expiration (years),
            option_type - type of an option (put or call)
        returns:
            price of the option
        """

        if self.option_type == 'call':
            return self.s * math.exp(-self.q * self.t) * norm.cdf(self.d1()) \
                   - self.k * math.exp(-self.r * self.t) * norm.cdf(self.d2())
        elif self.option_type == 'put':
            return self.k * math.exp(-self.r * self.t) * norm.cdf(-self.d2()) \
                   - self.s * math.exp(-self.q * self.t) * norm.cdf(-self.d1())

    def bs_derivative(self):
        """
        dprice/dt + 1/2*sigma^2*s^2*d2price/d2s + r*s*dprice/ds - r*price_deriv = 0
        parameters:
            s - stock price,
            k - strike price,
            sigma - volatility of the stock (percentage points),
            r - risk free rate (percentage points),
            q - continuosly compounded dividend yield (percentage points),
            t - time to expiration (years),
            v - value of the derivative
            option_type - type of an option (put or call)
        returns:
            derivative of the price of the option
        """
        return self.theta() \
               + 1.0 / 2 * self.sigma ** 2 * self.s ** 2 * self.gamma() \
               + self.r * self.s * self.delta() \
               - self.r * self.v

    def delta(self):
        """
        Delta is the change in option price over change in stock price
        parameters:
            s - stock price
            k - strike price
            r - risk free rate (percentage points)
            sigma - volatility of the stock (percentage points)
            q - continuosly compounded dividend yield (percentage points)
            t - time to expiration (years)
            option_type - type of an option (put or call)

        returns:
            delta of the european option
        """
        if self.option_type == 'call':
            return math.exp(-self.q * self.t) * norm.cdf(self.d1())
        elif self.option_type == 'put':
            return -math.exp(-self.q * self.t) * norm.cdf(self.d1())

    def vega(self):
        """
        Vega is the change in option price over change in stock volatility.
        It is identical for european put and call.
        parameters:
            s - stock price
            k - strike price
            r - risk free rate (percentage points)
            sigma - volatility of the stock (percentage points)
            q - continuosly compounded dividend yield (percentage points)
            t - time to expiration (years)

        returns:
            vega of the european option ($/units)
            divide by 100 if you want ($/%)
        """
        return self.s * math.exp(-self.q * self.t) * norm.pdf(self.d1()) * math.sqrt(self.t)

    def theta(self):
        """
        Theta is the change in option price over change in time to expiration.
        It is identical for european put and call.
        parameters:
            s - stock price
            k - strike price
            r - risk free rate (percentage points)
            sigma - volatility of the stock (percentage points)
            q - continuously compounded dividend yield (percentage points)
            t - time to expiration (years)
            option_type - type of an option (put or call)

        returns:
            theta of the european option ($/year)
            divide by 365.0 if you want ($/day)
        """
        if self.option_type == 'call':
            return -math.exp(-self.q * self.t) * self.s * norm.pdf(self.d1()) * self.sigma / (2.0 * math.sqrt(self.t)) \
                   - self.r * self.k * math.exp(-self.r * self.t) * norm.cdf(self.d2()) \
                   + self.q * self.s * math.exp(-self.q * self.t) * norm.cdf(self.d1())
        elif self.option_type == 'put':
            return -math.exp(-self.q * self.t) * self.s * norm.pdf(self.d1()) * self.sigma / (2.0 * math.sqrt(self.t)) \
                   + self.r * self.k * math.exp(-self.r * self.t) * norm.cdf(-self.d2()) \
                   - self.q * self.s * math.exp(-self.q * self.t) * norm.cdf(-self.d1())

    def rho(self):
        """
        Rho is the change in option price over change in interest rate.
        It is identical for european put and call.
        parameters:
            s - stock price
            k - strike price
            r - risk free rate (percentage points)
            sigma - volatility of the stock (percentage points)
            q - continuously compounded dividend yield (percentage points)
            t - time to expiration (years)
            option_type - type of an option (put or call)

        returns:
            rho of the european option ($/units)
            divide by 100 if you want ($/%)
        """
        if self.option_type == 'call':
            return self.k * self.t * math.exp(-self.r * self.t) * norm.cdf(self.d2())
        elif self.option_type == 'put':
            return -self.k * self.t * math.exp(-self.r * self.t) * norm.cdf(-self.d2())

    def gamma(self):
        """
        Gamma is the second derivative of change in option price over change is stock price
        It is identical for european put and call.
        parameters:
            s - stock price
            k - strike price
            r - risk free rate (percentage points)
            sigma - volatility of the stock (percentage points)
            q - continuously compounded dividend yield (percentage points)
            t - time to expiration (years)

        returns:
            gamma of the european option
        """
        return (math.exp(-self.q * self.t) * norm.pdf(self.d1())) / float(self.s * self.sigma * math.sqrt(self.t))

    def d1(self):
        """
        N(d1) is the factor by which the present value
        of contingent receipt of the stock exceeds the current stock price (Nielsen explanation)

        parameters:
            s - stock price
            k - strike price
            t - time to expiration (in years)
            r - risk free rate(percentage points)
            q - continuously compounded dividend yield (percentage points)
            sigma - volatility of a stock (percentage points)

        returns:
            value to input into a standard normal cumulative distribution function
        """
        return (math.log(self.s / self.k) + self.t * (self.r - self.q + self.sigma ** 2 / 2.0)) / (
                    self.sigma * math.sqrt(self.t))

    def d2(self):
        """
        N(d2) is the risk-adjusted probability that the option will
        be exercise (Nielsen explanation)

        parameters:
            s - starting price of a stock
            k - strike price
            t - time to expiration (in years)
            r - risk free rate(percentage points)
            q - continuously compounded dividend yield (percentage points)
            sigma - volatility of a stock (percentage points)

        returns:
            value to input into a standard normal cumulative distribution function
        """

        return self.d1() - self.sigma * math.sqrt(self.t)


    def option_payoff(self, stock_price):
        """
        calculate the payoff of the option given a stock price
        :param stock_price: the given price of a stock
        :return: the payoff
        """
        if self.option_type == 'put':
            return max(self.k - stock_price, 0)
        elif self.option_type == 'call':
            return max(stock_price - self.k, 0)


def within_range(myval, testval, acc_range):
    """
    check whether the value is within an acceptable range

    parameters:
        myval - value checked
        testval - benchmark value that we are testing against
        acc_range - acceptable range (numerical)

    returns:
        True or False
    """
    return testval - acc_range < myval < testval + acc_range


class TestBS(unittest.TestCase):
    """
    test Black-Scholes model
    """

    def test_greeks(self):
        log = logging.getLogger('TestBS.test_greeks')
        option1 = Option(s=10, k=10.5, r=0.02, q=0, t=180.0 / 365,
                         option_type='call', sigma='0.05', v=None)  # initialize the option
        t_delta = option1.delta()
        t_vega = option1.vega()
        t_theta = option1.theta()
        t_rho = option1.rho()
        t_gamma = option1.gamma()

        log.debug('delta = {}'.format(t_delta))
        log.debug('vega = {}'.format(t_vega))
        log.debug('theta = {}'.format(t_theta))
        log.debug('rho = {}'.format(t_rho))
        log.debug('gamma = {}'.format(t_gamma))

        self.assertTrue(within_range(t_delta, 0.138, 0.001))
        self.assertTrue(within_range(t_vega, 1.55, 0.1))
        self.assertTrue(within_range(t_theta, 0, 0.2))
        self.assertTrue(within_range(t_rho, 0.65, 0.1))
        self.assertTrue(within_range(t_gamma, 0.625, 0.003))

    def test_bs(self):
        log = logging.getLogger('TestBS.test_bs')
        option1 = Option(s=10, k=10.5, r=0.02, q=0, t=180.0 / 365,
                         option_type='call', sigma='0.05', v=None)  # initialize the option
        option2 = Option(s=10, k=10.5, r=0.02, q=0, t=180.0 / 365,
                         option_type='put', sigma='0.05', v=None)  # initialize the option
        c_price = option1.blackscholes()
        p_price = option2.blackscholes()
        log.debug('call price = {}'.format(c_price))
        log.debug('put price = {}'.format(p_price))
        self.assertTrue(within_range(c_price, 0.024, 0.001))
        self.assertTrue(within_range(p_price, 0.425, 0.005))

        option3 = Option(s=10, k=12, r=0.02, q=0, t=180.0 / 365,
                         option_type='call', sigma='0.05', v=None)  # initialize the option
        option4 = Option(s=10, k=12, r=0.02, q=0, t=180.0 / 365,
                         option_type='put', sigma='0.05', v=None)  # initialize the option
        c_price = option3.blackscholes()
        p_price = option4.blackscholes()
        log.debug('call price = {}'.format(c_price))
        log.debug('put price = {}'.format(p_price))
        self.assertTrue(0 <= c_price < 0.001)
        self.assertTrue(1.88 < p_price < 1.89)

        # add put-call parity check

    def test_newton(self):
        log = logging.getLogger('TestBS.test_newton')
        option1 = Option(s=10, k=10.5, r=0.02, q=0, t=180.0 / 365,
                         option_type='call', sigma=None, v=0.02414)  # initialize the option
        # kwargs = {}
        # f0_kwargs = {'s': 10, 'k':10.5, 'r':0.02, 'q':0, 't':180.0/365, 'p':0.02414,'option_type':'call'}
        # f0_d_kwargs = {'s': 10, 'k':10.5, 'r':0.02, 'q':0, 't':180.0/365}
        initial_guess = 0.01
        imp_sigma = newton_method(f0=option1.bs_implied_vol, f0_d=option1.get_vega, x0=initial_guess,
                                  tol=0.001, niter=100)
        log.debug('european call implied volatility is {}'.format(imp_sigma))

        res = newton_method(f0=fun1, f0_d=fun2, x0=10, tol=0.001, niter=100)

    def test_thomas(self):
        log = logging.getLogger('TestBS.test_thomas')
        leftmat = np.array([[3., -1., 0.], [-1., 3., -1.], [0., -1., 3.]])
        rightmat = np.array([-1., 7., 7.])
        x = thomas(leftmat, rightmat)
        log.debug('result was {}'.format(x))
        self.assertTrue(np.allclose(x, np.array([0.952, 3.857, 3.619]), atol=0.001))

    def test_cn(self):
        log = logging.getLogger('TestBS.test_cn')
        #option1 = Option(s=10, k=10.5, r=0.02, q=0, t=180.0 / 365,
        #                 option_type='call', sigma=None, v=0.02414)  # initialize the option
        #imp_sigma = option1.cn_implied_vol(x0=0.01, tol=0.00001, niter=100)
        #print(imp_sigma)
        #log.debug('implied volatility after crank nicolson is {}'.format(imp_sigma))
        #
        #option2 = Option(s=50, k=50, r=0.1, q=0, t=0.1, option_type='put', sigma=None, v=3)
        #imp_sigma2 = option2.cn_implied_vol(x0=0.01, tol=0.00001, niter=100)
        #print(imp_sigma2)

        # option3 = Option(s=50, k=50, r=0.1, q=0, t=0.1, option_type='put', sigma=None, v=8)
        # imp_sigma3 = option3.cn_implied_vol(x0=0.01, tol=0.00001, niter=100)
        # print(imp_sigma3)
        # 
        # option4 = Option(s=55., k=50., r=0.1, q=0, t=0.25, option_type='put', sigma=None, v=3.)
        # imp_sigma4 = option4.cn_implied_vol(x0=0.01, tol=0.00001, niter=100)
        # print(imp_sigma4)
        #
        # option5 = Option(s=10.1, k=10., r=0.02, q=0, t=2., option_type='put', sigma=None, v=0.46)
        # imp_sigma5 = option5.cn_implied_vol(x0=0.01, tol=0.00001, niter=100)
        # print(imp_sigma5)
        #
        # option6 = Option(s=50, k=50., r=0.03, q=0, t=2., option_type='put', sigma=None, v=0.5)
        # imp_sigma6 = option6.cn_implied_vol(x0=0.01, tol=0.00001, niter=100)
        # print(imp_sigma6)
        #
        # option7 = Option(s=9.9, k=10., r=0.02, q=0, t=5., option_type='put', sigma=None, v=0.3)
        # imp_sigma7 = option7.cn_implied_vol(x0=0.01, tol=0.00001, niter=100)
        # print(imp_sigma7)
        #
        option8 = Option(s=50, k=50., r=0.1, q=0, t=15., option_type='put', sigma=None, v=0.5)
        imp_sigma8 = option8.cn_implied_vol(x0=0.01, tol=0.00001, niter=100)
        print(imp_sigma8)
        #
        # option9 = Option(s=47.5, k=50., r=0.0, q=0, t=2., option_type='put', sigma=None, v=3.)
        # imp_sigma9 = option9.cn_implied_vol(x0=0.01, tol=0.00001, niter=100)
        # print(imp_sigma9)


if __name__ == '__main__':
    logging.basicConfig(stream=sys.stderr)
    #	logging.getLogger('TestBS.test_greeks').setLevel(logging.DEBUG)
    #	logging.getLogger('TestBS.test_bs').setLevel(logging.DEBUG)
    #	logging.getLogger('TestBS.test_newton').setLevel(logging.DEBUG)
    #	logging.getLogger('newton_method').setLevel(logging.DEBUG)
    #	logging.getLogger('TestBS.test_thomas').setLevel(logging.DEBUG)
    #	logging.getLogger('Thomas').setLevel(logging.DEBUG)
    logging.getLogger('TestBS.test_cn')
    unittest.main()


