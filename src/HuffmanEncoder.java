import java.io.IOException;
import java.util.List;
import java.util.Objects;

//Sembolleri kodlar ve bir Huffman kodlu bit akýþýna(stream) yazar
public final class HuffmanEncoder {
	private BitOutputStream output;
	public CodeTree codeTree;
	
	public HuffmanEncoder(BitOutputStream out) {
		Objects.requireNonNull(out);
		output = out;
	}
	//Belirtilen sembolü kodlar ve Huffman kodlu çýktý akýþýna(output stream) yazar
	public void write(int symbol) throws IOException {
		if (codeTree == null)
			throw new NullPointerException("Kod aðacý boþ deðerde!");
		List<Integer> bits = codeTree.getCode(symbol);
		for (int b : bits)
			output.write(b);
	}
}
