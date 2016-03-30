package sacred.alliance.magic.app.attri.calct;
import java.util.Map;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.domain.RoleLevelup;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.RoleNotifyAttribute;

import com.game.draco.GameContext;
import com.game.draco.app.target.cond.TargetCondType;
public class RoleCalct extends DefaultCalct<RoleInstance> {
	private RoleFormulaCalct roleFormulaCalct;
	
	
	@Override
	public void reCalct(RoleInstance role){
		super.reCalct(role);
		//重新计算战斗力
		reCalctBattleScore(role);
	}
	
	
	@Override
	protected void reCalctBattleScore(RoleInstance role){
		//int oldScore = role.getBattleScore() ;
		role.syncBattleScore();
		/*int newScore = role.getBattleScore() ;
		//旧战斗力>0 并且登录完成才通知战斗力变化
		if(oldScore >0 && role.isLoginCompleted() && oldScore != newScore){
			RoleAttributeChangeRespMessage respMsg = new RoleAttributeChangeRespMessage();
			respMsg.setRoleId(role.getIntRoleId());
			List<RoleAttributeChangeItem> items = new ArrayList<RoleAttributeChangeItem>();
			RoleAttributeChangeItem item = new RoleAttributeChangeItem();
			item.setAttType(AttributeType.battleScore.getType());
			item.setValue(newScore);
			items.add(item);
			respMsg.setItems(items);
			role.getBehavior().sendMessage(respMsg);
		}*/
		//目标系统
		GameContext.getTargetApp().updateTarget(role, TargetCondType.RoleBattleScore);
	}
	
	@Override
	public void bornAtrri(RoleInstance role) {
		super.reCalct(role);
		role.setCurHP(role.getMaxHP());
	}

	@Override
	protected int getByLevelup(RoleInstance role, AttributeType attriType) {
		return GameContext.getAttriApp().getLevelup(role.getLevel()).getAttriValue(attriType);
	}

	@Override
	protected Map<Byte,AttriItem> getByMultAdvanced(RoleInstance role) {
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		buffer.append(GameContext.getUserSkillApp().getAttriBuffer(role));
		buffer.append(GameContext.getTitleApp().getAttriBuffer(role));
		//点灯
		buffer.append(GameContext.getMedalApp().getAttriBuffer(role));
//		//社交
//		buffer.append(GameContext.getSocialApp().getAttriBuffer(role));
		//英雄
		buffer.append(GameContext.getHeroApp().getAttriBuffer(role));
		//秘药(暂时不需要)
		//buffer.append(GameContext.getNostrumApp().getAttriBuffer(role));
		// 宠物
		buffer.append(GameContext.getPetApp().getOnBattleRolePetBuffer(role));
		//坐骑
		buffer.append(GameContext.getRoleHorseApp().getAttriBuffer(role));
		 //天赋
		buffer.append(GameContext.getRoleTalentApp().getAttriBuffer(role));
		//buff模块提供的属性不计入战斗力计算
		AttriBuffer buffAppBuffer = GameContext.getUserBuffApp().getAttriBuffer(role);
		role.getBsNoAffectBuffer().clear();
		role.getBsNoAffectBuffer().append(buffAppBuffer);
		buffer.append(buffAppBuffer);
		return buffer.getMap();
	}



	@Override
	protected void autoUpgrade(RoleInstance role) {
		role.getBehavior().autoLevelup();
	}

	@Override
	protected int getFormulaCalct(RoleInstance role,
			AttributeType attriType) {
		return roleFormulaCalct.getBaseValue(attriType);
	}

	@Override
	protected void otherEffect(RoleInstance role, AttriBuffer buffer) {
		AttriItem expItem = buffer.getMap().get(AttributeType.exp.getType());
		if(null != expItem && expItem.getValue() > 0){
			int addExp = (int)expItem.getValue();
			RoleNotifyAttribute lastStatusAtt = role.getBehavior().getLastStatusAtt();
			if(null != lastStatusAtt){
				lastStatusAtt.setExpChange(lastStatusAtt.getExpChange() + addExp);
			}
			role.setTotalExp(role.getTotalExp() + addExp);
			autoUpgrade(role);
			//调用宠物加经验接口
			GameContext.getPetApp().changeBattlePetExp(role, addExp);
		}
	}

	@Override
	protected void changeExp(RoleInstance role, AttriItem item) {
		int addExp = (int)item.getValue();
		if(addExp <= 0){
			return ;
		}
		//达到最大等级，经验值不能超过最大经验累计值
		if(role.getLevel() >= GameContext.getAreaServerNotifyApp().getMaxLevel()){
			RoleLevelup roleLevelup = GameContext.getAttriApp().getLevelup(role.getLevel());
			int maxExp = roleLevelup.getMaxExp();
			if((role.getExp() + addExp) > maxExp){
				item.setValue(maxExp - role.getExp());
			}
		}
	}
	
	@Override
	protected void changeLevel(RoleInstance role, AttriItem item){
		//获取服务器的角色最大等级
		int maxLevel = GameContext.getAreaServerNotifyApp().getMaxLevel();
		//如果是升级，判断升级后是否超过满级
		if(item.getValue() + role.getLevel() > maxLevel){
			item.setValue(maxLevel - role.getLevel());
		}
		//如果是降级，判断升降级是否低于1级
		if(item.getValue() + role.getLevel() < 1){
			item.setValue(1 - role.getLevel());
		}
	}

	public void setRoleFormulaCalct(RoleFormulaCalct roleFormulaCalct) {
		this.roleFormulaCalct = roleFormulaCalct;
	}
	

	
	
	
}
