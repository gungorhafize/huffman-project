import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

//Sembol frekanslar� tablosu(s�kl�k tablosu)
//Ak��ta(STREAM) s�k��t�rmak istedi�imiz sembollerin frekanslar�n� toplar.
//Ge�erli frekanslar i�in statik olarak optimal olan bir kod a�ac� olu�turur
public final class FrequencyTable {
	public static void main(String args[]) {
	JFrame jfr = new JFrame("***Huffman Coding - Dynamic and Static***");
	jfr.setSize(600, 350);
	jfr.getContentPane().setLayout(new FlowLayout());
	JLabel label = new JLabel("Dosya �smi:");
	jfr.getContentPane().add(label);
	final JLabel label1 = new JLabel("                                       ");
	jfr.getContentPane().add(label1);
	JLabel label2 = new JLabel("Dosya Boyutu:");
	jfr.getContentPane().add(label2);
	final JLabel label3 = new JLabel("                                       ");
	jfr.getContentPane().add(label3);
    final JButton buton = new JButton("Dosya Y�kle");
    jfr.getContentPane().add(buton);
    final JButton buton2 = new JButton("S�k��t�r");
    jfr.getContentPane().add(buton2);
	final JFileChooser fc = new JFileChooser();
	fc.addChoosableFileFilter(new FileNameExtensionFilter("*.txt", "txt"));	fc.setCurrentDirectory(new java.io.File("C:/Users/Hafize/Desktop"));
	fc.setDialogTitle("Dosya Se�");
	fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	
	
	buton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){ //ActionListener,
			if(fc.showOpenDialog(buton)== JFileChooser.APPROVE_OPTION){
				System.out.println(fc.getSelectedFile().getName());
				label1.setText(fc.getSelectedFile().getName());
				String strbyte = Long.toString(fc.getSelectedFile().length());
				label3.setText(strbyte + "Byte");
				
			}
			
		}
	});
	jfr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	jfr.setVisible(true);
	
	}
	private int[] frequencies;//Uzunlu�u en az 2'dir ve her eleman negatif de�ildir
	
	
	
	public FrequencyTable(int[] freqs) { //Belirtilen frekans dizisinden bir frekans tablosu olu�turur.
		Objects.requireNonNull(freqs);
		if (freqs.length < 2)
			throw new IllegalArgumentException("En az 2 sembol gerekli!");
		frequencies = freqs.clone();  
		for (int x : frequencies) {
			if (x < 0)
				throw new IllegalArgumentException("Negatif Frekans!");
		}
	}
	
	public int getSymbolLimit() { //Bu frekans tablosundaki sembol say�s�n� d�nd�r�r
		return frequencies.length;
	}
	
	
	public int get(int symbol) {//Bu frekans tablosunda belirtilen sembol�n frekans�n� d�nd�r�r
		checkSymbol(symbol);
		return frequencies[symbol];
	}
	
	public void set(int symbol, int freq) {//Bu frekans tablosundaki belirtilen sembol�n frekans�n� belirtilen de�ere ayarlar
		checkSymbol(symbol);
		if (freq < 0)
			throw new IllegalArgumentException("Negatif Frekans!");
		frequencies[symbol] = freq;
	}
	
	
	public void increment(int symbol) {//Bu frekans tablosunda belirtilen sembol�n frekans�n� art�r�r
		checkSymbol(symbol);
		if (frequencies[symbol] == Integer.MAX_VALUE)
			throw new IllegalStateException("Maksimum frekansa ula��ld�!");
		frequencies[symbol]++;
	}
	
	private void checkSymbol(int symbol) {
		if (symbol < 0 || symbol >= frequencies.length)
			throw new IllegalArgumentException("Sembol aral�k d���nda!");
	}
	
	
	public String toString() { //Bu frekans tablosunun bir string g�sterimini d�nd�r�r,
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < frequencies.length; i++)
			sb.append(String.format("%d\t%d%n", i, frequencies[i]));
		return sb.toString();
	}
	public CodeTree buildCodeTree() { //Bu tablodaki sembol frekanslar� i�in en uygun kod a�ac�n� d�nd�r�r
	//A�a� daima en az 2 yaprak i�erir (0 frekansl� sembollerden gelse bile)
	//iki d���m�n frekans� ayn� ise, ba�lant� hangi a�ac�n en al�ak sembol� i�erdi�i ile bozulur.
		Queue<NodeWithFrequency> pqueue = new PriorityQueue<NodeWithFrequency>();
		for (int i = 0; i < frequencies.length; i++) {//S�f�r frekansl� simgeler i�in yapraklar ekle
			if (frequencies[i] > 0)
				pqueue.add(new NodeWithFrequency(new Leaf(i), i, frequencies[i]));
		}
		for (int i = 0; i < frequencies.length && pqueue.size() < 2; i++) {
			if (frequencies[i] == 0)
				pqueue.add(new NodeWithFrequency(new Leaf(i), i, 0));
		}
		if (pqueue.size() < 2)
			throw new AssertionError();
		while (pqueue.size() > 1) { //En d���k frekansa sahip iki d���m birbirini izleyerek birbirine ba�lar
			NodeWithFrequency x = pqueue.remove();
			NodeWithFrequency y = pqueue.remove();
			pqueue.add(new NodeWithFrequency(
				new InternalNode(x.node, y.node),
				Math.min(x.lowestSymbol, y.lowestSymbol),
				x.frequency + y.frequency));
		}
		//Geri kalan nodu geri getirir
		return new CodeTree((InternalNode)pqueue.remove().node, frequencies.length);
	}
	    //BuildCodeTree() i�in yard�mc� structure
        private static class NodeWithFrequency implements Comparable<NodeWithFrequency> {
		
		public final Node node;
		public final int lowestSymbol;
		public final long frequency;  // Uzun kulland�k, ta�may� �nler
		
		
		public NodeWithFrequency(Node nd, int lowSym, long freq) {
			node = nd;
			lowestSymbol = lowSym;
			frequency = freq;
		}
		
		//Artan frekansa g�re s�ralama, artan sembol de�erine g�re ba�lar� koparma
		public int compareTo(NodeWithFrequency other) {
			if (frequency < other.frequency)
				return -1;
			else if (frequency > other.frequency)
				return 1;
			else if (lowestSymbol < other.lowestSymbol)
				return -1;
			else if (lowestSymbol > other.lowestSymbol)
				return 1;
			else
				return 0;
		}
	

      }
  }
