package a_serial.data;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import a_serial.ByteUtil;

import com.google.common.collect.Lists;

public class DataTest {

	public static List<Person> parseData(byte [] targetData) {
		List<Person> list = Lists.newArrayList();
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(targetData));
			int size = in.readByte();
			for(int i=0;i<size;i++){
				Person b = new Person();
				b.setName("李柏然");
				b.setHeight(178);
				list.add(b);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}


	public static byte [] buildData(List<Person> list) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bout);
			out.writeByte(list.size());
			for(Person r : list){
				out.writeUTF(r.getName());
				out.writeInt(r.getHeight());
			}
			out.flush();
			out.close();
			return (bout.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	
	}
	public static void main(String[] args) throws Exception {
		String fileName = "序列化.os";
//		test1(fileName);
        
		test2(fileName);
        
	}


	private static void test2(String fileName) throws Exception {
		byte[] nowData = ByteUtil.getData(fileName);
		List<Person> list = parseData(nowData);
		System.out.println(list);
	}


	private static void test1(String fileName) throws FileNotFoundException,
			IOException {
		List<Person> persons = Lists.newArrayList();
		Person p = new Person("李柏然",178);
		Person p2 = new Person("李思雨",168);
		persons.add(p);
		persons.add(p2);
		byte[] data = buildData(persons);
		ByteUtil.writeFile(data, fileName);
	}
}
