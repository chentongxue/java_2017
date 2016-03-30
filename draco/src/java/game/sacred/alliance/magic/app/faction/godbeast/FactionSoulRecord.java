//package sacred.alliance.magic.app.faction.godbeast;
//
//import lombok.Data;
//import sacred.alliance.magic.domain.Faction;
//
//public @Data class FactionSoulRecord {
//	private String factionId;
//	private int soulId;
//	private int level; //神兽等级
//	private int flyNum;//飞升次数
//	private int growValue;
//	
//	public static FactionSoulRecord createFactionSoulRecord(Faction faction,int soulId,int level){
//		FactionSoulRecord record = new FactionSoulRecord();
//		record.setFactionId(faction.getFactionId());
//		record.setSoulId(soulId);
//		record.setLevel(level);
//		record.setFlyNum(0);
//		record.setGrowValue(0);
//		return record;
//	}
//}
