
/*
Line up the captives
====================
As you ponder sneaky strategies for assisting with the great rabbit escape,
you realize that you have an opportunity to fool Professor Booleans guards
into thinking there are fewer rabbits total than there actually are.
By cleverly lining up the rabbits of different heights, you can obscure the
sudden departure of some of the captives.
Beta Rabbits statisticians have asked you for some numerical analysis of how
this could be done so that they can explore the best options.
Luckily, every rabbit has a slightly different height, and the guards are lazy
and few in number. Only one guard is stationed at each end of the rabbit
line-up as they survey their captive population.
With a bit of misinformation added to the facility roster, you can make the
guards think there are different numbers of rabbits in holding.
To help plan this caper you need to calculate how many ways the rabbits can be
lined up such that a viewer on one end sees x rabbits, and a viewer on the other
end sees y rabbits, because some taller rabbits block the view of the shorter
ones.
For example, if the rabbits were arranged in line with heights 30 cm, 10 cm,
50 cm, 40 cm, and then 20 cm,a guard looking from the left would see 2 rabbits
while a guard looking from the right side would see 3 rabbits.
Write a method answer(x,y,n) which returns the number of possible ways to
arrange n rabbits of unique heights along an east to west line, so that only x
are visible from the west, and only y are visible from the east. The return
value must be a string representing the number in base 10.
If there is no possible arrangement, return "0".
The number of rabbits (n) will be as small as 3 or as large as 40
The viewable rabbits from either side (x and y) will be as small as 1 and as
large as the total number of rabbits (n).

*/

import java.math.BigInteger;
import java.util.Map;
import java.util.HashMap;
// line up the captives
public class q7Escape{

	public static Map<Integer, BigInteger> factorialMap = new HashMap<Integer, BigInteger>();
	// map contains values for the function fun(n,x,multiplier), which calculates number of seen rabbits from one side
	// first key = total number of rabbits
	// second key = number of rabbits seen from one side
	// value retrieved = total possible ways 
	public static HashMap<Integer, HashMap<Integer, BigInteger>> funValsMap = new HashMap<Integer, HashMap<Integer, BigInteger>>();
	public static boolean populated = false;

	public static void main(String[] args) {
			int iter = 40;
		    for (int i =1; i<=iter;i++ ) {
            System.out.println();
            System.out.println("new x =" + i);

            for (int j = 1;j<=iter;j++ ) {
                System.out.print(answer(i,j,iter) + " | ");
                
            }
        }	
	}
	// populates the hashmap for funValsMap
	public static void populate(int max){
		for(int i = 1; i<=max; i++){
			for (int j = 1;j <=i ;j++ ) {
				BigInteger temp = fun(i,j,BigInteger.ONE);
				if (funValsMap.containsKey(i)) {
					HashMap<Integer, BigInteger> funMap = funValsMap.get(i);
					funMap.put(j, temp);
					funValsMap.put(i, funMap);
				}
				else{

					HashMap<Integer, BigInteger> funMap = new HashMap<Integer, BigInteger>();
		 			funMap.put(j, temp);
		 			funValsMap.put(i, funMap);
		 		}
			}	
		}
	}
	public static String answer(int x, int y, int n){
		if(!populated){
			// populate the funValsMap
			populate(40);
			populated = true;
		}
		if((x == 1 && y ==1)){
		//	always zero
			return "0";
		}
		if (x+y > (n+1)){
		//	n and y are too large;
			return "0";
		}		
		int smaller;
		int larger;
		BigInteger ans = BigInteger.ZERO;
		BigInteger multiplier = BigInteger.ONE;
		if (x > y ){
			smaller = y;
			larger = x;
		}
		else {
			smaller = x;
			larger = y;
		}
		// base case: etiher x or y is one, that means the tallest rabbit is on the side
		if (smaller == 1){
			ans = fun(n-1, larger-1, multiplier);
		}

		else if (smaller == 2 && larger == 2) {
			ans = fun(n-1, smaller, multiplier).multiply(new BigInteger("2"));
		}
		else if(smaller == larger){
			for(int i = 0; smaller+larger+i<= n+1; i++){
				// the left and right side mirror each other
				if((smaller + i) > n/2 && n%2 == 0){
					ans = ans.multiply(new BigInteger("2"));
					break;
				}
			 	ans = ans.add(fun(n-smaller-i, larger-1,fun(smaller-1+i, smaller -1, comb(n-1,smaller-1+i))));
			}
		}
		// every other case: moves the tallest rabbit to all possible spots
		else {
			int xVal = 0;
			int combVal = 0;
			int nVal = 0;
			for(int i = 0; smaller+larger+i<= n+1; i++){
				// i moves the rabbit until the tallest rabbit cannot be placed without breaking the parameters
				ans = ans.add(fun(n-smaller-i, larger-1,fun(smaller-1+i, smaller -1, comb(n-1,n-smaller-i))));			
			}
		}
		String str = String.valueOf(ans);
		return str;

	}
	
	/*
		multiplier is number of possible numbers that could be there
	*/
	public static BigInteger fun(int n,int x,BigInteger multiplier){
		if (funValsMap.containsKey(n)) {
			HashMap<Integer, BigInteger> funMap = funValsMap.get(n);
			if(funMap.containsKey(x)){
				BigInteger big = funMap.get(x);
				return funMap.get(x).multiply(multiplier);
			}
		}
		if(n<x){
			return BigInteger.ZERO;
		}
		if (x < 1 )
			return BigInteger.ZERO;
		if (n == x){
			BigInteger temp = BigInteger.ONE;
			return temp.multiply(multiplier);
		}
		if(n==1){
			return multiplier;
		}
		else if (x == 1){

			BigInteger result =  perm(n-1);
			 return result.multiply(multiplier);
		}
		else if(x ==2)
		{

			String val = String.valueOf(n-1);
			BigInteger temp = fun(n-1, x, BigInteger.ONE );
			BigInteger result =  perm(n-2).multiply(multiplier).add(temp.multiply(multiplier.multiply(new BigInteger(val))));
			 return result;
		}
		// divides the problem into two smaller problems
		// e.g. n= 6, x = 4, divides the problem into n = 5, x= 5, and n= 5, x= 4
		else{

			String val = String.valueOf(n-1);
			BigInteger temp = fun(n-1, x, BigInteger.ONE );
		 	 BigInteger temp2 = fun(n-1, x-1, BigInteger.ONE );
			BigInteger result =  temp2.multiply(multiplier).add(temp.multiply(multiplier.multiply(new BigInteger(val))));
			 return result;

		}
	}

	// factorials or permutations for when tallest rabbit is at the front
	public static BigInteger perm(int n){
		if(!factorialMap.containsKey(n)){
			BigInteger product = BigInteger.ONE;
			for (int i = 1; i<=n;i++){
				product = product.multiply(new BigInteger(String.valueOf(i)));
			}
			factorialMap.put(n, product);
		}	
		return factorialMap.get(n);
	}
	// possible combinations
	public static BigInteger comb(int n, int r){
		if(factorialMap.containsKey(n) 
			&& factorialMap.containsKey(r) 
			&& factorialMap.containsKey(n-r)){
			return factorialMap.get(n).divide(factorialMap.get(r).multiply(factorialMap.get(n-r)));
		}
		BigInteger top = BigInteger.ONE;
		BigInteger bottom = BigInteger.ONE;
		for (int i = n; i > (n-r); i--){
			top = top.multiply(new BigInteger(String.valueOf(i)));
		}
		for (int i = 1; i <= r; i++){
			bottom = bottom.multiply(new BigInteger(String.valueOf(i)));
		}
		return top.divide(bottom);
	}
}
