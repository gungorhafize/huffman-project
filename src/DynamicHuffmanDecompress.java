import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public final class DynamicHuffmanDecompress {

	public static void main(String[] args) throws IOException{
		if (args.length != 2) {
			System.err.println("Usage: java AdaptiveHuffmanDecompress InputFile OutputFile");
			System.exit(1);
			return;
		}
		File inputFile  = new File("C:/Desktop/sample3.txt");
		File outputFile = new File("C:/Desktop/sample4.txt");
		try (BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFile)))) {
			try (OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
				decompress(in, out);
			}
		}
	
	} static void decompress(BitInputStream in, OutputStream out) throws IOException {
		int[] initFreqs = new int[257];
		Arrays.fill(initFreqs, 1);
		
		FrequencyTable freqs = new FrequencyTable(initFreqs);
		HuffmanDecoder dec = new HuffmanDecoder(in);
		dec.codeTree = freqs.buildCodeTree(); //Kompresör ile ayný algoritma
		int count = 0;//Çýktý dosyasýna yazýlan bayt sayýsý
		while (true) {
			// Bir bayt kodunu çözer ve yazar
			int symbol = dec.read();
			if (symbol == 256)  // EOF sembol
				break;
			out.write(symbol);
			count++;
			
			// Frekans tablosunu ve muhtemelen kod aðacýný günceller
			freqs.increment(symbol);
			if (count < 262144 && isPowerOf2(count) || count % 262144 == 0)  // Kod aðacýný günceller
				dec.codeTree = freqs.buildCodeTree();
			if (count % 262144 == 0)  // frekans tablosunu resetler
				freqs = new FrequencyTable(initFreqs);
		}
	}
	
	private static boolean isPowerOf2(int x) {
		return x > 0 && Integer.bitCount(x) == 1;
	}
}
