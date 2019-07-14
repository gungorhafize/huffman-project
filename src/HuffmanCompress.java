import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

//257 sembolden oluþan bir alfabe kullanýr - bayt deðerleri için 256 sembol ve EOF iþaretçisi için 1 sembol.
//Sýkýþtýrýlmýþ dosya formatý 257 kod uzunluklarý listesiyle baþlar
public class HuffmanCompress {

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: java HuffmanCompress InputFile OutputFile");
			System.exit(1);
			return;
		}
		File inputFile  = new File("C:/Desktop/sample1.txt");
		File outputFile = new File("C:/Desktop/sample2.txt");
		//sembol frekanslarýný hesaplamak için bir kez girdi dosyasýný okur.
		//Elde edilen oluþturulan kod, statik Huffman kodlamasý için uygundur.
		FrequencyTable freqs = getFrequencies(inputFile);
		freqs.increment(256);//EOF sembolü 1 frekans alýr
		CodeTree code = freqs.buildCodeTree();
		StandartCode standCode = new StandartCode(code, 257);
		//Kod aðacýný kurallý bir kodla(standart kodlar) deðiþtirir.veya her simgeyi
		//kod deðeri deðiþebilir, ancak kod uzunluðu ayný kalýr
		code = standCode.toCodeTree();
		//Giriþ dosyasýný tekrar okur, Huffman kodlamayla sýkýþtýrýr ve çýktý dosyasýný yazar
		try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile))) {
			try (BitOutputStream out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)))) {
				writeCodeLengthTable(out, standCode);
				compress(code, in, out);
			}
		}
	}
	//Verilen dosyadaki baytlara dayalý bir sýklýk(frequency) tablosu döndürür.
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
			// dosya biçimi için, yalnýzca 255 bit uzunluða kadar kodlarý destekliyor.
			if (val >= 256)
				throw new RuntimeException("Bir sembolün kodu çok uzun!");
			
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
