import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Her simgenin yalnýzca kod uzunluðunu tanýmlayan Huffman kodu. Deðiþmezdir.
//kod uzunluðu sýfýr ise o sembol için herhangi bir kod yoktur.
//Her sembolün ikili kodlarý, uzunluk bilgilerinden yeniden oluþturulabilir.
//düþük ikili kodlar düþük kod uzunluðuna sahip olan sembollere atanýr
public final class StandartCode {

	private int[] codeLengths;

	public StandartCode(int[] codeLens) {//codeLens -> sembol kod uzunluðu dizisi
		// Check basic validity
		Objects.requireNonNull(codeLens);
		if (codeLens.length < 2)
			throw new IllegalArgumentException("En az 2 simge gerekli!");
		for (int cl : codeLens) {
			if (cl < 0)
				throw new IllegalArgumentException("Geçersiz kod uzunluðu!");
		}
		codeLengths = codeLens.clone();
		Arrays.sort(codeLengths);
		int currentLevel = codeLengths[codeLengths.length - 1];
		int numNodesAtLevel = 0;
		for (int i = codeLengths.length - 1; i >= 0 && codeLengths[i] > 0; i--) {
			int cl = codeLengths[i];
			while (cl < currentLevel) {
				if (numNodesAtLevel % 2 != 0)
					throw new IllegalArgumentException("Under-full Huffman kod aðacý!");
				numNodesAtLevel /= 2;
				currentLevel--;
			}
			numNodesAtLevel++;
		}
		while (currentLevel > 0) {
			if (numNodesAtLevel % 2 != 0)
				throw new IllegalArgumentException("Under-full Huffman kod aðacý!");
			numNodesAtLevel /= 2;
			currentLevel--;
		}
		if (numNodesAtLevel < 1)
			throw new IllegalArgumentException("Under-full Huffman kod aðacý!");
		if (numNodesAtLevel > 1)
			throw new IllegalArgumentException("Over-full Huffman kod aðacý!");
		
		
		System.arraycopy(codeLens, 0, codeLengths, 0, codeLens.length);
	}
	public StandartCode(CodeTree tree, int symbolLimit) {
		Objects.requireNonNull(tree);
		if (symbolLimit < 2)
			throw new IllegalArgumentException("En az 2 sembol gerekli!");
		codeLengths = new int[symbolLimit];//symbolLimit aðaçtaki maksimum simge deðerinden daha büyük bir sayý
		buildCodeLengths(tree.root, 0);
	}
	
	//yukarýdaki constructor için recursive yardýmcý bir fonksiyon
	private void buildCodeLengths(Node node, int depth) {
		if (node instanceof InternalNode) {
			InternalNode internalNode = (InternalNode)node;
			buildCodeLengths(internalNode.leftChild , depth + 1);
			buildCodeLengths(internalNode.rightChild, depth + 1);
		} else if (node instanceof Leaf) {
			int symbol = ((Leaf)node).symbol;
			if (codeLengths[symbol] != 0)
				throw new AssertionError("Sembolün birden fazla kodu vardýr!");
			if (symbol >= codeLengths.length)
				throw new IllegalArgumentException("Sembol, sembol limitini aþtý!");
			codeLengths[symbol] = depth;
		} else {
			throw new AssertionError("Geçersiz düðüm tipi!");
		}
	}
	
	
	public int getSymbolLimit() {
		return codeLengths.length;
	}
	
	
	public int getCodeLength(int symbol) {//Belirtilen sembol deðerinin kod uzunluðunu döndürür.
		if (symbol < 0 || symbol >= codeLengths.length)//Sembolün düðüm kodu varsa sonuç 0'dýr, aksi halde sonuç pozitif bir sayýdýr
			throw new IllegalArgumentException("Sembol aralýk dýþýnda!");
		return codeLengths[symbol];
	}
	
	
	public CodeTree toCodeTree() {
		List<Node> nodes = new ArrayList<Node>();
		for (int i = max(codeLengths); i >= 0; i--) {  // kod uzunluðuna göre descending
			if (nodes.size() % 2 != 0)
				throw new AssertionError("Standart kod deðiþmezlerini ihlal etme!");
			List<Node> newNodes = new ArrayList<Node>();
			
			// Pozitif kod uzunluðundaki semboller için yapraklar ekler
			if (i > 0) {
				for (int j = 0; j < codeLengths.length; j++) {
					if (codeLengths[j] == i)
						newNodes.add(new Leaf(j));
				}
			}
			
			//Daha önceki katmandan düðüm çiftlerini birleþtir
			for (int j = 0; j < nodes.size(); j += 2)
				newNodes.add(new InternalNode(nodes.get(j), nodes.get(j + 1)));
			nodes = newNodes;
		}
		
		if (nodes.size() != 1)
			throw new AssertionError("Standart kod deðiþmezlerini ihlal etme!");
		return new CodeTree((InternalNode)nodes.get(0), codeLengths.length);
	}
	
	
	private static int max(int[] array) { //Belirtilen dizideki en az öðeye sahip maksimum deðeri döndürür.
		int result = array[0];
		for (int x : array)
			result = Math.max(x, result);
		return result;
	}
	
	
}