package a_effective.enum_;

import java.util.EnumMap;
import java.util.Map;

//142
/**
 * 按照序数进行索引（两次）的数组的数组，该序数表示两个枚举值的映射
 */
//using ordinal() to index array of arrays -DON'T DO THIS
public enum Phase2 {
	SOLID,
	LIQUID,
	GAS;
	public enum Transition{
		MELT(SOLID, LIQUID),
		FREEZE(LIQUID,SOLID),
		BOIL(LIQUID,GAS),
		CONDENSE(GAS,LIQUID),
		SUBLIME(SOLID,GAS),
		DEPOSIT(GAS,SOLID);
		private final Phase2 src;
		private final Phase2 dst;
		
		Transition(Phase2 src, Phase2 dst){
			this.src = src;
			this.dst = dst;
		}
		private static final Map<Phase2, Map<Phase2,Transition>> m = new EnumMap<Phase2, Map<Phase2,Transition>>(Phase2.class);
		static {
			for(Phase2 p :Phase2.values()){
				m.put(p, new EnumMap<Phase2,Transition>(Phase2.class));
			}
			for(Transition trans:Transition.values()){
				m.get(trans.src).put(trans.dst, trans);
			}
		}
		public static Transition from(Phase2 src,Phase2 dst){
			return m.get(src).get(dst);
		}
	}
}
