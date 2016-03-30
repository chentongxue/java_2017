package com.game.draco.app.quest.phase;

import com.game.draco.GameContext;
import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.QuestPhaseAdator;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.app.quest.phase.term.MapNpcLevelTerm;

import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

/** 杀怪限制 **/
public class KillMonsterLimitQuestPhase extends QuestPhaseAdator {
	
	private int minLevel;//怪的等级下限
	private int maxLevel;//怪的等级上限
	
	@Override
	public int killMonsterLimit(RoleInstance role, String npcId) {
		if(Util.isEmpty(npcId) || null == role){
			return 0;
		}
		int npcLevel = GameContext.getNpcApp().getNpcTemplate(npcId).getLevel();
		if(npcLevel < this.minLevel || npcLevel > this.maxLevel){
			return 0;
		}
		int index = startIndex ;
		for(QuestTerm term : this.questTermList){
			index ++ ;
			int nowNum = this.getCurrentNum(role, index);
			if(nowNum >= term.getCount()){
				//等级段的怪已经满足数量
				return  0;
			}
			this.incrCurrentNum(role, index);
			//发进度提示
			QuestHelper.pushQuestTipMessage(role, this.master, nowNum + 1, term, index);
			return 1;
		}
		return  0 ;
	}
	
	public KillMonsterLimitQuestPhase(int minLevel, int maxLevel, int count, String mapId){
		if(minLevel > maxLevel || count <= 0){
			return;
		}
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.questTermList.add(new MapNpcLevelTerm(QuestTermType.KillMonsterLimit, 
													count, 
													mapId, 
													minLevel, 
													maxLevel));
	}
	
}
