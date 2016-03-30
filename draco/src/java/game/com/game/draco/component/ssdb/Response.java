package com.game.draco.component.ssdb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Response{
	private static final String UTF8 = "UTF-8" ;
	public String status;
	public List<byte[]> raw;
	/**
	 * Indicates items' order
	 */
	public List<byte[]> keys = new ArrayList<byte[]>();
	/**
	 * key-value results
	 */
	public Map<byte[], byte[]> items = new LinkedHashMap<byte[], byte[]>();
	
	public List<String> stringKeys = new ArrayList<String>();
	/**
	 * key-value results
	 */
	public Map<String, String> stringItems = new LinkedHashMap<String, String>();
	
	public Response(List<byte[]> raw){
		this.raw = raw;
		if(raw.size() > 0){
			status = new String(raw.get(0));
		}
	}
	
	public Object exception() throws Exception{
		if(raw.size() >= 2){
			throw new Exception(new String(raw.get(1)));
		}else{
			throw new Exception("");
		}
	}

	public boolean ok(){
		return status.equals("ok");
	}
	
	public boolean not_found(){
		return status.equals("not_found");
	}
	
	public void buildKeys() {
		for (int i = 1; i < raw.size(); i++) {
			try {
				byte[] k = raw.get(i);
				keys.add(k);
				stringKeys.add(new String(k, UTF8));
			} catch (Exception ex) {
			}
		}
	}

	
	public void buildMap(){
		for(int i=1; i<raw.size(); i+=2){
			byte[] k = raw.get(i);
			byte[] v = raw.get(i+1);
			keys.add(k);
			items.put(k, v);
		}
	}
	
	public void buildStringMap(){
		for(int i=1; i<raw.size(); i+=2){
			try {
				byte[] k = raw.get(i);
				byte[] v = raw.get(i + 1);
				String sk = new String(k, UTF8);
				stringKeys.add(sk);
				stringItems.put(sk, new String(v, UTF8));
			}catch(Exception ex){
			}
		}
	}

	public void print(){
		System.out.println(String.format("%-15s %s", "key", "value"));
		System.out.println("---------------------");
		for (byte[] bs : keys) {
			System.out.print(String.format("%-15s", MemoryStream.repr(bs)));
			System.out.print(": ");
			System.out.print(MemoryStream.repr(items.get(bs)));
			System.out.println();
		}
	}
}
