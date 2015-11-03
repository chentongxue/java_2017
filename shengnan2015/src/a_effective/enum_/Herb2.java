package a_effective.enum_;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
//141
/**
 * 用EnumMap代替序数索引
 * 更简短，更清楚，更安全
 */
public class Herb2 {
	public enum Type{ ANNUAL, PERENNIAL, BIENNIAL }
	private final String name;
	private final Type type; 
	
	Herb2(String name,Type type){
		this.name = name;
		this.type = type;
	}
	@Override 
	public String toString(){
		return name;
	}
	
	private static Herb2[] garden = new Herb2[5];
	private static Map<Herb2.Type, Set<Herb2>> herbsByType = new EnumMap<Herb2.Type, Set<Herb2>>(Herb2.Type.class);
	static{
		for(Herb2.Type t:Herb2.Type.values()){
			herbsByType.put(t, new HashSet<Herb2>());
		}
		for (Herb2 h :garden) {
			herbsByType.get(h.type).add(h);
			System.out.println(herbsByType);
		}
	}
}
