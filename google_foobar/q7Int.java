

public class q7Int {   
	public static void main(String[] args) {
		System.out.println(perm(40));
		System.out.println(comb(6,2));
		int iter = 30;
		    for (int i =1; i<=iter;i++ ) {
            System.out.println();
            System.out.println("new x =" + i);

            for (int j = 1;j<=iter;j++ ) {
                System.out.print(answer(i,j,iter) + " | ");
                
            }
        }		
	}
    		public static String answer(int x, int y, int n){
		if((x == 1 && y ==1)){
		//	System.out.println("first 0");
			return "0";
		}
		if (x+y > (n+1)){
		//	System.out.println("n and y are too large");
			return "0";
		}		
		int smaller;
		int larger;
		int ans = 0;
		int multiplier = 1;
		if (x > y ){
			smaller = y;
			larger = x;
		}
		else {
			smaller = x;
			larger = y;
		}
		if (smaller == 1){
		//	System.out.println(n-1);
		//	System.out.println(larger);
			ans = fun(n-1, larger-1, multiplier);
		}
		else if (smaller == 2 && larger == 2) {
			ans = fun(n-1, smaller, multiplier) * 2;
		}

		else if(smaller == larger){
			for(int i = 0; smaller+larger+i<= n+1; i++){
				if((smaller + i) > n/2 && n%2 == 0){
					//System.out.println("broker at i = " + i);
					ans*=2;
					break;
				}
			//	else if(((double)smaller + i) > ((double)n/2))
			 	ans += fun(n-smaller-i, larger-1,fun(smaller-1+i, smaller -1, comb(n-1,smaller-1+i)));
			}
		//	ans*=2;
		}
		// else if ((smaller + larger) > n){
		// 	ans = comb(n-1,n-larger);
		// }
		else {
			int xVal = 0;
			int combVal = 0;
			int nVal = 0;
			for(int i = 0; smaller+larger+i<= n+1; i++){

	// 		  if((smaller + larger + i) > (n-1)){
	// 		  	xVal = xVal - 1 ;
	// //		  	combVal = n- larger
	// 		  }

			// System.out.println("i is " + i);
			// System.out.println("n is " + (n-smaller-i));
			// System.out.println(" x is " + (larger-1));
			// //System.out.println("combination of " + comb(n-1,n-smaller-i));
			// System.out.println("permutation " + fun(smaller+i,smaller-1,1));
			// System.out.println("-------------- " );
			// if((smaller + i) == (n-1) ){
			// 	ans += fun(n-1, larger-1, fun);
			// }
			// else
			//	 ans += fun( n-smaller-i, larger-1,comb(n-1,n-smaller-i));
			//ans += fun( n-smaller-i, larger-1,comb(n-1,smaller-1+i));
				ans +=fun(n-smaller-i, larger-1,fun(smaller-1+i, smaller -1, comb(n-1,n-smaller-i)));
			
			}
		}
		String str = String.valueOf(ans);
	//	System.out.println(ans);
		return str;

	}
	
	/*
		multiplier is number of possible numbers that could be there
	*/
	public static int fun(int n,int x,int multiplier){
		// System.out.println("n is " +n);
		// System.out.println("x is " +x);
		// System.out.println("multiplier is " + multiplier);

		// dont forget to memoize!
		if(n<x){
			return 0;
		}
		if (x < 1 )
			return 0;
/*		if (y < 1)
			return 0;*/
		if (n == x){
			return 1 * multiplier;
		}
		if(n==1){
			return multiplier;
		}
		else if (x == 1){
			int result = perm(n-1) * multiplier;
			//System.out.println(" result is " + result);
			return result;
		//	return perm(n-1) * multiplier;
		}
		else if(x ==2)
		{
			int result = perm(n-2) * multiplier + fun(n-1, x, multiplier * (n-1));
			//System.out.println(" result is " + result);
			return result;
		//	return perm(n-2) * multiplier + fun(n-1, x, multiplier * (n-1));
		}
		else{
			int result = fun(n-1, x-1, multiplier) + fun(n-1, x, multiplier * (n-1));
			//System.out.println(" result is " + result);
			return result;
		//	return fun(n-1, x-1, multiplier) + fun(n-1, x, multiplier * (n-1));
		}
	}

	public static int perm(int n){
		int product = 1;
		for (int i = 1; i<=n;i++){
			product *= i;
		}
		return product;
	}
	public static int comb(int n, int r){
		int top = 1;
		int bottom = 1;
		for (int i = n; i > (n-r); i--){
		//	System.out.println(i);
			top *= i;
		}
		for (int i = 1; i <= r; i++){
		//	System.out.println(i);
			bottom *= i;
		}
//		System.out.println(top);
//		System.out.println(bottom);
		return top/bottom;
	}
    } 
