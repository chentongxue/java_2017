package com.game.draco.app.social.domain;

import java.util.Date;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.social.config.SocialIntimateConfig;
import com.game.draco.app.social.vo.SocialType;

public @Data
class DracoSocialRelation {

	public static final String ROLEID = "roleId";
	public static final String FRIENDID = "friendId";
	public static final String SOCIALTYPE = "socialType";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private String roleId;// 角色ID
	private String roleName = "";// 角色名称
	private byte camp;// 阵营
	private byte sex;// 性别
	private String friendId = "";
	private String friendName = "";
	private byte friendCamp;
	private byte friendSex;
	private byte socialType;// 社交关系类型
	private int intimate;// 亲密度
	private Date praiseTime;// 点赞时间
	private int friendHeadId;// 好友头像ID
	private int friendLevel;// 好友等级
	private byte online = 0;// 是否在线

	private boolean modify = false;// 是否修改过

	/**
	 * 是否是好友关系
	 * @return boolean
	 */
	public boolean isFriend() {
		if (SocialType.Friend.getType() == this.socialType) {
			return true;
		}
		return false;
	}

	/**
	 * 创建好友关系
	 * @param role
	 * @param targRole
	 */
	public void createFriend(RoleInstance role, RoleInstance targRole) {
		if (null == role || null == targRole) {
			return;
		}
		this.createRelation(role, targRole, SocialType.Friend);
	}

	/**
	 * 删除好友关系
	 */
	public void removeFriend() {
		this.removeRelation(this.roleId, this.friendId, SocialType.Friend);
		this.removeRelation(this.friendId, this.roleId, SocialType.Friend);
	}

	/**
	 * 创建黑名单关系 将对方加入到自己黑名单（屏蔽）
	 * @param role
	 * @param targRole
	 */
	public void createBlackList(RoleInstance role, RoleInstance targRole) {
		if (null == role || null == targRole) {
			return;
		}
		this.createRelation(role, targRole, SocialType.Blacklist);
	}

	/**
	 * 删除黑名单（屏蔽）
	 */
	public void removeBlackList() {
		this.removeRelation(this.roleId, this.friendId, SocialType.Blacklist);
	}

	/**
	 * 创建关系（直接入库）
	 * @param role
	 * @param targRole
	 * @param socialType
	 */
	private void createRelation(RoleInstance role, RoleInstance targRole, SocialType socialType) {
		this.roleId = role.getRoleId();
		this.friendId = targRole.getRoleId();
		this.friendHeadId = GameContext.getHeroApp().getRoleHeroHeadId(this.friendId);
		this.socialType = socialType.getType();
		GameContext.getSocialDAO().insert(this);
	}

	/**
	 * 解除关系（直接入库）
	 * @param roleId
	 * @param targRoleId
	 */
	private void removeRelation(String roleId, String targRoleId, SocialType socialType) {
		GameContext.getSocialDAO().delete(this.getClass(), ROLEID, roleId, FRIENDID, targRoleId, SOCIALTYPE, socialType.getType());
	}

	/**
	 * 获取亲密度所在等级信息
	 * @return
	 */
	public SocialIntimateConfig getIntimateConfig() {
		return GameContext.getSocialApp().getSocialIntimateConfig(this.intimate);
	}

	/**
	 * 获取亲密度等级
	 * @return
	 */
	public int getIntimateLevel() {
		return this.getIntimateConfig().getLevel();
	}

	/**
	 * 修改亲密度
	 * @param intimate
	 */
	public void changeIntimate(int intimate) {
		this.intimate = intimate;
		this.modify = true;
	}

	public void changePraiseTime(Date praiseTime) {
		this.praiseTime = praiseTime;
		this.modify = true;
	}

	public void setFriendCamp(byte friendCamp) {
		if (this.friendCamp != friendCamp) {
			this.friendCamp = friendCamp;
		}
	}

	public void setFriendName(String friendName) {
		if (!this.friendName.equals(friendName)) {
			this.friendName = friendName;
		}
	}

	public void setFriendSex(byte sex) {
		if (this.friendSex != sex) {
			this.friendSex = sex;
		}
	}

	public void setFriendLevel(int friendLevel) {
		if (this.friendLevel != friendLevel) {
			this.friendLevel = friendLevel;
		}
	}

	public void setFriendHeadId(int friendId) {
		if (this.friendHeadId != friendId) {
			this.friendHeadId = friendId;
			this.modify = true;
		}
	}

	public byte canPraise() {
		if (DateUtil.sameDay(this.praiseTime, new Date())) {
			return 0;
		}
		return 1;
	}

	/**
	 * 持久化（操作数据库）
	 */
	public void persistent() {
		try {
			if (!this.modify) {
				return;
			}
			GameContext.getSocialDAO().update(this);// 修改数据库
			this.modify = false;
		} catch (Exception e) {
			this.logger.error("RoleSocialRelation.persistent error: ", e);
		}
	}
}