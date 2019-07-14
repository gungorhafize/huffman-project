import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

//okunabilir bir bit streami(akýþ). Toplam bir sayýsý daima 8'in katlarýdýr.
public final class BitInputStream implements AutoCloseable{

	private InputStream input;
	private int CurrentByte;
	private int numBitsRemaining;
	
	public BitInputStream(InputStream in)//constructor
	{
		Objects.requireNonNull(in);
		input = in;
		CurrentByte = 0;
		numBitsRemaining = 0;
	}
	
	public int read() throws IOException{//Bu metod akýþtan(stream) bir bit okur
		if(CurrentByte == -1) //Bir bit varsa 0 veya 1 döndürür yada akýþýn sonuna gelmiþse -1 döndürür.
			return -1;        //Stream sonu her zaman için bir byte sýnýrýndadýr.
		if(numBitsRemaining == 0){
			CurrentByte = input.read();
			if(CurrentByte == -1)
				return -1;
			numBitsRemaining=8;	
		}
		if(numBitsRemaining <= 0)
		{throw new AssertionError();
		}
		numBitsRemaining --;
		return (CurrentByte >>> numBitsRemaining) & 1;
		}
	
	 public int readNoEof() throws IOException{
		 int result = read();
		 if(result !=-1)
			 return result;
		 else
			 throw new EOFException();
	 }
	 
	 public void close() throws IOException{
		 input.close();
		 CurrentByte = -1;
		 numBitsRemaining = 0;
	 }
	}

