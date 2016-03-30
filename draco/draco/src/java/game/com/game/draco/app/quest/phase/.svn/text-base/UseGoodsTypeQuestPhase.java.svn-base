package com.game.draco.app.quest.phase;

import com.game.draco.GameContext;
import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.QuestPhaseAdator;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.app.quest.phase.term.CountParamTerm;

import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.vo.RoleInstance;

/** 某点使用某类物品 */
public class UseGoodsTypeQuestPhase extends QuestPhaseAdator{
	
	private GoodsType goodsType;
	
	@Override
	public int useGoods(RoleInstance role, int goodsId) {
		if(null == role){
			return 0;
		}
		GoodsBase goods = GameContext.getGoodsApp().getGoodsBase(goodsId);
		//不是指定类型的物品
		if(null == goods || goods.getGoodsType() != this.goodsType.getType()){
			return 0;
		}
		int index = startIndex ;
		for(QuestTerm term : this.questTermList){
			index ++ ;
			int nowNum = this.getCurrentNum(role, index);
			if(nowNum >= term.getCount()){
				//已经满足数量
				return 0;
			}
			this.incrCurrentNum(role, index);
			//发进度提示
			QuestHelper.pushQuestTipMessage(role, this.master, nowNum + 1, term, index);
			return 1;
		}
		return 0;
	}
	
	public UseGoodsTypeQuestPhase(int type, int count){
		GoodsType goodsType = GoodsType.get(type);
		if(null == goodsType || count <= 0){
			return;
		}
		this.goodsType = goodsType;
		this.questTermList.add(new CountParamTerm(QuestTermType.UseGoodsType, count, this.goodsType.getName()));
	}
	
}
