package sacred.alliance.magic.base;
public enum StateType {
	fixed(0,(short)1,"定身",false,true,true),
	mum(1,(short)2,"沉默",true,false,false),
	charm(2,(short)4,"魅惑",false,false,false),
	coma(3,(short)8,"昏迷",false,false,false),
	muss(4,(short)16,"混乱",true,false,false),
	noMove(5,(short)32,"不能移动",false,true,true),
	noUseSkill(6,(short)64,"不能使用技能",true,false,false),
	paralysis(7,(short)128,"瘫痪",false,false,false),
	soul(8,(short)256,"灵魂状态",true,false,false),
	blowFly(9,(short)512,"击飞",false,false,false),
	guideSkill(10,(short)1024,"引导攻击",true,true,false),
	frozen(11,(short)2048,"冰冻",false,false,false),
	;
	
	private final int type;
	private final short code ;
	private final String name;
	private final boolean canMove ;
	private final boolean canUseSkill ;
	private final boolean canUseCommonSkill ;
	StateType(int type,short code,String name,boolean canMove,
			boolean canUseSkill,boolean canUseCommonSkill){
		this.type = type;
		this.code = code ;
		this.name = name;
		this.canMove = canMove ;
		this.canUseSkill = canUseSkill ;
		this.canUseCommonSkill = canUseCommonSkill ;
	}
	public final int  getType(){
		return type;
	}
	public String getName(){
		return name;
	}
	
	public short getCode() {
		return code;
	}
	
	public boolean isCanMove() {
		return canMove;
	}
	public boolean isCanUseSkill() {
		return canUseSkill;
	}
	public static StateType getType(int type){
		for(StateType st : values()){
			if(type == st.getType()){
				return st ;
			}
		}
		return null ;
	}
	
	public static StateType getCode(short code){
		for(StateType st : values()){
			if(code == st.getCode()){
				return st ;
			}
		}
		return null ;
	}
	public boolean isCanUseCommonSkill() {
		return canUseCommonSkill;
	}
	
	
}
