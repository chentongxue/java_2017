package com.game.draco.app.skill.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.vo.AttributeOperateBean;

@Data
public class SkillLearnConfig {
	
	private short skillId;//技能ID 
	private int level;//等级 
	private int gameMoney;//游戏币
	private int potential;//潜能
	
	private List<AttributeOperateBean> consumeAttributeList = new ArrayList<AttributeOperateBean>();
	
	public void checkInit(String fileInfo){
		if(this.gameMoney > 0){
			this.consumeAttributeList.add(new AttributeOperateBean(AttributeType.gameMoney, this.gameMoney));
		}
		if(this.potential > 0){
			this.consumeAttributeList.add(new AttributeOperateBean(AttributeType.potential, this.potential));
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
