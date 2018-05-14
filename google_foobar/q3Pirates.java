/*
A Pirate Walks Into A Bar
You ask pirate 0 about a man, but he tells you to ask another pirate (a pirate between 0 and 5000), who then tells you to ask another pirate. Soon you realize that you are going in a loop of asking the same pirates. Write a program to figure out how many pirates are involved once you land in a loop. The loop does not necessarily involve the first pirate you ask.
*/

public class q3Pirates{

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4, 5, 2};

 //       int[] nums = {1,0};
        System.out.println("The answer is " + answer(nums));
    }

 public static int answer(int[] numbers) { 
        int result = 0;
        int position = 0;
        int prev = 0;
        int pirateAtZero;
        int[] beenThere = new int[numbers.length];
        beenThere[0] = -1;
        pirateAtZero = numbers[0];
        boolean visitedZero = false;
        // Your code goes here.
        for (int i = 0; i<=numbers.length ; i++)
        {
            position = numbers[prev];
            // check if was there
            if ((beenThere[position] == 0) && (position != pirateAtZero || !visitedZero)){
                System.out.println("beenThere[position] " + beenThere[position]);
                System.out.println("position " + position);
                System.out.println("visitedZero " + visitedZero);
                System.out.println("i " + i);
                beenThere[position] = i;
                visitedZero = true;
            }
            // was there
            else {
                            System.out.println("beenThere[position] " + beenThere[position]);
                System.out.println("else position " + position);
                System.out.println("else visitedZero " + visitedZero);
                System.out.println("else i " + i);
                result = i - beenThere[position];
                break;
            }
            prev = position;
        }
        return result;
    } 
}
