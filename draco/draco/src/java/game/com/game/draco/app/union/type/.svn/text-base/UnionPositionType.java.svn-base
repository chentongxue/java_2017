package com.game.draco.app.union.type;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;

public enum UnionPositionType {
	
	Leader((byte)0,"会长",1),
	Deputy((byte)1,"副会长",2),
	Elite((byte)2,"官员",4),
	Member((byte)3,"会员",-1),
	;
	
	private final byte type;
	private final String name;
	private final int num;
	
	UnionPositionType(byte type, String name,int num){
		this.type = type;
		this.name = name;
		this.num = num;
	}
	
	public final byte getType(){
		return type;
	}
	public String getName(){
		return name;
	}
	
	public int getNum() {
		return num;
	}

	public static UnionPositionType getPosition(byte type){
		for(UnionPositionType item : UnionPositionType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}
	
	/**
	 * 总人数
	 * @return
	 */
	public static int getPositionAllNum(){
		int num = 0;
		for(UnionPositionType item : UnionPositionType.values()){
			if(item.getNum() > 0){
				num += item.getNum();
			}
		}
		return num;
	}
	
	/**
	 * 当前职位对应人数
	 * @param type
	 * @return
	 */
	public static int getPositionNum(byte type){
		for(UnionPositionType item : UnionPositionType.values()){
			if(item.getType() == type){
				return item.getNum();
			}
		}
		return 0;
	}
	
	/**
	 * 判断职位大小
	 * @param bigPosition
	 * @param smallPosition
	 * @return
	 */
	public static Result isGreaterThan(UnionPositionType bigPosition, UnionPositionType smallPosition){
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
