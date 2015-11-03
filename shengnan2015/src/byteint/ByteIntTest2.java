package byteint;

import java.util.Arrays;

public class ByteIntTest2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for(int i = 0;i<2049;i++){
			byte b[] = intToByte(i);
			System.out.println(i+"->"+Arrays.toString(b));
		}
		int max = Integer.MAX_VALUE;
		byte b[] = new byte[] {0, 0, 2, -128};
		byte c[] = new byte[] {127, -1, -1, -1};
		System.out.println(" {0, 0, 2, -128}->"+bytesToInt(b));
		System.out.println(" {-1, -1, -1, -1}->"+bytesToInt(c));
		byte bb[] = intToByte(max);
		System.out.println(max+"->"+Arrays.toString(bb));
	}
	/**
	 * 127 [0, 0, 0, 127]
	 * @param i
	 * @return
	 */
	public static byte[] intToByte(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	public static int bytesToInt(byte[] bytes) {
		int offset = 0;
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (bytes[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}
}
