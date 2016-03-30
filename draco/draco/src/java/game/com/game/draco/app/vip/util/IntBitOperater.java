package com.game.draco.app.vip.util;


public class IntBitOperater {
	private static int DEFALT_LEN = 2<<3;
	/**
	 * <p> put 56171
	 * <p> get[1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1]
	 */
	public static byte[] intToBits(int val) {
		byte[] bits = new byte[DEFALT_LEN];
		for (int i = 0; i < bits.length; i++) {
			bits[bits.length -1-i] = (byte)((val>>i)& 0x1);
		}
		return bits;
	}
	/**
	 * <p> put[1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1]
	 * <p> get 56171
	 */
	public static int bitsToInt(byte[] bits) {
		int value = 0;
		for (int i = 0; i < bits.length; i++) {
			value += bits[i]<<(bits.length-1-i);
		}
		return value;
	}
}
