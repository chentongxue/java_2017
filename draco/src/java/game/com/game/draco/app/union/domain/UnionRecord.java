package com.game.draco.app.union.domain;

import lombok.Data;
import sacred.alliance.magic.constant.TextId;

import com.game.draco.GameContext;
import com.game.draco.app.union.type.UnionRecordType;

public @Data class UnionRecord {
	private String unionId;//门派ID 
	private byte type;//记录类型
	private int data1;//记录数据1
	private String data2;//记录数据2
	private String data3;//记录数据3(名字)
	private long createTime;//记录时间
	
	public UnionRecord(){
		createTime = System.currentTimeMillis();
	}
	
	/**
	 * 1、门派等级变化：2012-3-12 门派等级提升到了3级
	 * 2、门派建筑等级变化：2012-3-12 消耗了20000门派资金，门派建筑玲珑阁提升到了3级
	 * 3、门派仙兽等级变化：2012-3-12 仙兽等级提升到了3级
	 * 4、门派仙兽等级变化：2012-3-12 仙兽等级提升到了3级
	 * 5、门派成员进出：2012-3-12 张三加入了门派
	 * 6、门派成员进出：2012-3-12 张三离开了门派
	 * 7、门派成员进出：2012-3-12 张三被李四移除出了门派
	 * 8、门派元宝捐献：2012-3-12 张三进行了30元宝的捐献，获得了2000贡献度
	 * 9、本门派的门派战结果：2012-3-12 门派战结束，门派排名第2
	 * 10、门派仙兽逆袭活动开启和结束时间：2012-3-12 门派掌门开启了仙兽逆袭活动
	 * 11、张三成功弹劾掌门成功，成为了新掌门。
	 */
	public String getUnionRecord() {
		UnionRecordType frt = UnionRecordType.get(type);
		if(null == frt) {
			return "";
		}
		switch(frt) {
		case Union_Record_Level:
			return GameContext.getI18n().messageFormat(TextId.UNION_RECORD_LEVEL,data1);
		case Union_Record_Role_Join:
			return GameContext.getI18n().messageFormat(TextId.UNION_RECORD_ROLE_JOIN,data2);
		case Union_Record_Role_Leave:
			return GameContext.getI18n().messageFormat(TextId.UNION_RECORD_ROLE_LEAVE,data2);
		case Union_Record_Role_Kick:
			return GameContext.getI18n().messageFormat(TextId.UNION_RECORD_ROLE_KICK,data2,data3);
		case Union_Record_Donate:
			return GameContext.getI18n().messageFormat(TextId.UNION_RECORD_DPK,data3,data1,data2);
		case Union_Record_Impeach:
			return GameContext.getI18n().messageFormat(TextId.UNION_RECORD_IMPEACH,data2);
		case Uniuon_Record_Activity_Open:
			return GameContext.getI18n().messageFormat(TextId.UNION_RECORD_ACTIVITY_OPEN,data3,data2);
		case Uniuon_Record_Activity_Close:
			return GameContext.getI18n().messageFormat(TextId.UNION_RECORD_ACTIVITY_CLOSE,data3,data2);
		case Uniuon_Record_Position_LevelUp:
			return GameContext.getI18n().messageFormat(TextId.UNION_CHANGE_POSITION_LEVELUP_SUCCESS,data3,data2);
		case Uniuon_Record_Position_Demotion:
			return GameContext.getI18n().messageFormat(TextId.UNION_CHANGE_POSITION_DEMOTION_SUCCESS,data3,data2);
		case Union_Record_Summon_BOSS:
			return GameContext.getI18n().messageFormat(TextId.UNION_SUMMON_NOTIFY,data2);
		case Union_Record_KILL_BOSS:
			return GameContext.getI18n().messageFormat(TextId.UNION_SUMMON_KILL,data2);
		default:
			return "";
		}
	}
}
