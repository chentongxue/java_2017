package com.game.draco.app.title;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTitle;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.title.domain.TitleRecord;

public interface TitleApp extends AppSupport{
	/**
	 * 最大激活称号数目
	 */
	public static final int MAX_ACTIVATE_TITLE_NUM = 4 ;
	
	/**
	 * 玩家是否已经存在此称号
	 * @param role
	 * @param titleId
	 * @return
	 */
	public boolean isExistTitle(RoleInstance role , int titleId);
	
	
	/**
	 * 玩家激活称号
	 * @param role
	 * @param titleId
	 * @return
	 */
	public Status activateTitle(RoleInstance role ,int titleId);
	
	/**
	 * 玩家取消称号
	 * @param role
	 * @param titleId
	 * @return
	 */
	public Status cancelTitle(RoleInstance role ,int titleId);
	
	/**
	 * 玩家增加称号
	 * @param role
	 * @param titleId
	 * @return
	 */
	public Status addTitle(RoleInstance role ,GoodsTitle goodsTitle,boolean isActivate);
	
	/**
	 * 得到玩家的称号记录
	 * @param role
	 * @param titleId
	 * @return
	 */
	public TitleRecord getRoleTitle(RoleInstance role, int titleId);

	/**
	 * 称号过期时调用
	 * @param role
	 */
	public void timeout(RoleInstance role);

	/**
	 * 称号影响的属性
	 * @param player
	 * @return
	 */
	public AttriBuffer getAttriBuffer(AbstractRole player);

	/**
	 * 被杀死时广播
	 * @param role
	 */
	public void killTitleBroadcast(AbstractRole attacker , RoleInstance victim);

	/**
	 * 存在激活的称号
	 * @param role
	 * @param titleId
	 * @return
	 */
	public boolean isExistEffectiveTitle(RoleInstance role, int titleId);


	/**
	 * 初始化称号类别
	 * @param goodsTitleList
	 */
	public void initTitleCategory(List<? extends GoodsBase> goodsTitleList) ;
	
	public Map<Integer,TitleCategory> getTitleCategoryMap() ;
	
	public TitleStatus getTitleStatus(RoleInstance role,int titleId);
	
	public void timeoutExec(RoleInstance role,List<TitleRecord> timeoutList);
	
}
