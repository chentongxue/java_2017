package com.game.draco.app.npc.transfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.GameContext;
import com.game.draco.app.npc.type.NpcTransferQuestLimitType;
import com.game.draco.app.quest.Quest;

import lombok.Data;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.GoodsUseType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.StringUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class NpcTransfer {
	
	private int id;
	private String npcId;
	private String title;
	private String mapId;
	private int x;
	private int y;
	private int silver;
	private int camp = -1 ;
	private short activeId;//活动ID
	private int minLevel;
	private int maxLevel;
	private String withoutQuestIds;//没有接过的任务
	private String doingQuestIds;//正在做的任务
	private String completeQuestIds;//曾经完成过的任务
	private String openTime;
	private int askTitleId;//需要称号ID
	private String day;//星期几的限制
	private byte vipLevel ;
	private int needGoodsId;//所需物品ID
	private int needGoodsNum;//所需物品数量
	private byte needGoodsWay;//所需物品方式
	
	private GoodsUseType goodsUseType;
	private Map<NpcTransferQuestLimitType,List<Integer>> questsMap = new HashMap<NpcTransferQuestLimitType,List<Integer>>();
	
	/** 初始化数据 */
	public void init(String fileInfo){
		String info = fileInfo + "id=" + this.id + ",";
		if(this.activeId > 0){
			Active active = GameContext.getActiveApp().getActive(this.activeId);
			if(null == active){
				this.checkFail(info + "activeId=" + this.activeId + ",the active is not exist!");
			}
		}
		if(this.askTitleId > 0){
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(this.askTitleId);
			if(null == gb){
				this.checkFail(info + "askTitleId=" + this.askTitleId + ",the goods title is not exist!");
			}
		}
		if(this.needGoodsId > 0){
			GoodsBase goods = GameContext.getGoodsApp().getGoodsBase(this.needGoodsId);
			if(null == goods){
				this.checkFail(info + "needGoodsId = " + this.needGoodsId + ", the goods is not exist!");
			}
			if(this.needGoodsNum <= 0){
				this.checkFail(info + "needGoodsNum = " + this.needGoodsNum + ", config error!");
			}
			this.goodsUseType = GoodsUseType.get(this.needGoodsWay);
			if(null == this.goodsUseType){
				this.checkFail(info + "needGoodsWay = " + this.needGoodsWay + ", it is not support!");
			}
		}
		this.addToMap(this.withoutQuestIds, NpcTransferQuestLimitType.NotAccept, info);
		this.addToMap(this.doingQuestIds, NpcTransferQuestLimitType.NotComplete, info);
		this.addToMap(this.completeQuestIds, NpcTransferQuestLimitType.Completed, info);
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	private void addToMap(String quests, NpcTransferQuestLimitType type, String info){
		if(StringUtil.nullOrEmpty(quests)){
			return;
		}
		String[] questArr = quests.split(Cat.comma);
		for(String id : questArr){
			int questId = Integer.valueOf(id);
			if(questId < 0){
				continue;
			}
			Quest quest = GameContext.getQuestApp().getQuest(questId);
			if(null == quest){
				this.checkFail(info);
			}
			if(!this.questsMap.containsKey(type)){
				this.questsMap.put(type, new ArrayList<Integer>());
			}
			this.questsMap.get(type).add(questId);
		}
	}
	
	/**
	 * 星期几限制
	 * 有活动条件时，星期条件被忽略
	 */
	private boolean isTodayCanEnter(){
		if(this.activeId > 0){
			return true;
		}
		if(Util.isEmpty(day)){
			return true;
		}
		String today = String.valueOf(DateUtil.getWeek());
		if(day.indexOf(today) != -1){
			return true;
		}
		return false;
	}
	
	/**
	 * 是否满足职业
	 * */
	private boolean isSuitCamp(RoleInstance role){
		if(this.camp < 0){
			return true ;
		}
		return this.camp == role.getCampId() ;
	} 
	
	/**
	 * 是否满足等级
	 * 有活动条件时，等级条件被忽略
	 * @param role
	 * @return
	 */
	private boolean isSuitLevel(RoleInstance role){
		if(this.activeId > 0){
			return true;
		}
		int level = role.getLevel();
		boolean res = true;
		if(this.minLevel > 0){
			res &= level >= this.minLevel;
		}
		if(this.maxLevel > 0){
			res &= level <= this.maxLevel;
		}
		return res;
	}
	
	private boolean isSuitVipLevel(RoleInstance role){
		if(this.vipLevel <=0){
			return true ;
		}
		return GameContext.getVipApp().getVipLevel(role) >= this.vipLevel ;
	}
	
	/**
	 * 是否满足任务限制
	 * */
	private boolean isSuitQuest(RoleInstance role){
		if(Util.isEmpty(this.questsMap)){
			return true;
		}
		boolean suitQuest = true;//不同任务阶段如果都配置，要求必须都满足
		for(NpcTransferQuestLimitType type : this.questsMap.keySet()){
			if(null == type){
				continue;
			}
			boolean eachCondition = false;//多个任务满足一个即可
			for(int questId : this.questsMap.get(type)){
				if(questId <= 0){
					continue;
				}
				if(eachCondition){
					continue;
				}
				if(NpcTransferQuestLimitType.NotAccept == type){
					eachCondition = !role.hasReceiveQuestNow(questId) && !role.hasFinishQuest(questId);
					continue;
				}
				if(NpcTransferQuestLimitType.NotComplete == type){
					eachCondition = role.hasReceiveQuestNow(questId);
					continue;
				}
				if(NpcTransferQuestLimitType.Completed == type){
					eachCondition = role.hasFinishQuest(questId);
				}
			}
			suitQuest = suitQuest && eachCondition;
		}
		return suitQuest;
	}
	
	/**
	 * 是否在开放时间
	 * 有活动条件时，开放时间条件被忽略
	 * */
	private boolean isSuitTime(RoleInstance role){
		if(this.activeId > 0){
			return true;
		}
		if(StringUtil.nullOrEmpty(openTime)){
			return true;
		}
		return DateUtil.inOpenTime(new Date(), openTime);
	}
	
	private boolean isSuitActive(RoleInstance role){
		if(this.activeId <= 0){
			return true;
		}
		Active active = GameContext.getActiveApp().getActive(activeId);
		if(null == active){
			return false;
		}
		return active.isSuitLevel(role) && active.isTimeOpen();
	}
	
	/**
	 * 是否满足所有条件
	 * @param role
	 * @return
	 */
	public boolean suitAllCondition(RoleInstance role){
		return this.isSuitLevel(role)
					&& this.isSuitTime(role)
					&& this.isTodayCanEnter()
					&& this.isSuitQuest(role)
					&& this.isSuitCamp(role)
					&& this.isSuitVipLevel(role)
					&& this.isSuitActive(role);
	}
	
	
}
