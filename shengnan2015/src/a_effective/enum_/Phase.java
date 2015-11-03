package a_effective.enum_;
//141
/**
 * 按照序数进行索引（两次）的数组的数组，该序数表示两个枚举值的映射
 */
//using ordinal() to index array of arrays -DON'T DO THIS
public enum Phase {
	SOLID,
	LIQUID,
	GAS;
	public enum Transition{
		MELT,
		FREEZE,
		BOIL,
		CONDENSE,
		SUBLIME,
		DEPOSIT;
		//Rows indexed by src-ordinal,cols by dst-ordinal
		private static final Transition[][]TRANSITIONS = {
			{null, MELT, SUBLIME},
			{FREEZE, null, BOIL},
			{DEPOSIT, CONDENSE, null}
		};
		//returns the phase transition from one phase to another
		public static Transition from(Phase src, Phase dst){
			return TRANSITIONS[src.ordinal()][dst.ordinal()];
		}
	}
}
