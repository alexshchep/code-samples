/*
 Square supplies
 ===============
 With the zombie cure injections ready to go, it's time to start treating our zombified rabbit friends (known as zombits) at our makeshift zombit treatment center. You need to run out really fast to buy some gauze pads but you only have 30 seconds before you need to be back.
 Luckily, the corner store has unlimited gauze pads in squares of all sizes. Jackpot! The pricing is simple - a square gauze pad of size K x K costs exactly K * K coins. For example, a gauze pad of size 3x3 costs 9 coins.
 You're in a hurry and the cashier takes a long time to process each transaction. You decide the fastest way to get what you need is to buy as few gauze pads as possible, while spending all of your coins (you can always cut up the gauze later if you need to). Given that you have n coins, what's the fewest number of gauze pads you can buy?
 Write a method answer(n), which returns the smallest number of square gauze pads that can be bought with exactly n coins.
 n will be an integer, satisfying 1 <= n <= 10000.
 Languages
 =========
 To provide a Python solution, edit solution.py
 To provide a Java solution, edit solution.java
 Test cases
 ==========
 Inputs:
 (int) n = 24
 Output:
 (int) 3
 Inputs:
 (int) n = 160
 Output:
 (int) 2

*/

public class q6Coins{
//	private static int count = 0;
//	private static int smallest[] = new int[10001];

	public static void main(String[] args) {
		int result = answer(160);
		System.out.println(result);
	}
	public static int answer(int n){
		// answer array
		int[][] c = new int[101][n+1];
		// number of denominations
		int number = 100;
		int[] denom = new int[101];
		for (int i = 0; i< 101; i++ ){
			denom[i] = i*i;
			if (denom[i] == n){
				return 1;
			}
		}
		for (int i = 0;i<=n ;i++ ) {
			// row for paying with only $1 coin
			c[number][i] = i;
		}
		// i is position of denomination
		// j is value to pay
		for (int i = number - 1; i > 0; i--) {
			for (int j = 0;j<=n ;j++ ) {
				if((denom[i] > j) || (c[i+1][j] < 1+ c[i][j - denom[i]])){
					c[i][j] = c[i+1][j];
				}
				else{
					c[i][j] = 1 + c[i][j-denom[i]];
				}
			}
		}
		return c[1][n];
		// for (int i = 0; i< smallest.length ;i++ ) {
		// 	smallest[i] = INT_MAX;
		// }
		// int[] arr = new int[101];
		// int limit = 0;
		// for (int i = 0; i< 101; i++ ){
		// 	arr[i] = i*i;
		// 	if(arr[i] == n){
		// 		return 1;
		// 	}
		// 	smallest[i*i] = arr[i];
		// 	// else if(arr[i] > n){
		// 	// 	limit = i-1;
		// 	// 	count++;
		// 	// 	fun(arr, limit, n-arr[limit]);
		// 	// 	break;
		// 	// }
		// }
		// for (int i = 1; i<=n; i++) {
		// 	for(int j = 1;j<i; j++){

		// 	}
		// }
		// return count;
	}
	// public static void fun(int[] arr, int limit, int value){
	// 	for(int i = 1; i<= limit;i++){
	// 		if(arr[i] == value)
	// 			{count++;
	// 				break;}
	// 		else if (arr[i] > value) {
	// 		 	limit = i - 1;
	// 		 	count++;
	// 		 	value -= arr[limit];
	// 		 	fun(arr, limit, value);
	// 		 	break;
	// 		 } 
	// 	}
	// }
}
