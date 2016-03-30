package sacred.alliance.magic.app.social;

import java.util.List;

import com.game.draco.message.response.C1210_SocialFlowerListRespMessage;
import com.game.draco.message.response.C1205_SocialFriendListRespMessage;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.social.config.SocialIntimateConfig;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.RoleSocialRelation;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

public interface SocialApp extends Service {
	
	/**
	 * 角色登录时初始化社交关系
	 * @param role
	 */
	public void login(RoleInstance role);
	
	/**
	 * 初始化角色的社交关系数据（内部action调用）
	 * @param roleId
	 * @param relationList
	 */
	public void initRoleSocialRelation(String roleId, List<RoleSocialRelation> relationList);
	
	/**
	 * 角色下线时社交数据入库
	 * @param role
	 */
	public void logout(RoleInstance role);
	
	/**
	 * 角色下线时清楚角色社交数据（内部action调用）
	 * @param roleId
	 */
	public void logoutRemoveRoleSocialRelation(String roleId);
	
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
	public Result friendReply(RoleInstance role, byte type, String inviterId);
	
	/**
	 * 删除好友
	 * @param role
	 * @param targRoleId
	 * @return
	 */
	public Result friendRemove(RoleInstance role, String targRoleId);
	
	/**
	 * 获取好友列表
	 * @param role
	 * @return
	 */
	public List<RoleSocialRelation> getFriendList(RoleInstance role);
	
	/**
	 * 将玩家加入黑名单
	 * @param role
	 * @param targRoleId
	 * @param targRoleName
	 * @return
	 */
	public Result blackApply(RoleInstance role, String targRoleId, String targRoleName);
	
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
	public List<RoleSocialRelation> getBlackList(RoleInstance role);
	
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
	 * 好友亲密度变化
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
	 * 获取好友列表的message
	 * @param role
	 * @return
	 */
	public C1205_SocialFriendListRespMessage getFriendListMessage(RoleInstance role);
	
	/**
	 * 获取社交说明
	 * @return
	 */
	public String getSocialDesc();
	
	
	public AttriBuffer getAttriBuffer(RoleInstance role);
	
}
