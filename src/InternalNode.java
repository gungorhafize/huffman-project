import java.util.Objects;

public final class InternalNode extends Node{
    public final Node leftChild;  // Not null
	
	public final Node rightChild;  // Not null
	
	public InternalNode(Node left, Node right) { //A�a�taki bir i� d���m. �ocuk olarak 2 d���m vard�r.
		Objects.requireNonNull(left);
		Objects.requireNonNull(right);
		leftChild = left;
		rightChild = right;
	}

}
