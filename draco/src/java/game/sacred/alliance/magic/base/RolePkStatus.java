package sacred.alliance.magic.base;

import com.game.draco.GameContext;

import sacred.alliance.magic.constant.TextId;

public enum RolePkStatus {

	PEACE((byte)0,TextId.PK_STATUS_PEACE),
	BATTLE((byte)1,TextId.PK_STATUS_BATTLE),
	MASSACRE((byte)2,TextId.PK_STATUS_MASSACRE),
	;
	
	private byte type;
	private String name;
	
	RolePkStatus(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getName() {
		return GameContext.getI18n().getText(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public static RolePkStatus getRolePkStatus(byte type){
		switch(type){
		case 0:
			return RolePkStatus.PEACE;
		case 1:
			return RolePkStatus.BATTLE;
		case 2:
			return RolePkStatus.MASSACRE;
		default:
			return null;
		}
	}
}
