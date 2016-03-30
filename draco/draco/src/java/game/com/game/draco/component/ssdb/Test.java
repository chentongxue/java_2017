package com.game.draco.component.ssdb;

public class Test {

	public static void main(String[] args) throws Exception{
		SSDB ssdb = new SSDB("192.168.1.230", 8888);
		int value = ssdb.zrrank("test_z_set", "d");
		System.out.println(value);
	}
}
