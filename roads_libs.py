#!/bin/python

import math
import os
import random
import re
import sys
import time
from collections import deque


# Complete the roadsAndLibraries function below.
def roadsAndLibraries(n, c_lib, c_road, cities):
    # libraries are cheaper or equal to roads
    if c_lib <= c_road:
        return n * c_lib
    # libraries are more expensive than roads
    # build the broken roads
    global roads
    roads = {}
    #  myd = {key: value for key, value in cities}
    allcities = []
    for road in cities:
        a = road[0]
        b = road[1]
        allcities.append(a)
        allcities.append(b)
        if a in roads:
            roads[a].append(b)
        else:
            roads[a] = [b]
        if b in roads:
            roads[b].append(a)
        else:
            roads[b] = [a]
    allcities = set(allcities)
    lonelycities = set(range(1, n+1)).difference(allcities)
    global visited
    visited = [False] * (n + 1)
    neigborcities = 0
    for city in roads:
        if visited[city]:
            continue
        if len(roads[city]) == 1:
            neighbor = roads[city][0]
            if len(roads[neighbor]) == 1:
                neigborcities +=1
                visited[city] = True
                visited[neighbor] = True
    #print(min(lonelycities))
    # nislands = calculate_islands(roads, n)
    for city in lonelycities:
        visited[city] = True
    nlonely = len(lonelycities)
    nislands = bfs(min(lonelycities), nlonely+neigborcities)
    return nislands * c_lib + (n - nislands) * c_road


def bfs(current, nislands):
    myq = deque()
    myq.append(current)
    visited[current] = True
    if nislands == 0:
        nislands = 1
    ntimes = 0
    notvisited = 1
    trysort = False
    while myq:  # not empty
        ntimes += 1
        if ntimes %5000 == 0:
            print(ntimes)
        #    print(myq)
        current = myq.popleft()
        try:
            citylist = roads[current]
            citylist = [city for city in citylist if not visited[city]]
            myq.extend(citylist)
            for city in citylist:
                visited[city] = True
        except KeyError:
            pass

        if not myq:  # myq is empty
            try:
                if trysort == False:
                    trysortind = 0
                    sortedvis = [x[0]+1 for x in enumerate(visited[1:]) if x[1] == False]
                    trysort = True

                for x in sortedvis[trysortind:]:
                    if visited[x]:
                        trysortind += 1
                        continue
                    visited[x] = True
                    trysortind += 1
                    nislands += 1
                    myq.append(x)
                    break
                #notvisited = visited[notvisited:].index(False) + notvisited
                #nislands += 1
                #visited[notvisited] = True
                #myq.append(notvisited)
            except ValueError:
                pass

    print(ntimes)
    # print('nislands is {}'.format(nislands))
    return nislands


if __name__ == '__main__':
    totaltime = 0
    with open('test.txt') as f:
        numcases = int(next(f))
        #print(numcases)
     #   q = f.readlines()
     #   q = [x.strip() for x in q]

        for q_itr in xrange(numcases):
            nmC_libC_road = next(f).split()
            #print(nmC_libC_road)
            n = int(nmC_libC_road[0])

            m = int(nmC_libC_road[1])

            c_lib = int(nmC_libC_road[2])

            c_road = int(nmC_libC_road[3])

            cities = []
            #print('{} {} {} {}'.format(n,m,c_lib,c_road))
            for _ in xrange(m):
                cities.append(map(int, next(f).rstrip().split()))
            #print(cities)
            start = time.time()
            result = roadsAndLibraries(n, c_lib, c_road, cities)
            end = time.time()
            totaltime += (end - start)
            print(result)
            print(totaltime)

           # fptr.write(str(result) + '\n')
