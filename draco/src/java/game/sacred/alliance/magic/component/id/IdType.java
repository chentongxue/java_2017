package sacred.alliance.magic.component.id;

public enum IdType {
	ROLEID(1),
	NPCID(2),
	GOODS(3),
	FACTIONID(4),
	UNION(5),
	MAIL(6),
	PET(7),
	UNIONAUCTION(8),
	;
	
	public int type;
	
	IdType(int type){
		this.type = type ;
	}
	
	public int getType(){
		return type;
	}
	

	
}
