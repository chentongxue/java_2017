package com.game.draco.app.hero.arena.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AttributeOperateBean;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.vip.type.VipPrivilegeType;
import com.game.draco.message.item.AttriTypeValueItem;

@Data
public class HeroArenaRewardConfig {
	
	private int gateId;//关卡ID
	private int minLevel;//等级下限
	private int maxLevel;//等级上限
	private int exp;//经验
	private int silver;//金币
	private int potential;//潜能
	private int heroCoin;//徽章
	private String groupId;//奖励组
	private String randInt;//奖励数量
	private int mustGroupId;//必出奖励组
	private String mailTitle;//邮件标题
	private String mailContent;//邮件内容
	private static final float BASE_RATIO = 10000f;
	
	private List<AttributeOperateBean> attrList = new ArrayList<AttributeOperateBean>();
	
	public void checkInit(String fileInfo){
		String info = fileInfo + "gateId=" + this.gateId + ",";
		if(this.gateId <= 0){
			this.checkFail(info + "gateId is error.");
		}
		if(this.minLevel < 0){
			this.checkFail(info + "minLevel is error.");
		}
		if(this.maxLevel < 0){
			this.checkFail(info + "maxLevel is error.");
		}
		if(this.exp > 0){
			this.attrList.add(new AttributeOperateBean(AttributeType.exp, this.exp));
		}
		if(this.silver > 0){
			this.attrList.add(new AttributeOperateBean(AttributeType.gameMoney, this.silver));
		}
		if(this.potential > 0){
			this.attrList.add(new AttributeOperateBean(AttributeType.potential, this.potential));
		}
		if(this.heroCoin > 0){
			this.attrList.add(new AttributeOperateBean(AttributeType.heroCoin, this.heroCoin));
		}
		if(Util.isEmpty(this.mailTitle)){
			this.checkFail(info + "mailTile is empty.");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	public boolean isSuitLevel(RoleInstance role){
		if(null == role){
			return false;
		}
		int roleLevel = role.getLevel();
		return roleLevel >= this.minLevel && roleLevel <= this.maxLevel;
	}
	
	public List<AttriTypeValueItem> buildAttrAwardList(String roleId){
		int rewardRate = GameContext.getVipApp().getVipPrivilegeTimes(roleId, VipPrivilegeType.HERO_ARENA_REWARD_INCR.getType(),"");
		float rate = 0;
		if(rewardRate !=0){
			rate = rewardRate/BASE_RATIO;
		}
		List<AttriTypeValueItem> awardAttrList = new ArrayList<AttriTypeValueItem>();
		for(AttributeOperateBean bean : this.attrList){
			if(null == bean){
				continue;
			}
			AttriTypeValueItem item = new AttriTypeValueItem();
			item.setAttriType(bean.getAttrType().getType());
			item.setAttriValue(bean.getValue() + (int)(bean.getValue()*rate));
			awardAttrList.add(item);
		}
		return awardAttrList;
	}
	
}
