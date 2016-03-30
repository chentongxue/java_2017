package com.game.draco.app.role.systemset.vo;

/**
 * 系统设置的状态
 * 表示需要入库还是修改数据库
 *
 */
public enum SystemSetState {
	
	Initialize((byte)0,"初始状态"),//从数据库中读取，没有任何改动，不需要改动数据库
	Insert((byte)1,"新增数据"),//数据库中没有
	Update((byte)2,"修改数据"),//数据库中有，修改了设置
	
	;
	
	private final byte type;
	private final String name;
	
	SystemSetState(byte type, String name){
		this.type = type;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public static SystemSetState get(byte type){
		for(SystemSetState item : SystemSetState.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
}
