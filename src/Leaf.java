
public final class Leaf extends Node{//Aðaçtaki yaprak düðüm. Sembol bir deðeri var.
	public final int symbol;  
	public Leaf(int sym) {
		if (sym < 0)
			throw new IllegalArgumentException("Sembol deðeri negatif olmamalýdýr!");
		symbol = sym;
	}

}
