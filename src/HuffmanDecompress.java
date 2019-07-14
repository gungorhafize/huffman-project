import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class HuffmanDecompress {

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: java HuffmanDecompress InputFile OutputFile");
			System.exit(1);
			return;
		}
		File inputFile  = new File("C:/Desktop/sample1.txt");
		File outputFile = new File("C:/Desktop/sample2.txt");

	
		
	
	try (BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFile)))) {
		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
			StandartCode standCode = readCodeLengthTable(in);
			CodeTree code = standCode.toCodeTree();
			decompress(code, in, out);
		}
	}
}
	
	


static StandartCode readCodeLengthTable(BitInputStream in) throws IOException {
	int[] codeLengths = new int[257];
	for (int i = 0; i < codeLengths.length; i++) {
		// For this file format, we read 8 bits in big endian
		int val = 0;
		for (int j = 0; j < 8; j++)
			val = (val << 1) | in.readNoEof();
		codeLengths[i] = val;
	}
	return new StandartCode(codeLengths);
}
static void decompress(CodeTree code, BitInputStream in, OutputStream out) throws IOException {
	HuffmanDecoder dec = new HuffmanDecoder(in);
	dec.codeTree = code;
	while (true) {
		int symbol = dec.read();
		if (symbol == 256)  // EOF symbol
			break;
		out.write(symbol);
	}
}

}
