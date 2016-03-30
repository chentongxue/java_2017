package com.game.draco.app.quest.phase;

import java.util.HashSet;
import java.util.Set;

import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.QuestPhaseAdator;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.app.quest.phase.term.TypeNameTerm;

import sacred.alliance.magic.vo.RoleInstance;

public class CopyPassQuestPhase extends QuestPhaseAdator {

	/** 为了提供判断性能 */
	private Set<Short> copyIdSet = new HashSet<Short>();
	
	@Override
	public int copyPass(RoleInstance role, short copyId) {
		if(!this.copyIdSet.contains(copyId)){
			return 0;
		}
		int index = startIndex ;
		for(QuestTerm term : this.questTermList){
			index ++ ;
			if(!String.valueOf(copyId).equals(term.getParameter())){
				continue;
			}
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
	
	public CopyPassQuestPhase(short copyId1, String copyName1, short copyId2, String copyName2, short copyId3, String copyName3) {
		this.init(copyId1, copyName1);
		this.init(copyId2, copyName2);
		this.init(copyId3, copyName3);
	}
	
	public CopyPassQuestPhase(short copyId1, String copyName1, short copyId2, String copyName2) {
		this.init(copyId1, copyName1);
		this.init(copyId2, copyName2);
	}
	
	public CopyPassQuestPhase(short copyId, String copyName) {
		this.init(copyId, copyName);
	}
	
	private void init(short copyId, String copyName){
		if(copyId <= 0){
			return;
		}
		this.copyIdSet.add(copyId);
		this.questTermList.add(new TypeNameTerm(QuestTermType.CopyMapPass, 1, copyId, copyName));
	}
	
}
