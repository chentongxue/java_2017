package sacred.alliance.magic.app.attri.calct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttriLevelType;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

public abstract class DefaultCalct<T extends AbstractRole> implements Calct<T> {
	
	@Override
	public void safeChangeBattleScoreAttri(T role, AttriBuffer buffer){
		List<AttriItem> specialOrigList = null ;
		for(AttriItem ai : buffer.getMap().values()){
			AttributeType attriType = AttributeType.get(ai.getAttriTypeValue());
			if(null == attriType || 
					! GameContext.getAttriApp().isEffectBattleScore(attriType.getType())){
				continue ;
			}
			if(AttributeType.isSpecialOrig(attriType)){
				if(null == specialOrigList){
					specialOrigList = new ArrayList<AttriItem>();
				}
				specialOrigList.add(ai);
				continue ;
			}
			//非附加属性直接赋值
			if(attriType.getAttriLevelType() != AttriLevelType.added){
				assignValue(role,ai);
				continue ;
			}
			updateValueAndCache(role,AttriItem.merge(ai,
					role.getAttriCache().get(ai.getAttriTypeValue())));
		}
		assignValue(role, specialOrigList);
	}
	
	
	@Override
	public void changeAttri(T role, AttriBuffer buffer, boolean isEffectBattleScore){
		if((null == buffer) || buffer.isEmpty()){
			return ;
		}
		//重算的时候需要考虑的情况是有上限值的属性必须先计算
		//eg: maxHP 要优先于 curHP计算
		boolean reCalctBattleScore = false ;
		List<AttriItem> specialOrigList = null ;
		for(AttriItem ai : buffer.getMap().values()){
			AttributeType attriType = AttributeType.get(ai.getAttriTypeValue());
			if(null == attriType){
				continue ;
			}
			//处理有上限的属性
			if (ai.getValue() > 0 && role.getRoleType() == RoleType.PLAYER) {
				AttributeType ceilAttriType = GameContext.getAttriApp()
						.getCeilAttributeType(attriType);
				if (null != ceilAttriType) {
					float value = GameContext.getAttriApp()
							.ceilAttributeProcess((RoleInstance) role,
									attriType, ai.getValue());
					attriType = ceilAttriType;
					ai.setAttriTypeValue(ceilAttriType.getType());
					ai.setValue(value);
				}
			}
			
			if(!reCalctBattleScore &&
					GameContext.getAttriApp().isEffectBattleScore(ai.getAttriTypeValue())){
				reCalctBattleScore = true ;
			}
			if(AttributeType.isSpecialOrig(attriType)){
				if(null == specialOrigList){
					specialOrigList = new ArrayList<AttriItem>();
				}
				specialOrigList.add(ai);
				continue ;
			}
			//非附加属性直接赋值
			if(attriType.getAttriLevelType() != AttriLevelType.added){
				assignValue(role,ai);
				continue ;
			}
			updateValueAndCache(role,AttriItem.merge(ai,
					role.getAttriCache().get(ai.getAttriTypeValue())));
		}
		assignValue(role, specialOrigList);
		this.otherEffect(role, buffer);
		//计算战斗力
		if(role.getRoleType() != RoleType.PLAYER || !reCalctBattleScore ){
			return ;
		}
		if(!isEffectBattleScore){
			((RoleInstance)role).getBsNoAffectBuffer().append(buffer);
		}
		this.reCalctBattleScore(role);
	}
	
	/**
	 * 重新计算战斗力
	 * @param role
	 */
	protected void reCalctBattleScore(T role){
	}
	
	@Override
	public void reCalct(T role){
		Map<Byte,AttriItem> attriCache = role.getAttriCache();
		attriCache.clear();
		//清除状态[状态属性特殊,会在遍历buff的时候修改此值]
		role.clearState();
		Map<Byte,AttriItem> multAdvancedCache = this.getByMultAdvanced(role);
		for(AttributeType attriType:AttributeType.getAddedList()){
			updateValueAndCache(role,reCalct(role,attriType,multAdvancedCache));
		}
		//考虑curHp,curMp等的溢出情况
		this.maxAttriOverflow(role);
	}
	
	protected void maxAttriOverflow(T role){
		role.set(AttributeType.curHP.getType(), role.get(AttributeType.curHP));
	}
	
	
	private void assignValue(T role,AttriItem attriItem){
		if(attriItem.getAttriTypeValue() == AttributeType.exp.getType()){
			this.changeExp(role, attriItem);
		}
		if(attriItem.getAttriTypeValue() == AttributeType.level.getType()){
			this.changeLevel(role, attriItem);
		}
		int newValue = Util.safeIntAdd((int)attriItem.getValue(), (int)role.get(attriItem.getAttriTypeValue()));
		role.set(attriItem.getAttriTypeValue(), (int)newValue);
	}
	/**
	 * 赋值操作
	 * @param role
	 * @param list
	 */
	private void assignValue(T role,List<AttriItem> attriItemList){
		if((null == attriItemList) || 0 == attriItemList.size()){
			return ;
		}
		for(AttriItem attriItem : attriItemList){
			this.assignValue(role, attriItem);
		}
	}
	
	protected abstract void changeExp(T role,AttriItem item);
	
	protected abstract void changeLevel(T role, AttriItem item);
	
	/**
	 * 修改属角色性值并更新属性缓存
	 * @param role
	 * @param attriItem
	 */
	protected void updateValueAndCache(T role, AttriItem attriItem) {
		if(null == attriItem){
			return ;
		}
		int value = attriItem.calctValue();
		/**
		 * 只允许五行防御值为负，其余均不于0
		 */
		if ((value < 0) && !(AttributeType.isNegative(AttributeType
				.get(attriItem.getAttriTypeValue())))) {
			value = 0;
		}
		role.set(attriItem.getAttriTypeValue(), value);
		role.getAttriCache().put(attriItem.getAttriTypeValue(), attriItem);
	}
	
	/**
	 * 重计算单个属性
	 * @param role
	 * @param attriType
	 * @return
	 */
	protected AttriItem reCalct(T role,AttributeType attriType,Map<Byte,AttriItem> multAdvancedCache){
		if(null == attriType || null == role){
			return null;
		}
		role.getAttriCache().remove(attriType.getType());
		AttriItem multAdvancedItem = this.getAttriItem(attriType, multAdvancedCache);
		int formulaValue = getFormulaCalct(role,attriType);
		int levelupValue = getByLevelup(role,attriType);
		return AttriItem.merge(attriType.getType(),formulaValue+levelupValue,
				multAdvancedItem);
	}
	/**
	 * 得到装备、buff、技能对属性影响的 值、百分比
	 * @param attriType
	 * @param multAdvancedCache
	 * @return
	 */
	protected AttriItem getAttriItem(AttributeType attriType, Map<Byte, AttriItem> multAdvancedCache) {
		if(null != multAdvancedCache && multAdvancedCache.size() > 0){
			return multAdvancedCache.get(attriType.getType());
		}
		return null;
	}
	
	/**
	 * 角色升级
	 * @param role
	 */
	protected abstract void autoUpgrade(T role);
	
	
	/**
	 * 通过模板表得到基本属性(或者额外的衍生属性)
	 * @param role
	 * @param attritype
	 * @return
	 */
	protected abstract int getByLevelup(T role,AttributeType attriType);
	
	
	/**
	 * 通过多维高级属性计算衍生属性
	 * @param role
	 * @return
	 */
	protected abstract Map<Byte,AttriItem> getByMultAdvanced(T role);
	
	
	/**
	 * 按公式计算三级属性
	 * @param role
	 * @param attriType
	 * @return
	 */
	protected abstract int getFormulaCalct(T role,AttributeType attriType);
	
	/**
	 * 其它影响部分
	 * @param role
	 * @param attriType
	 * @return
	 */
	protected abstract void otherEffect(T role,AttriBuffer buffer);
	
	
}
