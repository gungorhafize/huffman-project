import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

//Sembol frekanslarý tablosu(sýklýk tablosu)
//Akýþta(STREAM) sýkýþtýrmak istediðimiz sembollerin frekanslarýný toplar.
//Geçerli frekanslar için statik olarak optimal olan bir kod aðacý oluþturur
public final class FrequencyTable {
	public static void main(String args[]) {
	JFrame jfr = new JFrame("***Huffman Coding - Dynamic and Static***");
	jfr.setSize(600, 350);
	jfr.getContentPane().setLayout(new FlowLayout());
	JLabel label = new JLabel("Dosya Ýsmi:");
	jfr.getContentPane().add(label);
	final JLabel label1 = new JLabel("                                       ");
	jfr.getContentPane().add(label1);
	JLabel label2 = new JLabel("Dosya Boyutu:");
	jfr.getContentPane().add(label2);
	final JLabel label3 = new JLabel("                                       ");
	jfr.getContentPane().add(label3);
    final JButton buton = new JButton("Dosya Yükle");
    jfr.getContentPane().add(buton);
    final JButton buton2 = new JButton("Sýkýþtýr");
    jfr.getContentPane().add(buton2);
	final JFileChooser fc = new JFileChooser();
	fc.addChoosableFileFilter(new FileNameExtensionFilter("*.txt", "txt"));	fc.setCurrentDirectory(new java.io.File("C:/Users/Hafize/Desktop"));
	fc.setDialogTitle("Dosya Seç");
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
	private int[] frequencies;//Uzunluðu en az 2'dir ve her eleman negatif deðildir
	
	
	
	public FrequencyTable(int[] freqs) { //Belirtilen frekans dizisinden bir frekans tablosu oluþturur.
		Objects.requireNonNull(freqs);
		if (freqs.length < 2)
			throw new IllegalArgumentException("En az 2 sembol gerekli!");
		frequencies = freqs.clone();  
		for (int x : frequencies) {
			if (x < 0)
				throw new IllegalArgumentException("Negatif Frekans!");
		}
	}
	
	public int getSymbolLimit() { //Bu frekans tablosundaki sembol sayýsýný döndürür
		return frequencies.length;
	}
	
	
	public int get(int symbol) {//Bu frekans tablosunda belirtilen sembolün frekansýný döndürür
		checkSymbol(symbol);
		return frequencies[symbol];
	}
	
	public void set(int symbol, int freq) {//Bu frekans tablosundaki belirtilen sembolün frekansýný belirtilen deðere ayarlar
		checkSymbol(symbol);
		if (freq < 0)
			throw new IllegalArgumentException("Negatif Frekans!");
		frequencies[symbol] = freq;
	}
	
	
	public void increment(int symbol) {//Bu frekans tablosunda belirtilen sembolün frekansýný artýrýr
		checkSymbol(symbol);
		if (frequencies[symbol] == Integer.MAX_VALUE)
			throw new IllegalStateException("Maksimum frekansa ulaþýldý!");
		frequencies[symbol]++;
	}
	
	private void checkSymbol(int symbol) {
		if (symbol < 0 || symbol >= frequencies.length)
			throw new IllegalArgumentException("Sembol aralýk dýþýnda!");
	}
	
	
	public String toString() { //Bu frekans tablosunun bir string gösterimini döndürür,
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < frequencies.length; i++)
			sb.append(String.format("%d\t%d%n", i, frequencies[i]));
		return sb.toString();
	}
	public CodeTree buildCodeTree() { //Bu tablodaki sembol frekanslarý için en uygun kod aðacýný döndürür
	//Aðaç daima en az 2 yaprak içerir (0 frekanslý sembollerden gelse bile)
	//iki düðümün frekansý ayný ise, baðlantý hangi aðacýn en alçak sembolü içerdiði ile bozulur.
		Queue<NodeWithFrequency> pqueue = new PriorityQueue<NodeWithFrequency>();
		for (int i = 0; i < frequencies.length; i++) {//Sýfýr frekanslý simgeler için yapraklar ekle
			if (frequencies[i] > 0)
				pqueue.add(new NodeWithFrequency(new Leaf(i), i, frequencies[i]));
		}
		for (int i = 0; i < frequencies.length && pqueue.size() < 2; i++) {
			if (frequencies[i] == 0)
				pqueue.add(new NodeWithFrequency(new Leaf(i), i, 0));
		}
		if (pqueue.size() < 2)
			throw new AssertionError();
		while (pqueue.size() > 1) { //En düþük frekansa sahip iki düðüm birbirini izleyerek birbirine baðlar
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
	    //BuildCodeTree() için yardýmcý structure
        private static class NodeWithFrequency implements Comparable<NodeWithFrequency> {
		
		public final Node node;
		public final int lowestSymbol;
		public final long frequency;  // Uzun kullandýk, taþmayý önler
		
		
		public NodeWithFrequency(Node nd, int lowSym, long freq) {
			node = nd;
			lowestSymbol = lowSym;
			frequency = freq;
		}
		
		//Artan frekansa göre sýralama, artan sembol deðerine göre baðlarý koparma
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
