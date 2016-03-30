package sacred.alliance.magic.app.user;

import com.game.draco.app.AppSupport;
import com.game.draco.message.response.C0400_RoleAttributeChangeRespMessage;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.service.exception.NotOnlineException;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.RoleNotifyAttribute;

public interface UserAttributeApp extends AppSupport, Service{
	/**
	 * 改变属性
	 * @param role
	 * @param buffer 属性都是变化量
	 */
	public void changeAttribute(AbstractRole role, AttriBuffer attribuffer);
	
	/**
	 * 改变属性
	 * @param role
	 * @param buffer 属性都是变化量
	 * @param isEffectBattleScore 是否影响战斗力
	 */
	public void changeAttribute(AbstractRole role, AttriBuffer attribuffer, boolean isEffectBattleScore);
	
	/**
	 * 重新计算所有属性
	 * @param role
	 */
	public void reCalct(AbstractRole role);
	
	
	/*********************************************************************以上为老代码内容***/

	/**
	 * 不要调用这个接口，要改变属性，请调用role.getBehavior().changeAttribute，然后调用
	 * role.getBehavior().notifyAttribute()
	 * 
	 * @param player
	 * @param attType
	 * @param operatorType
	 * @param value
	 */
	public void changeAttribute(AbstractRole player, AttributeType attType,
			OperatorType operatorType, int value,OutputConsumeType ocType);
	
	/**
	 * 改变玩家属性
	 * @param roleName
	 * @param attType
	 * @param operatorType
	 * @param value
	 * @throws NotOnlineException
	 */
	
	public RoleNotifyAttribute getRoleNotifyAttribute(AbstractRole player);
	
	/**
	 * 角色属性变化通知消息（-400）
	 * @param lastStatus
	 * @param nowStatus
	 * @return
	 */
	public C0400_RoleAttributeChangeRespMessage getRoleAttributeChangeMessage(
			RoleNotifyAttribute lastStatus, RoleNotifyAttribute nowStatus);

	/**
	 * 玩家升级
	 * 
	 * @param player
	 * @return
	 */
	boolean levelUp(AbstractRole player);
	
	/**
	 * 修改等级后重算属性（为GM调试平台提供）
	 * @param role
	 * @param operType
	 * @param value
	 */
	public void reCalctAfterChangeLevel(RoleInstance role, OperatorType operType, int value);
	/**
	 * 获得某一属性的区间值
	 * @param attributeType role
	 * @return
	 */
//	AttriScopeItem getAttriScope(AttributeType attributeType,AbstractRole role);
	
	
	/**
	 * 重计算和通知属性
	 * @param roleId
	 */
	public void reCalctAndNotify(AbstractRole player);
	/**
	 * 改变角色金钱
	 * @param player
	 * @param attType
	 * @param operatorType
	 * @param value
	 * @param consumeType
	 */
	public void changeRoleMoney(AbstractRole player, AttributeType attType,
			OperatorType operatorType, int value,OutputConsumeType outputConsumeType);
	
	/**
	 * 改变角色Dkp
	 * @param player
	 * @param attType
	 * @param operatorType
	 * @param value
	 * @param consumeType
	 */
	public void changeRoleDkp(AbstractRole player, AttributeType attType,
			OperatorType operatorType, int value,OutputConsumeType outputConsumeType);
	
	/**
	 * 【游戏币/潜能/钻石不足弹板】
	 * 必须所消耗的类型在配置表里，attribute_pop_config
	 * @param role 角色
	 * @param attr 属性类型
	 * @param num  消耗的值（数量）
	 * @return result ignore:出现弹板，success，足够，fail: 不足
	 * @date 2014-9-12 上午10:46:41
	 */
	public Result getEnoughResult(RoleInstance role, AttributeType attr, int num);
	/**
	 * 
	 * @param role
	 * @param attr
	 * @param num
	 * @param popDialog 是否弹出，金币/钻石/潜能  true：弹出；false：不弹出
	 * @return
	 * @date 2014-9-16 下午07:09:15
	 */
	public Result getEnoughResult(RoleInstance role, AttributeType attr, int num, boolean popDialog);

	public boolean isAttributeEnough(RoleInstance role, AttributeType att, int num);
}
