package sacred.alliance.magic.base;

public enum FactionRecordType {
	Faction_Record_Level((byte)1),
	Faction_Record_Build((byte)2),
	Faction_Record_Soul_Upgrade((byte)3),
	Faction_Record_Soul_Fly((byte)4),
	Faction_Record_Role_Join((byte)5),
	Faction_Record_Role_Leave((byte)6),
	Faction_Record_Role_Kick((byte)7),
	Faction_Record_Donate((byte)8),
	Faction_Record_War((byte)9),
	Faction_Record_Soul_War((byte)10),
	Faction_Record_Impeach((byte)11),
	;
	
	private final byte type;
	
	FactionRecordType(byte type){
		this.type = type;
	}
	public final byte getType(){
		return type;
	}
	
	public static FactionRecordType get(byte type){
		for(FactionRecordType fr : FactionRecordType.values()){
			if(fr.getType() == type){
				return fr;
			}
		}
		return null;
	}
 }
