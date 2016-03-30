//package sacred.alliance.magic.app.faction.war;
//
//import java.util.concurrent.atomic.AtomicInteger;
//
//import lombok.Data;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.game.draco.GameContext;
//
//import sacred.alliance.magic.app.arena.ArenaMatch;
//import sacred.alliance.magic.app.faction.war.config.FactionWarConfig;
//import sacred.alliance.magic.app.faction.war.domain.FactionWarInfo;
//import sacred.alliance.magic.base.MapLogicType;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public @Data class FactionWarMatch {
//	private final static Logger logger = LoggerFactory.getLogger(ArenaMatch.class);
//	private static AtomicInteger KEY_GEN = new AtomicInteger(0);
//	private String key ;
//	private FactionWarInfo faction1;
//	private FactionWarInfo faction2;
//	private String containerId = "" ;
//	private long createTime = 0 ;
//	private long overTime = 0;
//	private int rounds;
//	private boolean over = false;
//	private FactionWarConfig config;
//	private long beginTime;
//	private long enterTime;
//	
//	private FactionWarMatch(){
//		this.key = String.valueOf(KEY_GEN.incrementAndGet());
//		this.createTime = System.currentTimeMillis();
//	}
//	
//	public static FactionWarMatch create(FactionWarInfo team1,FactionWarInfo team2,FactionWarConfig config, int curRound, int beginRound){
//		try {
//			Faction faction1 = GameContext.getFactionApp().getFaction(team1.getFactionId());
//			Faction faction2 = GameContext.getFactionApp().getFaction(team2.getFactionId());
//			if(null == faction1 && null == faction2){
//				return null;
//			}
//			FactionWarMatch match = new FactionWarMatch();
//			match.setFaction1(team1);
//			match.setFaction2(team2);
//			team1.setMatch(match);
//			team2.setMatch(match);
//			team1.setPoint(config.getAttackPoint());
//			team2.setPoint(config.getDefendPoint());
//			team1.setNpcBorn(config.getFactionSoulBorn1());
//			team2.setNpcBorn(config.getFactionSoulBorn2());
//			match.setRounds(curRound);
//			match.setConfig(config);
//			match.setBeginTime(config.getCurRoundOpenTime(curRound, beginRound));
//			match.setEnterTime(match.getBeginTime() - config.getBeforeEnterTime() * 60 * 1000);
//			// 添加到管理器
//			GameContext.getFactionWarApp().addFactionWarMatch(match);
//			// 创建地图
//			Faction faction = faction1;
//			if(null == faction){
//				faction = faction2;
//			}
//			int roleId = faction.getLeaderId();
//			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
//			if(null == role){
//				role = GameContext.getBaseDAO().selectEntity(RoleInstance.class, "roleId", roleId);
//			}
//			MapLogicType.createMapInstance(role, team1.getPoint().getMapid());
//			return match;
//		}catch(Exception ex){
//			logger.error("",ex);
//		}
//		return null ;
//	}
//	
//	public String getOtherId(String factionId){
//		if(faction1.getFactionId().equals(factionId)){
//			return faction2.getFactionId();
//		}
//		return faction1.getFactionId();
//	}
//	
//	public FactionWarInfo getOtherFaction(FactionWarInfo info){
//		if(faction1.getFactionId().equals(info.getFactionId())){
//			return faction2;
//		}
//		return faction1;
//	}
//	
//	public boolean isFactionWarRole(RoleInstance role){
//		String factionId = role.getFactionId();
//		if(factionId.equals(faction1.getFactionId()) || factionId.equals(faction2.getFactionId())) {
//			return true;
//		}
//		return false;
//	}
//}
