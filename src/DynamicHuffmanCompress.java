import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

//uygulama, 257 sembolden oluþan düz bir frekans tablosundan baþlar (hepsi 1 frekansa ayarlanýr).
//baytlar kodlanýrken istatistikler toplar ve periyodik olarak Huffman kodunu yeniden üretir.
//baytlarý çözülürken onu günceller ve Huffman kodunu zamanla ayný noktalarda yeniden oluþturur.
//kompresörün ve açýcý kompresörün senkronize edilmiþ durumlarý vardýr, böylece veriler düzgün bir þekilde açýlabilir

public final class DynamicHuffmanCompress {

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: java AdaptiveHuffmanCompress InputFile OutputFile");
			System.exit(1);
			return;
		}
		
		File inputFile  = new File("C:/Desktop/sample3.txt");
		File outputFile = new File("C:/Desktop/sample4.txt");
		//Dosya sýkýþtýrmasýný gerçekleþtir.
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
		int count = 0;//Girdi dosyasýndan okunan bayt sayýsý
		while (true) {
			// Bir bayt okumak ve kodlamak
			int symbol = in.read();
			if (symbol == -1)
				break;
			enc.write(symbol);
			count++;
			freqs.increment(symbol);//Frekans tablosunu ve kod aðacýný günceller
			if (count < 262144 && isPowerOf2(count) || count % 262144 == 0)  // kood aðacýný günceller
				enc.codeTree = freqs.buildCodeTree();
			if (count % 262144 == 0)  // frekans tablosunu sýfýrlar
				freqs = new FrequencyTable(initFreqs);
		}
		enc.write(256);  // EOF
		
		
	}
	private static boolean isPowerOf2(int x) {
		return x > 0 && Integer.bitCount(x) == 1;
	}

}
