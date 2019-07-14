import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CodeTree {
	/*Bir binary tree semboller ve binary string aras�nda bir haritalama temsil eder.
	Veri yap�s� de�i�mez. K�k(root) alan�n� okur ve istenen bilgileri ��karmak i�in a�a�tan y�r�r.
	Belirli bir kodlanabilir sembol�n binary kodunu almak i�in getCode() �a��r�l�r.
	Yaprak(leaf) d���m�n�n yolu, yapra��n sembol kodunu belirler.
	K�kten ba�layarak, soldaki �ocu�a gitmek 0'� temsil eder.
	sa�daki �ocu�a gitmek 1'i temsil eder.
	K�k bir i� d���m ve a�a� sonlu olmal�d�r. Birden fazla yaprakta sembol de�er yoktur.
	olas� her sembol de�erin a�a�ta olma� gerekmez*/
	
	
	public final InternalNode root; //kod a�ac�n�n k�k d���m�
	private List<List<Integer>> codes; // her simgenin kodunu saklar yada sembol�n kodu yoksa null.
	//�rne�in 5 sembol� 10011 koduna sahipse --> codes.get(5) listesi [1, 0, 0, 1, 1]
	
	public CodeTree(InternalNode root, int symbolLimit) {//Bu Constructor belirtilen a�ac�n d���m�nden ve sembol limitinden bir kod a�ac� olu�turur.
		//a�ac�n her sembol�n�n sembol limitinden daha d���k bir de�ere sahip olmas� gerekir.
		Objects.requireNonNull(root);
		if (symbolLimit < 2)
			throw new IllegalArgumentException("En az 2 sembol gerekli!");
		
		this.root = root;
		codes = new ArrayList<List<Integer>>();  // Initially all null
		for (int i = 0; i < symbolLimit; i++)
			codes.add(null);
		buildCodeList(root, new ArrayList<Integer>());  // Fill 'codes' with appropriate data
	}
	//Constructor i�in recursive yard�mc� bir fonksiyon
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
				throw new IllegalArgumentException("Sembol, sembol limitini a��yor!");
			if (codes.get(leaf.symbol) != null)
				throw new IllegalArgumentException("Sembol�n birden fazla kodu var!");
			codes.set(leaf.symbol, new ArrayList<Integer>(prefix));
			
		} else {
			throw new AssertionError("Ge�ersiz d���m tipi!");
		}
	}
	
	//0 ve 1'lerden olu�an bir liste �eklinde belirtilen sembol�n Huffman kodunu d�nd�r�r.
	public List<Integer> getCode(int symbol) {
		if (symbol < 0)
			throw new IllegalArgumentException("Ge�ersiz sembol!");//sembol� negatifse veya onun i�in bir Huffman kodu mevcut de�ilse
		else if (codes.get(symbol) == null)
			throw new IllegalArgumentException("Verilen sembol i�in kod yoktur!");
		else
			return codes.get(symbol);
	}
	
	//Kod a�ac�n�n string bi�imini d�nd�r�r.
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString("", root, sb);
		return sb.toString();
	}
	
	//toString metodu i�in recursive yard�mc� fonksiyon
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
