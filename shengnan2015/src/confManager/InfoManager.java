package confManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
public class InfoManager {
	private InfoManager(){
		System.out.println("18 lastuser init()");
		init();
	}
	private final static Map<String,Info> cfMap = new HashMap<String, Info>();
	private final static InfoManager INSTANCE = new InfoManager();
	public static InfoManager getInstance(){
		System.out.println("24 lastuser init()");
		return INSTANCE;
	}
	static{
		System.out.println(InfoManager.getInstance().getIntegerPort("2"));
	}
	private static void init() {
		try {
//			File f = new File("server_id_port.dat");
			InputStream is =  Thread.currentThread().getContextClassLoader().getResourceAsStream ("server_id_port.dat");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = null;
			while((line=reader.readLine()) != null){
				if(line.equals("")||line.startsWith("#")){
					continue;
				}
				String[] ss = line.split("\t");
				System.out.println("40 lastuser init()"  + Arrays.toString(ss));
				if(ss.length < 3){
					continue;
				}
				Info cf = new Info(ss[0], ss[1], ss[2]);
				cfMap.put(cf.getServerId(), cf);
			}
			System.out.println("47 lastuser init() map"  + Arrays.toString(cfMap.values().toArray()));
		} catch (Exception e) {
			System.out.println("50 lastuser init() err"  + e.toString());
		}
	}
	public Collection<Info>  getAll(){
		return cfMap.values();
	}
	public String getStringPort(String id){
		return cfMap.get(id).getPort();
	}
	public Integer getIntegerPort(String serverId){
		return Integer.parseInt(cfMap.get(serverId).getPort());
	}
	//1
	public static void main(String[] args) {
		System.out.println("test");
//		System.err.println(InfoManager.getInstance());
//		System.out.println(InfoManager.getInstance().getAll());
		
	}
	public static void log(String s){
		System.out.println(s);
	}
}
