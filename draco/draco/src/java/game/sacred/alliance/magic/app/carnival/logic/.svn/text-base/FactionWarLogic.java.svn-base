//package sacred.alliance.magic.app.carnival.logic;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//import com.game.draco.GameContext;
//
//import sacred.alliance.magic.app.carnival.CarnivalRule;
//import sacred.alliance.magic.domain.CarnivalRankInfo;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.util.Util;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionWarLogic extends RoleAttrLogic {
//
//	private static FactionWarLogic instance = new FactionWarLogic();
//	
//	public static FactionWarLogic getInstance(){
//		return instance ;
//	}
//	
//	@Override
//	public List<CarnivalRankInfo> getCarnivalRank(CarnivalRule carnivalRule, int itemId) {
//		return sortFaction();
//	}
//	
//	private List<CarnivalRankInfo> sortFaction(){
//		List<CarnivalRankInfo> rankList = new ArrayList<CarnivalRankInfo>();
//		try{
//			List<Faction> allFaction = GameContext.getFactionApp().getFactionRankList(RANK_SIZE);
//			CarnivalRankInfo rankinfo = null;
//			byte index = 1;
//			for(Faction info : allFaction){
//				rankinfo = new CarnivalRankInfo();
//				rankinfo.setCampId(info.getFactionCamp());
//				rankinfo.setCareer((byte)-1);
//				rankinfo.setTargetId(info.getFactionId());
//				rankinfo.setName(info.getFactionName());
//				rankinfo.setTargetValue(info.getFactionLevel());
//				rankinfo.setRank(index++);
//				rankList.add(rankinfo);
//			}
//		}catch(Exception e){
//			logger.error("RoleLevelLogic.sortRole error: ", e);
//		}
//		return rankList;
//	}
//	
//	@Override
//	protected void rewardRank(int itemId, Collection<CarnivalRankInfo> rankReward){
//		try{
//			if(Util.isEmpty(rankReward)) {
//				return;
//			}
//			List<CarnivalRankInfo> rewardList = new ArrayList<CarnivalRankInfo>();
//			CarnivalRankInfo rankInfo = null;
//			for(CarnivalRankInfo carnivalRankInfo : rankReward){
//				rankInfo = new CarnivalRankInfo();
//				String targetId = carnivalRankInfo.getTargetId();
//				Faction faction = GameContext.getFactionApp().getFaction(targetId);
//				if(null == faction) {
//					continue;
//				}
//				RoleInstance leader = getRoleInstance(faction.getLeaderId());
//				if(null == leader) {
//					continue;
//				}
//				rankInfo.setCareer(leader.getCareer());
//				rankInfo.setTargetId(leader.getRoleId());
//				rankInfo.setRank(carnivalRankInfo.getRank());
//				rewardList.add(rankInfo);
//			}
//			this.reward(rewardList, itemId, RewardType.rank);
//		}catch(Exception e){
//			logger.error("rewardRank error:",e);
//		}
//	}
//	
//	private RoleInstance getRoleInstance(int roleId){
//		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
//		if(null == role){
//			role = GameContext.getBaseDAO().selectEntity(RoleInstance.class, "roleId", roleId);
//		}
//		return role;
//	}
//}
