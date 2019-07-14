import java.io.IOException;
import java.util.List;
import java.util.Objects;

//Sembolleri kodlar ve bir Huffman kodlu bit ak���na(stream) yazar
public final class HuffmanEncoder {
	private BitOutputStream output;
	public CodeTree codeTree;
	
	public HuffmanEncoder(BitOutputStream out) {
		Objects.requireNonNull(out);
		output = out;
	}
	//Belirtilen sembol� kodlar ve Huffman kodlu ��kt� ak���na(output stream) yazar
	public void write(int symbol) throws IOException {
		if (codeTree == null)
			throw new NullPointerException("Kod a�ac� bo� de�erde!");
		List<Integer> bits = codeTree.getCode(symbol);
		for (int b : bits)
			output.write(b);
	}
}
