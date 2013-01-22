package prograham.battleship;

/**
* 	Copyright Graham Blanshard
* 
* 	General String/Int coordiate helper functions
* 
*	https://github.com/GrahamBlanshard
*/
public class Helper {

	public static final String[] Alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	
	/**
	 * Converts an integer value to its alphabet value
	 * @param x The integer to convert to a alphabet value. i.e. 0 = A, 1 = B
	 * @return The letter
	 */
	public static String toAlpha(int n)
	{
		return Alphabet[n];
	}
	
	/**
	 * Finds the number representing the letter in the alphabet
	 * @param let letter to search for
	 * @return the index of the Alphabet array or -1 if not found
	 */
	public static int toInt(String s, int n)
	{
		for (int i = 0; i <Alphabet.length && i < n; i++) {
			if (s.equals(Alphabet[i]))
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Finds the number representing the letter in the alphabet
	 * @param let letter to search for
	 * @return the index of the Alphabet array or -1 if not found
	 */
	public static int toInt(String s)
	{
		for (int i = 0; i < Alphabet.length; i++) {
			if (s.equals(Alphabet[i]))
				return i;
		}
		
		return -1;
	}
	
}
