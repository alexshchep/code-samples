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

def crank_nicolson():
	pass

def tridiagonal():
	pass

def fun1(x):
	return x**2 - 612
def fun2(x):
	return 2*x

def newton_method(f0, f0_d, x0, tol, niter=100, random_guess_range = (0,1), f0_kwargs = {}, f0_d_kwargs = {}):
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
		f0_val = float(f0(x0, **f0_kwargs)) # output of the function
		f0_d_val = f0_d(x0, **f0_d_kwargs)  # output of the derivative function
		#log.debug('function returns {}, derivative returns {}'.format( f0_val,f0_d_val))
		if math.fabs(f0_val) < tol:
			return x0
		# avoid division by zero, and guess randomly instead	
		if f0_d_val == 0:
			x0 = random.uniform(random_guess_range[0],random_guess_range[1])
			continue			
		else:
			x1 = x0 - f0_val / f0_d_val
		
		log.debug('i is {}, x0 is {}, x1 is {}'.format(i, x0, x1))
		x0 = x1	

	return x1
class Option(object):
	"""
	A financial option, either a call or a put.
	Attributes:
		s - price of the stock
		k - strike price of the option
		r - risk free rate (percentage points)
		q - continuosly compounded dividend yield (percentage points)
		t - time to expiration (years)
		option_type - type of an option (put or call)
		sigma - stock volatility		
		v - option price
	"""

	def __init__(self, s, k, r, q, t, option_type, sigma, v):
	"""
	initialize object type Option
	"""
		self.s = s
		self.k = k 
		self.r = r
		self.q = q
		self.t = t
		self.option_type = option_type
		self.sigma = sigma
		self.v = v
 
	def bs_implied_vol(vol, p, **kwargs):
		"""
		equation to solve for the implied volatility using the newton method
	
		parameters:
			vol - volatility of the option	
			p - price of the option
			kwargs - other arguments for the blackscholes model
		returns:
			difference between the model prediction and the price
		"""
		return self.blackscholes(vol = vol, **kwargs) - self.v 	

	def blackscholes(s, k, r, vol, q, t, option_type):
		"""
		c_price = N(d1)*s - N(d2)*k*e^(-r*t)
		p_price = N(-d2)*k*e^(-r*t) - N(-d1)*s
	
		parameters:
			s - stock price,
			k - strike price,
			r - risk free rate (percentage points),	
			vol - volatility of the stock (percentage points),
			q - continuosly compounded dividend yield (percentage points),
			t - time to expiration (years),
			option_type - type of an option (put or call)
		returns:
			price of the option
		"""
		option_type = option_type.lower()
		assert option_type in ['call', 'put'], 'option_type needs to be either a put or a call'
		if option_type == 'call':
			return s * math.exp(-q * t) * norm.cdf(d1(s, k, t, r, q, vol)) \
			- k * math.exp(-r*t) * norm.cdf(d2(s, k, t, r, q, vol))
		elif option_type == 'put':
			return k * math.exp(-r * t) * norm.cdf(-d2(s, k, t, r, q, vol)) \
			- s * math.exp(-q * t) * norm.cdf(-d1(s, k, t, r, q, vol))

def bs_derivative(vol, s, k, r, q, t, p, option_type):
	"""
	dprice/dt + 1/2*vol^2*s^2*d2price/d2s + r*s*dprice/ds - r*price_deriv = 0 
	parameters:
		s - stock price,
		k - strike price,
		vol - volatility of the stock (percentage points),
		r - risk free rate (percentage points),	
		q - continuosly compounded dividend yield (percentage points),
		t - time to expiration (years),
		p - price derivative 
		option_type - type of an option (put or call)
	returns:
		derivative of the price of the option
	"""
	print('vol {}, s {}, k {}, r {}, q {}, t {}, p {}, option_type {}'.format(vol, s, k, r, q, t, p, option_type))
	option_type = option_type.lower()
	assert option_type in ['call', 'put'], 'option_type needs to be either a put or a call'
	return (theta(s, k, r, vol, q, t, option_type) + 1.0/2 * vol**2 * s**2 * gamma(s, k, r, vol, q, t) \
		+ r * s * delta(s, k, r, vol, q, t, option_type)) \
		- r * p

def delta(s, k, r, vol, q, t, option_type):
	"""
	Delta is the change in option price over change in stock price
	parameters:
		s - stock price
		k - strike price
		r - risk free rate (percentage points)
		vol - volatility of the stock (percentage points)
		q - continuosly compounded dividend yield (percentage points) 
		t - time to expiration (years)
		option_type - type of an option (put or call)
	
	returns:
		delta of the european option
	"""
	option_type = option_type.lower()
	assert option_type in ['call', 'put'], 'option_type needs to be either a put or a call'

	if option_type == 'call':
		return math.exp(-q * t) * norm.cdf(d1(s, k, t, r, q, vol))
	elif option_type == 'put':
		return -math.exp(-q * t) * norm.cdf(d1(s, k, t, r, q, vol))

def vega(vol, s, k, r, q, t):
	"""
	Vega is the change in option price over change in stock volatility.
	It is identical for european put and call.
	parameters:
		s - stock price
		k - strike price
		r - risk free rate (percentage points)
		vol - volatility of the stock (percentage points)
		q - continuosly compounded dividend yield (percentage points) 
		t - time to expiration (years)
	
	returns:
		vega of the european option ($/units)
		divide by 100 if you want ($/%)
	"""
	return s * math.exp(-q * t) * norm.pdf(d1(s, k, t, r, q, vol)) * math.sqrt(t) 

def theta(s, k, r, vol, q, t, option_type):
	"""
	Theta is the change in option price over change in time to expiration.
	It is identical for european put and call.
	parameters:
		s - stock price
		k - strike price
		r - risk free rate (percentage points)
		vol - volatility of the stock (percentage points)
		q - continuosly compounded dividend yield (percentage points) 
		t - time to expiration (years)
		option_type - type of an option (put or call)
	
	returns:
		theta of the european option ($/year)
		divide by 365.0 if you want ($/day)
	"""
	option_type = option_type.lower()
	assert option_type in ['call', 'put'], 'option_type needs to be either a put or a call'

	if option_type == 'call':
		return (-math.exp(-q * t) * (s * norm.pdf(d1(s, k, t, r, q, vol)) * vol) / float(2 * math.sqrt(t)) \
			- r * k * math.exp(-r * t) * norm.cdf(d2(s, k, t, r, q, vol)) \
			+ q * s * math.exp(-q * t) * norm.cdf(d1(s, k, t, r, q, vol))) 
	elif option_type == 'put':
		return (-math.exp(-q * t) * (s * norm.pdf(d1(s, k, t, r, q, vol)) * vol) / float(2 * math.sqrt(t)) \
			+ r * k * math.exp(-r * t) * norm.cdf(-d2(s, k, t, r, q, vol)) \
			- q * s * math.exp(-q * t) * norm.cdf(-d1(s, k, t, r, q, vol))) 


def rho(s, k, r, vol, q, t, option_type):
	"""
	Rho is the change in option price over change in interest rate.
	It is identical for european put and call.
	parameters:
		s - stock price
		k - strike price
		r - risk free rate (percentage points)
		vol - volatility of the stock (percentage points)
		q - continuosly compounded dividend yield (percentage points) 
		t - time to expiration (years)
		option_type - type of an option (put or call)
	
	returns:
		rho of the european option ($/units)
		divide by 100 if you want ($/%)
	"""
	option_type = option_type.lower()
	assert option_type in ['call', 'put'], 'option_type needs to be either a put or a call'

	if option_type == 'call':
		return k * t * math.exp(-r * t) * norm.cdf(d2(s, k, t, r, q, vol)) 
	elif option_type == 'put':
		return -k * t * math.exp(-r * t) * norm.cdf(-d2(s, k, t, r, q, vol))

def gamma(s, k, r, vol, q, t):
	"""
	Gamma is the second derivative of change in option price over change is stock price
	It is identical for european put and call.
	parameters:
		s - stock price
		k - strike price
		r - risk free rate (percentage points)
		vol - volatility of the stock (percentage points)
		q - continuosly compounded dividend yield (percentage points) 
		t - time to expiration (years)
	
	returns:
		gamma of the european option	
	"""
	return (math.exp(-q * t) * norm.pdf(d1(s, k, t, r, q, vol))) / float(s * vol * math.sqrt(t))



def d1(s, k, t, r, q, vol):
	"""
	N(d1) is the factor by which the present value
	of contingent receipt of the stock exceeds the current stock price (Nielsen explanation)
	
	parameters:
		s - stock price
		k - strike price 
		t - time to expiration (in years)
		r - risk free rate(percentage points)
		q - continuosly compounded dividend yield (percentage points) 
		vol - volatility of a stock (percentage points)
		
	returns:
		value to input into a standard normal cumulative distribution function
	"""
	return (math.log(float(s)/k) + t * (r - q + vol**2 / 2.0)) / (vol * math.sqrt(t))

def d2(s, k, t, r, q, vol):
	"""
	N(d2) is the risk-adjusted probability that the option will
	be exercise (Nielsen explanation)

	parameters:
		s - starting price of a stock
		k - strike price 
		t - time to expiration (in years)
		r - risk free rate(percentage points)
		q - continuosly compounded dividend yield (percentage points) 
		vol - volatility of a stock (percentage points)
		
	returns:
		value to input into a standard normal cumulative distribution function
	"""

	return d1(s, k, t, r, q, vol) - vol * math.sqrt(t)

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
	test BlackScholes model
	"""
	def test_greeks(self):
		log = logging.getLogger('TestBS.test_greeks')

		t_delta = delta(s=10, k=10.5, r=0.02, vol=0.05, q=0, t=180.0/365, option_type= 'call')
		t_vega = vega(s=10, k=10.5, r=0.02, vol=0.05, q=0, t=180.0/365)
		t_theta = theta(s=10, k=10.5, r=0.02, vol=0.05, q=0, t=180.0/365, option_type= 'call')
		t_rho = rho(s=10, k=10.5, r=0.02, vol=0.05, q=0, t=180.0/365, option_type= 'call')
		t_gamma = gamma(s=10, k=10.5, r=0.02, vol=0.05, q=0, t=180.0/365)

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
		c_price = blackscholes(s=10, k=10.5, r=0.02, vol=0.05, q=0, t=180.0/365, option_type= 'call')
		p_price = blackscholes(s=10, k=10.5, r=0.02, vol=0.05, q=0, t=180.0/365, option_type= 'put')
		log.debug('call price = {}'.format(c_price)) 
		log.debug('put price = {}'.format(p_price)) 
		self.assertTrue(within_range(c_price, 0.024, 0.001))
		self.assertTrue(within_range(p_price, 0.425, 0.005))

		c_price = blackscholes(s=10, k=12, r=0.02, vol=0.05, q=0, t=180.0/365, option_type= 'call')
		p_price = blackscholes(s=10, k=12, r=0.02, vol=0.05, q=0, t=180.0/365, option_type= 'put')
		log.debug('call price = {}'.format(c_price)) 
		log.debug('put price = {}'.format(p_price)) 
		self.assertTrue( 0 <= c_price < 0.001)
		self.assertTrue(1.88 < p_price < 1.89)

		# add put-call parity check
	def test_newton(self):
		log = logging.getLogger('TestBS.test_newton')
		f0_kwargs = {'s': 10, 'k':10.5, 'r':0.02, 'q':0, 't':180.0/365, 'p':0.02414,'option_type':'call'}
		f0_d_kwargs = {'s': 10, 'k':10.5, 'r':0.02, 'q':0, 't':180.0/365}
		initial_guess = 0.01
		imp_vol = newton_method(f0 = bs_implied_vol, f0_d = vega, x0 = initial_guess, 
			tol = 0.001, niter= 100, f0_kwargs = f0_kwargs, f0_d_kwargs = f0_d_kwargs)
		log.debug('european call implied volatility is {}'.format(imp_vol))

		res = newton_method(f0 = fun1, f0_d = fun2, x0 = 10, tol = 0.001, niter = 100)
		

if __name__ == '__main__':
	logging.basicConfig(stream = sys.stderr)
	logging.getLogger('TestBS.test_greeks').setLevel(logging.DEBUG)
	logging.getLogger('TestBS.test_bs').setLevel(logging.DEBUG)
	logging.getLogger('TestBS.test_newton').setLevel(logging.DEBUG)
	logging.getLogger('newton_method').setLevel(logging.DEBUG)
	unittest.main()
		
	
