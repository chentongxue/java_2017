package com.game.draco.app.qualify.action;

import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.GoodsHelper;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.qualify.config.QualifyGiftConfig;
import com.game.draco.message.item.NpcStoreConsumeItem;
import com.game.draco.message.item.QualifyRankGiftItem;
import com.game.draco.message.request.C1752_QualifyCheckRankGiftReqMessage;
import com.game.draco.message.response.C1752_QualifyCheckRankGiftRespMessage;
import com.google.common.collect.Lists;

public class QualifyShowRankGiftAction extends BaseAction<C1752_QualifyCheckRankGiftReqMessage> {

	@Override
	public Message execute(ActionContext context, C1752_QualifyCheckRankGiftReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		int rank = GameContext.getQualifyApp().getRoleRank(role);
		QualifyGiftConfig nowConfig = this.getQualifyGiftConfig(rank, role.getLevel());
		// 到下一阶段
		if (null == nowConfig) {
			rank --;
		} else {
			rank = nowConfig.getUpRank() - 1;
		}
		QualifyGiftConfig nextConfig = this.getQualifyGiftConfig(rank, role.getLevel());
		C1752_QualifyCheckRankGiftRespMessage resp = new C1752_QualifyCheckRankGiftRespMessage();
		if (null != nowConfig) {
			resp.setNowQualifyRankGift(this.getQualifyGiftItem(nowConfig));
		}
		if (null != nextConfig) {
			resp.setNextQualifyRankGift(this.getQualifyGiftItem(nextConfig));
		}
		return resp;
	}
	
	private QualifyGiftConfig getQualifyGiftConfig(int rank, int level) {
		return GameContext.getQualifyApp().getRankQualifyGiftConfig(rank, level);
	}
	
	private QualifyRankGiftItem getQualifyGiftItem(QualifyGiftConfig config) {
		QualifyRankGiftItem item = new QualifyRankGiftItem();
		item.setUpRank(config.getUpRank());
		item.setDownRank(config.getDownRank());
		item.setNpcStoreConsumeList(this.getNpcStoreConsumeList(config));
		item.setGoodsLiteNamedItem(GoodsHelper.getGoodsLiteNamedList(config.getGoodsList()));
		return item;
	}
	
	private List<NpcStoreConsumeItem> getNpcStoreConsumeList(QualifyGiftConfig config) {
		List<NpcStoreConsumeItem> list = Lists.newArrayList();
		if (config.getGameMoney() > 0) {
			list.add(this.getNpcStoreConsumeItem(AttributeType.gameMoney.getType(), config.getGameMoney()));
		}
		if (config.getGoldMoney() > 0) {
			list.add(this.getNpcStoreConsumeItem(AttributeType.goldMoney.getType(), config.getGoldMoney()));
		}
		if (config.getHonours() > 0) {
			list.add(this.getNpcStoreConsumeItem(AttributeType.honor.getType(), config.getHonours()));
		}
		if (config.getPotential() > 0) {
			list.add(this.getNpcStoreConsumeItem(AttributeType.potential.getType(), config.getPotential()));
		}
		return list;
	}
	
	private NpcStoreConsumeItem getNpcStoreConsumeItem(byte type, int count) {
		NpcStoreConsumeItem item = new NpcStoreConsumeItem();
		item.setType(type);
		item.setCount(count);
		return item;
	}
	
}
