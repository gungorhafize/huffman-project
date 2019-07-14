
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

//bitlerin yaz�laca�� bir streamdir. Stream'in biti�ine kadar 0'larla 8 bitin katlar� kadar doldurulur.
public final class BitOutputStream implements AutoCloseable{
	
	private OutputStream output;
	private int currentByte;
	private int numBitsFilled;
	public BitOutputStream(OutputStream out) {
		Objects.requireNonNull(out);
		output = out; //yaz�lacak temel byte streami
		currentByte = 0;//Ge�erli bayt i�in biriken bitler her zaman [0x00, 0xFF] aral���nda bulunur,
		numBitsFilled = 0;//Ge�erli bayttaki birikmi� bitlerin say�s�, daima 0 ile 7 aras�nda ( 0 ve 7 dahil).
	}
	
	public void write(int b) throws IOException{ //Stream'e bir bit yazar. bu bir 0 veya 1 olmal�d�r.
		if (b != 0 && b != 1)
		throw new IllegalArgumentException("De�i�ken 1 veya 0 olmal�d�r!");
		currentByte = (currentByte << 1) | b;
		numBitsFilled++;
		if (numBitsFilled == 8) {
			output.write(currentByte);
			currentByte = 0;
			numBitsFilled = 0;
		}
	}
	public void close() throws IOException { //Bu ak��� ve alttaki ��kt� ak���n� kapat�r.
		while (numBitsFilled != 0)  //Bu bit ak��� bir bayt s�n�r�nda olmad���nda �a�r�l�rsa,
			write(0);// en az say�da "0" biti (0 ve 7 aras�nda), sonraki bayt s�n�r�na ula�mak i�in doldurulur.
		output.close();
	}
}
