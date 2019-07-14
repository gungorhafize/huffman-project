
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

//bitlerin yazýlacaðý bir streamdir. Stream'in bitiþine kadar 0'larla 8 bitin katlarý kadar doldurulur.
public final class BitOutputStream implements AutoCloseable{
	
	private OutputStream output;
	private int currentByte;
	private int numBitsFilled;
	public BitOutputStream(OutputStream out) {
		Objects.requireNonNull(out);
		output = out; //yazýlacak temel byte streami
		currentByte = 0;//Geçerli bayt için biriken bitler her zaman [0x00, 0xFF] aralýðýnda bulunur,
		numBitsFilled = 0;//Geçerli bayttaki birikmiþ bitlerin sayýsý, daima 0 ile 7 arasýnda ( 0 ve 7 dahil).
	}
	
	public void write(int b) throws IOException{ //Stream'e bir bit yazar. bu bir 0 veya 1 olmalýdýr.
		if (b != 0 && b != 1)
		throw new IllegalArgumentException("Deðiþken 1 veya 0 olmalýdýr!");
		currentByte = (currentByte << 1) | b;
		numBitsFilled++;
		if (numBitsFilled == 8) {
			output.write(currentByte);
			currentByte = 0;
			numBitsFilled = 0;
		}
	}
	public void close() throws IOException { //Bu akýþý ve alttaki çýktý akýþýný kapatýr.
		while (numBitsFilled != 0)  //Bu bit akýþý bir bayt sýnýrýnda olmadýðýnda çaðrýlýrsa,
			write(0);// en az sayýda "0" biti (0 ve 7 arasýnda), sonraki bayt sýnýrýna ulaþmak için doldurulur.
		output.close();
	}
}
