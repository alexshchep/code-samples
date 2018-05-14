
public class Answer{

	public static void main(String[] args) {
		System.out.println(answer(0));

	}
    public static int answer(long x) { 

        // Your code goes here.
        if (x < 10)
            return (int) x;
        String numString = Long.toString(x);
        char[] arrNumber = numString.toCharArray();
        int sum = 0;
        for (char c : arrNumber){
            sum += Character.getNumericValue(c);
        }
        return answer(sum);
    } 
}