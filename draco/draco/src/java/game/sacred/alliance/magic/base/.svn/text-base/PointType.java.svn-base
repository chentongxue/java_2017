package sacred.alliance.magic.base;

public enum PointType {
	//默认无效
	Unknow(-1,false,false), 
	//任务采集点
	QuestCollectPoint(0,true,false), 
	//普通技能采集点
	//GeneralSkillCollectPoint(1,true,false), 
	//特殊技能采集点
	//SpecialSkillCollectPoint(2,true,false), 
	//跳转点
	JumpPoint(3,false,false), 
	//掉落点
	Box(4,false,true), ;

	private final int type;
	private final boolean collectPoint ;
	private final boolean box ;
	
	PointType(int type,boolean collectPoint,boolean box) {
		this.type = type;
		this.collectPoint = collectPoint ;
		this.box = box ;
	}

	
	
	public int getType() {
		return type;
	}

	/**
	 * 是否采集点
	 */
	public boolean isCollectPoint(){
		return this.collectPoint ;
	}
	
	public boolean isBox(){
		return this.box ;
	}
	
	public static PointType get(int type) {
		for (PointType pointType : PointType.values()) {
			if (type == pointType.getType()) {
				return pointType;
			}
		}
		return Unknow;
	}
}
