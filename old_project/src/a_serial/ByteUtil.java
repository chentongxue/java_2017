package a_serial;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ByteUtil {
	public static byte[] getData(String fileName) throws Exception{
		FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        byte[] data = (byte[]) ois.readObject();
        ois.close();
        return data;
	}
	
	public static void writeFile(byte[] data, String fileName) throws FileNotFoundException,
			IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream os= new ObjectOutputStream(fos);
        os.writeObject(data);
        os.close();
	}
}
