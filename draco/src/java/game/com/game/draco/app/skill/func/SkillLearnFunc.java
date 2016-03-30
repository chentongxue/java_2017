package com.game.draco.app.skill.func;

import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.vo.AttributeOperateBean;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.skill.config.SkillApplyType;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.app.target.cond.TargetCondType;
import com.game.draco.message.response.C0316_SkillStudyRespMessage;
import com.game.draco.message.response.C0319_SkillDeleteRespMessage;
import com.game.draco.message.response.C0321_SkillLevelupInfoRespMessage;

public abstract class SkillLearnFunc {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected SkillSourceType skillSourceType;
	
	public SkillLearnFunc(SkillSourceType skillSourceType){
		this.skillSourceType = skillSourceType;
	}
	
	public SkillSourceType getSkillSourceType() {
		return skillSourceType;
	}
	
	public abstract List<Short> getSkillList(RoleInstance role, String parameter);
	
	public abstract boolean hasLearnSkill(RoleInstance role, short skillId, String parameter);
	
	public abstract int getSkillLevel(RoleInstance role, short skillId, String parameter);
	
	public int getAstaff(RoleInstance role,String parameter){
		return 0 ;
	}
	
	public abstract long getLastProcessTime(RoleInstance role, short skillId, String parameter);
	
	protected abstract void saveSkill(RoleInstance role, String parameter, short skillId, int level);
	
	protected abstract void deleteSkill(RoleInstance role, String parameter, short skillId);
	
	public abstract Result verifyInnerLevel(RoleInstance role, String parameter, int innerLevel);
	
	public Result learnSkill(RoleInstance role, short skillId, String parameter){
		Result result = new Result();
		try {
			Skill skill = GameContext.getSkillApp().getSkill(skillId);
			if(null == skill){
				return result.setInfo(Status.Skill_Not_Exist.getTips());
			}
			if(!skill.isCanLearnFromSystem()){
				return result.setInfo(Status.Skill_CanLearn_Fail.getTips());
			}
			int skillLevel = this.getSkillLevel(role, skillId, parameter);
			if(0 >= skillLevel){
				result = this.addSkill(role, skill, parameter);
			}else{
				result = this.updateSkill(role, skill, parameter);
			}
			if(result.isSuccess()){
				GameContext.getTargetApp().updateTarget(role, TargetCondType.RoleHeroSkillLevel);
				//同步战斗力
				role.syncBattleScore();
				role.getBehavior().notifyAttribute();
			}
			return result ;
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + "learnSkill error: ", e);
			return result.setInfo(Status.Sys_Error.getTips());
		}
	}
	
	public Result removeSkill(RoleInstance role, String parameter, short skillId){
		Result result = new Result();
		try{
			if(!this.hasLearnSkill(role, skillId, parameter)){
				return result.setInfo(Status.Skill_NotOwn_Upgrade.getTips());
			}
			Skill skill = GameContext.getSkillApp().getSkill(skillId);
			if(null == skill){
				return result.setInfo(Status.Skill_Not_Exist.getTips());
			}
			AttriBuffer preBuffer = skill.getAttriBuffer(role);
			this.deleteSkill(role, parameter, skillId);
			//通知客户端
			C0319_SkillDeleteRespMessage resp = new C0319_SkillDeleteRespMessage();
			resp.setSkillId(skillId);
			role.getBehavior().sendMessage(resp);
			skill.skillLevelChanged(role, preBuffer);
			return result.success();
		}catch(Exception e) {
			this.logger.error(this.getClass().getName() + "removeSkill error: ", e);
			return result.setInfo(Status.Sys_Error.getTips());
		}
	}
	
	public Result addSkill(RoleInstance role, Skill skill, String parameter){
		Result result = new Result();
		try {
			short skillId = skill.getSkillId();
			if(this.hasLearnSkill(role, skillId, parameter)){
				return result.setInfo(Status.Skill_Own_Learn.getTips());
			}
			int level = 1;
			SkillDetail detail = skill.getSkillDetail(level);
			Result res = this.canLearn(role, parameter, detail);
			if(res.isIgnore()){
				return res;
			}
			if(!res.isSuccess()){
				return res;
			}
			//扣除消耗
			this.reduceLearnConsume(role, skill, level);
			//保存技能
			this.saveSkill(role, parameter, skillId, level);
			//通知技能变化消息
			this.sendSkillUpdateMessage(role, parameter, skill, level, null);
			return result.success();
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + "addSkill error: ", e);
			return result.setInfo(Status.Sys_Error.getTips());
		}
	}
	
	protected Result updateSkill(RoleInstance role, Skill skill, String parameter){
		Result result = new Result();
		try {
			short skillId = skill.getSkillId();
			int oldLevel = this.getSkillLevel(role, skillId, parameter);
			if(oldLevel <= 0){
				return result.setInfo(Status.Skill_NotOwn_Upgrade.getTips());
			}
			int level = oldLevel + 1;
			SkillDetail detail = skill.getSkillDetail(level);
			if(null == detail){
				return result.setInfo(Status.Skill_MaxLevel_Fail.getTips());
			}
			Result res = this.canLearn(role, parameter, detail);
			if(res.isIgnore()){
				return res;
			}
			if(!res.isSuccess()){
				return res;
			}
			//扣除消耗
			this.reduceLearnConsume(role, skill, level);
			AttriBuffer preBuffer = skill.getAttriBuffer(role);//技能变化前影响的属性
			//保存技能
			this.saveSkill(role, parameter, skillId, level);
			//通知技能变化消息
			this.sendSkillUpdateMessage(role, parameter, skill, level, preBuffer);
			return result.success();
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + "updateSkill error: ", e);
			return result.setInfo(Status.Sys_Error.getTips());
		}
	}
	
	private void reduceLearnConsume(RoleInstance role, Skill skill, int skillLevel){
		try {
			SkillDetail detail = skill.getSkillDetail(skillLevel);
			if(null == detail){
				return ;
			}
//			//扣除道具
//			int goodsId = detail.getConsumeGoodsId();
//			if(goodsId > 0){
//				GameContext.getUserGoodsApp().deleteForBag(role, goodsId, detail.getConsumeGoodsNum(), OutputConsumeType.player_skill_upgrade);
//			}
			//扣除属性消耗
			boolean change = false ;
			for(AttributeOperateBean bean : detail.getConsumeAttributeList()){
				if(null == bean){
					continue;
				}
				GameContext.getUserAttributeApp().changeRoleMoney(role, bean.getAttrType(), OperatorType.Decrease, bean.getValue(), OutputConsumeType.player_skill_upgrade);
				change = true ;
			}
			if(change){
				role.getBehavior().notifyAttribute();
			}
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + "reduceLearnConsume error: ", e);
		}
	}
	
	/** 技能更新通知消息 */
	private void sendSkillUpdateMessage(RoleInstance role, String parameter, Skill skill, int level, AttriBuffer preBuffer){
		try {
			C0316_SkillStudyRespMessage respMsg = new C0316_SkillStudyRespMessage();
			respMsg.setSourceType(this.skillSourceType.getType());
			respMsg.setParameter(parameter);
			short skillId = skill.getSkillId();
			respMsg.setSkillId(skillId);
			respMsg.setSkillLevel((byte) level);
			SkillDetail detail = skill.getSkillDetail(level);
			if(null != detail){
				respMsg.setRefreshAttrList(detail.getRefreshAttrTypeValueList(role));
			}
			SkillDetail nextDetail = skill.getSkillDetail(level + 1);
			if(null != nextDetail){
//				respMsg.setRoleLevel((byte) nextDetail.getRoleLevel());
//				short relySkillId = nextDetail.getRelySkillId();
//				respMsg.setReplySkillId(relySkillId);
//				respMsg.setReplySkillLevel(nextDetail.getRelySkillLevel());
//				Skill relySkill = GameContext.getSkillApp().getSkill(relySkillId);
//				if(null != relySkill){
//					respMsg.setRelySkillName(relySkill.getName());
//				}
//				GoodsLiteNamedItem goodsLiteNamedItem = nextDetail.getConsumeGoodsLiteNamedItem();
//				if(null != goodsLiteNamedItem){
//					respMsg.setGoodsLiteNamedItem(goodsLiteNamedItem);
//				}
				respMsg.setExpendAttrList(nextDetail.getConsumeAttrTypeValueList());
			}
			role.getBehavior().sendMessage(respMsg);
			if (SkillApplyType.active.getType() == skill.getSkillApplyType().getType()
					&& this.isSendActiveSkillLevelup(role, parameter, skill)) {
				// 主动技能
				C0321_SkillLevelupInfoRespMessage notifyMsg = new C0321_SkillLevelupInfoRespMessage();
				notifyMsg.setParameter(parameter);
				notifyMsg.setSourceType(skill.getSkillSourceType().getType());
				long lastProcessTime = this.getLastProcessTime(role, skillId, parameter);
				notifyMsg.setSkillItem(Converter.getRoleSkillItem(role, skill,level,lastProcessTime));
				role.getBehavior().sendMessage(notifyMsg);
			}
			skill.skillLevelChanged(role, preBuffer);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + "sendSkillUpdateMessage error: ", e);
		}
	}
	
	/**
	 * 主动技能升级后是否需要通知客户端新信息
	 * @param role
	 * @param parameter
	 * @param skill
	 * @return
	 */
	protected boolean isSendActiveSkillLevelup(RoleInstance role, String parameter, Skill skill){
		return false ;
	}
	
	protected Result canLearn(RoleInstance role, String parameter, SkillDetail detail){
		Result result = new Result();
		if(role.getLevel() < detail.getLevel()){
			return result.setInfo(Status.Skill_RoleLevel_Fail.getTips());
		}
		Result verifyRes = this.verifyInnerLevel(role, parameter, detail.getLevel());
		if(!verifyRes.isSuccess()){
			return verifyRes;
		}
//		//依赖技能
//		short relySkillId = detail.getRelySkillId();
//		Skill relySkill = GameContext.getSkillApp().getSkill(relySkillId);
//		if(null != relySkill){
//			if(!this.hasLearnSkill(role, relySkillId, parameter)){
//				return result.setInfo(Status.Skill_RelySkill_NotOwn.getTips());
//			}
//			int learnedRelyLevel = this.getSkillLevel(role, relySkillId, parameter);
//			if(detail.getRelySkillLevel() > learnedRelyLevel){
//				return result.setInfo(Status.Skill_RelySkill_Level.getTips());
//			}
//		}
		//判断消耗属性
		for(AttributeOperateBean bean : detail.getConsumeAttributeList()){
			if(null == bean){
				continue;
			}
			AttributeType attrType = bean.getAttrType();
			if(null == attrType){
				continue;
			}
			//【游戏币/潜能/钻石不足弹板】 判断
			Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, attrType, bean.getValue());
			if(ar.isIgnore()){//弹板
				return ar;
			}
			if(!ar.isSuccess()){//不足
				String info = MessageFormat.format(this.getText(TextId.Skill_Learn_Attr_Not_Enough), attrType.getName());
				return result.setInfo(info);
			}
//			if(role.get(attrType) < bean.getValue()){
//				String info = MessageFormat.format(this.getText(TextId.Skill_Learn_Attr_Not_Enough), attrType.getName());
//				return result.setInfo(info);
//			}
		}
		//判断依赖属性
//		AttributeType relyAttrType = detail.getRelyAttrType();
//		if(null != relyAttrType){
//			if(role.get(relyAttrType) < detail.getRelyAttrValue()){
//				String info = MessageFormat.format(this.getText(TextId.Skill_Learn_Rely_Attr_Not_Enough), relyAttrType.getName(), detail.getRelyAttrValue());
//				return result.setInfo(info);
//			}
//		}
//		//判断消耗物品
//		if(detail.getConsumeGoodsId() > 0){
//			int num = role.getRoleBackpack().countByGoodsId(detail.getConsumeGoodsId());
//			if(num < detail.getConsumeGoodsNum()){
//				return result.setInfo(this.getText(TextId.Skill_Learn_Goods_Not_Enough));
//			}
//		}
		return result.success();
	}
	
	protected String getText(String i18nKey){
		return GameContext.getI18n().getText(i18nKey);
	}
	
	public void innerAddSkill(RoleInstance role, String parameter, short skillId, int level){
		//内部添加技能接口，如无特殊需要，无须重写
		//坐骑系统需要增加指定等级的技能，而且不需要跟客户端交互
	}
	
}
