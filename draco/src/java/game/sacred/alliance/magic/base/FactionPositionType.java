package sacred.alliance.magic.base;

import sacred.alliance.magic.constant.Status;

public enum FactionPositionType {
	
	Leader((byte)0,"掌门"),
	Deputy((byte)1,"副掌门"),
	Elite((byte)2,"长老"),
	Member((byte)3,"弟子"),
	;
	
	private final byte type;
	private final String name;
	
	FactionPositionType(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public final byte getType(){
		return type;
	}
	public String getName(){
		return name;
	}
	
	public static FactionPositionType getPosition(byte type){
		for(FactionPositionType item : FactionPositionType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
	/**
	 * 判断职位大小
	 * @param bigPosition
	 * @param smallPosition
	 * @return
	 */
	public static Result isGreaterThan(FactionPositionType bigPosition, FactionPositionType smallPosition){
		Result result = new Result();
		if(null == bigPosition || null == smallPosition){
			return result.setInfo(Status.Faction_Kick_Error.getTips());
		}
		if(bigPosition.getType() < smallPosition.getType()) {
			return result.success();
		}
		if(bigPosition.getType() == smallPosition.getType()) {
			return result.setInfo(Status.Faction_Kick_Position_Same.getTips());
		}
		return result.setInfo(Status.Faction_Kick_Position_Low.getTips());
	}
 }
