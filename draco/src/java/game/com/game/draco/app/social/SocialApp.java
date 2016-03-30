package com.game.draco.app.social;

import java.util.List;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.social.config.SocialIntimateConfig;
import com.game.draco.app.social.domain.DracoSocialRelation;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.response.C1210_SocialFlowerListRespMessage;

public interface SocialApp extends Service, AppSupport {
	
	/**
	 * 获得好友列表
	 * @param role
	 * @return
	 */
	public List<DracoSocialRelation> getFriendList(RoleInstance role);
	
	/**
	 * 获得在线好友列表
	 * @param role
	 * @return
	 */
	public List<DracoSocialRelation> getSimpleFriendList(RoleInstance role);
	
	/**
	 * 请求添加好友
	 * @param role
	 * @param targetRole
	 * @return
	 */
	public Result friendApply(RoleInstance role, RoleInstance targetRole);
	
	/**
	 * 答复好友请求
	 * @param role
	 * @param type
	 * @param inviterId
	 * @return
	 */
	public Result friendReply(RoleInstance role, byte type, RoleInstance inviter);
	
	/**
	 * 删除好友
	 * @param role
	 * @param targRoleId
	 * @return
	 */
	public Result friendRemove(RoleInstance role, String targRoleId);
	
	/**
	 * 将玩家加入黑名单
	 * @param role
	 * @param targRoleId
	 * @param targRoleName
	 * @return
	 */
	public Result blackApply(RoleInstance role, RoleInstance targetRole);
	
	/**
	 * 将玩家从黑名单中移除
	 * @param role
	 * @param targRoleId
	 * @return
	 */
	public Result blackRemove(RoleInstance role, String targRoleId);
	
	/**
	 * 获取黑名单列表
	 * @param role
	 * @return
	 */
	public List<DracoSocialRelation> getBlackList(RoleInstance role);
	
	/**
	 * 判断是否被对方屏蔽
	 * @param roleId
	 * @param targetRoleId
	 * @return
	 */
	public boolean isShieldByTarget(String roleId, String targetRoleId);
	
	/**
	 * 获取角色好友数量
	 * @param role
	 * @return
	 */
	public int getFriendNumber(RoleInstance role);
	
	/**
	 * 获取角色黑名单数量
	 * @param role
	 * @return
	 */
	public int getBlacklistNumber(RoleInstance role);
	
	/**
	 * 是否为好友
	 * @param role
	 * @param targetRole
	 * @return
	 */
	public boolean isFirend(RoleInstance role, RoleInstance targRole);

	/**
	 * 改变好友亲密度
	 * @param role
	 * @param targRole
	 * @param intimate
	 */
	public void changeFriendIntimate(RoleInstance role, RoleInstance targRole, int intimate);

	/**
	 * 获取好友亲密度
	 * @param role
	 * @param targRole
	 * @return
	 */
	public int getFriendIntimate(RoleInstance role, RoleInstance targRole);
	
	/**
	 * 获取鲜花列表
	 * @param roleId 目标角色ID
	 * @return
	 */
	public C1210_SocialFlowerListRespMessage getFlowerListMessage(int roleId);
	
	/**
	 * 送花
	 * @param role
	 * @param roleId
	 * @param flowerId
	 * @return
	 */
	public Result giveFlower(RoleInstance role, int roleId, short flowerId);
	
	/**
	 * 推送结识仙友界面
	 * @param role
	 */
	public void pushFriendBatchViewMessage(RoleInstance role);
	
	/**
	 * 一键征友请求
	 * 获取推送好友的界面
	 * @param role
	 * @return
	 */
	public Result pushFriendBatchView(RoleInstance role);
	
	/**
	 * 批量添加好友
	 * @param role
	 * @param roleIds
	 */
	public void friendApply(RoleInstance role, int[] roleIds);
	
	/**
	 * 获取亲密度所在等级信息
	 * @param intimate
	 * @return
	 */
	public SocialIntimateConfig getSocialIntimateConfig(int intimate);
	
	/**
	 * 同步亲密度影响的属性
	 * @param role
	 */
	public void syncIntimateAttribute(RoleInstance role);
		
	/**
	 * 获取社交说明
	 * @return
	 */
	public String getSocialDesc();
	
	/**
	 * 被赞奖励礼包
	 * @return
	 */
	public GoodsLiteItem getPraiseGoodsInfo(RoleInstance role);
	
	/**
	 * 获取最大的可得到奖励的被赞次数
	 * @return
	 */
	public int getCanGetGoodsTimes();
	
	/**
	 * 点赞
	 * @return
	 */
	public Result givePraise(RoleInstance role, RoleInstance targRole);
	
	/**
	 * 领取被赞奖励礼包
	 * @return
	 */
	public Result getPraiseGoods(RoleInstance role);
	
	/**
	 * 请求传功
	 * @param role
	 * @param targetRole
	 * @return
	 */
	public Result transmissionApply(RoleInstance role, RoleInstance targetRole);
	
	/**
	 * 答复传功请求
	 * @param role
	 * @param type
	 * @param inviterId
	 * @return
	 */
	public Result transmissionReply(RoleInstance role, byte type, String inviterId);
	
	/**
	 * 获取与某位好友的关系对象
	 * @return
	 */
	public DracoSocialRelation getFriendRelation(String roleId, String targetRoleId);
	
	/**
	 * 获取亲密度加成
	 * @param role
	 * @return
	 */
	public byte getIntimateAddition(RoleInstance role);
	
}
