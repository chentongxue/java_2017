package com.game.draco.app.quest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.game.draco.app.quest.base.QuestStatus;
import com.game.draco.app.quest.domain.RoleQuestLogInfo;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public class QuestPhaseAdator extends QuestPhase {

	protected List<QuestTerm> questTermList = new ArrayList<QuestTerm>();
	//存放在数据库里的起始索引
	protected int startIndex = -1;
	
	@Override
	public int triggerEvent(RoleInstance role,String eventId) {
		return 0 ;
	}

	@Override
	public int death(RoleInstance role) {
		return 0 ;
	}

	@Override
	public int enterMap(RoleInstance role) {
		return 0 ;
	}

	@Override
	public int getGoods(RoleInstance role, int goodsId,int goodsNum) {
		return 0 ;
	}

	@Override
	public int killMonster(RoleInstance role, String npcId) {
		return 0 ;
	}
	@Override
	public int killNpcFallCount(RoleInstance role, String npcId) {
		return 0;
	}
	@Override
	public int talkNpc(RoleInstance role, String npcId) {
		return 0 ;
	}

	@Override
	public int update(RoleInstance role) {
		if(this.master.getTimeLimit() <=0){
			return 0 ;
		}
		//判断任务是否已经超时
		try {
			RoleQuestLogInfo info = role.getQuestLogInfo(this.master.getQuestId());
			if(null == info){
				return 0 ;
			}
			long useTime = System.currentTimeMillis() - info.getCreateTime().getTime();
			if(useTime < this.master.getTimeLimit() * 60 * 1000){
				return 0 ;
			}
			//已经超时,设置为失败
			context.getUserQuestApp().updateQuestStatus(role, this.master.getQuestId(), QuestStatus.failure);
			//发生失败提示信息
			QuestHelper.pushQuestFailureTipMessage(role, this.master);
			return 1 ;
		} catch (Exception e) {
		}
		return 0 ;
	}

	@Override
	public int useGoods(RoleInstance role, int goodsId) {
		return 0 ;
	}


	public int completePhaseAction(RoleInstance role){
		return 1 ;
	}


	@Override
	public Map<Integer,Integer> submitQuestGoodsMap() {
		return null;
	}

      @Override
    public Map<Integer, Integer> giveupQuestGoodsMap() {
        return null ;
    }

	@Override
	public List<GoodsOperateBean> getQuestFall(RoleInstance role, String key) {
		return null;
	}

	@Override
	public int getCurrentNum(RoleInstance role,int index){
		RoleQuestLogInfo logInfo = role.getQuestLogInfo(this.master.getQuestId());
		if(null == logInfo){
			return 0 ;
		}
		switch(index){
		case 0:
			return logInfo.getData1();
		case 1:
			return logInfo.getData2();
		case 2:
			return logInfo.getData3();
		default:
			return 0;
		}
	}
	
	protected void incrCurrentNum(RoleInstance role, int index){
		try {
			context.getUserQuestApp().updateQuestLog(role, master.getQuestId(), index, OperatorType.Add, 1);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public List<QuestTerm> termList() {
		return questTermList ;
	}
	
	@Override
	public boolean isPhaseComplete(RoleInstance role) {
		try {
			return QuestHelper.isPhaseComplete(role, master, this, questTermList);
		} catch (ServiceException e) {
			logger.error("",e);
		}
		return false ;
	}

	public void setStartIndex(int startIndex){
		this.startIndex = startIndex;
	}

	@Override
	public void giveUp(RoleInstance role) {
		
	}

	@Override
	public int getAttribute(RoleInstance role, int type) {
		return 0;
	}

	@Override
	public int killRole(RoleInstance role) {
		return 0;
	}

	@Override
	public int chooseMenu(RoleInstance role, int menuId) {
		return 0;
	}

	@Override
	public int killMonsterLimit(RoleInstance role, String npcId) {
		return 0;
	}

	@Override
	public Point getEventPoint(RoleInstance role) {
		return null;
	}

	@Override
	public int copyMapPass(RoleInstance role, String mapId) {
		return 0;
	}

	@Override
	public int mapRefreshNpc(RoleInstance role, int refreshIndex) {
		return 0;
	}

	@Override
	public int copyPass(RoleInstance role, short copyId) {
		return 0;
	}
	
}
