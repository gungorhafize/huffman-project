import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

//uygulama, 257 sembolden olu�an d�z bir frekans tablosundan ba�lar (hepsi 1 frekansa ayarlan�r).
//baytlar kodlan�rken istatistikler toplar ve periyodik olarak Huffman kodunu yeniden �retir.
//baytlar� ��z�l�rken onu g�nceller ve Huffman kodunu zamanla ayn� noktalarda yeniden olu�turur.
//kompres�r�n ve a��c� kompres�r�n senkronize edilmi� durumlar� vard�r, b�ylece veriler d�zg�n bir �ekilde a��labilir

public final class DynamicHuffmanCompress {

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: java AdaptiveHuffmanCompress InputFile OutputFile");
			System.exit(1);
			return;
		}
		
		File inputFile  = new File("C:/Desktop/sample3.txt");
		File outputFile = new File("C:/Desktop/sample4.txt");
		//Dosya s�k��t�rmas�n� ger�ekle�tir.
		try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile))) {
			try (BitOutputStream out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)))) {
				compress(in, out);
			}
		}
	}
	
	static void compress(InputStream in, BitOutputStream out) throws IOException {
		int[] initFreqs = new int[257];
		Arrays.fill(initFreqs, 1);
		
		FrequencyTable freqs = new FrequencyTable(initFreqs);
		HuffmanEncoder enc = new HuffmanEncoder(out);
		enc.codeTree = freqs.buildCodeTree();
		int count = 0;//Girdi dosyas�ndan okunan bayt say�s�
		while (true) {
			// Bir bayt okumak ve kodlamak
			int symbol = in.read();
			if (symbol == -1)
				break;
			enc.write(symbol);
			count++;
			freqs.increment(symbol);//Frekans tablosunu ve kod a�ac�n� g�nceller
			if (count < 262144 && isPowerOf2(count) || count % 262144 == 0)  // kood a�ac�n� g�nceller
				enc.codeTree = freqs.buildCodeTree();
			if (count % 262144 == 0)  // frekans tablosunu s�f�rlar
				freqs = new FrequencyTable(initFreqs);
		}
		enc.write(256);  // EOF
		
		
	}
	private static boolean isPowerOf2(int x) {
		return x > 0 && Integer.bitCount(x) == 1;
	}

}
