package com.game.draco.app.buff;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.game.draco.app.AppSupport;
import com.game.draco.app.buff.stat.BuffStat;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.domain.RoleBuff;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;


public interface UserBuffApp extends AppSupport{
	
	public BuffAddResult addBuffStat(AbstractRole player, AbstractRole caster, short buffId,int effectTime,int buffLevel);
	
	public BuffAddResult addBuffStat(AbstractRole player, AbstractRole caster,
			short buffId,int effectTime,int buffLevel,Object contextInfo);
	
	public BuffAddResult addForceBuffStat(AbstractRole player, AbstractRole caster, short buffId,int effectTime,int buffLevel);
	
	public BuffAddResult addBuffStat(AbstractRole player, AbstractRole caster, short buffId,int buffLevel);
	
	public void runBuff(AbstractRole owner,long timeDiff);
	
	public Collection<BuffStat> initBuffStatList(List<RoleBuff> buffList,RoleInstance role);
	
	public void delBuffStat(AbstractRole player, BuffStat buffStat, boolean timeOver);
	
	public void delBuffStat(AbstractRole player, short buffId, boolean timeOver);
	
	public void delBuffStat(AbstractRole player, short buffId, boolean timeOver,String casterId);
	
	//返回已经扣掉的个数
	public int cleanBuffBySeries(AbstractRole player, int buffSeries, int count);

	/**
	 * 获取物品加buff的状态
	 * @param player
	 * @param buffId
	 * @param goodsId
	 * @return
	 */
	//public byte getGoodsAddBuffStat(AbstractRole player, int goodsId);
	
	public AttriBuffer getAttriBuffer(AbstractRole player) ;
	
	/**吸收伤害,传入伤害总量,返回吸收伤害量*/
	public int hurtAbsorb(AbstractRole role,int hurts) ;
	
	public int cleanBuffById(AbstractRole role,Set<Short> buffs,int count);
	
	public void cleanBuffById(AbstractRole role);
	
	public int cleanBuffById(AbstractRole role,short buffId,int count);
	
	/**
	 * 切换英雄时删除某些buff
	 * @param role
	 */
	public void delBuffOnSwitchHero(AbstractRole role);
	
	/**
	 * 角色重置时恢复npc形象
	 * @param role
	 */
	public void recoverNpcShape(AbstractRole role);
	
}
