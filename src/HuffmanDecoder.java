import java.io.IOException;
import java.util.Objects;

//Bir Huffman kodlu bit akýþýndan okur ve sembolleri çözer
public final class HuffmanDecoder {

	private BitInputStream input;
	//Kodlayýcý ve kod çözücü, kod akýþýnda ayný noktaya sahip olduðu sürece; Aðaç, her simge çözüldükten sonra deðiþtirilebilir
	public CodeTree codeTree;
	
	public HuffmanDecoder(BitInputStream in) { //Belirtilen bit girdi akýþýna(input stream) dayanan bir Huffman dekoderi(kod çözücü)
		Objects.requireNonNull(in);
		input = in;
	}
	
	public int read() throws IOException {//Bir sonraki Huffman kodlu sembolü çözmek için giriþ akýþýndan okur
		if (codeTree == null)
			throw new NullPointerException("Kod aðacý boþ!");
		
		InternalNode currentNode = codeTree.root;
		while (true) {
			int temp = input.readNoEof();
			Node nextNode;
			if      (temp == 0) nextNode = currentNode.leftChild;
			else if (temp == 1) nextNode = currentNode.rightChild;
			else throw new AssertionError("readNoEof()'den geçersiz deðer!");
			
			if (nextNode instanceof Leaf)
				return ((Leaf)nextNode).symbol;
			else if (nextNode instanceof InternalNode)
				currentNode = (InternalNode)nextNode;
			else
				throw new AssertionError("Geçersiz düðüm tipi!");
		}
	}
}
