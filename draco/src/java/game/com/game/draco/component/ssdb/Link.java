package com.game.draco.component.ssdb;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Link {
	private final static String UTF8 = "utf-8" ;
	private Socket sock;
	private MemoryStream input = new MemoryStream();
	private boolean available = true ;
	private Lock lock = new ReentrantLock();
	private String host ;
	private int port ;
	private int timeout ;

	public Link(String host, int port) throws Exception{
		this(host, port, 0);
	}
	

	public Link(String host, int port, int timeout_ms) throws Exception{
		this.host = host ;
		this.port = port ;
		this.timeout = timeout_ms ;
		this.newSock(); 
	}
	
	private void newSock() throws Exception{
		sock = new Socket();
		if(this.timeout > 0){
			sock.setSoTimeout(this.timeout);
		}
		sock.setTcpNoDelay(true);
		sock.connect(new java.net.InetSocketAddress(host, port), timeout);
		available = true ;
	}
	
	public void close(){
		try{
			if(null != sock){
				sock.close();
			}
		}catch(Exception e){
		}
		this.available = false ;
	}
	
	public boolean isAvailable(){
		return (available && null != sock && sock.isConnected()) ;
	}


	public Response request(String cmd, byte[]...params) throws Exception{
		ArrayList<byte[]> list = new ArrayList<byte[]>();
		for(byte[] s : params){
			list.add(s);
		}
		return this.request(cmd, list);
	}
	
	public Response request(String cmd, String...params) throws Exception{
		ArrayList<byte[]> list = new ArrayList<byte[]>();
		for(String s : params){
			list.add(s.getBytes(UTF8));
		}
		return this.request(cmd, list);
	}
	
	public Response requestString(String cmd, List<String> params) throws Exception{
		ArrayList<byte[]> list = new ArrayList<byte[]>();
		for(String s : params){
			list.add(s.getBytes(UTF8));
		}
		return this.request(cmd, list);
	}
	
	public Response requestString(String cmd, String name, List<String> params) throws Exception{
		ArrayList<byte[]> list = new ArrayList<byte[]>();
		list.add(name.getBytes(UTF8));
		for(String s : params){
			list.add(s.getBytes(UTF8));
		}
		return this.request(cmd, list);
	}

	private Response request(String cmd, List<byte[]> params) throws Exception{
		MemoryStream buf = new MemoryStream(4096);
		Integer len = cmd.length();
		buf.write(len.toString());
		buf.write('\n');
		buf.write(cmd);
		buf.write('\n');
		for(byte[] bs : params){
			len = bs.length;
			buf.write(len.toString());
			buf.write('\n');
			buf.write(bs);
			buf.write('\n');
		}
		buf.write('\n');
		List<byte[]> list = null ;
		
		//同一时刻只能一个线程调用
		this.lock.lock(); 
		try{
			this.reconn();
			send(buf);
			list = recv();
		}catch(Exception ex){
			this.close();
			throw ex ;
		}finally{
			lock.unlock();
		}
		return new Response(list);
	}
	
	private void reconn() throws Exception{
		if(this.isAvailable()){
			return ;
		}
		this.newSock();
	}
	
	private void send(MemoryStream buf) throws Exception{
		//System.out.println(">> " + buf.printable());
		OutputStream os = sock.getOutputStream();
		os.write(buf.buf, buf.data, buf.size);
		os.flush();
	}
	
	private List<byte[]> recv() throws Exception{
		input.nice();
		InputStream is = sock.getInputStream();
		while(true){
			List<byte[]> ret = parse();
			if(ret != null){
				return ret;
			}
			byte[] bs = new byte[8192];
			int len = is.read(bs);
			//System.out.println("<< " + (new MemoryStream(bs, 0, len)).printable());
			input.write(bs, 0, len);
		}
	}
	
	private List<byte[]> parse(){
		ArrayList<byte[]> list = new ArrayList<byte[]>();
		byte[] buf = input.buf;
		
		int idx = 0;
		while(true){
			int pos = input.memchr('\n', idx);
			//System.out.println("pos: " + pos + " idx: " + idx);
			if(pos == -1){
				break;
			}
			if(pos == idx || (pos == idx + 1 && buf[idx] == '\r')){
				// ignore empty leading lines
				if(list.isEmpty()){
					idx += 1; // if '\r', next time will skip '\n'
					continue;
				}else{
					input.decr(idx + 1);
					return list;
				}
			}
			String str = new String(buf, input.data + idx, pos - idx);
			int len = Integer.parseInt(str);
			idx = pos + 1;
			if(idx + len >= input.size){
				break;
			}
			byte[] data = Arrays.copyOfRange(buf, input.data + idx, input.data + idx + len);
			//System.out.println("len: " + len + " data: " + data.length);
			idx += len + 1; // skip '\n'
			list.add(data);
		}
		return null;		
	}

}
