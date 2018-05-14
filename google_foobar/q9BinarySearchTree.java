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

import java.util.ArrayList;
import java.util.Arrays;
public class q9BinarySearchTree{
	public static void main(String[] args) {
	//	int [] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
	//	int[] arr ={5, 9, 8, 2, 1};
		int[] arr ={5, 9, 1};
		String result = answer(arr);
	}
	static class Node{
		// instance variables
		Node left = null;
		Node right = null;
		int value;
		int depth;
		// constructor
		public Node(int value, int depth){
			this.value = value;
			left = null;
			right = null;
			this.depth = depth;
		}
	}
	static class BST {
		ArrayList<Integer> depthList = new ArrayList<Integer>();
		int[] depthArr = new int[51];
		Node root = null;
		public BST(int value){
			root = new Node(value, 1);
			depthList.add(1);
			// for(int i = 0; i<depthArr.length; i++){
			// 	System.out.println("doing");
			// 	depthArr[i] = 0;
			// }
			depthArr[value] = 1;
		}
		public BST(BST clon){
			System.out.println("cloned a tree");
			this.root = clon.root;
			this.depthArr = clon.depthArr;
			this.depthList = clon.depthList;
		}
		public void insert(Node root, int value){
			if(root == null){
				root = new Node(value, 1);
				return;
			}
			if (root.value < value){
				if(root.right == null){
					root.right = new Node(value, root.depth + 1);
					depthList.add(root.depth+1);
					depthArr[value] = root.depth+1;
		//			System.out.println("the depth of value " + value + " is " + (root.depth+1));
				}
				else{
					insert(root.right, value);
				}
			}
			else if(root.value > value){
				if(root.left == null){
					root.left = new Node(value, root.depth + 1);
					depthList.add(root.depth+1);
					depthArr[value] = root.depth+1;
			//		System.out.println("the depth of value is " + value + " is "+ (root.depth+1));
				}
				else {
					insert(root.left, value);
				}

			}
		}
		public void clear(){
			root.right = null;
			root.left = null;
			root = null;
		}
		public void deleteChildren(){
			root.right = null;
			root.left = null;
		}


	}
	public static String answer(int[] seq){

		int[] depth = new int[seq.length];
		int[] origDepth = new int[51];
		// create BST
		BST tree = new BST(seq[0]);
		depth[0] = 1;
		// loop through the sequence to create the tree and figure out the depth of each value
		for (int i = 1;i <seq.length ;i++ ) {
			tree.insert(tree.root, seq[i]);
			// BST.insert(tree.root, seq[i]);
			depth[i] = tree.depthList.get(i);
			origDepth[seq[i]] = tree.depthList.get(i);
			System.out.println("the depth of value " + seq[i] + " is " + tree.depthList.get(i));
		}
	//	System.out.println("before clear ");
	//	tree.clear();
	//	System.out.println("after clear ");
		//BST.insert(tree.root, seq[0]);
		// loop through the possible trees comparing the depth to the original
		//for(int i = 1; i<seq.length; i++){
		//	BST.insert(tree.root, seq[i]);
			
		//}
		int ans = fun(seq, new BST(seq[0]), origDepth, 0);
		System.out.println(" the answer is " + ans);
		return " ";
	}
	public static int fun(int[] numbers, BST realtree, int[] origDepth, int result){
			BST tree = new BST(realtree);
			System.out.println("numbers length is " + numbers.length);
			//for()
			for (int i = 0; i < numbers.length; i++){
				System.out.println("-------------------------------" );
				System.out.println("i is " + i);

				//get val
				int temp = numbers[i];
				System.out.println("value is " + temp);
				// check that value is not in tree already
				if(tree.depthArr[temp] != 0){
					System.out.println("the value is already in the tree ");
					System.out.println("the depth of the value is  " + tree.depthArr[temp]);
					if ((numbers.length - i) == 1) {
						System.out.println("everyting matches + 1");
						return 1;
					}
					continue;
				}
				System.out.println("the value is not in the tree ");
				System.out.println("tree.root is " + tree.root.value);
				System.out.println("temp is " + temp);
				// insert into the tree
				tree.insert(tree.root, temp);
				// BST.insert(tree.root, temp);
				System.out.println("tree.depthArr[temp] is  " + tree.depthArr[temp]);
				System.out.println("origDepth[temp] is  " + origDepth[temp]);
				// check that depth is same as original
				if(tree.depthArr[temp] != origDepth[temp]){
					System.out.println("depth is not the same");
					break;
				}
				if (numbers.length == 1) {
					System.out.println("everyting matches + 1");
					return 1;
				}
				System.out.println("calling fun ");
	
				result += fun(numbers, tree, origDepth, result);
				//return fun(Arrays.copyOfRange(numbers, 1, numbers.length), tree, origDepth);
			}
			return result;
		}
	// public static int compare(BST tree, int[] depth){
	// 	for (int i = 0;i<depth.length;i++ ) {
	// 		BST.insert(tree.root, seq[i]);
	// 		int temp = tree.depthList.get(depthList.size() - 1);
	// 		if(temp == depth[i]){

	// 		}
	// 	}
	// }
}
