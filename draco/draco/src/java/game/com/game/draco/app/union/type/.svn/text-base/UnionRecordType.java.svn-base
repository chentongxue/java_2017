package com.game.draco.app.union.type;

public enum UnionRecordType {
	//级别
	Union_Record_Level((byte)1),
	//进入
	Union_Record_Role_Join((byte)2),
	//离开
	Union_Record_Role_Leave((byte)3),
	//踢出
	Union_Record_Role_Kick((byte)4),
	//贡献
	Union_Record_Donate((byte)5),
	//弹劾
	Union_Record_Impeach((byte)6),
	//开启活动
	Uniuon_Record_Activity((byte)7),
	;
	
	private final byte type;
	
	UnionRecordType(byte type){
		this.type = type;
	}
	public final byte getType(){
		return type;
	}
	
	public static UnionRecordType get(byte type){
		for(UnionRecordType fr : UnionRecordType.values()){
			if(fr.getType() == type){
				return fr;
			}
		}
		return null;
	}
 }
