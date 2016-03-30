package sacred.alliance.magic.app.carnival;

import sacred.alliance.magic.app.carnival.logic.CarnivalLogic;
import sacred.alliance.magic.app.carnival.logic.RoleCampAttrLogic;
import sacred.alliance.magic.app.carnival.logic.RoleCareerAttrLogic;
import sacred.alliance.magic.app.carnival.logic.RoleCareerDataLogic;
import sacred.alliance.magic.app.carnival.logic.RoleDataLogic;
import sacred.alliance.magic.app.carnival.logic.RoleMountLogic;
import sacred.alliance.magic.base.AttributeType;

public enum CarnivalType {
	Role_Level(1,"等级", "level", "exp", AttributeType.level, AttributeType.exp),
	Role_BattleScore(2,"战斗力", "battleScore", "level", AttributeType.battleScore, AttributeType.level),
	Role_Arena(3,"封神榜", null, null, null, null),
	Role_Recharge(4,"充值" ,null, null, null, null),
	Faction_War(5,"门派战", "level", "exp", AttributeType.level, AttributeType.exp),
	Role_Honor(6,"荣誉","honor", "level", AttributeType.honor, AttributeType.level),
	Role_Consume(7,"消耗",null, null, null, null),
	Role_Tower(8,"九重天",null, null, null, null),
	Role_Mount(9,"坐骑",null , null, null, AttributeType.level),
	//10为特殊，策划需求改为前后不一致，所以声望写死，等级和经验为全民奖励条件
	//Role_TotalMagicSoul(10,"法宝声望", "level", "exp", AttributeType.level, AttributeType.exp),
	;
	
	
	private final int type;
	private final String name ;
	private final String columnName ;
	private final String subColumnName ;
	private final AttributeType attriType;
	private final AttributeType subAttriType;
	
	CarnivalType(int type,String name, String columnName, String subColumnName, AttributeType attriType, AttributeType subAttriType){
		this.type = type;
		this.name = name ;
		this.columnName = columnName ;
		this.subColumnName = subColumnName;
		this.attriType = attriType;
		this.subAttriType = subAttriType;
	}
	
	public int getType(){
		return type;
	}
	
	public static CarnivalType get(int type){
		for(CarnivalType v : values()){
			if(type == v.getType()){
				return v;
			}
		}
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	public String getColumnName() {
		return columnName;
	}

	public AttributeType getAttriType() {
		return attriType;
	}

	public AttributeType getSubAttriType() {
		return subAttriType;
	}

	public String getSubColumnName() {
		return subColumnName;
	}

	public CarnivalLogic createCarnivalLogic(CarnivalType carnivalType){
		switch(carnivalType){
			case Role_Level:
				return RoleCareerAttrLogic.getInstance() ;
			case Role_BattleScore:
				return RoleCareerAttrLogic.getInstance() ;
			case Role_Arena:
				return RoleCareerDataLogic.getInstance() ;
			case Role_Recharge:
				return RoleDataLogic.getInstance() ;
//			case Faction_War:
//				return FactionWarLogic.getInstance() ;
			case Role_Honor:
				return RoleCampAttrLogic.getInstance() ;
			case Role_Consume:
				return RoleDataLogic.getInstance() ;
			case Role_Tower:
				return RoleCareerDataLogic.getInstance() ;
			case Role_Mount:
				return RoleMountLogic.getInstance() ;
			default:
				return null;
		}
	}
}
