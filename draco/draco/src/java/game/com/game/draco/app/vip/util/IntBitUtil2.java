package com.game.draco.app.vip.util;

import java.util.Arrays;


public class IntBitUtil2 {
	private static int DEFALT_LEN = 2<<3;
	public static byte[] intToBits(int val) {
		byte[] bits = new byte[DEFALT_LEN];
		for (int i = 0; i < bits.length; i++) {
			bits[i] = (byte)((val>>i)& 0x1);
		}
		return bits;
	}
	public static int bitsToInt(byte[] bits) {
		int value = 0;
		for (int i = 0; i < bits.length; i++) {
			value += bits[i]<<(i);
		}
		return value;
	}
	public static void main(String s[]){
		int a = 8;
		a = a|1<<0;
		byte [] b = intToBits (a);
		int va = bitsToInt(b);
		System.out.println(Arrays.toString(b));
		System.out.println(va);
		System.out.println("JAJA");
		System.out.println(0<<2);
	}
}
