package a_effective.enum_;

import java.util.HashSet;
import java.util.Set;
//140
/**
 * ÐòÊýË÷Òý
 */
public class Herb {
	public enum Type{ ANNUAL, PERENNIAL, BIENNIAL }
	private final String name;
	private final Type type; 
	
	Herb(String name,Type type){
		this.name = name;
		this.type = type;
	}
	@Override 
	public String toString(){
		return name;
	}
	
	private static Herb[] garden = new Herb[5];
	private static Set<Herb>[] herbsByType = (Set<Herb>[]) new Set[Herb.Type.values().length];
	static{
		for(int i = 0; i < herbsByType.length; i++){
			herbsByType[i] = new HashSet<Herb>();
		}
		
		for(Herb h:garden){
			herbsByType[h.type.ordinal()].add(h);
		}
		
		for (int i = 0; i < herbsByType.length; i++) {
			System.out.printf("%s:%s%n", Herb.Type.values()[i], herbsByType[i]);
		}
	}
}
