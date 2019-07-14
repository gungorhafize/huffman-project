import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Her simgenin yaln�zca kod uzunlu�unu tan�mlayan Huffman kodu. De�i�mezdir.
//kod uzunlu�u s�f�r ise o sembol i�in herhangi bir kod yoktur.
//Her sembol�n ikili kodlar�, uzunluk bilgilerinden yeniden olu�turulabilir.
//d���k ikili kodlar d���k kod uzunlu�una sahip olan sembollere atan�r
public final class StandartCode {

	private int[] codeLengths;

	public StandartCode(int[] codeLens) {//codeLens -> sembol kod uzunlu�u dizisi
		// Check basic validity
		Objects.requireNonNull(codeLens);
		if (codeLens.length < 2)
			throw new IllegalArgumentException("En az 2 simge gerekli!");
		for (int cl : codeLens) {
			if (cl < 0)
				throw new IllegalArgumentException("Ge�ersiz kod uzunlu�u!");
		}
		codeLengths = codeLens.clone();
		Arrays.sort(codeLengths);
		int currentLevel = codeLengths[codeLengths.length - 1];
		int numNodesAtLevel = 0;
		for (int i = codeLengths.length - 1; i >= 0 && codeLengths[i] > 0; i--) {
			int cl = codeLengths[i];
			while (cl < currentLevel) {
				if (numNodesAtLevel % 2 != 0)
					throw new IllegalArgumentException("Under-full Huffman kod a�ac�!");
				numNodesAtLevel /= 2;
				currentLevel--;
			}
			numNodesAtLevel++;
		}
		while (currentLevel > 0) {
			if (numNodesAtLevel % 2 != 0)
				throw new IllegalArgumentException("Under-full Huffman kod a�ac�!");
			numNodesAtLevel /= 2;
			currentLevel--;
		}
		if (numNodesAtLevel < 1)
			throw new IllegalArgumentException("Under-full Huffman kod a�ac�!");
		if (numNodesAtLevel > 1)
			throw new IllegalArgumentException("Over-full Huffman kod a�ac�!");
		
		
		System.arraycopy(codeLens, 0, codeLengths, 0, codeLens.length);
	}
	public StandartCode(CodeTree tree, int symbolLimit) {
		Objects.requireNonNull(tree);
		if (symbolLimit < 2)
			throw new IllegalArgumentException("En az 2 sembol gerekli!");
		codeLengths = new int[symbolLimit];//symbolLimit a�a�taki maksimum simge de�erinden daha b�y�k bir say�
		buildCodeLengths(tree.root, 0);
	}
	
	//yukar�daki constructor i�in recursive yard�mc� bir fonksiyon
	private void buildCodeLengths(Node node, int depth) {
		if (node instanceof InternalNode) {
			InternalNode internalNode = (InternalNode)node;
			buildCodeLengths(internalNode.leftChild , depth + 1);
			buildCodeLengths(internalNode.rightChild, depth + 1);
		} else if (node instanceof Leaf) {
			int symbol = ((Leaf)node).symbol;
			if (codeLengths[symbol] != 0)
				throw new AssertionError("Sembol�n birden fazla kodu vard�r!");
			if (symbol >= codeLengths.length)
				throw new IllegalArgumentException("Sembol, sembol limitini a�t�!");
			codeLengths[symbol] = depth;
		} else {
			throw new AssertionError("Ge�ersiz d���m tipi!");
		}
	}
	
	
	public int getSymbolLimit() {
		return codeLengths.length;
	}
	
	
	public int getCodeLength(int symbol) {//Belirtilen sembol de�erinin kod uzunlu�unu d�nd�r�r.
		if (symbol < 0 || symbol >= codeLengths.length)//Sembol�n d���m kodu varsa sonu� 0'd�r, aksi halde sonu� pozitif bir say�d�r
			throw new IllegalArgumentException("Sembol aral�k d���nda!");
		return codeLengths[symbol];
	}
	
	
	public CodeTree toCodeTree() {
		List<Node> nodes = new ArrayList<Node>();
		for (int i = max(codeLengths); i >= 0; i--) {  // kod uzunlu�una g�re descending
			if (nodes.size() % 2 != 0)
				throw new AssertionError("Standart kod de�i�mezlerini ihlal etme!");
			List<Node> newNodes = new ArrayList<Node>();
			
			// Pozitif kod uzunlu�undaki semboller i�in yapraklar ekler
			if (i > 0) {
				for (int j = 0; j < codeLengths.length; j++) {
					if (codeLengths[j] == i)
						newNodes.add(new Leaf(j));
				}
			}
			
			//Daha �nceki katmandan d���m �iftlerini birle�tir
			for (int j = 0; j < nodes.size(); j += 2)
				newNodes.add(new InternalNode(nodes.get(j), nodes.get(j + 1)));
			nodes = newNodes;
		}
		
		if (nodes.size() != 1)
			throw new AssertionError("Standart kod de�i�mezlerini ihlal etme!");
		return new CodeTree((InternalNode)nodes.get(0), codeLengths.length);
	}
	
	
	private static int max(int[] array) { //Belirtilen dizideki en az ��eye sahip maksimum de�eri d�nd�r�r.
		int result = array[0];
		for (int x : array)
			result = Math.max(x, result);
		return result;
	}
	
	
}