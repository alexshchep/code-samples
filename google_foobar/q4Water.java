/*
When it rains it pours
======================
It's raining, it's pouring. You and your agents are nearing the building where the captive rabbits are being held, but a
sudden storm puts your escape plans at risk. The structural integrity of the rabbit hutches you've built to house the
fugitive rabbits is at risk because they can buckle when wet. Before the rabbits can be rescued from Professor Boolean's
lab, you must compute how much standing water has accumulated on the rabbit hutches.
Specifically, suppose there is a line of hutches, stacked to various heights and water is poured from the top (and
allowed to run off the sides). We'll assume all the hutches are square, have side length 1, and for the purposes of this
problem we'll pretend that the hutch arrangement is two-dimensional.
For example, suppose the heights of the stacked hutches are [1,4,2,5,1,2,3] (the hutches are shown below):
...X...
.X.X...
.X.X..X
.XXX.XX
XXXXXXX
1425123
When water is poured over the top at all places and allowed to runoff, it will remain trapped at the 'O' locations:
...X...
.XOX...
.XOXOOX
.XXXOXX
XXXXXXX
1425123
The amount of water that has accumulated is the number of Os, which, in this instance, is 5.
Write a function called answer(heights) which, given the heights of the stacked hutches from left-to-right as
a list,computes the total area of standing water accumulated when water is poured from the top and allowed to run off
the sides.
The heights array will have at least 1 element and at most 9000 elements. Each element will have a value of at least 1,
and at most 100000.

Test cases
==========
Inputs:
    (int list) heights = [1, 4, 2, 5, 1, 2, 3]
Output:
    (int) 5
Inputs:
    (int list) heights = [1, 2, 3, 2, 1]
Output:
    (int) 0
*/

 public class q4Water{
    public static void main(String[] args) {
      //  int ans = 9000*100000;
     //   System.out.println(ans);
        int [] heights = {1, 4, 2, 5, 1, 2, 3};
       // answer(heights);
        System.out.println(answer(heights));
    }
    static class Node{
        private int height;
        private int position;
        Node next = null;
        public Node(int height, int position){
            this.height = height;
            this.position = position;
            this.next = null;
        }
        public int getHeight(){
            return height;
        }
        public int getPosition(){
            return position;
        }
        public void addLast(int height, int position){
            Node end = new Node(height, position);
            Node n = this;
            while(n.next != null){
                n = n.next;
            }
            n.next = end;
        }
    }
    static class Stack{
        private Node top;

        public void push(int height, int position){
            Node t = new Node(height, position);
            t.next = top;
            top = t;
        }
        public Node pop(){
            Node temp = top;
            top = top.next;
            return temp;
        }
        public Node peek(){
            return top;
        }
    }
    /*
     * Creates a stack which holds the height and position of the walls.
     * Area gets filled when previous (stack) and 
     * next (array) walls are greater than the heights of the inside of the pool 
    */
    public static int answer(int[] heights){
        // when the walls start getting smaller, pool is created
        boolean pool = false;
        int area = 0;
        int temp = 0;
        int last = 0;
        // stack will contain heights of the walls to the left of the iterator
        Stack st = new Stack();
        st.push(heights[0], 0);
        int len = heights.length;
        int max = 0;
        max = heights[0];
        // array leftMax controls that we do not fill the area that would overflow the largest side to the left
        boolean[] leftMax = new boolean[len];
        leftMax[0] = true;
        for (int i = 1; i < len; i++){
            if(max < heights[i]){
                max = heights[i];
                leftMax[i] = true;
            }
  //          System.out.println("-------------");
  //          System.out.println(i);
  //          System.out.println("top of stack " + st.peek().getHeight());
   //         System.out.println("next wall " + heights[i]);
            if(st.peek().getHeight() > heights[i]){
                pool = true;
                st.push(heights[i], i);
            }
            else if(st.peek().getHeight() < heights[i]) {
                if (pool){
    //                System.out.println("pool");
                    while((st.peek() != null) && (st.peek().getHeight() < heights[i]) && (!leftMax[st.peek().getPosition()])) {
   //                     System.out.println("Entering while loop");
   //                     System.out.println("top of stack " + st.peek().getHeight());
                        Node fed = st.pop();
                        int min = 0;
                        if ((st.peek() != null) && (st.peek().getHeight() < heights[i])){
                            min = st.peek().getHeight();
                        }
                        else 
                            min = heights[i];
                        temp = (min - fed.getHeight()) * (i - fed.getPosition());
                        area += temp;
  //                      System.out.println("area in while " + area);
                        if(temp >0){
 //                           System.out.println("adding to stack");
                            // replace the bottom height with the height of the wall plus the water that was already filled
                            st.push(min, fed.getPosition());
                        }
                    }                    
                }
                // if not pool
                else{
     //               System.out.println("not pool");
                    if (last > heights[i]){
                        pool = true;
                    }
                    last = heights[i];     
                    st.pop(); 
                }
                st.push(heights[i], i);
            }
  //          System.out.println("area is " + area);
        } // end for loop
        return area;
    }
 // public static int answer(int[] heights) { 

 //        // Your code goes here
 //        int len = heights.length;
 //        int leftWallPos = 0;
 //        int last = 0;
 //        int fill = 0;
 //        int leftWall = heights[0];
 //        int area = 0;
 //        int temp = 0;

 //        boolean hole = false;

 //        for (int i = 1; i< len; i++){
 //            System.out.println("i is " + i + " area is " + area);
 //            System.out.println("last " + last);
 //            System.out.println("leftWall " + leftWall);
 //            System.out.println("heights " + heights[i]);
 //            System.out.println("leftWallPos " + leftWallPos);
 //            System.out.println("temp " + temp);

 //            if (hole){
 //                System.out.println("hole");
 //                if (leftWall > heights[i]){
 //                    if (last < heights[i]){
 //                        System.out.println("filling the hole... ");
 //                        // water is saved
 //                        fill = temp - (leftWall - heights[i]) * (i - 1 - leftWallPos);
 //                        System.out.println("fill is " + fill);
 //                        area += fill;
 //                        temp -= fill;

 //                    }
 //                    last = heights[i];
 //                    temp += leftWall - heights[i];

 //                }
 //                else if(leftWall <= heights[i]){
                    
 //                    area += temp;
 //                    temp = 0;
 //                    leftWall = heights[i];
 //                    leftWallPos = i;
 //                    hole = false;
 //                }

 //            }
 //            // if not hole
 //            else{
 //                System.out.println("no hole");
 //                if(leftWall <= heights[i])
 //                {
 //                   leftWall = heights[i] ;// change left wall
 //                   leftWallPos = i;
 //                }
 //                else {
 //                    hole = true;
 //                    temp = leftWall - heights[i];
 //                    last = heights[i];
 //                    System.out.println("changing the wall");
 //                }
                
 //                } // end left wall > heights else
 //                System.out.println("end of i is " + i + " area is " + area);
 //                System.out.println("-----------------");
 //            } // end for

 //            return area;
 //        } // end method answer }
    }
