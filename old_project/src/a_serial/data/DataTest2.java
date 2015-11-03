package a_serial.data;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DataTest2 {

	public static void main(String[] args) throws Exception {
		Person p = new Person("李胜男",158);
		FileOutputStream fos = new FileOutputStream("无序列化.os");
        ObjectOutputStream os= new ObjectOutputStream(fos);
        os.writeObject(p);//java.io.NotSerializableException:
        os.close();
        
        
//        FileInputStream fis = new FileInputStream("无序列化.os");
//        ObjectInputStream ois = new ObjectInputStream(fis);
//        Person p2 = (Person) ois.readObject();
//        ois.close();
//        System.out.println(p2);
	}
}
