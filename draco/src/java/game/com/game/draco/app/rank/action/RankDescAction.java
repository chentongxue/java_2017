package com.game.draco.app.rank.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.domain.RankLogRoleInfo;
import com.game.draco.app.rank.domain.RankReward;
import com.game.draco.app.rank.domain.RankRewardRank;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.RankN2MRewardItem;
import com.game.draco.message.request.C0814_RankDescReqMessage;
import com.game.draco.message.response.C0814_RankDescRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.vo.RoleInstance;

public class RankDescAction extends BaseAction<C0814_RankDescReqMessage> {

	@Override
	public Message execute(ActionContext context, C0814_RankDescReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		int rankId = reqMsg.getRankId();
		RankInfo rankInfo = GameContext.getRankApp().getRankInfo(rankId);
		if(null == rankInfo){
			return null;
		}
		C0814_RankDescRespMessage respMsg = new C0814_RankDescRespMessage();
		respMsg.setSortRule(rankInfo.getSortRule());
		respMsg.setRefreshRule(rankInfo.getRefreshRule());
		respMsg.setRewardRule(rankInfo.getRewardRule());
		RankLogRoleInfo roleInfo = GameContext.getRankApp().getRoleRank(rankId, role.getRoleId());
		List<RankRewardRank> rewardRankList = null;
		if(null == roleInfo || roleInfo.getRank() <= 0){
			rewardRankList = GameContext.getRankApp().getRewardRankList(role.getLevel(),
					role.getSex(), rankId);
		}
		else {
			rewardRankList = GameContext.getRankApp().getRewardRankList(roleInfo.getLevel(), 
					roleInfo.getGender(), rankId);
		}
		//如果没有取到对应的rewardRank列表返回
		if(null == rewardRankList){
			return respMsg;
		}
		List<RankN2MRewardItem> rewardItemList = new ArrayList<RankN2MRewardItem>();
		for(RankRewardRank rewardRank : rewardRankList){
			if(null == rewardRank){
				continue;
			}
			RankReward rankReward = GameContext.getRankApp().getRankReward(rewardRank.getRankKey());
			if(null == rankReward){
				continue;
			}
			//如果奖励配置了比排名的名次多则退出
			if(rankReward.getRankStart() > rankInfo.getDisCount()){
				break;
			}
			RankN2MRewardItem n2MrewardItem = new RankN2MRewardItem();
			n2MrewardItem.setRankTop(rankReward.getRankEnd());
			//n2MrewardItem.setBindMoney(rankReward.getBindMoney());
			n2MrewardItem.setGameMoney(rankReward.getGameMoney());
			List<GoodsOperateBean> goodsList = rankReward.getGoodsList();
			
			if(null != goodsList && goodsList.size() > 0){
				List<GoodsLiteNamedItem> goodsItemList = new ArrayList<GoodsLiteNamedItem>();
				for(GoodsOperateBean gb : goodsList){
					GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(gb.getGoodsId());
					if(null == goodsBase){
						continue ;
					}
					GoodsLiteNamedItem rewardGoods = goodsBase.getGoodsLiteNamedItem() ;
					//绑定类型和数量
					rewardGoods.setBindType(gb.getBindType().getType());
					rewardGoods.setNum((short)gb.getGoodsNum());
					goodsItemList.add(rewardGoods);
				}
				n2MrewardItem.setGoodsItemList(goodsItemList);
			}
			rewardItemList.add(n2MrewardItem);
		}
		respMsg.setRewardItemList(rewardItemList);
		return respMsg;
	}

}
