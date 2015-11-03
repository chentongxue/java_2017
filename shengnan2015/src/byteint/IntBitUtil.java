package byteint;

import java.util.Arrays;

public class IntBitUtil {

	/**
	 * @param args 
	 * [1, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1]
	 *  1  1  0  1  0  1  1  0  1  1  0  1  1  0  1  1
	 *  
	 *  56171 1101101101101011
	 *  55003 1101011011011011
	 */
	public static void main(String[] args) {
		int a = 56171;
		byte [] b = intToByte (a);
		int va = bytesToInt(b);
		System.out.println(Arrays.toString(b));
		System.out.println(va);
	}
	/**
	 * 127 [0, 0, 0, 127]
	 * @param i
	 * @return
	 */
	public static byte[] intToByte(int val) {
		byte[] result = new byte[16];
		for (int i = 0; i < result.length; i++) {
			result[15-i] = (byte)((val>>i)& 0x1);
		}
		return result;
	}
	public static int bytesToInt(byte[] bytes) {
		int value = 0;
		for (int i = 0; i < bytes.length; i++) {
			value += bytes[i]<<(15-i);
		}
		return value;
	}
}
