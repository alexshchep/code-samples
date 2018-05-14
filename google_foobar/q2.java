/*
Peculiar balance
Can we save them? Beta Rabbit is trying to break into a lab that contains the only known zombie cure - but there's an obstacle. The door will only open if a challenge is solved correctly. The future of the zombified rabbit population is at stake, so Beta reads the challenge: There is a scale with an object on the left-hand side, whose mass is given in some number of units. Predictably, the task is to balance the two sides. But there is a catch: You only have this peculiar weight set, having masses 1, 3, 9, 27, ... units. That is, one for each power of 3. Being a brilliant mathematician, Beta Rabbit quickly discovers that any number of units of mass can be balanced exactly using this set.

To help Beta get into the room, write a method called answer(x), which outputs a list of strings representing where the weights should be placed, in order for the two sides to be balanced, assuming that weight on the left has mass x units.

The first element of the output list should correspond to the 1-unit weight, the second element to the 3-unit weight, and so on. Each string is one of:

"L" : put weight on left-hand side
"R" : put weight on right-hand side
"-" : do not use weight
To ensure that the output is the smallest possible, the last element of the list must not be "-".

x will always be a positive integer, no larger than 1000000000.

Test cases
Inputs: (int) x = 2
Output: (string list) ["L", "R"]

Inputs: (int) x = 8
Output: (string list) ["L", "-", "R"]

*/ 
import java.util.ArrayList;
public class q2{

    public static void main(String[] args) {
        String[] str= answer(8);
        for (String s: str)
            System.out.print(s);
        int sum = 0;
        for (int i = 0; i < 18; i++ )
            sum += Math.pow(3,i);
        System.out.println("the sum is " + sum);
    }

    public static String[] answer(int x) { 
                boolean flag = false; 
        int place = 0;
        int countNotNull = 0;
        int powerLimit = 20;
        String[] list = new String[powerLimit];
        // array that holds the value before extra symbol needs to be used (cumulative sums of powers of 3)
        int[] arr = new int[powerLimit];
        arr[0] = 1;
        for (int i = 1; i<powerLimit; i++){
            arr[i] = (int) Math.pow(3, i)+arr[i-1];
        }
        for (int i = powerLimit; i>1; i--){
            // check if list was started
            if (flag){
                // place the weight on the right
                if (x > arr[i-2])
                    {
                        list[place++] = "R";
                        x -= Math.pow(3,i-1);
                    }
                // skip the weight
                else if(x >= -arr[i-2])
                    {
                        list[place++] = "-";
                    }
                // place the weight on the left 
                else
                    {
                        list[place++] = "L";
                        x += Math.pow(3,i-1);
                    }
            }
            // list is still empty
            else {
                // biggest weight put will always go on the right scale
                if (x > arr[i-2]){
                    list[place++] = "R";
                    flag = true;
                    x -= Math.pow(3,i-1);
                    countNotNull = i;
                }
            }                
        }
        // base cases
            if (x == 1)
               list[place] = "R";
            if (x == 0)
                list[place] = "-";
            if (x == -1)
                list[place] = "L";
            // int count = 0;
            // for (String s : list){
            //     if (s != null)
            //         count++;
            // }
            String[] finalList = new String[countNotNull];
            for (int i =0; i< countNotNull; i++){
                finalList[i] = list[countNotNull-i-1];
            }
        return finalList;
    } 
        // Your code goes here.
    //     List<String> list = new ArrayList<String>();
    //     // flag for when list begins to fill
    //     boolean flag = false; 
    //     int powerLimit = 20;
    //     // array that holds the value before extra symbol needs to be used (cumulative sums of powers of 3)
    //     int[] arr = new int[powerLimit];
    //     arr[0] = 1;
    //     for (int i = 1; i<powerLimit; i++){
    //         arr[i] = (int) Math.pow(3, i)+arr[i-1];
    //     }
    //     for (int i = powerLimit; i>1; i--){
    //         System.out.println(i + " " + x);
    //         // check if list was started
    //         if (flag){
    //             // place the weight on the right
    //             if (x > arr[i-2])
    //                 {
    //                     list.add(0,"R");
    //                     x -= Math.pow(3,i-1);
    //                 }
    //             // skip the weight
    //             else if(x >= -arr[i-2])
    //                 {
    //                     list.add(0,"-");
    //                 }
    //             // place the weight on the left 
    //             else
    //                 {
    //                     list.add(0,"L");
    //                     x += Math.pow(3,i-1);
    //                 }
    //         }
    //         // list is still empty
    //         else {
    //             // biggest weight put will always go on the right scale
    //             if (x > arr[i-2]){
    //                 list.add(0,"R");
    //                 flag = true;
    //                 x -= Math.pow(3,i-1);
    //             }
    //         }                
    //     }
    //     // base cases
    //         if (x == 1)
    //             list.add(0,"R");
    //         if (x == 0)
    //             list.add(0,"-");
    //         if (x == -1)
    //             list.add(0,"L");
    //     return list;

    // }
        } 
