package sacred.alliance.magic.base;

import com.game.draco.GameContext;

import sacred.alliance.magic.constant.TextId;

public enum OneKeyOpType {
	STRENGTHEN((byte)1,TextId.ONE_KEY_OPTYPE_STRENGTHEN),
	RECASTING((byte)2,TextId.ONE_KEY_OPTYPE_RECASTING),
	MOUNT_QUALITY_INCR((byte)3,TextId.ONE_KEY_OPTYPE_MOUNT_QUALITY_INCR),
	MOUNT_SKILL_UPGRADE((byte)4,TextId.ONE_KEY_OPTYPE_SKILL_UPGRADE),
	SOUL_PHASE_INCR((byte)5,TextId.ONE_KEY_OPTYPE_SOUL_PHASE_INCR),
	SOUL_SKILL_INCR((byte)6,TextId.ONE_KEY_OPTYPE_SKILL_UPGRADE),
	MAGIC_WEAPON_SLOT_PHASE_INCR((byte)7,TextId.ONE_KEY_OPTYPE_MAGIC_WEAPON_SLOT_PHASE),
	;
	
	private final byte type;
	private final String name;
	
	OneKeyOpType(byte type, String name){
		this.type = type;
		this.name = name;
	}
	
	public byte getType(){
		return type;
	}
	
	public String getName(){
		return GameContext.getI18n().getText(name);
	}
}
