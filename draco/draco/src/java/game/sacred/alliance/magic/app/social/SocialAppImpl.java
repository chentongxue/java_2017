package sacred.alliance.magic.app.social;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.app.social.config.SocialFlowerConfig;
import sacred.alliance.magic.app.social.config.SocialFriendBatch;
import sacred.alliance.magic.app.social.config.SocialIntimateConfig;
import sacred.alliance.magic.app.team.Team;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.SkyEffectType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.channel.EmptyChannelSession;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TimeoutConstant;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.dao.impl.SocialDAOImpl;
import sacred.alliance.magic.domain.RoleSocialRelation;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.AttributeOperateBean;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.message.internal.C0058_SocialRelationInitInternalMessage;
import com.game.draco.message.internal.C0059_SocialRelationRemoveInternalMessage;
import com.game.draco.message.item.SocialFlowerItem;
import com.game.draco.message.item.SocialFriendBatchItem;
import com.game.draco.message.item.SocialFriendItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C1109_SkyEffectNotifyMessage;
import com.game.draco.message.response.C1202_SocialFriendApplyForwardRespMessage;
import com.game.draco.message.response.C1205_SocialFriendListRespMessage;
import com.game.draco.message.response.C1210_SocialFlowerListRespMessage;
import com.game.draco.message.response.C1212_SocialFriendBatchViewRespMessage;

public class SocialAppImpl implements SocialApp {
	
	/** 好友黑名单数据
	 *  每一组数据表示一对好友的关系或单向黑名单的关系 
	 *  */
	private Map<String,Map<String,RoleSocialRelation>> socialRelationMap = new ConcurrentHashMap<String,Map<String,RoleSocialRelation>>();
	private SocialDAOImpl socialDAO;
	private static final ChannelSession emptyChannelSession = new EmptyChannelSession();
	private static final String Mark = "-1";//当角色ID与标记相同时，使用角色名称查找角色对象
	private static final byte FriendAcceptType = 1;//接受好友申请类型
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Integer,SocialIntimateConfig> intimateMap = new HashMap<Integer,SocialIntimateConfig>();
	private Map<Integer,AttributeOperateBean> intimateAttributeMap = new HashMap<Integer,AttributeOperateBean>();
	private Map<Short,SocialFlowerConfig> flowerMap = new HashMap<Short,SocialFlowerConfig>();
	private String socialDesc;
	private SocialIntimateConfig maxIntimateConfig;//最高等级的亲密度配置
	private SocialFriendBatch batchFriendParam;//批量加友参数

	public SocialDAOImpl getSocialDAO() {
		return socialDAO;
	}

	public void setSocialDAO(SocialDAOImpl socialDAO) {
		this.socialDAO = socialDAO;
	}

	@Override
	public void login(RoleInstance role) {
		try {
			String roleId = role.getRoleId();
			List<RoleSocialRelation> relationList = this.socialDAO.selectFriendList(roleId);
			//内部action初始化社交数据
			C0058_SocialRelationInitInternalMessage message = new C0058_SocialRelationInitInternalMessage();
			message.setRole(role);
			message.setRelationList(relationList);
			GameContext.getUserSocketChannelEventPublisher().publish(role.getUserId(), message, emptyChannelSession);
		} catch (RuntimeException e) {
			this.logger.error("SocialApp.login error: ", e);
		}
	}
	
	@Override
	public void initRoleSocialRelation(String roleId, List<RoleSocialRelation> relationList) {
		try {
			if(!this.socialRelationMap.containsKey(roleId)){
				//!!!!!这里创建线程安全的map，以防多线程可能出现的bug
				this.socialRelationMap.put(roleId, new ConcurrentHashMap<String, RoleSocialRelation>());
			}
			for(RoleSocialRelation relation : relationList){
				if(null == relation){
					continue;
				}
				String otherRoleId = relation.getOtherRoleId(roleId);
				Map<String, RoleSocialRelation> otherRelationMap = this.socialRelationMap.get(otherRoleId);
				//如果目标的数据中包含这条记录，直接引用过来保存到自己的信息里
				if(!Util.isEmpty(otherRelationMap) && otherRelationMap.containsKey(roleId)){
					this.socialRelationMap.get(roleId).put(otherRoleId, otherRelationMap.get(roleId));
					continue;
				}
				//没有找到相同的记录，把查到的新数据缓存下来
				this.socialRelationMap.get(roleId).put(otherRoleId, relation);
			}
		} catch (RuntimeException e) {
			this.logger.error("SocialApp.initRoleSocialRelation error: ", e);
		}
	}

	@Override
	public void logout(RoleInstance role) {
		//获取角色社交关系数据
		String roleId = role.getRoleId();
		Map<String,RoleSocialRelation> relationMap = this.socialRelationMap.get(roleId);
		//只能判断null，即便是size为0也得释放内存
		if(null == relationMap){
			return;
		}
		for(RoleSocialRelation relation : relationMap.values()){
			if(null == relation){
				continue;
			}
			relation.persistent();
		}
		//内部action处理删除社交缓存
		C0059_SocialRelationRemoveInternalMessage message = new C0059_SocialRelationRemoveInternalMessage();
		message.setRoleId(roleId);
		GameContext.getUserSocketChannelEventPublisher().publish(role.getUserId(), message, emptyChannelSession);
	}
	
	@Override
	public void logoutRemoveRoleSocialRelation(String roleId) {
		this.socialRelationMap.remove(roleId);
	}

	@Override
	public Result blackApply(RoleInstance role, String targRoleId, String targRoleName) {
		Result result = new Result();
		RoleInstance targetRole = this.getTargRoleInstance(targRoleId, targRoleName);
		if(null == targetRole){
			return result.setInfo(Status.Social_TargRole_Offline.getTips());
		}
		if(this.isBlacklistFull(role)){
			return result.setInfo(Status.Social_Black_IsFull.getTips());
		}
		String roleId = role.getRoleId();
		RoleSocialRelation relation = this.getRoleSocialRelation(roleId, targRoleId);
		targRoleId = targetRole.getRoleId();
		//屏蔽对方，可以是从好友变成黑名单
		if(null != relation){
			relation.shieldOther(roleId);
			this.socialRelationMap.get(roleId).put(targRoleId, relation);
			return result.success();
		}
		//建立新的关系，将对方加入黑名单中
		relation = new RoleSocialRelation();
		relation.createBlackList(role, targetRole);
		this.socialRelationMap.get(roleId).put(targRoleId, relation);
		return result.success();
	}

	@Override
	public Result blackRemove(RoleInstance role, String targRoleId) {
		Result result = new Result();
		String roleId = role.getRoleId();
		//没有社交关系，提示成功
		if(!this.socialRelationMap.containsKey(roleId)){
			return result.success();
		}
		//自己和对方没有社交关系，也提示成功
		RoleSocialRelation relation = this.socialRelationMap.get(roleId).get(targRoleId);
		if(null == relation){
			return result.success();
		}
		//将对方从屏蔽中删除
		relation.cancelShield(roleId);
		C0003_TipNotifyMessage tips = new C0003_TipNotifyMessage(role.getRoleName() + Status.Social_Black_Remove_Tip.getTips());
		GameContext.getMessageCenter().sendByRoleId(null, targRoleId, tips);
		return result.success();
	}

	@Override
	public Result friendApply(RoleInstance role, RoleInstance targetRole) {
		Result result = this.doFriendApply(role, targetRole);
		//好友请求成功之后，给目标角色转发弹板消息
		if(result.isSuccess()){
			C1202_SocialFriendApplyForwardRespMessage forwardMsg = new C1202_SocialFriendApplyForwardRespMessage();
			forwardMsg.setRoleId(role.getIntRoleId());
			forwardMsg.setRoleName(role.getRoleName());
			targetRole.getBehavior().sendMessage(forwardMsg);
		}
		return result;
	}
	
	private Result doFriendApply(RoleInstance role, RoleInstance targetRole){
		Result result = new Result();
		if(null == role){
			return result.setInfo(Status.Social_Error.getTips());
		}
		if(null == targetRole){
			return result.setInfo(Status.Social_TargRole_Offline.getTips());
		}
		String roleId = role.getRoleId();
		String targRoleId = targetRole.getRoleId();
		if(roleId.equals(targRoleId)){
			return result.setInfo(Status.Social_Friend_Add_Self.getTips());
		}
		if(this.isFriendListFull(role)){
			return result.setInfo(Status.Social_Friend_IsFull.getTips());
		}
		long currTime = System.currentTimeMillis();
		if(currTime - targetRole.getSocialApplyTime() < TimeoutConstant.Social_Friend_Reply_Time){
			return result.setInfo(Status.Social_TargRole_Busy.getTips());
		}
		RoleSocialRelation relation = this.getRoleSocialRelation(roleId, targRoleId);
		if(null == relation){
			//给目标角色设置申请时间，等待目标角色处理
			targetRole.setSocialApplyTime(currTime);
			return result.success();
		}
		String targRoleName = targetRole.getRoleName();
		if(relation.isFriend()){
			return result.setInfo(targRoleName + Status.Social_Already_Friend.getTips());
		}
		if(relation.beShield(roleId)){
			String info = Status.Social_Already_In_Black.getTips().replace(Wildcard.Role_Name, targRoleName);
			return result.setInfo(info);
		}
		//给目标角色设置申请时间，等待目标角色处理
		targetRole.setSocialApplyTime(currTime);
		return result.success();
	}
	
	@Override
	public Result friendRemove(RoleInstance role, String targRoleId) {
		Result result = new Result();
		String roleId = role.getRoleId();
		//没有社交关系，提示成功
		if(!this.socialRelationMap.containsKey(roleId)){
			return result.success();
		}
		//自己和对方没有社交关系，也提示成功
		RoleSocialRelation relation = this.socialRelationMap.get(roleId).get(targRoleId);
		if(null == relation){
			return result.success();
		}
		//解除好友关系
		relation.removeRelation();
		//给对方发邮件提示好友断交，若在线发则额外发浮动提示
		RoleInstance targRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(targRoleId);
		String content = Status.Social_Break_Friend_content.getTips().replace(Wildcard.Role_Name, role.getRoleName());
		if(null != targRole){
			C0003_TipNotifyMessage tips = new C0003_TipNotifyMessage();
			tips.setMsgContext(content);
			targRole.getBehavior().sendMessage(tips);
		}
		try{
			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
			mail.setTitle(Status.Social_Break_Friend_Title.getTips());
			mail.setSendRole(MailSendRoleType.Social_Friend.getName());
			mail.setContent(content);
			mail.setRoleId(targRoleId);
			GameContext.getMailApp().sendMail(mail);
		}catch(Exception e){
			this.logger.error("SocialApp.friendRemove error: ", e);
		}
		return result.success();
	}
	
	@Override
	public Result friendReply(RoleInstance role, byte type, String inviterId) {
		Result result = this.doFriendReply(role, type, inviterId);
		return result;
	}
	
	private Result doFriendReply(RoleInstance role, byte type, String inviterId) {
		//角色处理社交请求时，重置操作时间为0
		role.setSocialApplyTime(0);
		Result result = new Result();
		RoleInstance inviter = GameContext.getOnlineCenter().getRoleInstanceByRoleId(inviterId);
		if(null == inviter){
			return result.setInfo(Status.Social_TargRole_Offline.getTips());
		}
		String roleName = role.getRoleName();
		String tipInfo = Status.Social_Friend_Add_Tip.getTips();
		C0003_TipNotifyMessage tipsToInviter = new C0003_TipNotifyMessage(tipInfo.replace(Wildcard.Role_Name, roleName));//给邀请者的浮动提示
		C0003_TipNotifyMessage tipsToSelf = new C0003_TipNotifyMessage(tipInfo.replace(Wildcard.Role_Name, inviter.getRoleName()));//给自己的浮动提示
		//拒绝添加好友
		if(FriendAcceptType != type){
			tipsToInviter.setMsgContext(Status.Social_Friend_Refuse_Tip.getTips().replace(Wildcard.Role_Name, roleName));
			inviter.getBehavior().sendMessage(tipsToInviter);
			return result.success();
		}
		//判断自己的好友上限
		if(this.isFriendListFull(role)){
			return result.setInfo(Status.Social_Friend_IsFull.getTips());
		}
		//判断对方的好友上限
		if(this.isFriendListFull(inviter)){
			return result.setInfo(Status.Social_Targ_Friend_IsFull.getTips());
		}
		//接受添加好友
		String roleId = role.getRoleId();
		RoleSocialRelation relation = this.getRoleSocialRelation(roleId, inviterId);
		if(null != relation){
			if(relation.isFriend()){
				return result.setInfo(inviter.getRoleName() + Status.Social_Already_Friend.getTips());
			}
			//将黑名单中的角色添加为好友，修改关系
			relation.becomeFriend();
			this.socialRelationMap.get(roleId).put(inviterId, relation);
			this.socialRelationMap.get(inviterId).put(roleId, relation);
			inviter.getBehavior().sendMessage(tipsToInviter);
			role.getBehavior().sendMessage(tipsToSelf);
			return result.success();
		}
		//从互为陌生人建立好友关系
		relation = new RoleSocialRelation();
		relation.createFriend(role, inviter);
		this.socialRelationMap.get(roleId).put(inviterId, relation);
		this.socialRelationMap.get(inviterId).put(roleId, relation);
		inviter.getBehavior().sendMessage(tipsToInviter);
		role.getBehavior().sendMessage(tipsToSelf);
		return result.success();
	}

	@Override
	public List<RoleSocialRelation> getBlackList(RoleInstance role) {
		List<RoleSocialRelation> blackList = new ArrayList<RoleSocialRelation>();
		String roleId = role.getRoleId();
		Map<String,RoleSocialRelation> socialMap = this.socialRelationMap.get(roleId);
		if(Util.isEmpty(socialMap)){
			return blackList;
		}
		int index = 0;
		for(RoleSocialRelation relation : socialMap.values()){
			//长度超过了黑名单的最大数值（index的值过大出线上现过bug）
			if(index > GameContext.getSocialConfig().getBlacklistMaxNum()){
				break;
			}
			if(null == relation){
				continue;
			}
			if(!relation.isSelfShieldOther(roleId)){
				continue;
			}
			blackList.add(relation);
			index ++;
		}
		return blackList;
	}

	@Override
	public List<RoleSocialRelation> getFriendList(RoleInstance role) {
		List<RoleSocialRelation> friendList = new ArrayList<RoleSocialRelation>();
		String roleId = role.getRoleId();
		Map<String,RoleSocialRelation> socialMap = this.socialRelationMap.get(roleId);
		if(Util.isEmpty(socialMap)){
			return friendList;
		}
		int index = 0;
		for(RoleSocialRelation relation : socialMap.values()){
			//长度超过了好友的最大数值（index的值过大出线上现过bug）
			if(index > GameContext.getSocialConfig().getFriendMaxNum()){
				break;
			}
			if(null == relation){
				continue;
			}
			if(!relation.isFriend()){
				continue;
			}
			friendList.add(relation);
			index++;
		}
		return friendList;
	}
	
	/**
	 * 根据请求的id和name查找角色信息
	 * @param targRoleId
	 * @param targRoleName
	 * @return
	 */
	private RoleInstance getTargRoleInstance(String targRoleId, String targRoleName){
		if(Mark.equals(targRoleId)){
			return GameContext.getOnlineCenter().getRoleInstanceByRoleName(targRoleName);
		}else{
			return GameContext.getOnlineCenter().getRoleInstanceByRoleId(targRoleId);
		}
	}
	
	/**
	 * 获取社交关系
	 * @param role
	 * @param targRole
	 * @return
	 */
	private RoleSocialRelation getRoleSocialRelation(RoleInstance role, RoleInstance targRole){
		if(null == role || null == targRole){
			return null;
		}
		return this.getRoleSocialRelation(role.getRoleId(), targRole.getRoleId());
	}
	
	/**
	 * 查找社交关系的记录
	 * @param roleId
	 * @param targRoleId
	 * @return
	 */
	private RoleSocialRelation getRoleSocialRelation(String roleId, String targRoleId){
		RoleSocialRelation relation = null;
		Map<String,RoleSocialRelation> relationMap = this.socialRelationMap.get(roleId);
		if(!Util.isEmpty(relationMap)){
			relation = relationMap.get(targRoleId);
		}
		if(null != relation){
			return relation;
		}
		relationMap = this.socialRelationMap.get(targRoleId);
		if(!Util.isEmpty(relationMap)){
			relation = relationMap.get(roleId);
		}
		if(null != relation){
			this.socialRelationMap.get(roleId).put(targRoleId, relation);
		}
		return relation;
	}

	@Override
	public boolean isShieldByTarget(String roleId, String targetRoleId) {
		RoleSocialRelation relation = this.getRoleSocialRelation(roleId, targetRoleId);
		if(null == relation){
			return false;
		}
		return relation.beShield(roleId);
	}

	@Override
	public int getFriendNumber(RoleInstance role) {
		// TODO:目前是获取好友列表的长度，可优化
		return this.getFriendList(role).size();
	}

	@Override
	public int getBlacklistNumber(RoleInstance role) {
		// TODO:目前是获取黑名单列表的长度，可优化
		return this.getBlackList(role).size();
	}
	
	/**
	 * 好友列表是否已满
	 * @param role
	 * @return
	 */
	private boolean isFriendListFull(RoleInstance role){
		int maxFriend = GameContext.getSocialConfig().getFriendMaxNum();
		return this.getFriendNumber(role) > maxFriend;
	}
	
	/**
	 * 黑名单列表是否已满
	 * @param role
	 * @return
	 */
	private boolean isBlacklistFull(RoleInstance role){
		int maxBlack = GameContext.getSocialConfig().getBlacklistMaxNum();
		return this.getBlacklistNumber(role) > maxBlack;
	}
	
	@Override
	public void changeFriendIntimate(RoleInstance role, RoleInstance targRole, int intimate){
		try {
			if(null == role || null == targRole || intimate <= 0){
				return;
			}
			RoleSocialRelation relation = this.getRoleSocialRelation(role, targRole);
			if(null == relation || !relation.isFriend()){
				return;
			}
			int oldIntimate = relation.getIntimate();
			int newIntimate = oldIntimate + intimate;
			//容错，超过系统最大亲密度，则设置为最大值
			if(newIntimate > this.maxIntimateConfig.getMaxIntimate()){
				newIntimate = this.maxIntimateConfig.getMaxIntimate();
			}
			//修改亲密度
			relation.changeIntimate(newIntimate);
			//同步亲密度影响的属性
			this.syncIntimateAttribute(role);
			this.syncIntimateAttribute(targRole);
			//飘字提示亲密度增加
			this.notifyIntimateChange(role, targRole, intimate);
		} catch (RuntimeException e) {
			this.logger.error("SocialApp.changeFriendIntimate error: ", e);
		}
	}
	
	/**
	 * 飘字通知亲密度改变
	 * @param role
	 * @param intimate
	 */
	private void notifyIntimateChange(RoleInstance role, RoleInstance targRole, int intimate){
		try {
			String msgContext = Status.Social_Friend_Intimate_Change_Tip.getTips().replace(Wildcard.Number, String.valueOf(intimate));
			this.sendMessage(role, new C0003_TipNotifyMessage(msgContext.replace(Wildcard.Role_Name, targRole.getRoleName())));
			this.sendMessage(targRole, new C0003_TipNotifyMessage(msgContext.replace(Wildcard.Role_Name, role.getRoleName())));
		} catch (RuntimeException e) {
			this.logger.error("SocialApp.notifyIntimateChange error: ", e);
		}
	}
	
	@Override
	public boolean isFirend(RoleInstance role, RoleInstance targRole){
		RoleSocialRelation relation = this.getRoleSocialRelation(role, targRole);
		if(null == relation){
			return false;
		}
		return relation.isFriend();
	}
	
	@Override
	public int getFriendIntimate(RoleInstance role, RoleInstance targRole){
		RoleSocialRelation relation = this.getRoleSocialRelation(role, targRole);
		if(null == relation){
			return 0;
		}
		return relation.getIntimate();
	}

	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadSocialFirendConfig();
	}

	@Override
	public void stop() {
		
	}
	
	private void loadSocialFirendConfig(){
		String fileName = "";
		String sheetName = "";
		try {
			fileName = XlsSheetNameType.social_intimate.getXlsName();
			sheetName = XlsSheetNameType.social_intimate.getSheetName();
			String xlsPath = GameContext.getPathConfig().getXlsPath();
			//①加载亲密度配置
			List<SocialIntimateConfig> imList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, SocialIntimateConfig.class);
			//配置信息按等级从低到高排序
			this.sortIntimateConfigList(imList);
			for(SocialIntimateConfig config : imList){
				if(null == config){
					continue;
				}
				config.init();
				int level = config.getLevel();
				this.intimateMap.put(level, config);
				//如果增加属性，记录下来
				AttributeOperateBean bean = config.getAttributeBean();
				if(null != bean){
					this.intimateAttributeMap.put(level, bean);
				}
			}
			//设置最大亲密度
			this.maxIntimateConfig = imList.get(imList.size()-1);
			if(null == this.maxIntimateConfig){
				this.checkFail("load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".The maxIntimate is config error!");
			}
			//②加载鲜花配置
			fileName = XlsSheetNameType.social_flower.getXlsName();
			sheetName = XlsSheetNameType.social_flower.getSheetName();
			List<SocialFlowerConfig> flwList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, SocialFlowerConfig.class);
			for(SocialFlowerConfig flower : flwList){
				if(null == flower){
					continue;
				}
				flower.init();
				this.flowerMap.put(flower.getFlowerId(), flower);
			}
			//③加载说明文字
			fileName = XlsSheetNameType.social_desc.getXlsName();
			sheetName = XlsSheetNameType.social_desc.getSheetName();
			List<String> descList = XlsPojoUtil.sheetToStringList(xlsPath + fileName, sheetName);
			this.socialDesc = descList.get(0);
			if(Util.isEmpty(this.socialDesc)){
				this.checkFail("load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".The socialDesc is not config!");
			}
			//④加载批量添加好友配置
			fileName = XlsSheetNameType.social_batch_friend.getXlsName();
			sheetName = XlsSheetNameType.social_batch_friend.getSheetName();
			List<SocialFriendBatch> paramList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, SocialFriendBatch.class);
			SocialFriendBatch batchConfig = paramList.get(0);
			if(null == batchConfig){
				this.checkFail("load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".The batch_friend is not config!");
			}
			batchConfig.init();
			this.batchFriendParam = batchConfig;
		}catch(Exception e) {
			this.checkFail("load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	/**
	 * 亲密度配置排序
	 * @param list
	 */
	private void sortIntimateConfigList(List<SocialIntimateConfig> list){
		Collections.sort(list, new Comparator<SocialIntimateConfig>(){
			@Override
			public int compare(SocialIntimateConfig conf1, SocialIntimateConfig conf2) {
				int level1 = conf1.getLevel();
				int level2 = conf2.getLevel();
				if(level1 < level2){
					return -1;
				}
				if(level1 > level2){
					return 1;
				}
				return 0;
			}
		});
	}
	
	/**
	 * 获取鲜花配置
	 * @param flowerId
	 * @return
	 */
	private SocialFlowerConfig getSocialFlowerConfig(short flowerId){
		return this.flowerMap.get(flowerId);
	}

	@Override
	public C1210_SocialFlowerListRespMessage getFlowerListMessage(int roleId) {
		List<SocialFlowerItem> flowerList = new ArrayList<SocialFlowerItem>();
		for(SocialFlowerConfig flower : this.flowerMap.values()){
			SocialFlowerItem item = new SocialFlowerItem();
			item.setFlowerId(flower.getFlowerId());
			item.setName(flower.getFlowerName());
			item.setIconId(flower.getIconId());
			item.setGoodsId(flower.getGoodsId());
			item.setGoldMoney(flower.getGold());
			item.setIntimate(flower.getIntimate());
			item.setInfo(flower.getInfo());
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
		try{
			RoleInstance targRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
			if(null == targRole){
				return result.setInfo(Status.Social_TargRole_Offline.getTips());
			}
			SocialFlowerConfig flower = this.getSocialFlowerConfig(flowerId);
			if(null == flower){
				return result.setInfo(Status.Social_Error.getTips());
			}
			if(!this.isFirend(role, targRole)){
				return result.setInfo(Status.Social_Flower_Not_Friend.getTips());
			}
			int goodsId = flower.getGoodsId();
			Result goodsRes = null;
			//优先扣除物品
			if(goodsId > 0){
				goodsRes = GameContext.getUserGoodsApp().deleteForBag(role, goodsId, 1, OutputConsumeType.social_flower_consume);
			}
			//物品扣除失败，需要扣钱
			if(null == goodsRes || !goodsRes.isSuccess()){
				int goldMoney = flower.getGold();
				if(role.getGoldMoney() < goldMoney){
					return result.setInfo(Status.Social_Gold_Not_Enough.getTips());
				}
				//扣钱
				GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, OperatorType.Decrease, goldMoney, OutputConsumeType.social_flower_consume);
				//通知客户端属性变化
				role.getBehavior().notifyAttribute();
			}
			//加亲密度
			this.changeFriendIntimate(role, targRole, flower.getIntimate());
			try {
				//统计收到鲜花的次数
				GameContext.getCountApp().receiveFlower(targRole, flower.getFlowerNum());
			} catch (RuntimeException e) {
				this.logger.error("", e);
			}
			//播放天空特效，广播消息
			this.sendSkyEffectMessage(role, targRole, flower);
			return result.success();
		}catch(Exception e) {
			this.logger.error("SocialApp.giveFlower error: ", e);
			return result.setInfo(Status.Social_Error.getTips());
		}
	}
	
	/**
	 * 播放天空特效
	 * @param role
	 * @param targRole
	 * @param flower
	 */
	private void sendSkyEffectMessage(RoleInstance role, RoleInstance targRole, SocialFlowerConfig flower){
		try {
			String broadcastInfo = flower.getBroadcastInfo();//广播消息
			if(!Util.isEmpty(broadcastInfo)){
				//替换通配符
				broadcastInfo = broadcastInfo.replace(Wildcard.Sender, role.getRoleName())
					.replace(Wildcard.Receiver, targRole.getRoleName());
			}
			SkyEffectType effectType = flower.getSkyEffectType();
			Collection<RoleInstance> receviers = new ArrayList<RoleInstance>();
			switch(effectType){
			case Both_Role:
				receviers.add(role);
				receviers.add(targRole);
				//需要给双方发系统消息
				if(!Util.isEmpty(broadcastInfo)){
					GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.System, broadcastInfo, null, role);
					GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.System, broadcastInfo, null, targRole);
				}
				break;
			case Both_Map:
				MapInstance mapIns = role.getMapInstance();
				receviers.addAll(mapIns.getRoleList());
				//给双方所在地图发消息
				GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Map, broadcastInfo, null, mapIns);
				if(!role.getMapId().equals(targRole.getMapId())){
					MapInstance targMapIns = targRole.getMapInstance();
					receviers.addAll(targMapIns.getRoleList());
					GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Map, broadcastInfo, null, targMapIns);
				}
				break;
			case World:
				receviers = GameContext.getOnlineCenter().getAllOnlineRole();
				//发走马灯消息
				GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, broadcastInfo, null, null);
				break;
			}
			//一次发特效消息
			C1109_SkyEffectNotifyMessage message = new C1109_SkyEffectNotifyMessage();
			message.setEffectId(flower.getEffect());
			message.setTime(flower.getTime());
			for(RoleInstance recevier : receviers){
				if(null == recevier){
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
	 * @param recevier
	 * @param message
	 */
	private void sendMessage(RoleInstance recevier, Message message){
		GameContext.getMessageCenter().sendSysMsg(recevier, message);
	}
	
	@Override
	public Result pushFriendBatchView(RoleInstance role) {
		Result result = new Result();
		int remainNum = this.batchFriendParam.getCount() - role.getRoleCount().getFriendBatchNum();
		if(remainNum <= 0){
			return result.setInfo(Status.Social_Batch_Friend_Num_Full.getTips());
		}
		if(this.isFriendListFull(role)){
			return result.setInfo(Status.Social_Friend_IsFull.getTips());
		}
		List<RoleInstance> roleList = this.searchBatchRoleList(role);
		if(Util.isEmpty(roleList)){
			return result.setInfo(Status.Social_No_Have_Role.getTips());
		}
		//推送一键加友消息
		this.pushFriendBatchViewMessage(role, roleList);
		//统计一键加友次数
		role.getRoleCount().incrFriendBatchNum();
		//飘字提示剩余次数
		String msgContext = Status.Social_Batch_Friend_Remain_Num.getTips().replace(Wildcard.Number, String.valueOf(remainNum-1));
		this.sendMessage(role, new C0003_TipNotifyMessage(msgContext));
		return result.success();
	}
	
	/**
	 * 结识仙友筛选玩家
	 * @param role
	 * @return
	 */
	private List<RoleInstance> searchBatchRoleList(RoleInstance role){
		List<RoleInstance> roleList = new ArrayList<RoleInstance>();
		Collection<RoleInstance> collection = GameContext.getOnlineCenter().getAllOnlineRole();
		String roleId = role.getRoleId();
		int roleLevel = role.getLevel();
		int differLevel = this.batchFriendParam.getLevel();//浮动等级
		int number = this.batchFriendParam.getRoleNum();//需要的总人数
		int index = 0;
		for(RoleInstance instance : collection){
			if(index >= number){
				break;
			}
			if(null == instance){
				continue;
			}
			String instanceId = instance.getRoleId();
			if(roleId.equals(instanceId)){
				continue;
			}
			if(this.isFirend(role, instance)){
				continue;
			}
			int absValue = Math.abs(instance.getLevel() - roleLevel);
			if(absValue > differLevel){
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
	 * @param role
	 * @param roleList
	 */
	private void pushFriendBatchViewMessage(RoleInstance role, List<RoleInstance> roleList) {
		try {
			if(Util.isEmpty(roleList)){
				return;
			}
			List<SocialFriendBatchItem> batchViewList = new ArrayList<SocialFriendBatchItem>();
			for(RoleInstance instance : roleList){
				if(null == instance){
					continue;
				}
				SocialFriendBatchItem item = new SocialFriendBatchItem();
				item.setRoleId(instance.getIntRoleId());
				item.setRoleName(instance.getRoleName());
				item.setSex(instance.getSex());
				item.setCamp(instance.getCampId());
				item.setCareer(instance.getCampId());
				item.setRoleLevel((byte) instance.getLevel());
				batchViewList.add(item);
			}
			C1212_SocialFriendBatchViewRespMessage message = new C1212_SocialFriendBatchViewRespMessage();
			message.setBatchViewList(batchViewList);
			//发消息
			role.getBehavior().sendMessage(message);
		} catch (RuntimeException e) {
			this.logger.error("SocialApp.pushFriendBatchViewMessage error: ", e);
		}
	}
	
	@Override
	public void friendApply(RoleInstance role, int[] roleIds) {
		if(null == roleIds || 0 == roleIds.length){
			return;
		}
		for(int roleId : roleIds){
			if(roleId <= 0){
				continue;
			}
			RoleInstance targRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
			//依次发送好友申请
			this.friendApply(role, targRole);
		}
	}

	@Override
	public SocialIntimateConfig getSocialIntimateConfig(int intimate) {
		//只要超过最高等级的下限，就返回最高等级的配置
		if(intimate >= this.maxIntimateConfig.getMinIntimate()){
			return this.maxIntimateConfig;
		}
		for(SocialIntimateConfig config : this.intimateMap.values()){
			if(null == config){
				continue;
			}
			if(config.isWithin(intimate)){
				return config;
			}
		}
		return null;
	}

	@Override
	public void syncIntimateAttribute(RoleInstance role) {
		try {
			Team team = role.getTeam();
			//若当前没有队伍，清除亲密度所加的属性；若当前有队伍，根据队伍变化判断亲密度属性是否改变。
			if(null == team|| team.getPlayerNum() <=1){
				this.clearIntimateAttribute(role);
				return;
			}
			//找到最大的亲密度
			int maxIntimate = -1;
			for(AbstractRole member : team.getMembers()){
				RoleSocialRelation relation = this.getRoleSocialRelation(role, (RoleInstance)member);
				if(null == relation || !relation.isFriend()){
					continue;
				}
				int intimate = relation.getIntimate();
				if(intimate > maxIntimate){
					maxIntimate = intimate;
				}
			}
			if(-1 == maxIntimate){
				return;
			}
			SocialIntimateConfig config = this.getSocialIntimateConfig(maxIntimate);
			if(null == config){
				return;
			}
			int level = config.getLevel();
			int oldLevel = role.getMaxIntimateLevel();
			if(level == oldLevel){
				return;
			}
			AttriBuffer buffer;
			if(oldLevel < level){
				buffer = this.getAttriBuffer(oldLevel, level, false);
			}else{
				buffer = this.getAttriBuffer(level, oldLevel, true);
			}
			//修改属性
			GameContext.getUserAttributeApp().changeAttribute(role, buffer);
			role.getBehavior().notifyAttribute();
			//记录最大亲密度等级
			role.setMaxIntimateLevel(config.getLevel());
		} catch (RuntimeException e) {
			this.logger.error("SocialApp.syncIntimateAttribute error: ", e);
		}
	}
	
	@Override
	public AttriBuffer getAttriBuffer(RoleInstance role){
		if(null == role || role.getMaxIntimateLevel() <=0){
			return null ;
		}
		return this.getAttriBuffer(0, role.getMaxIntimateLevel(), false);
	}
	
	/**
	 * 清除亲密度属性
	 * @param role
	 */
	private void clearIntimateAttribute(RoleInstance role){
		try {
			int maxLevel = role.getMaxIntimateLevel();
			if(maxLevel <= 0){
				return;
			}
			AttriBuffer buffer = this.getAttriBuffer(0, maxLevel, true);
			//修改属性
			GameContext.getUserAttributeApp().changeAttribute(role, buffer);
			//记录最大亲密度等级
			role.setMaxIntimateLevel(0);
			role.getBehavior().notifyAttribute();
		} catch (RuntimeException e) {
			this.logger.error("SocialApp.clearIntimateAttribute error: ", e);
		}
	}
	
	/**
	 * 获取需要改变的属性
	 * @param minLevel
	 * @param maxLevel
	 * @param reduce
	 * @return
	 */
	private AttriBuffer getAttriBuffer(int minLevel, int maxLevel, boolean reduce){
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		int next = minLevel + 1;//下一个等级
		while(next<=maxLevel){
			AttributeOperateBean bean = this.intimateAttributeMap.get(next);
			if(null == bean){
				continue;
			}
			if(reduce){
				buffer.append(bean.getAttrType(), -bean.getValue(), -bean.getPrecValue());
			}else{
				buffer.append(bean.getAttrType(), bean.getValue(), bean.getPrecValue());
			}
			next++;
		}
		return buffer;
	}

	@Override
	public C1205_SocialFriendListRespMessage getFriendListMessage(RoleInstance role) {
		try {
			String selfRoleId = role.getRoleId();
			List<SocialFriendItem> friendList = new ArrayList<SocialFriendItem>();
			for(RoleSocialRelation relation : this.getFriendList(role)){
				if(null == relation){
					continue;
				}
				SocialFriendItem item = new SocialFriendItem();
				String otherRoleId = relation.getOtherRoleId(selfRoleId);
				item.setRoleId(Integer.valueOf(otherRoleId));
				item.setRoleName(relation.getRoleName(otherRoleId));
				item.setSex(relation.getSex(otherRoleId));
				item.setCamp(relation.getCamp(otherRoleId));
				item.setCareer(relation.getCareer(otherRoleId));
				item.setIntimate(relation.getIntimate());
				SocialIntimateConfig config = relation.getIntimateConfig();
				if(null != config){
					item.setIntimatelevel((byte) config.getLevel());
					item.setMaxIntimate(config.getMaxIntimate());
				}
				RoleInstance otherRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(otherRoleId);
				if(null != otherRole){
					//角色名从在线角色上取
					item.setRoleName(otherRole.getRoleName());
					item.setOnline((byte) 1);
					item.setRoleLevel((byte) otherRole.getLevel());
					item.setCamp(otherRole.getCampId());
					item.setCareer(otherRole.getCareer());
					MapInstance mapInstance = otherRole.getMapInstance();
					if(null != mapInstance){
						item.setMapName(mapInstance.getMap().getMapConfig().getMapdisplayname());
					}
					//更新好友记录中，在线玩家的名称、性别、阵营、职业（update时不会入库）
					relation.updateOnlineRole(otherRole);
				}
				friendList.add(item);
			}
			this.sortFriendList(friendList);
			C1205_SocialFriendListRespMessage resp = new C1205_SocialFriendListRespMessage();
			resp.setFlowerNum(role.getRoleCount().getFlowerNum());
			resp.setFriendList(friendList);
			return resp;
		} catch (Exception e) {
			this.logger.error("", e);
			return null;
		}
	}
	
	/**
	 * 好友列表排序
	 * @param friendList
	 */
	private void sortFriendList(List<SocialFriendItem> friendList){
		Collections.sort(friendList, new Comparator<SocialFriendItem>(){
			@Override
			public int compare(SocialFriendItem item1, SocialFriendItem item2) {
				byte online1 = item1.getOnline();
				byte online2 = item2.getOnline();
				if(online1 > online2){
					return -1;
				}
				if(online1 < online2){
					return 1;
				}
				int intimate1 = item1.getIntimate();
				int intimate2 = item2.getIntimate();
				if(intimate1 > intimate2){
					return -1;
				}
				if(intimate1 < intimate2){
					return 1;
				}
				byte level1 = item1.getRoleLevel();
				byte level2 = item2.getRoleLevel();
				if(level1 > level2){
					return -1;
				}
				if(level1 < level2){
					return 1;
				}
				return 0;
			}
		});
	}

	@Override
	public String getSocialDesc() {
		return this.socialDesc;
	}
	
}
