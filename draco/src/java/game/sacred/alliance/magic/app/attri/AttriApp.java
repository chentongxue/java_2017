package sacred.alliance.magic.app.attri;
import java.util.Collection;
import java.util.Map;

import sacred.alliance.magic.app.attri.config.AttriBattleScore;
import sacred.alliance.magic.app.attri.config.AttriHurtRestrict;
import sacred.alliance.magic.app.attri.config.AttriHurtSeries;
import sacred.alliance.magic.app.attri.config.ExpHookClean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.RoleLevelup;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;

public  interface AttriApp extends Service, AppSupport {

	public  RoleLevelup getLevelup(int level);
	
	public int getEffectBattleScore(RoleInstance role);
	
	public int getAttriBattleScore(AttriBuffer attriBuffer) ;
	
	public Map<Byte, AttriBattleScore> getBattleScoreMap();
	
	/**
	 * 计算属性相克伤害加成系数
	 * @return
	 */
	public float getAttriRestrictHurtRate(AbstractRole attacker, AbstractRole defender);
	
	public Collection<AttriHurtSeries> getAttriHurtSeriesList();

	public Collection<AttriHurtRestrict> getAttriHurtRestrictList() ;
	
	public boolean isEffectBattleScore(byte attriType) ;
	
	public AttributeType getCeilAttributeType(AttributeType attType) ;
	
	public float ceilAttributeProcess(RoleInstance role,AttributeType attType,float value) ;
	
	
	public int getMaxValue(RoleInstance role,AttributeType attType) ;
	
	public ExpHookClean getExpHookClean(int times) ;
	public ExpHookClean getExpHookCleanByVip(int vipLevel) ;
	public int getCleanFatigueMaxTimes() ;
	public void autoCleanFatigue(RoleInstance role) ;

    public Message getAttriHelpMessage(RoleInstance role) ;
}
