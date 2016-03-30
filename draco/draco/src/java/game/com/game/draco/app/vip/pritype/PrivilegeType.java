package com.game.draco.app.vip.pritype;

import java.util.HashMap;
import java.util.Map;

import sacred.alliance.magic.base.AttributeType;

import com.game.draco.GameContext;
@Deprecated
public enum PrivilegeType {
	
	Privilege((byte)1,""),
	;
	private final byte type;
	private final String name;
	private final static Map<Byte,AttributeType> privilegeMap = new HashMap<Byte,AttributeType>();
	static {
		for (AttributeType attriType : AttributeType.values()) {
			byte value = attriType.getType();
			privilegeMap.put(value, attriType);
		}
	}
	private PrivilegeType(byte type, String name){
		this.type = type;
		this.name = name;
	}
	public String getName(){
		return GameContext.getI18n().getText(name);
	}
	
}
