package com.game.draco.app.skill.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.vo.AttributeOperateBean;

import com.game.draco.GameContext;

@Data
public class SkillLearnConfig {
	
	private short skillId;//技能ID 
	private int level;//等级 
	private int roleLevel;//角色等级
	private int innerLevel;//内部等级
	private int gold;//元宝
	private int bindGold;//绑元
	private int gameMoney;//游戏币
	private int zp;//真气
	private byte attrType;//消耗属性类型
	private int attrValue;//消耗属性值
	private int goodsId;//消耗物品ID
	private short goodsNum;//消耗物品数量
	private short relySkillId;//技能依赖
	private byte relySkillLevel;//依赖技能级别
	private byte relyAttrType;//依赖属性类型
	private int relyAttrValue;//依赖属性值
	
	private SkillSourceType skillSourceType;
	private List<AttributeOperateBean> consumeAttributeList = new ArrayList<AttributeOperateBean>();
	private AttributeType relyAttributeType;//依赖属性类型
	
	public void checkInit(String fileInfo){
		String info = fileInfo + "skillId = " + this.skillId + ", level = " + this.level + ". ";;
		if(this.gold > 0){
			this.consumeAttributeList.add(new AttributeOperateBean(AttributeType.goldMoney, this.gold));
		}
		if(this.bindGold > 0){
			this.consumeAttributeList.add(new AttributeOperateBean(AttributeType.bindingGoldMoney, this.bindGold));
		}
		if(this.gameMoney > 0){
			this.consumeAttributeList.add(new AttributeOperateBean(AttributeType.silverMoney, this.gameMoney));
		}
		if(this.zp > 0){
			this.consumeAttributeList.add(new AttributeOperateBean(AttributeType.potential, this.zp));
		}
		if(this.attrType > 0){
			AttributeType consAttr = AttributeType.get(this.attrType);
			if(null == consAttr){
				this.checkFail(info + "attrType error, it's not exist.");
			}
			if(consAttr.isMoney() || AttributeType.potential == consAttr){
				this.checkFail(info + "attrType error, it's money or zp.");
			}
			if(this.attrValue <= 0){
				this.checkFail(info + "attrValue error, it must be greater than zero.");
			}
			this.consumeAttributeList.add(new AttributeOperateBean(consAttr, this.attrValue));
		}
		if(this.goodsId > 0){
			if(null == GameContext.getGoodsApp().getGoodsBase(this.goodsId)){
				this.checkFail(info + "goodsId error, it's not exist.");
			}
			if(this.goodsNum <= 0){
				this.checkFail(info + "goodsNum error, it must be greater than zero.");
			}
		}
		if(this.relyAttrType > 0){
			this.relyAttributeType = AttributeType.get(this.relyAttrType);
			if(null == relyAttributeType){
				this.checkFail(info + "relyAttrType error, it's not exist.");
			}
			if(relyAttributeType.isMoney() || AttributeType.potential == relyAttributeType){
				this.checkFail(info + "relyAttrType error, it's money or zp.");
			}
			if(this.relyAttrValue <= 0){
				this.checkFail(info + "relyAttrValue error, it must be greater than zero.");
			}
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
