package com.game.draco.app.social;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.SkyEffectType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.TimeoutConstant;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.dao.impl.SocialDAOImpl;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.dailyplay.DailyPlayType;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.social.config.SocialFlowerConfig;
import com.game.draco.app.social.config.SocialFriendBatch;
import com.game.draco.app.social.config.SocialIntimateConfig;
import com.game.draco.app.social.config.SocialPraiseConfig;
import com.game.draco.app.social.config.SocialPraiseGoodsConfig;
import com.game.draco.app.social.config.SocialPraiseRecvConfig;
import com.game.draco.app.social.config.SocialTransmissionConfig;
import com.game.draco.app.social.config.SocialTransmissionLevelConfig;
import com.game.draco.app.social.config.SocialTransmissionLevelmConfig;
import com.game.draco.app.social.domain.DracoSocialRelation;
import com.game.draco.app.social.vo.SocialDate;
import com.game.draco.app.team.Team;
import com.game.draco.app.union.domain.Union;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.item.SocialFlowerItem;
import com.game.draco.message.item.SocialFriendBatchItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C0005_TipMultiNotifyMessage;
import com.game.draco.message.push.C1109_SkyEffectNotifyMessage;
import com.game.draco.message.push.C1216_SocialReceivePraiseRespMessage;
import com.game.draco.message.response.C1202_SocialFriendApplyForwardRespMessage;
import com.game.draco.message.response.C1210_SocialFlowerListRespMessage;
import com.game.draco.message.response.C1212_SocialFriendBatchViewRespMessage;
import com.game.draco.message.response.C1219_SocialTransmissionApplyRespMessage;
import com.google.common.collect.Maps;

public class SocialAppImpl implements SocialApp {

	private static final byte FriendAcceptType = 1;// 接受好友申请类型
	private static final byte TransmissionType = 1; // 接受传功类型
	private SocialDAOImpl socialDAO;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<String, ConcurrentHashMap<String, DracoSocialRelation>> socialFriendMap = Maps.newConcurrentMap();
	private Map<String, ConcurrentHashMap<String, DracoSocialRelation>> socialShieldMap = Maps.newConcurrentMap();
	private String socialDesc; // 说明配置
	private SocialFriendBatch batchFriendParam;// 批量加友参数
	private SocialIntimateConfig maxIntimateConfig;// 最高等级的亲密度配置
	private Map<Integer, SocialIntimateConfig> intimateMap = Maps.newHashMap();// 亲密度配置
	private Map<Short, SocialFlowerConfig> flowerMap = Maps.newHashMap();// 鲜花配置
	private SocialPraiseConfig praiseParam; // 点赞配置
	private Map<String, SocialPraiseRecvConfig> praiseRecvParam = Maps.newHashMap(); // 点赞奖励
	private Map<String, SocialPraiseGoodsConfig> praiseGoodsParam = Maps.newHashMap();// 被赞奖励配置
	private SocialTransmissionConfig transmissionParam; // 传功配置
	private Map<String, SocialTransmissionLevelConfig> transmissionLevelParam = Maps.newHashMap(); // 传功等级奖励
	private Map<String, SocialTransmissionLevelmConfig> transmissionLevelmParam = Maps.newHashMap(); // 传功等级差奖励
	private Map<String, SocialDate> socialDateMap = Maps.newConcurrentMap(); // 请求响应时间
	private static double MULTIPLE = 1.2;// 好友传功经验倍数

	public SocialDAOImpl getSocialDAO() {
		return socialDAO;
	}

	public void setSocialDAO(SocialDAOImpl socialDAO) {
		this.socialDAO = socialDAO;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		// 角色登录时初始化社交关系
		try {
			String roleId = role.getRoleId();
			this.socialFriendMap.put(roleId, new ConcurrentHashMap<String, DracoSocialRelation>());
			this.socialShieldMap.put(roleId, new ConcurrentHashMap<String, DracoSocialRelation>());
			List<DracoSocialRelation> relationList = this.socialDAO.selectFriendList(roleId);
			for (DracoSocialRelation relation : relationList) {
				if (null == relation) {
					continue;
				}
				if (relation.isFriend()) {
					this.socialFriendMap.get(roleId).put(relation.getFriendId(), relation);
					continue;
				}
				this.socialShieldMap.get(roleId).put(relation.getFriendId(), relation);
			}
			this.telFriendOnline(role);
		} catch (RuntimeException e) {
			this.logger.error("SocialApp.login error: ", e);
			return 0;
		}

		return 1;
	}

	private void telFriendOnline(RoleInstance role) {
		C0003_TipNotifyMessage message = new C0003_TipNotifyMessage();
		message.setMsgContext(Status.Social_Friend_Online.getTips().replace(Wildcard.Role_Name, role.getRoleName()));
		for (DracoSocialRelation relation : this.socialFriendMap.get(role.getRoleId()).values()) {
			if (null == relation) {
				continue;
			}
			String targetRoleId = relation.getFriendId();
			GameContext.getMessageCenter().sendByRoleId(null, targetRoleId, message);
		}
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		// 角色下线时社交数据入库
		String roleId = role.getRoleId();
		try {
			Map<String, DracoSocialRelation> friendMap = this.socialFriendMap.remove(roleId);
			if (null != friendMap) {
				for (DracoSocialRelation relation : friendMap.values()) {
					if (null == relation) {
						continue;
					}
					relation.persistent();
				}
			}
			this.onCleanup(roleId, context);
		} catch (Exception ex) {
			Log4jManager.OFFLINE_ERROR_LOG.error("social error,roleId=" + roleId + ",userId=" + role.getUserId(), ex);
			return 0;
		}
		return 1;
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		try {
			this.socialDateMap.remove(roleId);
			this.socialShieldMap.remove(roleId);
		} catch (Exception ex) {
			logger.error("Social app onCleanup error!" + roleId, ex);
			return 0;
		}
		return 1;
	}

	@Override
	public List<DracoSocialRelation> getFriendList(RoleInstance role) {
		String roleId = role.getRoleId();
		List<DracoSocialRelation> friendList = new ArrayList<DracoSocialRelation>();
		ConcurrentHashMap<String, DracoSocialRelation> roleRelationMap = socialFriendMap.get(roleId);
		if (Util.isEmpty(roleRelationMap)) {
			return friendList;
		}
		int index = 0; // 防止好友超过上限
		for (DracoSocialRelation relation : roleRelationMap.values()) {
			if (index > GameContext.getSocialConfig().getFriendMaxNum()) {
				break;
			}
			if (null == relation) {
				continue;
			}
			RoleInstance friendRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(relation.getFriendId());
			if (null == friendRole) {
				relation.setOnline((byte) 0);
				friendList.add(relation);
				continue;
			}
			relation.setOnline((byte) 1); // 设置当前好友在线
			relation.setFriendCamp(friendRole.getCampId());
			relation.setFriendName(friendRole.getRoleName());
			relation.setFriendSex(friendRole.getSex());
			relation.setFriendLevel(friendRole.getLevel());
			relation.setFriendHeadId(GameContext.getHeroApp().getRoleHeroHeadId(relation.getFriendId()));
			friendList.add(relation);
		}
		this.sortRelationList(friendList);
		return friendList;
	}

	@Override
	public List<DracoSocialRelation> getSimpleFriendList(RoleInstance role) {
		String roleId = role.getRoleId();
		List<DracoSocialRelation> friendList = new ArrayList<DracoSocialRelation>();
		ConcurrentHashMap<String, DracoSocialRelation> roleRelationMap = socialFriendMap.get(roleId);
		if (Util.isEmpty(roleRelationMap)) {
			return friendList;
		}
		int index = 0; // 防止好友超过上限
		for (DracoSocialRelation relation : roleRelationMap.values()) {
			if (index > GameContext.getSocialConfig().getFriendMaxNum()) {
				break;
			}
			if (null == relation) {
				continue;
			}
			RoleInstance friendRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(relation.getFriendId());
			if (null == friendRole) {
				continue;
			}
			relation.setFriendCamp(friendRole.getCampId());
			relation.setFriendName(friendRole.getRoleName());
			relation.setFriendSex(friendRole.getSex());
			relation.setFriendLevel(friendRole.getLevel());
			relation.setFriendHeadId(GameContext.getHeroApp().getRoleHeroHeadId(relation.getFriendId()));
			friendList.add(relation);
		}
		this.sortRelationList(friendList);
		return friendList;
	}

	private void sortRelationList(List<DracoSocialRelation> relationList) {
		Collections.sort(relationList, new Comparator<DracoSocialRelation>() {
			@Override
			public int compare(DracoSocialRelation item1, DracoSocialRelation item2) {
				byte online1 = item1.getOnline();
				byte online2 = item2.getOnline();
				if (online1 > online2) {
					return -1;
				}
				if (online1 < online2) {
					return 1;
				}
				int intimate1 = item1.getIntimate();
				int intimate2 = item2.getIntimate();
				if (intimate1 > intimate2) {
					return -1;
				}
				if (intimate1 < intimate2) {
					return 1;
				}
				int level1 = item1.getFriendLevel();
				int level2 = item2.getFriendLevel();
				if (level1 > level2) {
					return -1;
				}
				if (level1 < level2) {
					return 1;
				}
				return 0;
			}
		});
	}

	@Override
	public Result friendApply(RoleInstance role, RoleInstance targetRole) {
		// 好友请求成功之后，给目标角色转发弹板消息
		Result result = this.doFriendApply(role, targetRole);
		if (result.isSuccess()) {
			C1202_SocialFriendApplyForwardRespMessage forwardMsg = new C1202_SocialFriendApplyForwardRespMessage();
			forwardMsg.setRoleId(role.getIntRoleId());
			forwardMsg.setRoleName(role.getRoleName());
			this.sendMessage(targetRole, forwardMsg);
		}
		return result;
	}

	private Result doFriendApply(RoleInstance role, RoleInstance targetRole) {
		Result result = new Result();
		if (null == role) {
			return result.setInfo(Status.Social_Error.getTips());
		}
		if (null == targetRole) {
			return result.setInfo(Status.Social_TargRole_Offline.getTips());
		}
		if (this.isFriendListFull(role)) {
			return result.setInfo(Status.Social_Friend_IsFull.getTips());
		}
		long currTime = System.currentTimeMillis();
		if (currTime - this.getFriendApplyTime(targetRole.getRoleId()) < TimeoutConstant.Social_Friend_Reply_Time) {
			return result.setInfo(Status.Social_TargRole_Busy.getTips());
		}
		DracoSocialRelation relation = this.getFriendRelation(role.getRoleId(), targetRole.getRoleId());
		if (null != relation) {
			String targRoleName = targetRole.getRoleName();
			return result.setInfo(targRoleName + Status.Social_Already_Friend.getTips());
		}
		// 给目标角色设置申请时间，等待目标角色处理
		this.setFriendApplyTime(targetRole.getRoleId(), currTime);
		return result.success();
	}

	@Override
	public Result friendReply(RoleInstance role, byte type, RoleInstance inviter) {
		Result result = this.doFriendReply(role, type, inviter);
		return result;
	}

	private Result doFriendReply(RoleInstance role, byte type, RoleInstance inviter) {
		// 角色处理社交请求时，重置操作时间为0
		this.setFriendApplyTime(role.getRoleId(), 0);
		Result result = new Result();
		String roleName = role.getRoleName();
		C0003_TipNotifyMessage tipsToRole = new C0003_TipNotifyMessage();// 浮动提示
		C0003_TipNotifyMessage tipsToInviter = new C0003_TipNotifyMessage();// 浮动提示
		// 拒绝添加好友
		if (FriendAcceptType != type) {
			tipsToInviter.setMsgContext(Status.Social_Friend_Refuse_Tip.getTips().replace(Wildcard.Role_Name, roleName));
			this.sendMessage(inviter, tipsToInviter);
			return result.success();
		}
		// 判断自己的好友上限
		if (this.isFriendListFull(role)) {
			return result.setInfo(Status.Social_Friend_IsFull.getTips());
		}
		// 判断对方的好友上限
		if (this.isFriendListFull(inviter)) {
			return result.setInfo(Status.Social_Targ_Friend_IsFull.getTips());
		}
		String roleId = role.getRoleId();
		String inviterId = inviter.getRoleId();
		DracoSocialRelation relation = new DracoSocialRelation();
		relation.createFriend(role, inviter);
		this.socialFriendMap.get(roleId).put(inviterId, relation);

		DracoSocialRelation relationTarget = new DracoSocialRelation();
		relationTarget.createFriend(inviter, role);
		this.socialFriendMap.get(inviterId).put(roleId, relationTarget);

		// 发送添加好友成功消息
		tipsToInviter.setMsgContext(Status.Social_Friend_Add_Tip.getTips().replace(Wildcard.Role_Name, roleName));
		this.sendMessage(inviter, tipsToInviter);
		String targetName = inviter.getRoleName();
		tipsToRole.setMsgContext(Status.Social_Friend_Add_Tip.getTips().replace(Wildcard.Role_Name, targetName));
		this.sendMessage(role, tipsToRole);
		return result.success();
	}

	private boolean isFriendListFull(RoleInstance role) {
		int maxFriend = GameContext.getSocialConfig().getFriendMaxNum();
		return this.getFriendNumber(role) > maxFriend;
	}

	@Override
	public Result friendRemove(RoleInstance role, String targRoleId) {
		Result result = new Result();
		String roleId = role.getRoleId();
		// 没有社交关系，提示成功
		if (!this.socialFriendMap.containsKey(roleId)) {
			return result.success();
		}
		// 自己和对方没有社交关系，也提示成功
		DracoSocialRelation relation = this.getFriendRelation(roleId, targRoleId);
		if (null == relation) {
			return result.success();
		}

		// 解除好友关系
		relation.removeFriend();// 删除数据库中的两条记录
		this.socialFriendMap.get(roleId).remove(targRoleId);
		RoleInstance targetRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(targRoleId);
		if (null != targetRole) {
			this.socialFriendMap.get(targRoleId).remove(roleId);
		}

		// 给对方发邮件提示好友断交
		try {
			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
			mail.setTitle(Status.Social_Break_Friend_Title.getTips());
			mail.setSendRole(MailSendRoleType.Social_Friend.getName());
			mail.setContent(Status.Social_Break_Friend_content.getTips().replace(Wildcard.Role_Name, role.getRoleName()));
			mail.setRoleId(targRoleId);
			GameContext.getMailApp().sendMail(mail);
		} catch (Exception e) {
			this.logger.error("SocialApp.friendRemove error: ", e);
		}
		return result.success();
	}

	@Override
	public List<DracoSocialRelation> getBlackList(RoleInstance role) {
		List<DracoSocialRelation> blackList = new ArrayList<DracoSocialRelation>();
		ConcurrentHashMap<String, DracoSocialRelation> socialMap = this.socialShieldMap.get(role.getRoleId());
		if (Util.isEmpty(socialMap)) {
			return blackList;
		}
		int index = 0;
		for (DracoSocialRelation relation : socialMap.values()) {
			// 长度超过了黑名单的最大数值（index的值过大出线上现过bug）
			if (index > GameContext.getSocialConfig().getBlacklistMaxNum()) {
				break;
			}
			if (null == relation) {
				continue;
			}
			blackList.add(relation);
			index++;
		}
		this.sortRelationList(blackList);
		return blackList;
	}

	@Override
	public Result blackApply(RoleInstance role, RoleInstance targetRole) {
		Result result = new Result();
		if (this.isBlacklistFull(role)) {
			return result.setInfo(Status.Social_Black_IsFull.getTips());
		}
		String roleId = role.getRoleId();
		if (this.isShieldTarget(role, targetRole)) {
			return result.setInfo(Status.Social_Black_Already.getTips().replace(Wildcard.Role_Name, targetRole.getRoleName()));
		}

		// 建立新的关系，将对方加入黑名单中
		DracoSocialRelation relation = new DracoSocialRelation();
		relation.createBlackList(role, targetRole);
		this.socialShieldMap.get(roleId).put(targetRole.getRoleId(), relation);
		return result.success();
	}

	private boolean isShieldTarget(RoleInstance role, RoleInstance targetRole) {
		DracoSocialRelation relation = this.getShieldRelation(role.getRoleId(), targetRole.getRoleId());
		if (null == relation) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isShieldByTarget(String roleId, String targetRoleId) {
		DracoSocialRelation relation = this.getShieldRelation(targetRoleId, roleId);
		if (null == relation) {
			return false;
		}
		return true;
	}

	private boolean isBlacklistFull(RoleInstance role) {
		int maxBlack = GameContext.getSocialConfig().getBlacklistMaxNum();
		return this.getBlacklistNumber(role) > maxBlack;
	}

	@Override
	public int getBlacklistNumber(RoleInstance role) {
		return this.socialShieldMap.size();
	}

	@Override
	public Result blackRemove(RoleInstance role, String targRoleId) {
		Result result = new Result();
		String roleId = role.getRoleId();
		// 没有社交关系，提示成功
		if (!this.socialShieldMap.containsKey(roleId)) {
			return result.success();
		}
		// 自己和对方没有社交关系，也提示成功
		DracoSocialRelation relation = this.getShieldRelation(roleId, targRoleId);
		if (null == relation) {
			return result.success();
		}
		// 将对方从屏蔽中删除
		relation.removeBlackList();
		this.socialShieldMap.get(role.getRoleId()).remove(targRoleId);
		return result.success();
	}

	private DracoSocialRelation getRoleSocialRelation(RoleInstance role, RoleInstance targRole) {
		if (null == role || null == targRole) {
			return null;
		}
		Map<String, DracoSocialRelation> relationMap = this.socialFriendMap.get(role.getRoleId());
		if (Util.isEmpty(relationMap)) {
			return null;
		}
		return relationMap.get(targRole.getRoleId());
	}

	@Override
	public int getFriendNumber(RoleInstance role) {
		return this.socialFriendMap.get(role.getRoleId()).size();
	}

	@Override
	public void changeFriendIntimate(RoleInstance role, RoleInstance targRole, int intimate) {
		try {
			if (null == role || null == targRole || intimate <= 0) {
				return;
			}
			DracoSocialRelation relation = this.getRoleSocialRelation(role, targRole);
			if (null == relation || !relation.isFriend()) {
				return;
			}
			int oldIntimate = relation.getIntimate();
			int newIntimate = oldIntimate + intimate;
			// 容错，超过系统最大亲密度，则设置为最大值
			if (newIntimate > this.maxIntimateConfig.getMaxIntimate()) {
				newIntimate = this.maxIntimateConfig.getMaxIntimate();
				intimate = newIntimate - oldIntimate;
			}
			// 修改亲密度
			relation.changeIntimate(newIntimate);
			this.getFriendRelation(targRole.getRoleId(), role.getRoleId()).changeIntimate(newIntimate);
			// 同步亲密度影响的属性
			this.syncIntimateAttribute(role);
			this.syncIntimateAttribute(targRole);
			// 飘字提示亲密度增加
			this.notifyIntimateChange(role, targRole, intimate);
		} catch (RuntimeException e) {
			this.logger.error("SocialApp.changeFriendIntimate error: ", e);
		}
	}

	private void notifyIntimateChange(RoleInstance role, RoleInstance targRole, int intimate) {
		try {
			String msgContext = Status.Social_Friend_Intimate_Change_Tip.getTips().replace(Wildcard.Number, String.valueOf(intimate));
			this.sendMessage(role, new C0003_TipNotifyMessage(msgContext.replace(Wildcard.Role_Name, targRole.getRoleName())));
			this.sendMessage(targRole, new C0003_TipNotifyMessage(msgContext.replace(Wildcard.Role_Name, role.getRoleName())));
		} catch (RuntimeException e) {
			this.logger.error("SocialApp.notifyIntimateChange error: ", e);
		}
	}

	@Override
	public boolean isFirend(RoleInstance role, RoleInstance targRole) {
		DracoSocialRelation relation = this.getRoleSocialRelation(role, targRole);
		if (null == relation) {
			return false;
		}
		return relation.isFriend();
	}

	@Override
	public int getFriendIntimate(RoleInstance role, RoleInstance targRole) {
		DracoSocialRelation relation = this.getRoleSocialRelation(role, targRole);
		if (null == relation) {
			return 0;
		}
		return relation.getIntimate();
	}

	@Override
	public void start() {
		String xlsPath = GameContext.getPathConfig().getXlsPath();
		this.loadDescConfig(xlsPath);
		this.loadFlowerConfig(xlsPath);
		this.loadFriendBatchConfig(xlsPath);
		this.loadIntimateConfig(xlsPath);
		this.loadPraiseConfig1(xlsPath);
		this.loadPraiseGoodsConfig(xlsPath);
		this.loadPraiseRecvConfig(xlsPath);
		this.loadTransmissionConfig1(xlsPath);
		this.loadTransmissionLevelConfig(xlsPath);
		this.loadTransmissionLevelmConfig(xlsPath);
	}

	private void loadIntimateConfig(String xlsPath) {
		String fileName = XlsSheetNameType.social_intimate.getXlsName();
		String sheetName = XlsSheetNameType.social_intimate.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			// 加载亲密度配置
			List<SocialIntimateConfig> imList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, SocialIntimateConfig.class);
			// 配置信息按等级从低到高排序
			this.sortIntimateConfigList(imList);
			for (SocialIntimateConfig config : imList) {
				if (null == config) {
					continue;
				}
				config.init(info);
				int level = config.getLevel();
				this.intimateMap.put(level, config);
			}
			// 设置最大亲密度
			this.maxIntimateConfig = imList.get(imList.size() - 1);
			if (null == this.maxIntimateConfig) {
				this.checkFail("load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".The maxIntimate is config error!");
			}
		} catch (Exception e) {
			logger.error(info, e);
			Log4jManager.checkFail();
		}
	}

	private void sortIntimateConfigList(List<SocialIntimateConfig> list) {
		Collections.sort(list, new Comparator<SocialIntimateConfig>() {
			@Override
			public int compare(SocialIntimateConfig conf1, SocialIntimateConfig conf2) {
				int level1 = conf1.getLevel();
				int level2 = conf2.getLevel();
				if (level1 < level2) {
					return -1;
				}
				if (level1 > level2) {
					return 1;
				}
				return 0;
			}
		});
	}

	private void loadFlowerConfig(String xlsPath) {
		String fileName = XlsSheetNameType.social_flower.getXlsName();
		String sheetName = XlsSheetNameType.social_flower.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			List<SocialFlowerConfig> flwList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, SocialFlowerConfig.class);
			for (SocialFlowerConfig flower : flwList) {
				if (null == flower) {
					continue;
				}
				flower.init(info);
				this.flowerMap.put(flower.getFlowerId(), flower);
			}
		} catch (Exception e) {
			logger.error(info, e);
			Log4jManager.checkFail();
		}
	}

	private void loadDescConfig(String xlsPath) {
		String fileName = XlsSheetNameType.social_desc.getXlsName();
		String sheetName = XlsSheetNameType.social_desc.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			List<String> descList = XlsPojoUtil.sheetToStringList(xlsPath + fileName, sheetName);
			this.socialDesc = descList.get(0);
			if (Util.isEmpty(this.socialDesc)) {
				this.checkFail("load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".The socialDesc is not config!");
			}
		} catch (Exception e) {
			logger.error(info, e);
			Log4jManager.checkFail();
		}
	}

	private void loadFriendBatchConfig(String xlsPath) {
		String fileName = XlsSheetNameType.social_batch_friend.getXlsName();
		String sheetName = XlsSheetNameType.social_batch_friend.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.batchFriendParam = XlsPojoUtil.getEntity(xlsPath + fileName, sheetName, SocialFriendBatch.class);
			if (null == this.batchFriendParam) {
				this.checkFail("load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".The batch_friend is not config!");
			}
			this.batchFriendParam.init(info);
		} catch (Exception e) {
			logger.error(info, e);
			Log4jManager.checkFail();
		}
	}

	private void loadPraiseConfig1(String xlsPath) {
		String fileName = XlsSheetNameType.social_praise.getXlsName();
		String sheetName = XlsSheetNameType.social_praise.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			// 加载点赞配置
			this.praiseParam = XlsPojoUtil.getEntity(xlsPath + fileName, sheetName, SocialPraiseConfig.class);
			if (null == this.praiseParam) {
				this.checkFail("load excel error:fileName=" + fileName + ",sheetName" + sheetName + ".The praise is not config!");
			}
			this.praiseParam.init(info);
		} catch (Exception e) {
			logger.error(info, e);
			Log4jManager.checkFail();
		}
	}

	private void loadPraiseRecvConfig(String xlsPath) {
		String fileName = XlsSheetNameType.social_praise_recv.getXlsName();
		String sheetName = XlsSheetNameType.social_praise_recv.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.praiseRecvParam = XlsPojoUtil.sheetToMap(xlsPath + fileName, sheetName, SocialPraiseRecvConfig.class);
			for (SocialPraiseRecvConfig recv : this.praiseRecvParam.values()) {
				if (null == recv) {
					continue;
				}
				recv.init(info);
			}
		} catch (Exception e) {
			logger.error(info, e);
			Log4jManager.checkFail();
		}
	}

	private void loadPraiseGoodsConfig(String xlsPath) {
		String fileName = XlsSheetNameType.social_praise_goods.getXlsName();
		String sheetName = XlsSheetNameType.social_praise_goods.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.praiseGoodsParam = XlsPojoUtil.sheetToMap(xlsPath + fileName, sheetName, SocialPraiseGoodsConfig.class);
			for (SocialPraiseGoodsConfig goods : this.praiseGoodsParam.values()) {
				if (null == goods) {
					continue;
				}
				goods.init(info);
			}
		} catch (Exception e) {
			logger.error(info, e);
			Log4jManager.checkFail();
		}
	}

	private void loadTransmissionConfig1(String xlsPath) {
		String fileName = XlsSheetNameType.social_transmission.getXlsName();
		String sheetName = XlsSheetNameType.social_transmission.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.transmissionParam = XlsPojoUtil.getEntity(xlsPath + fileName, sheetName, SocialTransmissionConfig.class);
			if (null == this.transmissionParam) {
				this.checkFail("load excel error:fileName=" + fileName + ",sheetName" + sheetName + ".The transmission is not config!");
			}
			this.transmissionParam.init(info);
		} catch (Exception e) {
			logger.error(info, e);
			Log4jManager.checkFail();
		}
	}

	private void loadTransmissionLevelConfig(String xlsPath) {
		String fileName = XlsSheetNameType.social_transmission_level.getXlsName();
		String sheetName = XlsSheetNameType.social_transmission_level.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.transmissionLevelParam = XlsPojoUtil.sheetToMap(xlsPath + fileName, sheetName, SocialTransmissionLevelConfig.class);
			for (SocialTransmissionLevelConfig level : this.transmissionLevelParam.values()) {
				if (null == level) {
					continue;
				}
				level.init(info);
			}
		} catch (Exception e) {
			logger.error(info, e);
			Log4jManager.checkFail();
		}
	}

	private void loadTransmissionLevelmConfig(String xlsPath) {
		String fileName = XlsSheetNameType.social_transmission_levelm.getXlsName();
		String sheetName = XlsSheetNameType.social_transmission_levelm.getSheetName();
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.transmissionLevelmParam = XlsPojoUtil.sheetToMap(xlsPath + fileName, sheetName, SocialTransmissionLevelmConfig.class);
			for (SocialTransmissionLevelmConfig levelm : this.transmissionLevelmParam.values()) {
				if (null == levelm) {
					continue;
				}
				levelm.init(info);
			}
		} catch (Exception e) {
			logger.error(info, e);
			Log4jManager.checkFail();
		}
	}

	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

	@Override
	public C1210_SocialFlowerListRespMessage getFlowerListMessage(int roleId) {
		List<SocialFlowerItem> flowerList = new ArrayList<SocialFlowerItem>();
		for (SocialFlowerConfig flower : this.flowerMap.values()) {
			SocialFlowerItem item = new SocialFlowerItem();
			item.setFlowerId(flower.getFlowerId());
			item.setName(flower.getFlowerName());
			item.setIconId(flower.getIconId());
			item.setMoneyType(flower.getMoneyType());
			item.setMoney(flower.getMoney());
			item.setIntimate(flower.getIntimate());
			item.setInfo(flower.getInfo());
			item.setGoodsId(flower.getGoodsId());
			flowerList.add(item);
		}
		C1210_SocialFlowerListRespMessage message = new C1210_SocialFlowerListRespMessage();
		message.setRoleId(roleId);
		message.setFlowerList(flowerList);
		return message;
	}

	@Override
	public Result giveFlower(RoleInstance role, int roleId, short flowerId) {
		Result result = new Result();
		boolean isFirend = true;
		try {
			RoleInstance targRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
			if (null == targRole) {
				return result.setInfo(Status.Social_TargRole_Offline.getTips());
			}
			SocialFlowerConfig flower = this.getSocialFlowerConfig(flowerId);
			if (null == flower) {
				return result.setInfo(Status.Social_Error.getTips());
			}
			if (!this.isFirend(role, targRole)) {
				isFirend = false;
			}
			int goodsId = flower.getGoodsId();
			Result goodsRes = null;
			// 优先扣除物品
			if (goodsId > 0) {
				goodsRes = GameContext.getUserGoodsApp().deleteForBag(role, goodsId, 1, OutputConsumeType.social_flower_consume);
			}
			// 物品扣除失败，需要扣钱
			if (null == goodsRes || !goodsRes.isSuccess()) {
				int money = flower.getMoney();
				AttributeType attrType = flower.getFlowerMoneyType();
				// 【游戏币/潜能/钻石不足弹板】 判断
				Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, attrType, money);
				if (ar.isIgnore()) {// 弹板
					return ar;
				}
				if (!ar.isSuccess()) {// 不足
					return result.setInfo(Status.Social_Gold_Not_Enough.getTips().replace(Wildcard.AttrName, attrType.getName()));
				}
				// 扣钱
				GameContext.getUserAttributeApp().changeRoleMoney(role, attrType, OperatorType.Decrease, money, OutputConsumeType.social_flower_consume);
				// 通知客户端属性变化
				role.getBehavior().notifyAttribute();
			}
			if (isFirend) {
				// 加亲密度
				this.changeFriendIntimate(role, targRole, flower.getIntimate());
			}
			try {
				// 统计收到鲜花的次数
				GameContext.getCountApp().receiveFlower(targRole, flower.getFlowerNum());
			} catch (RuntimeException e) {
				this.logger.error("", e);
			}
			// 播放天空特效，广播消息
			this.sendSkyEffectMessage(role, targRole, flower);
			return result.success();
		} catch (Exception e) {
			this.logger.error("SocialApp.giveFlower error: ", e);
			return result.setInfo(Status.Social_Error.getTips());
		}
	}

	@Override
	public Result givePraise(RoleInstance role, RoleInstance targetRole) {
		Result result = new Result();
		DracoSocialRelation relation = this.getFriendRelation(role.getRoleId(), targetRole.getRoleId());
		if (null == relation) {
			return result.setInfo(Status.Social_Praise_Not_Friend.getTips());
		}
		Date now = new Date();
		if (DateUtil.sameDay(now, relation.getPraiseTime())) {
			// 如果今天已经点过赞
			return result.setInfo(Status.Social_Praise_Already.getTips());
		}
		this.addPraiseTimes(role, targetRole);// 更改玩家点赞次数和目标被赞次数
		relation.changePraiseTime(now);
		result.setInfo(GameContext.getI18n().messageFormat(TextId.Social_Give_Praise, targetRole.getRoleName()));
		// 活跃度
		GameContext.getDailyPlayApp().incrCompleteTimes(role, 1, DailyPlayType.friend_praise, "");
		if (role.getRoleCount().getRoleTimesToInt(CountType.TodayPraiseTimes) > this.praiseParam.getCanHaveRecvTimes()) {
			// 如果今日点赞次数超出可领奖上限
			this.sendPraiseMessage(role, targetRole);
			return result.success();
		}
		// 发放点赞奖励
		SocialPraiseRecvConfig config = this.getPraiseRecvConfig(role.getLevel());
		if (null == config) {
			return result.success();
		}
		role.getBehavior().changeAttribute(AttributeType.exp, OperatorType.Add, config.getExp());
		role.getBehavior().changeAttribute(AttributeType.potential, OperatorType.Add, config.getProf());
		GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, OperatorType.Add, config.getGold(), OutputConsumeType.social_praise_consume);
		role.getBehavior().notifyAttribute();
		// 通知玩家
		this.sendPraiseMessage(role, targetRole);
		return result.success();
	}

	/**
	 * 增加赞和被赞次数
	 * @param role
	 * @param targetRole
	 */
	private void addPraiseTimes(RoleInstance role, RoleInstance targetRole) {
		role.getRoleCount().changeTimes(CountType.TodayPraiseTimes);//.incrTodayPraiseTimes();
		role.getRoleCount().changeTimes(CountType.PraiseTimes);//incrPraiseTimes();
		targetRole.getRoleCount().changeTimes(CountType.TodayReceivePraiseTimes);//.incrTodayReceivePraiseTimes();
		targetRole.getRoleCount().changeTimes(CountType.ReceivePraiseTimes);//.incrReceivePraiseTimes();
	}
	
	/**
	 * 通知目标被赞成功
	 * @param role
	 * @param target
	 */
	private void sendPraiseMessage(RoleInstance role, RoleInstance target) {
		try {
			C1216_SocialReceivePraiseRespMessage resp = new C1216_SocialReceivePraiseRespMessage();
			resp.setReceiveNum(target.getRoleCount().getRoleTimesToInt(CountType.TodayReceivePraiseTimes));//.getTodayReceivePraiseTimes());
			resp.setRoleName(role.getRoleName());
			target.getBehavior().sendMessage(resp);
		} catch (Exception e) {
			logger.error("SocialAppImpl sendPraiseMessage error!", e);
		}
	}

	/**
	 * 播放天空特效
	 * @param role
	 * @param targRole
	 * @param flower
	 */
	private void sendSkyEffectMessage(RoleInstance role, RoleInstance targRole, SocialFlowerConfig flower) {
		try {
			String broadcastInfo = flower.getBroadcastInfo();// 广播消息
			if (!Util.isEmpty(broadcastInfo)) {
				// 替换通配符
				broadcastInfo = broadcastInfo.replace(Wildcard.Sender, role.getRoleName()).replace(Wildcard.Receiver, targRole.getRoleName());
			}
			SkyEffectType effectType = flower.getSkyEffectType();
			Collection<RoleInstance> receviers = new ArrayList<RoleInstance>();
			switch (effectType) {
			case Both_Role:
				receviers.add(role);
				receviers.add(targRole);
				// 需要给双方发系统消息
				if (!Util.isEmpty(broadcastInfo)) {
					GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.System, broadcastInfo, null, role);
					GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.System, broadcastInfo, null, targRole);
				}
				break;
			case Both_Map:
				MapInstance mapIns = role.getMapInstance();
				receviers.addAll(mapIns.getRoleList());
				// 给双方所在地图发消息
				GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Map, broadcastInfo, null, mapIns);
				if (!role.getMapId().equals(targRole.getMapId())) {
					MapInstance targMapIns = targRole.getMapInstance();
					receviers.addAll(targMapIns.getRoleList());
					GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Map, broadcastInfo, null, targMapIns);
				}
				break;
			case World:
				receviers = GameContext.getOnlineCenter().getAllOnlineRole();
				// 发走马灯消息
				GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, broadcastInfo, null, null);
				break;
			}
			// 一次发特效消息
			C1109_SkyEffectNotifyMessage message = new C1109_SkyEffectNotifyMessage();
			message.setEffectId(flower.getEffect());
			message.setTime(flower.getTime());
			for (RoleInstance recevier : receviers) {
				if (null == recevier) {
					continue;
				}
				this.sendMessage(recevier, message);
			}
		} catch (RuntimeException e) {
			this.logger.error("SocialApp.sendSkyEffectMessage error: ", e);
		}
	}

	/**
	 * 给角色发消息
	 * 
	 * @param recevier
	 * @param message
	 */
	private void sendMessage(RoleInstance recevier, Message message) {
		GameContext.getMessageCenter().sendSysMsg(recevier, message);
	}

	@Override
	public Result pushFriendBatchView(RoleInstance role) {
		Result result = new Result();
		int remainNum = this.batchFriendParam.getCount() - role.getRoleCount().getRoleTimesToInt(CountType.FriendBatchNum);//.getFriendBatchNum();
		if (remainNum <= 0) {
			return result.setInfo(Status.Social_Batch_Friend_Num_Full.getTips());
		}
		if (this.isFriendListFull(role)) {
			return result.setInfo(Status.Social_Friend_IsFull.getTips());
		}
		List<RoleInstance> roleList = this.searchBatchRoleList(role);
		// 推送一键加友消息
		this.pushFriendBatchViewMessage(role, roleList);
		// 统计一键加友次数
		role.getRoleCount().changeTimes(CountType.FriendBatchNum);//.incrFriendBatchNum();
		// 飘字提示剩余次数
		String msgContext = Status.Social_Batch_Friend_Remain_Num.getTips().replace(Wildcard.Number, String.valueOf(remainNum - 1));
		this.sendMessage(role, new C0003_TipNotifyMessage(msgContext));
		return result.success();
	}

	/**
	 * 结识仙友筛选玩家
	 * 
	 * @param role
	 * @return
	 */
	private List<RoleInstance> searchBatchRoleList(RoleInstance role) {
		List<RoleInstance> roleList = new ArrayList<RoleInstance>();
		Collection<RoleInstance> collection = GameContext.getOnlineCenter().getAllOnlineRole();
		String roleId = role.getRoleId();
		int roleLevel = role.getLevel();
		int differLevel = this.batchFriendParam.getLevel();// 浮动等级
		int number = this.batchFriendParam.getRoleNum();// 需要的总人数
		int index = 0;
		for (RoleInstance instance : collection) {
			if (index >= number) {
				break;
			}
			if (null == instance) {
				continue;
			}
			String instanceId = instance.getRoleId();
			if (roleId.equals(instanceId)) {
				continue;
			}
			if (this.isFirend(role, instance)) {
				continue;
			}
			int absValue = Math.abs(instance.getLevel() - roleLevel);
			if (absValue > differLevel) {
				continue;
			}
			roleList.add(instance);
			index++;
		}
		return roleList;
	}

	@Override
	public void pushFriendBatchViewMessage(RoleInstance role) {
		List<RoleInstance> roleList = this.searchBatchRoleList(role);
		this.pushFriendBatchViewMessage(role, roleList);
	}

	/**
	 * 推送结识仙友面板
	 * 
	 * @param role
	 * @param roleList
	 */
	private void pushFriendBatchViewMessage(RoleInstance role, List<RoleInstance> roleList) {
		try {
			List<SocialFriendBatchItem> batchViewList = new ArrayList<SocialFriendBatchItem>();
			C1212_SocialFriendBatchViewRespMessage message = new C1212_SocialFriendBatchViewRespMessage();
			if (Util.isEmpty(roleList)) {
				message.setBatchViewList(batchViewList);
				this.sendMessage(role, message);
			}
			for (RoleInstance instance : roleList) {
				if (null == instance) {
					continue;
				}
				SocialFriendBatchItem item = new SocialFriendBatchItem();
				item.setRoleId(instance.getIntRoleId());
				item.setRoleName(instance.getRoleName());
				item.setSex(instance.getSex());
				item.setCamp(instance.getCampId());
				item.setRoleLevel((byte) instance.getLevel());
				item.setHeadId(GameContext.getHeroApp().getRoleHeroHeadId(instance.getRoleId()));
				Union union = GameContext.getUnionApp().getUnion(instance);
				if (null != union) {
					item.setUnionName(union.getUnionName());
				}
				batchViewList.add(item);
			}
			message.setBatchViewList(batchViewList);
			// 发消息
			this.sendMessage(role, message);
		} catch (RuntimeException e) {
			this.logger.error("SocialApp.pushFriendBatchViewMessage error: ", e);
		}
	}

	@Override
	public void friendApply(RoleInstance role, int[] roleIds) {
		if (null == roleIds || 0 == roleIds.length) {
			return;
		}
		for (int roleId : roleIds) {
			if (roleId <= 0) {
				continue;
			}
			RoleInstance targRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
			// 依次发送好友申请
			this.friendApply(role, targRole);
		}
	}

	@Override
	public SocialIntimateConfig getSocialIntimateConfig(int intimate) {
		// 只要超过最高等级的下限，就返回最高等级的配置
		if (intimate >= this.maxIntimateConfig.getMaxIntimate()) {
			return this.maxIntimateConfig;
		}
		for (SocialIntimateConfig config : this.intimateMap.values()) {
			if (null == config) {
				continue;
			}
			if (config.isWithin(intimate)) {
				return config;
			}
		}
		return null;
	}

	@Override
	public void syncIntimateAttribute(RoleInstance role) {
		try {
			Team team = role.getTeam();
			// 若当前没有队伍或者已下线，清除亲密度所加的属性；若当前有队伍，根据队伍变化判断亲密度属性是否改变。
			if (null == team || team.getPlayerNum() <= 1) {
				this.clearIntimateAttribute(role);
				return;
			}
			// 如果玩家离线，buff离线移除
			if (team.getOfflineMembers().containsKey(role.getRoleId())) {
				return;
			}
			// 找到最大的亲密度
			int maxIntimate = -1;
			for (AbstractRole member : team.getMembers()) {
				DracoSocialRelation relation = this.getRoleSocialRelation(role, (RoleInstance) member);
				if (null == relation || !relation.isFriend()) {
					continue;
				}
				int intimate = relation.getIntimate();
				if (intimate > maxIntimate) {
					maxIntimate = intimate;
				}
			}
			if (-1 == maxIntimate) {
				this.clearIntimateAttribute(role);
				return;
			}
			SocialIntimateConfig config = this.getSocialIntimateConfig(maxIntimate);
			if (null == config) {
				return;
			}
			int level = config.getLevel();
			int oldLevel = role.getMaxIntimateLevel();
			if (level == oldLevel) {
				return;
			}
			// 添加buff
			GameContext.getUserBuffApp().addBuffStat(role, role, (short) config.getBuffId(), config.getBuffLevel());
			// 记录最大亲密度等级
			role.setMaxIntimateLevel(config.getLevel());
		} catch (RuntimeException e) {
			this.logger.error("SocialApp.syncIntimateAttribute error: ", e);
		}
	}
	
	/**
	 * 获取亲密度加成
	 * @param role
	 * @return
	 */
	@Override
	public byte getIntimateAddition(RoleInstance role) {
		try {
			Team team = role.getTeam();
			// 若当前没有队伍或者已下线，清除亲密度所加的属性；若当前有队伍，根据队伍变化判断亲密度属性是否改变。
			if (null == team || team.getPlayerNum() <= 1) {
				return 0;
			}
			// 如果玩家离线
			if (team.getOfflineMembers().containsKey(role.getRoleId())) {
				return 0;
			}
			// 找到最大的亲密度
			int maxIntimate = -1;
			for (AbstractRole member : team.getMembers()) {
				DracoSocialRelation relation = this.getRoleSocialRelation(role, (RoleInstance) member);
				if (null == relation || !relation.isFriend()) {
					continue;
				}
				int intimate = relation.getIntimate();
				if (intimate > maxIntimate) {
					maxIntimate = intimate;
				}
			}
			if (-1 == maxIntimate) {
				return 0;
			}
			SocialIntimateConfig config = this.getSocialIntimateConfig(maxIntimate);
			if (null == config) {
				return 0;
			}
			return config.getBuffAddition();
		} catch (Exception e) {
		}
		return 0;
	}
	
	/**
	 * 清除亲密度属性
	 * 
	 * @param role
	 */
	private void clearIntimateAttribute(RoleInstance role) {
		try {
			int maxLevel = role.getMaxIntimateLevel();
			if (maxLevel <= 0) {
				return;
			}
			SocialIntimateConfig config = this.intimateMap.get(maxLevel);
			if (null == config) {
				return;
			}
			role.setMaxIntimateLevel(0);
			GameContext.getUserBuffApp().delBuffStat(role, (short) config.getBuffId(), false);
		} catch (RuntimeException e) {
			this.logger.error("SocialApp.clearIntimateAttribute error: ", e);
		}
	}

	@Override
	public String getSocialDesc() {
		return this.socialDesc;
	}

	@Override
	public int getCanGetGoodsTimes() {
		return this.praiseParam.getCanGetGoodsTimes();
	}

	@Override
	public GoodsLiteItem getPraiseGoodsInfo(RoleInstance role) {
		SocialPraiseGoodsConfig praiseGoods = this.getPraiseGoodsConfig(role.getLevel());
		if (praiseGoods == null) {
			return new GoodsLiteItem();
		}
		GoodsBase base = GameContext.getGoodsApp().getGoodsBase(praiseGoods.getGoodsId());
		if (null == base) {
			return new GoodsLiteItem();
		}
		GoodsLiteItem item = base.getGoodsLiteItem();
		if (null != item) {
			item.setBindType((byte) praiseGoods.getBindType());
			item.setNum((short) praiseGoods.getGoodsNum());
			item.setGoodsLevel((byte) praiseGoods.getLevel());
		}
		return item;
	}

	@Override
	public Result transmissionApply(RoleInstance role, RoleInstance targetRole) {
		Result result = this.doTransmissionApply(role, targetRole);
		if (result.isSuccess()) {
			C1219_SocialTransmissionApplyRespMessage forwardMsg = new C1219_SocialTransmissionApplyRespMessage();
			forwardMsg.setRoleId(role.getIntRoleId());
			forwardMsg.setRoleName(role.getRoleName());
			targetRole.getBehavior().sendMessage(forwardMsg);
		}
		return result;
	}

	private Result doTransmissionApply(RoleInstance role, RoleInstance targetRole) {
		Result result = new Result();
		if (null == role) {
			return result.setInfo(Status.Social_Error.getTips());
		}
		if (null == targetRole) {
			return result.setInfo(Status.Social_TargRole_Offline.getTips());
		}
		String roleId = role.getRoleId();
		String targRoleId = targetRole.getRoleId();
		if (roleId.equals(targRoleId)) {
			return result.setInfo(Status.Social_Transmission_Self.getTips());
		}
		if (!this.isLevelUpTargetRole(role, targetRole)) {
			return result.setInfo(Status.Social_Transmission_Level_low.getTips());
		}
		if (!this.haveTransmissionTimes(role)) {
			return result.setInfo(Status.Social_Transmission_No_Times.getTips());
		}
		if (!this.canReceiveTransmission(targetRole)) {
			String message = GameContext.getI18n().messageFormat(TextId.Social_Transmission_No_RecvTimes, targetRole.getRoleName());
			return result.setInfo(message);
		}
		long currTime = System.currentTimeMillis();
		if (currTime - this.getTransmissionApplyTime(targRoleId) < TimeoutConstant.Social_Friend_Reply_Time) {
			return result.setInfo(Status.Social_TargRole_Busy.getTips());
		}
		// 给目标角色设置申请时间，等待目标角色处理
		this.setTransmissionApplyTime(targRoleId, currTime);
		result.setInfo(GameContext.getI18n().getText(TextId.Social_Transmission_Success));
		return result.success();
	}

	private boolean isLevelUpTargetRole(RoleInstance role, RoleInstance targetRole) {
		return role.getLevel() > targetRole.getLevel();
	}

	private boolean haveTransmissionTimes(RoleInstance role) {
		int transmissionTimes = role.getRoleCount().getRoleTimesToInt(CountType.TransmissionTimes);//.getTransmissionTimes();
		return transmissionTimes < this.transmissionParam.getMaxTransmissionTimes();
	}

	private int remainTransmissionTimes(RoleInstance role) {
		return this.transmissionParam.getMaxTransmissionTimes() - role.getRoleCount().getRoleTimesToInt(CountType.TransmissionTimes);//.getTransmissionTimes();
	}

	private boolean canReceiveTransmission(RoleInstance role) {
		int recvTranTimes = role.getRoleCount().getRoleTimesToInt(CountType.ReceiveTransmissionTimes);//.getReceiveTransmissionTimes();
		return recvTranTimes < this.transmissionParam.getMaxReceiveTransmissionTimes();
	}

	@Override
	public Result transmissionReply(RoleInstance role, byte type, String inviterId) {
		Result result = this.doTransmissionReply(role, type, inviterId);
		return result;
	}

	private Result doTransmissionReply(RoleInstance role, byte type, String inviterId) {
		this.setTransmissionApplyTime(role.getRoleId(), 0);
		Result result = new Result();
		// 获取请求者对象
		RoleInstance inviter = GameContext.getOnlineCenter().getRoleInstanceByRoleId(inviterId);
		if (null == inviter) {
			return result.setInfo(Status.Social_TargRole_Offline.getTips());
		}
		String roleName = role.getRoleName();

		// 拒绝传功
		if (TransmissionType != type) {
			C0003_TipNotifyMessage tipsToInviter = new C0003_TipNotifyMessage();// 给邀请者的浮动提示
			tipsToInviter.setMsgContext(Status.Social_Transmission_Refuse_Tip.getTips().replace(Wildcard.Role_Name, roleName));
			this.sendMessage(inviter, tipsToInviter);
			return result.success();
		}
		if (!this.haveTransmissionTimes(inviter)) {
			String message = GameContext.getI18n().messageFormat(TextId.Social_Transmission_No_GiveTimes, inviter.getRoleName());
			return result.setInfo(message);
		}
		if (!this.canReceiveTransmission(role)) {
			return result.setInfo(Status.Social_Transmission_No_SRTimes.getTips());
		}
		// 记录传功和被传功次数
		role.getRoleCount().changeTimes(CountType.ReceiveTransmissionTimes);//.incrReceiveTransmissionTimes();
		inviter.getRoleCount().changeTimes(CountType.TransmissionTimes);//.incrTransmissionTimes();
		DracoSocialRelation relation = this.getFriendRelation(role.getRoleId(), inviterId);
		int roleLevel = role.getLevel();
		int inviterLevel = inviter.getLevel();
		double rate = (null == relation) ? 1 : MULTIPLE;
		SocialTransmissionLevelConfig roleTranConfig = this.getTransmissionLevelConfig(roleLevel);
		SocialTransmissionLevelmConfig roleTranLevelConfig = this.getTransmissionLevelmConfig(inviterLevel - roleLevel);
		SocialTransmissionLevelConfig targetTranConfig = this.getTransmissionLevelConfig(inviterLevel);
		// 增加亲密度和经验
		if (null != roleTranConfig && null != roleTranLevelConfig && null != targetTranConfig) {
			int expRole = (int) ((roleTranConfig.getExpRecv() + roleTranLevelConfig.getExp()) * rate);
			int expInviter = (int) (targetTranConfig.getExp() * rate);
			this.changeTransmissionAttribute(role, inviter, expRole, expInviter);
		}
		// 活跃度
		GameContext.getDailyPlayApp().incrCompleteTimes(inviter, 1, DailyPlayType.friend_transmission, "");
		return result.success();
	}

	private void changeTransmissionAttribute(RoleInstance role, RoleInstance inviter, int expRole, int expInviter) {
		role.getBehavior().changeAttribute(AttributeType.exp, OperatorType.Add, expRole);
		inviter.getBehavior().changeAttribute(AttributeType.exp, OperatorType.Add, expInviter);
		String tipInfo = Status.Social_Transmission_Recv_Success.getTips();
		this.sendMessage(role, new C0005_TipMultiNotifyMessage(tipInfo.replace(Wildcard.Role_Name, inviter.getRoleName()).replace(Wildcard.Number, String.valueOf(expRole))));
		tipInfo = GameContext.getI18n().messageFormat(TextId.Social_Transmission_Success_Tip, role.getRoleName(), expInviter, this.remainTransmissionTimes(inviter));
		this.sendMessage(inviter, new C0005_TipMultiNotifyMessage(tipInfo));
	}

	@Override
	public Result getPraiseGoods(RoleInstance role) {
		Result result = new Result();
		if (role.getRoleCount().getRoleTimesToByte(CountType.HaveReceivePraiseGift) == 1) {
			result.setInfo(Status.Social_Praise_Have_Got.getTips());
			return result;
		}
		if (role.getRoleCount().getRoleTimesToInt(CountType.TodayReceivePraiseTimes) < this.praiseParam.getCanGetGoodsTimes()) {
			result.setInfo(Status.Social_Praise_Not_Enough.getTips());
			return result;
		}
		SocialPraiseGoodsConfig config = this.getPraiseGoodsConfig(role.getLevel());
		if (null == config) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 添加物品
		GoodsResult gr = GameContext.getUserGoodsApp().addGoodsForBag(role, config.getGoodsId(), config.getGoodsNum(), BindingType.get(config.getBindType()), OutputConsumeType.social_praise_consume);
		if (!gr.isSuccess()) {
			return gr;
		}
		role.getRoleCount().changeTimes(CountType.HaveReceivePraiseGift, 1);//.setHaveReceivePraiseGift(1);
		return result.success();
	}

	@Override
	public DracoSocialRelation getFriendRelation(String roleId, String targetRoleId) {
		ConcurrentHashMap<String, DracoSocialRelation> relationMap = this.socialFriendMap.get(roleId);
		if (null == relationMap) {
			return null;
		}
		return relationMap.get(targetRoleId);
	}

	private DracoSocialRelation getShieldRelation(String roleId, String targetRoleId) {
		ConcurrentHashMap<String, DracoSocialRelation> relationMap = this.socialShieldMap.get(roleId);
		if (null == relationMap) {
			return null;
		}
		return relationMap.get(targetRoleId);
	}

	private SocialFlowerConfig getSocialFlowerConfig(short flowerId) {
		return this.flowerMap.get(flowerId);
	}

	private SocialPraiseRecvConfig getPraiseRecvConfig(int key) {
		return this.praiseRecvParam.get(String.valueOf(key));
	}

	private SocialPraiseGoodsConfig getPraiseGoodsConfig(int key) {
		return this.praiseGoodsParam.get(String.valueOf(key));
	}

	private SocialTransmissionLevelConfig getTransmissionLevelConfig(int key) {
		return this.transmissionLevelParam.get(String.valueOf(key));
	}

	private SocialTransmissionLevelmConfig getTransmissionLevelmConfig(int key) {
		return this.transmissionLevelmParam.get(String.valueOf(key));
	}

	private long getFriendApplyTime(String key) {
		SocialDate date = this.socialDateMap.get(key);
		if (null == date) {
			return 0;
		}
		return date.getFriendApplyTime();
	}

	private void setFriendApplyTime(String key, long time) {
		SocialDate date = this.socialDateMap.get(key);
		if (null == date) {
			date = new SocialDate();
		}
		date.setFriendApplyTime(time);
		this.socialDateMap.put(key, date);
	}

	private long getTransmissionApplyTime(String key) {
		SocialDate date = this.socialDateMap.get(key);
		if (null == date) {
			return 0;
		}
		return date.getTransmissionApplyTime();
	}

	private void setTransmissionApplyTime(String key, long time) {
		SocialDate date = this.socialDateMap.get(key);
		if (null == date) {
			date = new SocialDate();
		}
		date.setTransmissionApplyTime(time);
		this.socialDateMap.put(key, date);
	}

	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void stop() {
	}

}
