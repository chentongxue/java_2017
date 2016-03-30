package com.game.draco.app.copy.config;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import sacred.alliance.magic.base.GoodsUseType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.copy.vo.CopyCountType;
import com.game.draco.app.copy.vo.CopyEnterType;
import com.game.draco.app.copy.vo.CopyPassJumpType;
import com.game.draco.app.copy.vo.CopyShowType;
import com.game.draco.app.copy.vo.CopySignType;
import com.game.draco.app.copy.vo.CopyType;
import com.game.draco.app.quest.base.QuestStatus;
import com.game.draco.app.quest.domain.RoleQuestLogInfo;

public @Data class CopyConfig {
	public static final byte ENTER_INCR = 0;// 进入时扣除次数
	public static final byte PASS_INCR = 1;// 通关时扣除次数
	
	private short copyId;
	private byte type;
	private String copyName;
	private int imageId;// 副本面板图片
	private byte raids;// 可否扫荡
	private byte incrType;// 0. 进入时扣次数 1.通关时扣次数
	private byte canBuyNum;// 是否可购买次数
	private int lootId;// 掉落索引
	private byte signType;//标记类型
	private byte difficulty;
	private int minLevel;//最低等级
	private int maxLevel;//最高等级 0表示没有上限
	private int minEnterCount;
	private int maxEnterCount;
	private byte countType;//次数类型[0=每天,1=每周]
	private int count;//副本次数 -1表示不计次数
	private int power;//消耗的体力值
	private int totalTime;//通关时间限制(秒)
	private String enterMapId;
	private int mapX;
	private int mapY;
	private String content;
	private int needGoodsId;//所需物品ID
	private int needGoodsNum;//所需物品数量
	private byte needGoodsWay;//所需物品方式
	private String needQuestIds;
	private int needTitleId;
	private int needVipLvl;
	private int needFactionLvl;
	private byte showSign;//在副本列表中显示方式 0：一直显示 1：拥有任务时才显示
	private String passTips;//副本通关提示信息
	private byte passJumpType;//单人副本通关后刷跳转点的类型 0：刷进入副本前的坐标  1：刷配置的跳转点-目标地图
	private int intimate;//组队副本亲密度奖励
	private byte reset;//角色死亡是否重置 1重置 0不重置
	private int prestigePoint;// 通关可获得威望
	
	/** 入口寻路点，服务器启动时构建 **/
	private Point piont;
	private CopyType copyType;
	private CopySignType copySignType;
	private CopyEnterType copyEnterType;
	private Set<Integer> needQuestSet = null;//所需任务的集合，只要身上正在做其中的任意一个都行
	private CopyShowType copyShowType;
	private CopyPassJumpType copyPassJumpType = CopyPassJumpType.Enter_Point;//默认是副本进入点
	private GoodsUseType goodsUseType;
	private CopyCountType copyCountType;
	
	/**
	 * 验证并初始化信息
	 * @param fileInfo 文件信息
	 */
	public void checkAndInit(String fileInfo){
		String info = fileInfo + "copyId=" + this.copyId + ",";
		this.copyType = CopyType.get(this.type);
		if(null == this.copyType){
			this.checkFail(info + ",type=" + this.type + ",the copyType is no support..");
		}
		this.copyCountType = CopyCountType.getCopyType(this.countType);
		if(null == this.copyCountType){
			this.checkFail(info + ",countType=" + this.countType + ",the countType is not exist!");
		}
		this.copySignType = CopySignType.getCopyType(this.signType);
		if(null == this.copySignType){
			this.checkFail(info + ",signType=" + this.signType + ",the signType is no exist!");
		}
		this.copyShowType = CopyShowType.getCopyType(this.showSign);
		if(null == this.copyShowType){
			this.checkFail(info + ",showSign=" + this.showSign + ",the showSign config error!");
		}
		//需要任务条件才显示，但没配置任务条件
		if(CopyShowType.Have_Quest == this.copyShowType && Util.isEmpty(this.needQuestIds)){
			this.checkFail(info + "showSign=" + this.showSign + ",it has showSign but not config needQuestIds.");
		}
		if(this.needGoodsId > 0){
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(needGoodsId);
			if(null == goodsBase){
				this.checkFail(info + "needGoodsId," + this.needGoodsId + ", goods is no exist.");
			}
			if(this.needGoodsNum <= 0){
				this.checkFail(info + "needGoodsNum = " + this.needGoodsNum + ", config error!");
			}
			this.goodsUseType = GoodsUseType.get(this.needGoodsWay);
			if(null == this.goodsUseType){
				this.checkFail(info + "needGoodsWay = " + this.needGoodsWay + ", it is not support!");
			}
		}
		if(0 == this.count){
			//-1表示不计次数 
			this.checkFail(info + "count=" + this.count + ",count is not config.");
		}
		if(this.intimate < 0){
			this.checkFail(info + "intimate=" + this.intimate + "intimate config error.");
		}
		if(this.power < 0){
			this.checkFail(info + "minutePower=" + this.power + ",minutePower config error.");
		}
		this.copyPassJumpType = CopyPassJumpType.getCopyType(this.passJumpType);
		try {
			//如果需要任务条件
			if(!Util.isEmpty(this.needQuestIds)){
				this.needQuestSet = new HashSet<Integer>();
				String[] questIds = this.needQuestIds.split(Cat.comma);
				for(String id : questIds){
					this.needQuestSet.add(Integer.valueOf(id));
				}
			}
		} catch (Exception e) {
			this.checkFail(info + "needQuestIds=" + this.needQuestIds + ",it's config error.");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	public String enterRoleCount(){
		return GameContext.getI18n().messageFormat(TextId.Copy_Condition_Role, this.minEnterCount, this.maxEnterCount);
	}
	
	/** 副本进入条件判断 **/
	public Result enterCondition(RoleInstance role){
		Result res = this.showCondition(role);
		if(!res.isSuccess()){
			return res;
		}
		Result result = new Result();
		//判断物品
		if(this.needGoodsId > 0){
			int ownNum = role.getRoleBackpack().countByGoodsId(this.needGoodsId);
			if(ownNum < this.needGoodsNum){
				String goodsName = "";
				GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(this.needGoodsId);
				if(null != gb){
					goodsName = gb.getColorName();
				}
				String info = GameContext.getI18n().messageFormat(TextId.Copy_Enter_No_Goods, this.needGoodsNum, goodsName);
				return result.setInfo(info);
			}
		}
		return result.success();
	}
	
	/**
	 * 副本进入条件
	 * NPC功能、副本面板不需要判断物品
	 * @param role
	 * @return
	 */
	public Result showCondition(RoleInstance role) {
		Result result = new Result();
		// 等级
		int roleLevel = role.getLevel();
		if (roleLevel < this.minLevel || roleLevel > this.maxLevel) {
			return result.setInfo(GameContext.getI18n().getText(TextId.Copy_Enter_No_Lvl));
		}
		if (!Util.isEmpty(this.needQuestSet)) {
			boolean haveQuest = false;
			for (int questId : this.needQuestSet) {
				RoleQuestLogInfo questInfo = role.getQuestLogInfo(questId);
				if (null != questInfo && questInfo.getStatus() == QuestStatus.notComplete.getType()) {
					haveQuest = true;
					break;
				}
			}
			if (!haveQuest) {
				return result.setInfo(GameContext.getI18n().getText(TextId.Copy_Enter_No_Quest));
			}
		}
		if (needTitleId > 0) {
			boolean hadTitle = GameContext.getTitleApp().isExistEffectiveTitle(role, needTitleId);
			if (!hadTitle) {
				return result.setInfo(GameContext.getI18n().getText(TextId.Copy_Enter_No_Title));
			}
		}
		if (needVipLvl > 0) {
			if (needVipLvl > GameContext.getVipApp().getVipLevel(role)) {
				return result.setInfo(GameContext.getI18n().getText(TextId.Copy_Enter_No_VipLvl));
			}
		}
		if (needFactionLvl > 0) {
			if (role.getUnionLevel() <= 0) {
				return result.setInfo(GameContext.getI18n().getText(TextId.UNION_INSTANCE_NO));
			}
			if (needFactionLvl > role.getUnionLevel()) {
				return result.setInfo(GameContext.getI18n().messageFormat(TextId.UNION_INSTANCE_NO_OPEN, this.getCopyName(), needFactionLvl));
			}
		}
		return result.success();
	}
	
	/** 最大人数限制 **/
	public boolean roleCountIsFull(int roleCount){
		if(roleCount <= maxEnterCount){
			return false;
		}
		return true;
	}
	
	/** 副本人数限制 **/
	public boolean roleCountLimit(int roleCount){
		if(roleCount >= this.minEnterCount && roleCount <= this.maxEnterCount){
			return true;
		}
		return false;
	}
	
	/**
	 * 能否在每日副本列表中显示
	 * @return
	 */
	public boolean canShow(RoleInstance role){
		if(CopyShowType.Display == this.copyShowType){
			return true;
		}
		switch(this.copyShowType){
		case Display:
			return true;
		case Not_Display:
			return false;
		case Have_Quest:
			if(Util.isEmpty(this.needQuestSet)){
				return true;
			}
			//需要拥有任务才显示的
			boolean haveQuest = false;
			for(int questId : this.needQuestSet){
				if(role.hasReceiveQuestNow(questId)){
					haveQuest = true;
					break;
				}
			}
			return haveQuest;
		default:
			return true;
		}
	}
	
	/**
	 * 是否可扫荡
	 * @return
	 */
	public boolean showRaids() {
		return 1 == this.raids;
	}
	
	/**
	 * 是否可购买次数
	 * @return
	 */
	public boolean showBuy() {
		return 1 == this.canBuyNum;
	}
	
}
