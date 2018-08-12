import math
import os
import random
import re
import sys
import itertools
import time

class Trie():
    def __init__(self):
        self.root = [None, None]
        self.current = self.root
        self.counter = 0

    def addword(self, word):
        parent = self.root
        chararr = map(int, word)
        for c in chararr:
            if parent[c] is None: # create new
                node = [None, None]
                parent[c] = node
                parent = node
            else:
                parent = parent[c]

    def find_closest_and_compute(self, word):
        #       print('------finding closest, word is {}-----------'.format(word))
        ans = []
        append = ans.append
        parent = self.root
        chararr = map(int, word)
        for cr in chararr:
            c = cr != 1 # reverse the int
            if parent[c] is not None:
                parent = parent[c]
                append('1')
            else:
                append('0')
                parent = parent[cr]
        return int(''.join(ans), 2)

# Complete the maxXor function below.
def maxXor(arr, queries):
    ans = []
    tree = Trie()
    arr = sorted(arr)
    start = time.time()
    arr = map('{:030b}'.format, arr)
    map(tree.addword, arr)
    end = time.time()
    print('time to add {}'.format(end - start))
    start = time.time()
    queries = map('{:030b}'.format, queries)
    ans = map(tree.find_closest_and_compute, queries)
    end = time.time()
    print('time to find {}'.format(end - start))
    return ans

if __name__ == '__main__':
    totaltime = 0
    with open('testxor3.txt') as f:
        numcases = int(next(f))
        arrs = next(f).split(' ')
        arr = []
        for a in arrs:
            a = int(a.strip())
            arr.append(a)
        numq = int(next(f))
        queries = []
        for i in range(numq):
            q = int(next(f))
            queries.append(q)
        start = time.time()
        result = maxXor(arr, queries)
        end = time.time()
        totaltime += (end - start)
        print(totaltime)
       # print(result[:10])

    # check answers
    with open('testxor3ans.txt') as f:
        ans = []
        for line in f:
            try:
                x = int(line.strip())
                ans.append(x)
            except ValueError:
                pass
    zipped = zip(result, ans)
    for  idx, myz in enumerate(zipped):
        if myz[0] != myz[1]:
            print(idx)
            print('wrong')
        #    print(myz[0])
         #   print(myz[1])
    print('correct')