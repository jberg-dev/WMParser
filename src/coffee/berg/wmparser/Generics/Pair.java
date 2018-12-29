package coffee.berg.wmparser.Generics;

/**
 * Generic pair class. Credit to Claes Morgen
 * https://stackoverflow.com/questions/6044923/generic-pair-class
 */

public class Pair<F, S> {
	private F first; //first member of pair
	private S second; //second member of pair

	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	public F getFirst() {
		return first;
	}

	public S getSecond() {
		return second;
	}

	@Override
	public String toString()
	{
		return "" + first + ", " + second;
	}
}