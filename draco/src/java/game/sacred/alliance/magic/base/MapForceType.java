package sacred.alliance.magic.base;

public enum MapForceType {

	map_normal(0,"同阵营不可PK,不同阵营可PK"),
	map_samecampcanpk(1,"同阵营可PK"),
	map_diffcampcannotpk(2,"不同阵营不可PK");
	
	private int type;
	
	private String name;
	
	MapForceType(int type, String name){
		this.name=name;
		this.type=type;
	}

	public static MapForceType getType(int type){
		switch(type){
		case 0:
			return map_samecampcanpk;
		case 1:
			return map_diffcampcannotpk;	
		default:
			return map_normal;
		}
	}
	public int getType(){
		return type;
	}
	
	public String getName() {
		return name;
	}
}
