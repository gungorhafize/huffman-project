
public final class Leaf extends Node{//A�a�taki yaprak d���m. Sembol bir de�eri var.
	public final int symbol;  
	public Leaf(int sym) {
		if (sym < 0)
			throw new IllegalArgumentException("Sembol de�eri negatif olmamal�d�r!");
		symbol = sym;
	}

}
