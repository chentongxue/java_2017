package byteint;

import java.util.Arrays;

public class Test {
	public static void main(String args[]){
		System.out.println("haha");
		byte[] header = new byte[8];
		byte[] buf = new byte[]{1,1,0,1};
		intToBytes(header, 0, buf.length + 8);
		System.out.println(Arrays.toString(header));
	}
	public static void intToBytes(byte[] b,int offset,int v) {
		for(int i = 0; i < 4; ++i) {
			b[offset + i] = (byte)(v >>> (24 - i * 8));
		}
	}
	
	public static int bytesToInt(byte[] b,int offset) {
		int num = 0;
		for(int i = offset; i < offset+4; ++i) {
			num <<= 8;
			num |= (b[i] & 0xff);
		}
		return num;
	}
}
