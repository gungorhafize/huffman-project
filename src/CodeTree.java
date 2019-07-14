import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CodeTree {
	/*Bir binary tree semboller ve binary string arasýnda bir haritalama temsil eder.
	Veri yapýsý deðiþmez. Kök(root) alanýný okur ve istenen bilgileri çýkarmak için aðaçtan yürür.
	Belirli bir kodlanabilir sembolün binary kodunu almak için getCode() çaðýrýlýr.
	Yaprak(leaf) düðümünün yolu, yapraðýn sembol kodunu belirler.
	Kökten baþlayarak, soldaki çocuða gitmek 0'ý temsil eder.
	saðdaki çocuða gitmek 1'i temsil eder.
	Kök bir iç düðüm ve aðaç sonlu olmalýdýr. Birden fazla yaprakta sembol deðer yoktur.
	olasý her sembol deðerin aðaçta olmaý gerekmez*/
	
	
	public final InternalNode root; //kod aðacýnýn kök düðümü
	private List<List<Integer>> codes; // her simgenin kodunu saklar yada sembolün kodu yoksa null.
	//örneðin 5 sembolü 10011 koduna sahipse --> codes.get(5) listesi [1, 0, 0, 1, 1]
	
	public CodeTree(InternalNode root, int symbolLimit) {//Bu Constructor belirtilen aðacýn düðümünden ve sembol limitinden bir kod aðacý oluþturur.
		//aðacýn her sembolünün sembol limitinden daha düþük bir deðere sahip olmasý gerekir.
		Objects.requireNonNull(root);
		if (symbolLimit < 2)
			throw new IllegalArgumentException("En az 2 sembol gerekli!");
		
		this.root = root;
		codes = new ArrayList<List<Integer>>();  // Initially all null
		for (int i = 0; i < symbolLimit; i++)
			codes.add(null);
		buildCodeList(root, new ArrayList<Integer>());  // Fill 'codes' with appropriate data
	}
	//Constructor için recursive yardýmcý bir fonksiyon
	private void buildCodeList(Node node, List<Integer> prefix) {
		if (node instanceof InternalNode) {
			InternalNode internalNode = (InternalNode)node;
			
			prefix.add(0);
			buildCodeList(internalNode.leftChild , prefix);
			prefix.remove(prefix.size() - 1);
			
			prefix.add(1);
			buildCodeList(internalNode.rightChild, prefix);
			prefix.remove(prefix.size() - 1);
			
		} else if (node instanceof Leaf) {
			Leaf leaf = (Leaf)node;
			if (leaf.symbol >= codes.size())
				throw new IllegalArgumentException("Sembol, sembol limitini aþýyor!");
			if (codes.get(leaf.symbol) != null)
				throw new IllegalArgumentException("Sembolün birden fazla kodu var!");
			codes.set(leaf.symbol, new ArrayList<Integer>(prefix));
			
		} else {
			throw new AssertionError("Geçersiz düðüm tipi!");
		}
	}
	
	//0 ve 1'lerden oluþan bir liste þeklinde belirtilen sembolün Huffman kodunu döndürür.
	public List<Integer> getCode(int symbol) {
		if (symbol < 0)
			throw new IllegalArgumentException("Geçersiz sembol!");//sembolü negatifse veya onun için bir Huffman kodu mevcut deðilse
		else if (codes.get(symbol) == null)
			throw new IllegalArgumentException("Verilen sembol için kod yoktur!");
		else
			return codes.get(symbol);
	}
	
	//Kod aðacýnýn string biçimini döndürür.
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString("", root, sb);
		return sb.toString();
	}
	
	//toString metodu için recursive yardýmcý fonksiyon
	private static void toString(String prefix, Node node, StringBuilder sb) {
		if (node instanceof InternalNode) {
			InternalNode internalNode = (InternalNode)node;
			toString(prefix + "0", internalNode.leftChild , sb);
			toString(prefix + "1", internalNode.rightChild, sb);
		} else if (node instanceof Leaf) {
			sb.append(String.format("Code %s: Symbol %d%n", prefix, ((Leaf)node).symbol));
		} else {
			throw new AssertionError("Illegal node type");
		}
	}
}
