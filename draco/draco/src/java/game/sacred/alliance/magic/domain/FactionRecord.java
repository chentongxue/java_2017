//package sacred.alliance.magic.domain;
//
//import java.util.Date;
//
//import com.game.draco.GameContext;
//
//import lombok.Data;
//import sacred.alliance.magic.base.FactionRecordType;
//import sacred.alliance.magic.constant.TextId;
//
//public @Data class FactionRecord {
//	private int id;//唯一标识
//	private String factionId;//门派ID 
//	private byte type;//记录类型
//	private int data1;//记录数据1
//	private String data2;//记录数据2
//	private String data3;//记录数据3(名字)
//	private Date createTime;//记录时间
//	
//	public FactionRecord(){
//		createTime = new Date();
//	}
//	
//	/**
//	 * 1、门派等级变化：2012-3-12 门派等级提升到了3级
//	 * 2、门派建筑等级变化：2012-3-12 消耗了20000门派资金，门派建筑玲珑阁提升到了3级
//	 * 3、门派仙兽等级变化：2012-3-12 仙兽等级提升到了3级
//	 * 4、门派仙兽等级变化：2012-3-12 仙兽等级提升到了3级
//	 * 5、门派成员进出：2012-3-12 张三加入了门派
//	 * 6、门派成员进出：2012-3-12 张三离开了门派
//	 * 7、门派成员进出：2012-3-12 张三被李四移除出了门派
//	 * 8、门派元宝捐献：2012-3-12 张三进行了30元宝的捐献，获得了2000贡献度
//	 * 9、本门派的门派战结果：2012-3-12 门派战结束，门派排名第2
//	 * 10、门派仙兽逆袭活动开启和结束时间：2012-3-12 门派掌门开启了仙兽逆袭活动
//	 * 11、张三成功弹劾掌门成功，成为了新掌门。
//	 */
//	public String getFactionRecord() {
//		FactionRecordType frt = FactionRecordType.get(type);
//		if(null == frt) {
//			return "";
//		}
//		switch(frt) {
//		case Faction_Record_Level:
//			return GameContext.getI18n().messageFormat(TextId.Faction_Record_Level,data1);
//		case Faction_Record_Build:
//			return GameContext.getI18n().messageFormat(TextId.Faction_Record_Build,data1,data3,data2);
//		case Faction_Record_Soul_Upgrade:
//			return GameContext.getI18n().messageFormat(TextId.Faction_Record_Soul_Upgrade,data1);
//		case Faction_Record_Soul_Fly:
//			return GameContext.getI18n().messageFormat(TextId.Faction_Record_Soul_Fly,data1);
//		case Faction_Record_Role_Join:
//			return GameContext.getI18n().messageFormat(TextId.Faction_Record_Role_Join,data2);
//		case Faction_Record_Role_Leave:
//			return GameContext.getI18n().messageFormat(TextId.Faction_Record_Role_Leave,data2);
//		case Faction_Record_Role_Kick:
//			return GameContext.getI18n().messageFormat(TextId.Faction_Record_Role_Kick,data2,data3);
//		case Faction_Record_Donate:
//			return GameContext.getI18n().messageFormat(TextId.Faction_Record_Donate,data3,data1,data2);
//		case Faction_Record_War:
//			return GameContext.getI18n().messageFormat(TextId.Faction_Record_War,data1);
//		case Faction_Record_Soul_War:
//			return TextId.Faction_Record_Soul_War;
//		case Faction_Record_Impeach:
//			return GameContext.getI18n().messageFormat(TextId.Faction_Record_Impeach,data2);
//		default:
//			return "";
//		}
//	}
//}
