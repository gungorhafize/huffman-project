import java.io.IOException;
import java.util.Objects;

//Bir Huffman kodlu bit ak���ndan okur ve sembolleri ��zer
public final class HuffmanDecoder {

	private BitInputStream input;
	//Kodlay�c� ve kod ��z�c�, kod ak���nda ayn� noktaya sahip oldu�u s�rece; A�a�, her simge ��z�ld�kten sonra de�i�tirilebilir
	public CodeTree codeTree;
	
	public HuffmanDecoder(BitInputStream in) { //Belirtilen bit girdi ak���na(input stream) dayanan bir Huffman dekoderi(kod ��z�c�)
		Objects.requireNonNull(in);
		input = in;
	}
	
	public int read() throws IOException {//Bir sonraki Huffman kodlu sembol� ��zmek i�in giri� ak���ndan okur
		if (codeTree == null)
			throw new NullPointerException("Kod a�ac� bo�!");
		
		InternalNode currentNode = codeTree.root;
		while (true) {
			int temp = input.readNoEof();
			Node nextNode;
			if      (temp == 0) nextNode = currentNode.leftChild;
			else if (temp == 1) nextNode = currentNode.rightChild;
			else throw new AssertionError("readNoEof()'den ge�ersiz de�er!");
			
			if (nextNode instanceof Leaf)
				return ((Leaf)nextNode).symbol;
			else if (nextNode instanceof InternalNode)
				currentNode = (InternalNode)nextNode;
			else
				throw new AssertionError("Ge�ersiz d���m tipi!");
		}
	}
}
