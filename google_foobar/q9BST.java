/*
 * Binary bunnies
==============
As more and more rabbits were rescued from Professor Booleans horrid laboratory, you had to develop a system to track them, since some habitually continue to gnaw on the heads of their brethren and need extra supervision. For obvious reasons, you based your rabbit survivor tracking system on a binary search tree, but all of a sudden that decision has come back to haunt you.
To make your binary tree, the rabbits were sorted by their ages (in days) and each, luckily enough, had a distinct age. For a given group, the first rabbit became the root, and then the next one (taken in order of rescue) was added, older ages to the left and younger to the right. The order that the rabbits returned to you determined the end pattern of the tree, and herein lies the problem.
Some rabbits were rescued from multiple cages in a single rescue operation, and you need to make sure that all of the modifications or pathogens introduced by Professor Boolean are contained properly. Since the tree did not preserve the order of rescue, it falls to you to figure out how many different sequences of rabbits could have produced an identical tree to your sample sequence, so you can keep all the rescued rabbits safe.
For example, if the rabbits were processed in order from [5, 9, 8, 2, 1], it would result in a binary tree identical to one created from [5, 2, 9, 1, 8].
You must write a function answer(seq) that takes an array of up to 50 integers and returns a string representing the number (in base-10) of sequences that would result in the same tree as the given sequence.
Languages
=========
To provide a Python solution, edit solution.py
To provide a Java solution, edit solution.java
Test cases
==========
Inputs:
    (int list) seq = [5, 9, 8, 2, 1]
Output:
    (string) "6"
Inputs:
    (int list) seq = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
Output:
    (string) "1"
*/
import java.math.BigInteger;
import java.util.Map;
import java.util.HashMap;
public class q9BST{
	public static Map<Integer, BigInteger> factorialMap = new HashMap<Integer, BigInteger>();
		public static void main(String[] args) {
		int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		String result = answer(arr);
		System.out.println(result);
	}
		static class Node{
		// instance variables
		Node left = null;
		Node right = null;
		int value;
		// constructor
		public Node(int value){
			this.value = value;
			left = null;
			right = null;
		}
		}
		static class BST {
		Node root = null;
		public BST(int value){
			root = new Node(value);
		}
		public void insert(Node root, int value){
			if(root == null){
				root = new Node(value);
				return;
			}
			if (root.value < value){
				if(root.right == null){
					root.right = new Node(value);
				}
				else{
					insert(root.right, value);
				}
			}
			else if(root.value > value){
				if(root.left == null){
					root.left = new Node(value);
				}
				else {
					insert(root.left, value);
				}

			}
		}
	}

	public static String answer(int[] seq){

		BST tree = new BST(seq[0]);
		// loop through the sequence to create the tree 
		for (int i = 1;i <seq.length ;i++ ) {
			tree.insert(tree.root, seq[i]);
		}
		BigInteger result = fun(tree.root);
		return String.valueOf(result);
	}
	public static BigInteger fun(Node root){
		if (root.right == null && root.left == null )
			return BigInteger.ONE;
		else if (root.right == null && root.left !=null){
			int totalchildren = size(root)-1;
			int rightchildren = sizeRight(root)-1;
			return  fun(root.left).multiply(comb(totalchildren, rightchildren));
		}
		else if (root.left == null && root.right !=null){
			int totalchildren = size(root)-1;
			int rightchildren = sizeRight(root)-1;
			return  fun(root.right).multiply(comb(totalchildren, rightchildren));
		}
		else {
			int totalchildren = size(root)-1;
			int rightchildren = sizeRight(root)-1;
			return fun(root.right).multiply(fun(root.left)).multiply(comb(totalchildren, rightchildren));
		}
	}
	// public static BigInteger fact(int n){
	// 	if(!factorialMap.containsKey(n)){
	// 		BigInteger product = BigInteger.ONE;
	// 		for (int i = 1; i<=n;i++){
	// 			product = product.multiply(new BigInteger(String.valueOf(i)));
	// 		}
	// 		factorialMap.put(n, product);
	// 	}	
	// 	return factorialMap.get(n);
	// }
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
	//  size of the tree
		public static int size(Node node) { 
		  if (node == null) return(0); 
		  else { 
		    return(size(node.left) + 1 + size(node.right)); 
		  }  
		} 
		//  size of the tree minus the left children
		public static int sizeRight(Node node) { 
		  if (node == null) return(0); 
		  else { 
		    return(1 + size(node.right)); 
		  } 
		}

}
