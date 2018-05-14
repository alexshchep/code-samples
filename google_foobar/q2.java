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