//package sacred.alliance.magic.app.faction.war.domain;
//
//import java.util.Date;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
//import lombok.Data;
//import sacred.alliance.magic.app.faction.war.FactionWarMatch;
//import sacred.alliance.magic.app.map.data.NpcBorn;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.vo.Point;
//
//public @Data class FactionWarInfo {
//	private String factionId;
//	private String factionName;
//	private int rounds;
//	private FactionWarMatch match;
//	private int factionWarIndex;
//	private Point point;
//	private Set<String> roleSet = new HashSet<String>();//计算人数用set
//	private Map<String,Integer> factionKilledMap = new ConcurrentHashMap<String,Integer>();
//	private Set<String> roleDeathSet = new HashSet<String>();//死亡列表
//	private Date createTime;
//	private NpcBorn npcBorn;
//	
//	public FactionWarInfo(){
//		
//	}
//	
//	public FactionWarInfo(Faction faction, int rounds, int index){
//		this.factionId = faction.getFactionId();
//		this.factionName = faction.getFactionName();
//		this.rounds = rounds;
//		this.factionWarIndex = index;
//		this.createTime = new Date();
//	}
//	
//	public void reset(){
//		roleSet.clear();
//		roleDeathSet.clear();
//		factionKilledMap.clear();
//		this.setMatch(null);
//	}
//}
