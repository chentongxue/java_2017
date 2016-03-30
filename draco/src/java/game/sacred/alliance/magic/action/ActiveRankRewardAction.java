//package sacred.alliance.magic.action;
//
//import java.util.List;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C2321_ActiveRankRewardReqMessage;
//import com.game.draco.message.response.C2321_ActiveRankRewardRespMessage;
//
//import sacred.alliance.magic.app.active.rank.ActiveRankApp;
//import sacred.alliance.magic.app.active.rank.ActiveRankInfo;
//import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
//import sacred.alliance.magic.app.rank.domain.RankInfo;
//import sacred.alliance.magic.app.rank.domain.RankLogRoleInfo;
//import sacred.alliance.magic.app.rank.domain.RankReward;
//import sacred.alliance.magic.app.rank.domain.RankRewardRank;
//import sacred.alliance.magic.base.AttributeType;
//import sacred.alliance.magic.base.OperatorType;
//import sacred.alliance.magic.base.OutputConsumeType;
//import sacred.alliance.magic.constant.Status;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.domain.RankDbInfo;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class ActiveRankRewardAction extends BaseAction<C2321_ActiveRankRewardReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C2321_ActiveRankRewardReqMessage reqMsg) {
//		RoleInstance role = this.getCurrentRole(context);
//		if(null == role){
//			return null;
//		}
//		int rankId = reqMsg.getRankId();
//		RankInfo rankItem = GameContext.getRankApp().getRankInfo(rankId);
//		if(null == rankItem){
//			return null;
//		}
//		ActiveRankInfo aRankItem = rankItem.getActiveRankInfo();
//		if(null == aRankItem){
//			return null;
//		}
//		byte rewardStat = GameContext.getActiveRankApp().getRewardStat(role, rankItem);
//		if(rewardStat == ActiveRankApp.REWARD_STAT_ERROR){
//			return null;
//		}
//		C2321_ActiveRankRewardRespMessage respMsg = new C2321_ActiveRankRewardRespMessage();
//		//已领取
//		if(rewardStat == ActiveRankApp.REWARD_STAT_REWARDED){
//			respMsg.setResult(Status.FAILURE.getInnerCode());
//			respMsg.setInfo(Status.Rank_REWARD_REWARDED.getTips());
//			return respMsg;
//		}
//	  //没有奖励
//		if(rewardStat == ActiveRankApp.REWARD_STAT_NO){
//			respMsg.setResult(Status.FAILURE.getInnerCode());
//			respMsg.setInfo(Status.Rank_REWARD_NO.getTips());
//			return respMsg;
//		}
//		//未到领奖时间
//		if(rewardStat == ActiveRankApp.REWARD_STAT_DISABLE){
//			respMsg.setResult(Status.FAILURE.getInnerCode());
//			respMsg.setInfo(Status.Rank_REWARD_DISABLE.getTips());
//			return respMsg;
//		}
//		//可以领奖
//		RankLogRoleInfo roleInfo = GameContext.getRankApp().getRoleRank(rankId, role.getRoleId());
//		if(null == roleInfo){
//			respMsg.setResult(Status.FAILURE.getInnerCode());
//			respMsg.setInfo(Status.FAILURE.getTips());
//			return respMsg;
//		}
//		List<RankRewardRank> rewardRankList = GameContext.getRankApp().getRewardRankList(roleInfo.getLevel(), 
//				roleInfo.getCamp(), roleInfo.getGender(), rankId);
//		if(null == rewardRankList){
//			respMsg.setResult(Status.FAILURE.getInnerCode());
//			respMsg.setInfo(Status.Rank_REWARD_NoExist.getTips());
//			return respMsg;
//		}
//		RankRewardRank targetRewardRank = null;
//		for(RankRewardRank rewardRank : rewardRankList){
//			if(null == rewardRank){
//				continue;
//			}
//			int rank = roleInfo.getRank();
//			if(rewardRank.getRankStart() <= rank && rewardRank.getRankEnd() >= rank){
//				targetRewardRank = rewardRank;
//				break;
//			}
//		}
//		//没有配奖励
//		if(null == targetRewardRank){
//			respMsg.setResult(Status.FAILURE.getInnerCode());
//			respMsg.setInfo(Status.Rank_REWARD_NoExist.getTips());
//			return respMsg;
//		}
//		RankReward rankReward = GameContext.getRankApp().getRankReward(targetRewardRank.getRankKey());
//		if(null == rankReward){
//			respMsg.setResult(Status.FAILURE.getInnerCode());
//			respMsg.setInfo(Status.Rank_REWARD_NoExist.getTips());
//			return respMsg;
//		}
//		//活动物品
//		GoodsResult goodsResult = GameContext.getUserGoodsApp().addGoodsBeanForBag(role, rankReward.getGoodsList(), 
//				OutputConsumeType.active_rank_award);
//		if(goodsResult.isSuccess()){
//			RankDbInfo rankDbInfo = GameContext.getRankApp().getRankDbInfo(role, rankItem);
//			rankDbInfo.setReward(RankDbInfo.REWARDED_YES);
//			//实时入库
//			try{
//				GameContext.getActiveRankApp().realTimeWriteDB(rankDbInfo);
//			}catch (Exception e){
//				logger.error("RankDbInfo realTime write db", e);
//			}
//			int bindingGoldMoney = rankReward.getBindMoney();
//			int silverMoney = rankReward.getSilverMoney();
//			if(bindingGoldMoney > 0){
//				GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.bindingGoldMoney, OperatorType.Add, 
//						bindingGoldMoney, OutputConsumeType.active_rank_award);
//			}
//			if(silverMoney > 0){
//				GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.silverMoney, OperatorType.Add, 
//						silverMoney, OutputConsumeType.active_rank_award);
//			}
//			if(bindingGoldMoney > 0 || silverMoney > 0){
//				role.getBehavior().notifyAttribute();
//			}
//			respMsg.setResult(Status.SUCCESS.getInnerCode());
//		}
//		else{
//			respMsg.setResult(Status.FAILURE.getInnerCode());
//			respMsg.setInfo(goodsResult.getInfo());
//		}
//		return respMsg;
//	}
//
//}
