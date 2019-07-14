import java.util.Objects;

public final class InternalNode extends Node{
    public final Node leftChild;  // Not null
	
	public final Node rightChild;  // Not null
	
	public InternalNode(Node left, Node right) { //Aðaçtaki bir iç düðüm. Çocuk olarak 2 düðüm vardýr.
		Objects.requireNonNull(left);
		Objects.requireNonNull(right);
		leftChild = left;
		rightChild = right;
	}

}
