package com.game.draco.app.quest.phase;

import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.QuestPhaseAdator;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.app.quest.phase.term.MapTerm;

import sacred.alliance.magic.vo.RoleInstance;

/**杀人阶段**/
public class KillRoleQuestPhase extends QuestPhaseAdator {

	@Override
	public int killRole(RoleInstance role) {
		if(null == role){
			return 0;
		}
		int index = startIndex ;
		for(QuestTerm term : this.questTermList){
			index ++ ;
			int nowNum = this.getCurrentNum(role, index);
			if(nowNum >= term.getCount()){
				//已经满足数量
				return  0;
			}
			this.incrCurrentNum(role, index);
			//发进度提示
			QuestHelper.pushQuestTipMessage(role, this.master, nowNum + 1, term, index);
			return 1;
		}
		return  0 ;
	}
	
	public KillRoleQuestPhase(int roleCount){
		this(roleCount, null);
	}
	
	public KillRoleQuestPhase(int roleCount, String mapId){
		if(roleCount <= 0){
			return;
		}
		this.questTermList.add(new MapTerm(QuestTermType.Role, roleCount, mapId));
	}
	
}
