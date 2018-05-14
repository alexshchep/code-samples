/*
 Minion's bored game
 ===================
 There you have it. Yet another pointless "bored" game created by the bored minions of Professor Boolean.
 The game is a single player game, played on a board with n squares in a horizontal row. The minion places a token on the left-most square and rolls a special three-sided die.
 If the die rolls a "Left", the minion moves the token to a square one space to the left of where it is currently. If there is no square to the left, the game is invalid, and you start again.
 If the die rolls a "Stay", the token stays where it is.
 If the die rolls a "Right", the minion moves the token to a square, one space to the right of where it is currently. If there is no square to the right, the game is invalid and you start again.
 The aim is to roll the dice exactly t times, and be at the rightmost square on the last roll. If you land on the rightmost square before t rolls are done then the only valid dice roll is to roll a "Stay". If you roll anything else, the game is invalid (i.e., you cannot move left or right from the rightmost square).
 To make it more interesting, the minions have leaderboards (one for each n,t pair) where each minion submits the game he just played: the sequence of dice rolls. If some minion has already submitted the exact same sequence, they cannot submit a new entry, so the entries in the leader-board correspond to unique games playable.
 Since the minions refresh the leaderboards frequently on their mobile devices, as an infiltrating hacker, you are interested in knowing the maximum possible size a leaderboard can have.
 Write a function answer(t, n), which given the number of dice rolls t, and the number of squares in the board n, returns the possible number of unique games modulo 123454321. i.e. if the total number is S, then return the remainder upon dividing S by 123454321, the remainder should be an integer between 0 and 123454320 (inclusive).
 n and t will be positive integers, no more than 1000. n will be at least 2.
*/


import java.util.Map;
import java.util.HashMap;
import java.math.BigInteger;


public class q8Game{
	public static class Key{
		int position;
		int squaresLeft;
		int rolls;
		public Key(int position, int squaresLeft, int rolls){
			this.position = position;
			this.squaresLeft = squaresLeft;
			this.rolls = rolls;
		}
		@Override
		public boolean equals(Object o){
			if (this == o) return true;
			if(!(o instanceof Key )) return false;
			Key key = (Key) o;
			return position == key.position 
			&& squaresLeft == key.squaresLeft
			&& rolls == key.rolls;
		}
		@Override
		public int hashCode(){
			int result = 1;
			result = result * 31 + position;
			result = result * 31 + squaresLeft;
			result = result * 31 + rolls;
			return result;

		}
	}
	public static Map<Key, Integer> map = new HashMap<Key, Integer>();
	//public static int originalN = 0;
	//public static boolean set = false;
	// static class Moves{
	// 	public int rights;
	// 	public int lefts;
	// 	public int stays;
	// }
	//public static map<Integer, Moves> = new HashMap<Integer, Moves>();
//	public static Map<Integer, BigInteger> map = new HashMap<Integer, BigInteger>();
//	public static HashMap<Integer, HashMap<Integer, BigInteger>> bigMap = new HashMap<Integer, HashMap<Integer, BigInteger>>();

	public static void main(String[] args) {
		//int t = 1000;
		//int n = 12;
		int iter = 10;
		for (int t =0; t<=iter;t++ ) {
	//		System.out.println("+++++++++++++++++++++");
            System.out.println();
            System.out.println("new t =" + t);

            for (int n = 0;n<=iter;n++ ) {
    //        	set = false;
     //       	System.out.println();
                System.out.print(answer(t,n) +  " | ");
                
            	//(t,n);
                
                
            }
        }	
	//	System.out.println(answer(3,2));
		// Moves move = new Moves();
		// System.out.println(move.rights);
	}
	public static int answer(int t, int n){
		if (n == 0)
			return 0;
		return calc(0,n-1,t);
	}
	public static int calc(int pos, int sqLeft, int rollsLeft){
		Key temp = new Key(pos, sqLeft, rollsLeft);
		if(map.containsKey(temp)){
			return map.get(temp);
		}
		if (pos < 0){
			return 0;
		}
		else if(rollsLeft < sqLeft){
			return 0;
		}
		else if(rollsLeft == sqLeft){
			return 1;
		}
		else if(sqLeft == 0){
			return 1;
		}
		else if(pos == 0){
			int value = calc(pos+1, sqLeft-1, rollsLeft-1)%123454321 + calc(pos, sqLeft, rollsLeft-1)%123454321;
			map.put(temp, value);
			return value;
		}
		else{
			int value = calc(pos+1, sqLeft-1, rollsLeft-1)%123454321 + calc(pos, sqLeft, rollsLeft-1)%123454321 + calc(pos-1, sqLeft+1, rollsLeft-1)%123454321;
			map.put(temp, value);
			return value;
		}
	}

	//	System.out.println(" t is " + t);
// 	//	System.out.println("n is " + n);
// 		int myVal = answer2(t,n).intValue();
// 		return myVal;
// 	}
// 	public static BigInteger answer2(int t, int n){
// 		if (!set){
			
// 			originalN = n;
// //			System.out.println("originalN is " + originalN);
// 			set = true;
// 		}
// 		if (originalN < n){
// 	//		System.out.println("went to the left of the first square");
// 			return BigInteger.ZERO;
// 		}
// 		if (n == 0){
// 	//		System.out.println("n is zero");
// 			return BigInteger.ZERO;
// 		}
// 		if(bigMap.containsKey(n)){
// 			HashMap<Integer, BigInteger> tempMap = bigMap.get(n);
// 			if(tempMap.containsKey(t) && originalN > n){
// 		//		 System.out.println("-----------------");
// 		//		 System.out.println("t is " + t);
// 	 	//		System.out.println("n is " + n);
// 	 	//	  	System.out.println("original n is " + originalN);
// 		//		 System.out.println("retrieved " + tempMap.get(t));
// 				return tempMap.get(t);
// 			}
// 		}
// 		else {

// 			HashMap<Integer, BigInteger> tempMap = new HashMap<Integer, BigInteger>();
// 			bigMap.put(n, tempMap);
// 		}
// 		HashMap<Integer, BigInteger> tempMap = bigMap.get(n);
// 		// System.out.println("------------------------------");
// 		// System.out.println("t is " + t);
// 	 // 	System.out.println("n is " + n);
// 	 //	System.out.println("added letter is " + result);

// 		if (n > (t+1)){
// 		//	System.out.println("finish is unreachable");
// 			// tempMap.put(t, 1)
// 			// bigMap.put(n,tempMap);
// 			return  BigInteger.ZERO;
// 		}
// 		if (n == 1){
// 		//	System.out.println("n is 1 adding: " + (t-2));
// 	//		System.out.println("last space reached: " + (t-2));;
// 			tempMap.put(t, BigInteger.ONE);
// 			bigMap.put(n,tempMap);
// 			return BigInteger.ONE;
// 		}
// 		// if (n == 2){
// 		// 	String atTwo = String.valueOf(t);
// 		// 	tempMap.put(t, new BigInteger(atTwo));
// 		// 	bigMap.put(n,tempMap);
// 		// 	return  new BigInteger(atTwo);
// 		// }
// 		if (n == (t+1)){
// 		//	System.out.println("remaining ones are all r");
// 			tempMap.put(t,  BigInteger.ONE);
// 			bigMap.put(n,tempMap);
// 			return  BigInteger.ONE;
// 		}

// 		//System.out.println("++++++++++++++++++++++++++++++++");
// 		// return right + stay + left
// 		String val = String.valueOf(123454321);
// 		BigInteger  ans1 = answer2( t-1, n-1).mod(new BigInteger(val));
// 		BigInteger ans2 = answer2( t-1, n).mod(new BigInteger(val));
// 		BigInteger ans3 = answer2(t-1, n+1).mod(new BigInteger(val));
// 		// System.out.println("-------------------------------");
		 
// 	 //  	System.out.println("n is " + n);
// 	 //  	System.out.println("t is " + t);
// 	 //  	System.out.println("-------------------------------");
// 	//	BigInteger result = (answer2( t-1, n-1).add(answer2( t-1, n)).add(answer2(t-1, n+1))).mod(new BigInteger(val));
// 		BigInteger result = (ans1.add(ans2).add(ans3)).mod(new BigInteger(val));
// 		//tempMap = bigMap.get
// 		 if (!tempMap.containsKey(t)){
// 	//		System.out.println("-------------------------------");
// 		 	// System.out.println("-------------------------------");
// 		 	// System.out.println("ans1 is " + ans1);
// 	   // 	System.out.println("ans2 is " + ans2);
// 	   // 	System.out.println("ans3 is " + ans3);
// 		 	// System.out.println(" t is " + t);
// 		 	// System.out.println(" n is " + n);
// 		 	// System.out.println(result);
// 		 	// System.out.println("-------------------------------");
// 	//	 	System.out.println("-------------------------------");
// 			tempMap.put(t, result);
// 			bigMap.put(n,tempMap);
// 		}

	//	bigMap.put(n,tempMap);
//		return result;
		// Moves[] movesArr = new Moves[t-n+2];
		// for (int i =0; i<movesArr.length; i++) {
		// 	movesArr[i] = new Moves();
		// }
		// movesArr[0].rights = n-1;
		// movesArr[1].rights = n-1;
		// movesArr[1].stays = 1;
		// for (int i =2; i<movesArr.length;i++ ) {
		// 	movesArr[i].stays = 
		// }
	//	return count(t,n,n-1,0,0);

//	}
	// public static int count(int t, int n){
	// 	System.out.println("t is " + t);
	// 	System.out.println("n is " + n);
	// 	System.out.println("rights is " + rights);
	// 	System.out.println("lefts is " + lefts);
	// 	System.out.println("stays is " + stays);
	// 	System.out.println("--------------------");
	// 	if(n == 2){
	// 		return t;
	// 	}
	// 	else if (n > (t+1)){
	// 		return 0;
	// 	}
	// 	else if (n == (t+1)){
	// 		return 1;
	// 	}
	// 	else if (n==0){
	// 		return 0;
	// 	}
	// 	else {
	// 		// place 'stay' between rights 
	// 		// place "RL"
	// 		return count(t-1, n, rights, lefts+(rights+stays-1), stays+(rights+lefts-1)) + (rights+lefts-1) * (t-1) 
	// 		+ (rights+stays-1) * (t-1) ;
	// 	}

	// }
}
