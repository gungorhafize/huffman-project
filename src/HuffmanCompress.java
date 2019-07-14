import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

//257 sembolden olu�an bir alfabe kullan�r - bayt de�erleri i�in 256 sembol ve EOF i�aret�isi i�in 1 sembol.
//S�k��t�r�lm�� dosya format� 257 kod uzunluklar� listesiyle ba�lar
public class HuffmanCompress {

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: java HuffmanCompress InputFile OutputFile");
			System.exit(1);
			return;
		}
		File inputFile  = new File("C:/Desktop/sample1.txt");
		File outputFile = new File("C:/Desktop/sample2.txt");
		//sembol frekanslar�n� hesaplamak i�in bir kez girdi dosyas�n� okur.
		//Elde edilen olu�turulan kod, statik Huffman kodlamas� i�in uygundur.
		FrequencyTable freqs = getFrequencies(inputFile);
		freqs.increment(256);//EOF sembol� 1 frekans al�r
		CodeTree code = freqs.buildCodeTree();
		StandartCode standCode = new StandartCode(code, 257);
		//Kod a�ac�n� kurall� bir kodla(standart kodlar) de�i�tirir.veya her simgeyi
		//kod de�eri de�i�ebilir, ancak kod uzunlu�u ayn� kal�r
		code = standCode.toCodeTree();
		//Giri� dosyas�n� tekrar okur, Huffman kodlamayla s�k��t�r�r ve ��kt� dosyas�n� yazar
		try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile))) {
			try (BitOutputStream out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)))) {
				writeCodeLengthTable(out, standCode);
				compress(code, in, out);
			}
		}
	}
	//Verilen dosyadaki baytlara dayal� bir s�kl�k(frequency) tablosu d�nd�r�r.
	private static FrequencyTable getFrequencies(File file) throws IOException {
		FrequencyTable freqs = new FrequencyTable(new int[257]);
		try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {
			while (true) {
				int b = input.read();
				if (b == -1)
					break;
				freqs.increment(b);
			}
		}
		return freqs;
	}
	static void writeCodeLengthTable(BitOutputStream out, StandartCode canonCode) throws IOException {
		for (int i = 0; i < canonCode.getSymbolLimit(); i++) {
			int val = canonCode.getCodeLength(i);
			// dosya bi�imi i�in, yaln�zca 255 bit uzunlu�a kadar kodlar� destekliyor.
			if (val >= 256)
				throw new RuntimeException("Bir sembol�n kodu �ok uzun!");
			
			for (int j = 7; j >= 0; j--)
				out.write((val >>> j) & 1);
		}
	}
	
	
	static void compress(CodeTree code, InputStream in, BitOutputStream out) throws IOException {
		HuffmanEncoder enc = new HuffmanEncoder(out);
		enc.codeTree = code;
		while (true) {
			int b = in.read();
			if (b == -1)
				break;
			enc.write(b);
		}
		enc.write(256);  // EOF
	}
	
	
	
	
	
	}
