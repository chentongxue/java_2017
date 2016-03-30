package sacred.alliance.magic.app.attri.calct;
import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.vo.AbstractRole;
public interface Calct<T extends AbstractRole> {
	/**
	 * 改变属性
	 * @param role
	 * @param buffer
	 */
	public void changeAttri(T role, AttriBuffer buffer,  boolean isEffectBattleScore);
	
	/**
	 * 重新计算所有属性
	 * @param role
	 */
	public void reCalct(T role);
	
	/**
	 * 创建角色 原始赋值
	 * @param role
	 */
	public void bornAtrri(T role);
	
	
	public void safeChangeBattleScoreAttri(T role, AttriBuffer buffer);
	
}
