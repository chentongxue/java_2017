package sacred.alliance.magic.app.user;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.calct.CalctManager;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.domain.RoleLevelup;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.RoleNotifyAttribute;

import com.game.draco.GameContext;
import com.game.draco.app.union.domain.UnionMember;
import com.game.draco.message.item.RoleAttributeChangeItem;
import com.game.draco.message.response.C0400_RoleAttributeChangeRespMessage;

public class UserAttributeAppImpl implements UserAttributeApp {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private CalctManager calctManager;


	@Override
	public void changeRoleMoney(AbstractRole player, AttributeType attType,
			OperatorType operatorType, int value,OutputConsumeType outputConsumeType){
		
		if (null == player) {
			return;
		}
		try{
			synchronized (player) {
				//如果值为负数，不符合正常逻辑，返回。
				if(value <0){
					Log4jManager.CHANGE_MONEY_ERROR.error("[金额为负数]\troleId="+player.getRoleId()+"\ttype="+attType.getName()+"\tvalue="+value);
					return ;
				}
				
				int _oldMoney = getMoney(player,attType);//记录修改前属性
				int originValue = player.get(attType);
				
				int result = this.compute(originValue, operatorType, value);
				if (result <= 0) {
					result = 0;
				}
				
				if(OperatorType.Add.getType() == operatorType.getType()){
					//充值表金条信息
					if(attType.getType() == AttributeType.goldMoney.getType()){
						player.getRolePayRecord().addCurrMoney(value,0);
						//已经在充值处处理
						//player.setRolePayGold(player.getRolePayGold() + value);
					}
					player.set(attType.getType(), result);
				}else if(OperatorType.Decrease.getType() == operatorType.getType()){
					RoleInstance role = (RoleInstance) player;
					if(attType.getType() == AttributeType.goldMoney.getType()
							&& (null != outputConsumeType && outputConsumeType
									.isCountGoldMoney())) {
						// 首次消费日志
						GameContext.getStatLogApp().roleConsumeLog(role, value, outputConsumeType);
						// 统计消耗
						player.set(AttributeType.residueGoldMoney.getType(),
								Util.safeIntAdd(role.getRoleConsumeGold(),
										value));
						// 玩家帐号消耗
						player.getRolePayRecord().addConsumeMoney(value);
						// 兑换需要
						GameContext.getCountApp().updateRoleBuy(role, value, outputConsumeType);
					}else if(attType.getType() == AttributeType.bindingGoldMoney.getType()){
						player.set(AttributeType.residueBindingMoney.getType(),Util.safeIntAdd(role.getConsumeBindMoney(), value));
					}
					player.set(attType.getType(), result);
				}else{
					return;//如果不是加减返回。
				}
				//money变化日志
				GameContext.getStatLogApp().roleMoneyLog((RoleInstance) player, attType, outputConsumeType, value, "");
				//重新计算判断是否正确
				int _newMoney = OperatorType.compute(_oldMoney, operatorType, value);
				if( _newMoney != getMoney(player,attType)){ 
					Log4jManager.CHANGE_MONEY_ERROR.error("[金钱变化有误]\troleId="+player.getRoleId()+"\ttype="+attType.getName()
							+"\tOperatorType="+operatorType.toString()
							+"\toldRoleMoney="+_oldMoney+"\tnowRoleMoney="+getMoney(player,attType)+"\tvalue="+value);
				}
			}
		}catch(Exception e){
			logger.error("changeRoleMoney Exception:roleId="+player.getRoleId()+"\ttype="+attType.getName()+"\troleMoney="+getMoney(player,attType)+"\tvalue="+value,e);
		}
	}
	
	private int compute(int attributeValue, OperatorType operatorType, int operateValue){
		if(operatorType==OperatorType.Add){
			attributeValue = Util.safeIntAdd(attributeValue,operateValue);
		}else if(operatorType == OperatorType.Decrease){
			attributeValue -= operateValue;
		}else if(operatorType == OperatorType.Equal){
			attributeValue = operateValue;
		}
		if(attributeValue < 0){
			return 0;
		}
		return attributeValue;
	}
	
	
	//返回相应玩家金钱
	private int getMoney(AbstractRole role,AttributeType attType){
		RoleInstance player = (RoleInstance)role;
		if(attType == AttributeType.goldMoney){
			return player.getGoldMoney();
		}
		if(attType == AttributeType.bindingGoldMoney){
			return player.getBindingGoldMoney();
		}
		if(attType == AttributeType.silverMoney){
			return  player.getSilverMoney();
		}
		return 0;
	}
	
	/**
	 * 在出生,复活情况下修改curHP,不能调用此方法,必须调用setCurHP
	 */
	@Override
	public void changeAttribute(AbstractRole player, AttributeType attType,
			OperatorType operatorType, int value,OutputConsumeType ocType) {
		//如果是钱属性类型直接调用 changeRoleMoney
		if(null == attType){
			return ;
		}
		if(attType.isMoney()){
			this.changeRoleMoney(player, attType, operatorType, value, ocType);
			return ;
		}
		
		if(operatorType == OperatorType.Equal){
			player.set(attType.getType(), value);
			return ;
		}
		if(0 == value){
			return ;
		}
		int prefix = (operatorType == OperatorType.Add)?1:-1;
		this.changeAttribute(player, AttriBuffer.createAttriBuffer().append(attType, prefix*value));
		//经验日志
		if(attType == AttributeType.exp){
			GameContext.getStatLogApp().roleExpLog((RoleInstance) player, value, "", ocType);
		}
		//TODO:属性变化影响相关的勋章
		if(player.getRoleType() == RoleType.PLAYER) {
			GameContext.getMedalApp().updateMedal((RoleInstance) player, attType);
		}
	}


	@Override
	public RoleNotifyAttribute getRoleNotifyAttribute(AbstractRole player) {
		RoleNotifyAttribute notify = new RoleNotifyAttribute();
		
		notify.setHpCur(player.get(AttributeType.curHP));
		notify.setHpMax(player.get(AttributeType.maxHP));
		notify.setLv(player.get(AttributeType.level));
		notify.setMpCur(player.get(AttributeType.curMP));
		notify.setMpMax(player.get(AttributeType.maxMP));
		notify.setSpeed(player.get(AttributeType.speed));
		notify.setState((byte)player.get(AttributeType.state));
		
		
		if(player.getRoleType() == RoleType.PLAYER){
			notify.setExp(player.get(AttributeType.exp.getType()));
			notify.setGoldMoney(player.get(AttributeType.goldMoney));
			notify.setBindMoney(player.get(AttributeType.bindingGoldMoney));
			notify.setSilverMoney(player.get(AttributeType.silverMoney));
			notify.setMaxExp(player.get(AttributeType.maxExp));
			notify.setHonor(player.get(AttributeType.honor));
			notify.setZp(player.get(AttributeType.potential));
//			notify.setContribute(player.get(AttributeType.contribute));
			notify.setBattleScore(player.get(AttributeType.battleScore));
			notify.setTodayCoupon(player.get(AttributeType.todayCoupon));
			notify.setCoupon(player.get(AttributeType.coupon));
		
			//notify.setMagicSoul(player.get(AttributeType.magicSoul));
			//notify.setTotalMagicSoul(player.get(AttributeType.totalMagicSoul));
			notify.setLq(player.get(AttributeType.lq));
//			notify.setDkp(GameContext.getUnionApp().getUnionMemberDkp((RoleInstance)player));
			notify.setDkp(player.get(AttributeType.dkp));
		}
		return notify;
	}

	@Override
	public C0400_RoleAttributeChangeRespMessage getRoleAttributeChangeMessage(
			RoleNotifyAttribute lastStatus, RoleNotifyAttribute nowStatus) {
		List<RoleAttributeChangeItem> changeList = new ArrayList<RoleAttributeChangeItem>();
		this.buildAttrChangeList(lastStatus.getHpMax(), nowStatus.getHpMax(), 
				AttributeType.maxHP, changeList);
		this.buildAttrChangeList(lastStatus.getHpCur(), nowStatus.getHpCur(),
				AttributeType.curHP, changeList);
		this.buildAttrChangeList(lastStatus.getMpMax(), nowStatus.getMpMax(),
				AttributeType.maxMP, changeList);
		this.buildAttrChangeList(lastStatus.getMpCur(), nowStatus.getMpCur(),
				AttributeType.curMP, changeList);
		this.buildAttrChangeList(lastStatus.getLv(), nowStatus.getLv(),
				AttributeType.level, changeList);
		this.buildAttrChangeList(lastStatus.getSpeed(), nowStatus.getSpeed(),
				AttributeType.speed, changeList);
		this.buildAttrChangeList(lastStatus.getState(), nowStatus.getState(),
				AttributeType.state, changeList);
		this.buildAttrChangeList(lastStatus.getExp(), nowStatus.getExp(),
				AttributeType.exp, changeList);
		this.buildAttrChangeList(lastStatus.getGoldMoney(), nowStatus.getGoldMoney(), 
				AttributeType.goldMoney, changeList);
		this.buildAttrChangeList(lastStatus.getBindMoney(), nowStatus.getBindMoney(), 
				AttributeType.bindingGoldMoney, changeList);
		this.buildAttrChangeList(lastStatus.getSilverMoney(), nowStatus.getSilverMoney(), 
				AttributeType.silverMoney, changeList);
		this.buildAttrChangeList(lastStatus.getMaxExp(), nowStatus.getMaxExp(),
				AttributeType.maxExp, changeList);
		this.buildAttrChangeList(lastStatus.getHonor(), nowStatus.getHonor(),
				AttributeType.honor, changeList);
		this.buildAttrChangeList(lastStatus.getZp(), nowStatus.getZp(),
				AttributeType.potential, changeList);
//		this.buildAttrChangeList(lastStatus.getContribute(), nowStatus.getContribute(),
//				AttributeType.contribute, changeList);
		this.buildAttrChangeList(lastStatus.getBattleScore(), nowStatus.getBattleScore(),
				AttributeType.battleScore, changeList);
		this.buildAttrChangeList(lastStatus.getTodayCoupon(), nowStatus.getTodayCoupon(),
				AttributeType.todayCoupon, changeList);
		this.buildAttrChangeList(lastStatus.getCoupon(), nowStatus.getCoupon(),
				AttributeType.coupon, changeList);
		
		this.buildAttrChangeList(lastStatus.getExpChange(), nowStatus.getExpChange(),
				AttributeType.expChange, changeList);
		//女神经验变化
		this.buildAttrChangeList(lastStatus.getGoddessExpChange(), nowStatus.getGoddessExpChange(),
				AttributeType.goddessExpChange, changeList);
		
		this.buildAttrChangeList(lastStatus.getDkp(), nowStatus.getDkp(), 
				AttributeType.dkp, changeList);
		
		this.buildAttrChangeList(lastStatus.getLq(), nowStatus.getLq(),
				AttributeType.lq, changeList);
		if (0 == changeList.size()) {
			return null;
		}
		C0400_RoleAttributeChangeRespMessage resp = new C0400_RoleAttributeChangeRespMessage();
		resp.setItems(changeList);
		return resp;
	}
	
	private void buildAttrChangeList(int originValue,
			int nowValue, AttributeType attType, List<RoleAttributeChangeItem> changeList) {
		if(nowValue == originValue){
			return;
		}
		RoleAttributeChangeItem item = new RoleAttributeChangeItem();
		item.setAttType(attType.getType());
		item.setValue(nowValue);
		//!!!! 经验变化特殊,参数调用反过来
		if(attType.getType() == AttributeType.expChange.getType()
				|| attType.getType() == AttributeType.goddessExpChange.getType()){
			item.setValue(originValue);
		}
		changeList.add(item);
	}

	public boolean levelUp(AbstractRole player) {
		RoleInstance role = (RoleInstance) player;
		RoleLevelup roleLevelup = GameContext.getAttriApp().getLevelup(role.getLevel());
		//减去升级所需经验
		int upExp = roleLevelup.getUpExp();
		if(upExp>0){
			this.changeAttribute(player, AttributeType.exp, OperatorType.Decrease, upExp, OutputConsumeType.role_level_up);
		}
		role.getBehavior().changeAttribute(AttributeType.level,OperatorType.Add, 1);

		//有效玩家日志
		GameContext.getStatLogApp().userValidLog(role);
		
		role.roleLevelUp(1);
		//this.pushRoleLevelAttriChangeNotifyMessage(role, roleLevelup);
		//动态菜单
		GameContext.getMenuApp().refreshByUpgrade(role);
		//在线领奖是达到相关等级才开始
		GameContext.getOnlineRewardApp().roleLevelUpgrade(role);
		//初始化坐骑
		//GameContext.getUserMountApp().init(role);
		//阵营平衡相关
		GameContext.getCampBalanceApp().roleLevelUp(role);
		//打印日志
		GameContext.getLogApp().userGradeLog(role);
		/****新属性修改****/
		return this.reCalctForLevelup(role);
	}
	
	
	/**
	 * 角色等级改变后，重计算属性
	 * @param role
	 * @param roleLevelup
	 * @return
	 */
	private boolean reCalctForLevelup(RoleInstance role){
		this.reCalct(role);
		
		try{
			//通知任务
			GameContext.getUserQuestApp().roleUpgrade(role);
		}catch(Exception ex){
			logger.error("",ex);
		}
		try {
			RoleLevelup roleLevelup = GameContext.getAttriApp().getLevelup(role.getLevel());
			if (null == roleLevelup) {
				logger.error("attri upgrade map is null,roleId = "+ role.getRoleId());
				return false;
			}
			// 计算升级后的最大经验
			int upgNeedExp = (int) roleLevelup.getUpExp();
			role.getBehavior().changeAttribute(AttributeType.maxExp,OperatorType.Equal, upgNeedExp);
			role.setCurHP(role.getMaxHP());
			role.setCurMP(role.getMaxMP());
			role.getBehavior().notifyAttribute();
		} catch (Exception e) {
			logger.error("", e);
		}

		//通知技能更新
		try {
			GameContext.getUserSkillApp().roleLevelup(role);
		} catch (Exception e) {
			logger.error("roleLevelup notify skill update error", e);
		}
		//达到满级输出的日志
		return true;
	}

	@Override
	public void reCalctAndNotify(AbstractRole player) {
		if(null == player){
			return ;
		}
		// 属性计算重计算
		this.reCalct(player);
		// 通知属性
		player.getBehavior().notifyAttribute();
	}

	public void setCalctManager(CalctManager calctManager) {
		this.calctManager = calctManager;
	}

	@Override
	public void changeAttribute(AbstractRole role, AttriBuffer attribuffer) {
		this.changeAttribute(role, attribuffer, true);	
	}
	
	@Override
	public void changeAttribute(AbstractRole role, AttriBuffer attribuffer, boolean isEffectBattleScore) {
		calctManager.getCalct(role).changeAttri(role, attribuffer, isEffectBattleScore);
	}

	@Override
	public void reCalct(AbstractRole role) {
		if(null == role){
			return ;
		}
		calctManager.getCalct(role).reCalct(role);
	}

	@Override
	public void reCalctAfterChangeLevel(RoleInstance role, OperatorType operType, int value) {
		this.reCalctForLevelup(role);
	}

	@Override
	public void changeRoleDkp(AbstractRole player, AttributeType attType,
			OperatorType operatorType, int value,
			OutputConsumeType outputConsumeType) {
		if (player == null) {
			return;
		}
		try{
			synchronized (player) {
				//如果值为负数，不符合正常逻辑，返回。
				if(value <0){
					Log4jManager.CHANGE_DKP_ERROR.error("[DKP为负数]\troleId="+player.getRoleId()+"\ttype="+attType.getName()+"\tvalue="+value);
					return ;
				}
				RoleInstance role = (RoleInstance)player;
				UnionMember member = GameContext.getUnionApp().getUnionMember(role.getUnionId(),role.getIntRoleId());
				if(member != null){
					if(OperatorType.Add == operatorType){
						member.setDkp(member.getDkp() + value);
					}else{
						member.setDkp(member.getDkp() - value);
					}
					GameContext.getUnionApp().saveOrUpdUnionMember(member);
					
					//DKP变化日志
					GameContext.getStatLogApp().roleMoneyLog((RoleInstance) player, attType, outputConsumeType, value, "");
				}
			}
		}catch(Exception e){
			logger.error("changeRoleDkp Exception:roleId="+player.getRoleId()+"\ttype="+attType.getName()+ "\troledkp=" +getMoney(player,attType)+"\tvalue="+value,e);
		}
	}
}
