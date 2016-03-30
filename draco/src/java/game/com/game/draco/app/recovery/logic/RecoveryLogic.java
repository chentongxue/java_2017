package com.game.draco.app.recovery.logic;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.recovery.config.RecoveryConfig;
import com.game.draco.app.recovery.config.RecoveryConsumeConfig;
import com.game.draco.app.recovery.config.RecoveryOutPutConfig;
import com.game.draco.app.recovery.domain.RoleRecovery;
import com.game.draco.app.recovery.type.RecoveryConsumeType;
import com.game.draco.app.recovery.vo.RecoveryResult;
import com.game.draco.app.union.FunType;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.RecoveryConsumeItem;
import com.game.draco.message.item.RecoveryShowItem;
import com.google.common.collect.Lists;

public abstract class RecoveryLogic implements IRecoveryLogic {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public List<RecoveryConsumeItem> getRecoveryConsumeItemList(final RoleRecovery recovery,  byte vipLevel){
		List<RecoveryConsumeItem> list = Lists.newArrayList();
		if(recovery.getRecoveryNum() <= 0){
			return list;
		}
		//消耗
		Collection<RecoveryConsumeConfig> consumeCfs = GameContext.getRecoveryApp().getConsumeConfigsByRecoveryId(recovery.getRecoveryId());
		if(Util.isEmpty(consumeCfs)){
			return list;
		}
		for (RecoveryConsumeConfig consumeCf : consumeCfs) {
			if(!consumeCf.meetCondition(vipLevel)){//VIP验证
				continue;
			}
			RecoveryConsumeItem consumeItem = buildRecoveryConsumeItem(consumeCf, recovery);
			if(consumeItem != null){
				list.add(consumeItem);
			}
		}
		return list;
	}
	/**
	 * 离线经验获追回资源覆盖此方法
	 */
	public RecoveryResult recoveryAwardAndConsume(RoleInstance role, String id, byte consumeType, int num){
		RecoveryResult result = new RecoveryResult();
		RoleRecovery rc = GameContext.getRecoveryApp().getRoleRecovery(role, id);
		if(rc == null){
			return result.failure();
		}
		if(!canRecovery(rc)){
			result.setInfo(getText(TextId.RECOVERY_COUNT_EMPTY));
			return result.failure();
		}
		//获得角昨天的角色等级
		byte vipLevel = GameContext.getVipApp().getVipLevel(role);
		//获得消耗的配置
		RecoveryConsumeConfig  consumeConfig = GameContext.getRecoveryApp().getRoleRecoveryConsumeConfig(id, consumeType ,vipLevel);
		if(consumeConfig == null){
			result.setInfo(getText(TextId.RECOVERY_CONSUME_PARAM_ERROR));
			return result.failure();
		}
		int consume = consumeConfig.getValue() * num;
		if(consumeType != RecoveryConsumeType.RECOVERY_CONSUME_FREE.getType()){//免费
			//获得消耗类型的属性
			AttributeType attr = RecoveryConsumeType.getType(consumeType).getAttributeType();
			//【游戏币/潜能/钻石不足弹板】 判断
			Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, attr, consume);
			if(ar.isIgnore()){//弹板
				return result.ignore();
			}
			if(!ar.isSuccess()){//不足
				return result.failure();
			}
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					attr, OperatorType.Decrease,
					consume, OutputConsumeType.recover_consume);
		}
		//产出
		Collection<RecoveryOutPutConfig> outPutCfs = GameContext.getRecoveryApp().getRecoveryOutPutConfigs(rc.getRecoveryId(), rc.getRoleLevel()); 
		if(Util.isEmpty(outPutCfs)){
			result.setInfo(getText(TextId.RECOVERY_OUTPUT_PARAM_ERROR));
			return result.failure();
		}
		int percentage = consumeConfig.getPercentage();
		sendAwards(role, outPutCfs, percentage, num);

		//保存追回次数
		rc.incrementNum(num);
		GameContext.getRecoveryApp().saveUpdateRecovery(rc);
		return result.success();
	}
	
	public RecoveryResult recoveryAward(RoleInstance role, String id, byte consumeType, int num){
		RecoveryResult result = new RecoveryResult();
		RoleRecovery rc = GameContext.getRecoveryApp().getRoleRecovery(role, id);
		if(rc == null){
			return result.failure();
		}
		if(!canRecovery(rc)){
			result.setInfo(getText(TextId.RECOVERY_COUNT_EMPTY));
			return result.failure();
		}
		//获得角昨天的角色等级
		byte vipLevel = GameContext.getVipApp().getVipLevel(role);
		//获得消耗的配置
		RecoveryConsumeConfig  consumeConfig = GameContext.getRecoveryApp().getRoleRecoveryConsumeConfig(id, consumeType ,vipLevel);
		if(consumeConfig == null){
			result.setInfo(getText(TextId.RECOVERY_CONSUME_PARAM_ERROR));
			return result.failure();
		}
		//产出
		Collection<RecoveryOutPutConfig> outPutCfs = GameContext.getRecoveryApp().getRecoveryOutPutConfigs(rc.getRecoveryId(), rc.getRoleLevel()); 
		if(Util.isEmpty(outPutCfs)){
			result.setInfo(getText(TextId.RECOVERY_OUTPUT_PARAM_ERROR));
			return result.failure();
		}
		
		int percentage = consumeConfig.getPercentage();
		sendAwards(role, outPutCfs, percentage, num);
		//保存追回次数
		rc.incrementNum(num);
		GameContext.getRecoveryApp().saveUpdateRecovery(rc);
		
		return result.success();
	}
	/**
	 * 
	 * @param role
	 * @param outPutCfs
	 * @param percentage
	 * @param num 追回几次，每次点击追回num = 1;
	 * @date 2014-10-23 下午02:05:19
	 */
	private void sendAwards(RoleInstance role,
			Collection<RecoveryOutPutConfig> outPutCfs, int percentage, int num) {
		List<GoodsOperateBean> addList = null;
		for (RecoveryOutPutConfig cf : outPutCfs) {
			if(!cf.checkSuccess()){
				continue;
			}
			if(cf.isAttribute()){
				AttributeType attr = AttributeType.get((byte)cf.getOutputId());
				int value = cf.getValue(percentage,num);
				addRoleAttribute(role, attr,value);
			}
			if(cf.isGoods()){
				GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(cf.getOutputId());
				if(null == goodsBase){
					continue;
				}
				if(addList == null){
					addList = Lists.newArrayList();
				}
				GoodsOperateBean bean = new GoodsOperateBean();
				bean.setGoodsId(cf.getOutputId());
				bean.setGoodsNum(cf.getValue(percentage,num));
				addList.add(bean);
			}
		}
		if(Util.isEmpty(addList)){
			sendGoodsAwards(role,addList);
		}
	}
	private void addRoleAttribute(RoleInstance role, AttributeType attr, int value) {
		switch (attr) {
		case dkp:
			GameContext.getUnionApp().changeMemberDkp(role, value, OperatorType.Add, FunType.recovery,true);
			break;
		default:
			role.getBehavior().changeAttribute(attr, OperatorType.Add, value);
			role.getBehavior().notifyAttribute();
			break;
		}
		
	}
	private void sendGoodsAwards(RoleInstance role, List<GoodsOperateBean> addList) {
		AddGoodsBeanResult goodsResult = GameContext.getUserGoodsApp()
		.addSomeGoodsBeanForBag(role, addList,
				OutputConsumeType.recovery_output);
		// 背包满发邮件
		List<GoodsOperateBean> putFailureList = goodsResult
			.getPutFailureList();
		try {
			if (!Util.isEmpty(putFailureList)) {
				String context = this.getText(TextId.RECOVERY_MAIL_CONTEXT);
				GameContext.getMailApp().sendMail(role.getRoleId(),
						MailSendRoleType.Recovery.getName(), context,
						MailSendRoleType.Recovery.getName(),
						OutputConsumeType.recovery_output.getType(),
						putFailureList);
				//发送广播
//				C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage(getText(TextId.RECOVERY_REWARD_BY_EMAIL_TIPS));
//				role.getBehavior().sendMessage(msg);
				}
			} catch (Exception e) {
				logger.error("base recovery logic sendGoodsAwards err:", e);
			}
	}
	protected String getText(String textId) {
		return GameContext.getI18n().getText(textId);
	}
	/**
	 * 离线经验的一键追回覆盖此方法
	 * @param consumeCf
	 * @param recovery
	 * @return
	 * @date 2014-10-22 下午08:46:40
	 */
	protected RecoveryConsumeItem buildRecoveryConsumeItem(
			final RecoveryConsumeConfig consumeCf, 
			final RoleRecovery recovery) {
		//产出
		Collection<RecoveryOutPutConfig> outPutCfs = GameContext.getRecoveryApp().getRecoveryOutPutConfigs(recovery.getRecoveryId(), recovery.getRoleLevel()); 
		if(Util.isEmpty(outPutCfs)){
			return null;
		}
		RecoveryConsumeItem it = new RecoveryConsumeItem();
		it.setConsumeType(consumeCf.getConsumeType());
		it.setConsumeValue(consumeCf.getValue());
		it.setOutPutPercent(consumeCf.getPercentage());
		return it;
	}
	public RecoveryShowItem getRecoveryShowItem(RecoveryConfig cf, RoleRecovery recovery,
			byte vipLevel){
		RecoveryShowItem it = new RecoveryShowItem();
		//产出
		Collection<RecoveryOutPutConfig> outPutCfs = GameContext.getRecoveryApp().getRecoveryOutPutConfigs(recovery.getRecoveryId(), recovery.getRoleLevel()); 
		if(Util.isEmpty(outPutCfs)){
			return null;
		}
		
		List<AttriTypeValueItem> attList = Lists.newArrayList();
		List<GoodsLiteNamedItem> goodsList = Lists.newArrayList();
		//如果是离线经验追回，追回的资源（经验），与角色等级有关
		for (RecoveryOutPutConfig cof : outPutCfs) {
			if(!cof.checkSuccess()){
				continue;
			}
			Object o = cof.getAwardItem();
			if(o instanceof AttriTypeValueItem){
				attList.add((AttriTypeValueItem)o);
				continue;
			}
			if(o instanceof GoodsLiteNamedItem){
				goodsList.add((GoodsLiteNamedItem)o);
			}
		}
		
		it.setAttList(attList);
		it.setGoodsList(goodsList);

		it.setIcon(cf.getIcon());
		it.setId(recovery.getRecoveryId());
		it.setNum(recovery.getRecoveryNum());
		it.setRecoveryName(cf.getName());
		
		List<RecoveryConsumeItem>  cs = getRecoveryConsumeItemList(recovery, vipLevel);
		it.setConsumeList(cs);
		return it;
	}
	protected void buildOutPutList(List<AttriTypeValueItem> attList,
			List<GoodsLiteNamedItem> goodsList,
			final Collection<RecoveryOutPutConfig> outPutCfs, 
			final RecoveryConsumeConfig consumeCf) {
		if(attList == null){
			attList = Lists.newArrayList();
		}
		if(goodsList == null){
			goodsList = Lists.newArrayList();
		}
		//如果是离线经验追回，追回的资源（经验），与角色等级有关
		for (RecoveryOutPutConfig cf : outPutCfs) {
			if(!cf.checkSuccess()){
				continue;
			}
			Object o = cf.getAwardItem(consumeCf.getPercentage());
			if(o instanceof AttriTypeValueItem){
				attList.add((AttriTypeValueItem)o);
				continue;
			}
			if(o instanceof GoodsLiteNamedItem){
				goodsList.add((GoodsLiteNamedItem)o);
			}
		}
	}
	
	public int getRecoveryAwardConsumeValue(RoleInstance role, String id,
			byte consumeType) {
		RoleRecovery rc = GameContext.getRecoveryApp().getRoleRecovery(role, id);
		if(rc == null){
			return 0;
		}
		byte vipLevel = GameContext.getVipApp().getVipLevel(role);
		//消耗配置
		RecoveryConsumeConfig consumeCf = GameContext.getRecoveryApp().getRoleRecoveryConsumeConfig(rc.getRecoveryId(),consumeType, vipLevel);
		if(consumeCf == null){
			return 0;
		}
		return consumeCf.getValue();
	}
	@Override
	public boolean canRecovery(RoleRecovery rc) {
		return rc.getRecoveryNum() > 0;
	}
}
