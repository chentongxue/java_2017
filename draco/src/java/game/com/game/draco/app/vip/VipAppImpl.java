package com.game.draco.app.vip;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.app.quickbuy.QuickCostHelper;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import baoutil.LogB;

import com.game.draco.GameContext;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.vip.config.VipGiftConfig;
import com.game.draco.app.vip.config.VipLevelAwardConfig;
import com.game.draco.app.vip.config.VipLevelFunctionConfig;
import com.game.draco.app.vip.config.VipLevelUpConfig;
import com.game.draco.app.vip.config.VipPrivilegeConfig;
import com.game.draco.app.vip.domain.RoleVip;
import com.game.draco.app.vip.domain.VipConstant;
import com.game.draco.app.vip.type.VipFunctionType;
import com.game.draco.app.vip.vo.VipLevelUpResult;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.ShopVipGiftItem;
import com.game.draco.message.item.VipLevelFunctionItem;
import com.game.draco.message.item.VipLevelUpAwardItem;
import com.game.draco.message.push.C2514_VipLevelUpNotifyMessage;
import com.game.draco.message.request.C2512_VipCallFunctionReqMessage;
import com.game.draco.message.request.C2516_VipInfoReqMessage;
import com.game.draco.message.response.C2106_VipShopGiftRespMessage;
import com.game.draco.message.response.C2107_VipLevelGiftReceiveRespMessage;
import com.game.draco.message.response.C2510_VipDisplayRespMessage;
import com.game.draco.message.response.C2511_VipGalleryShiftRespMessage;
import com.game.draco.message.response.C2512_VipCallFunctionRespMessage;
import com.game.draco.message.response.C2513_VipLevelUpAwardReceiveRespMessage;
import com.game.draco.message.response.C2516_VipInfoRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
/**
 * new vip
 */
public class VipAppImpl implements VipApp {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final short CALL_FUNCTION_CMDID = new C2512_VipCallFunctionReqMessage().getCommandId();
	// roleVipApp
	private Map<String, RoleVip> roleVipMap = Maps.newConcurrentMap();
	private VipConfigProvider vipConfProvider;

	@Override
	public int onLogin(RoleInstance role, Object context) {
		LogB.ic("VIP登入");
		LogB.setRole(role);
		try {
			RoleVip roleVip = GameContext.getBaseDAO().selectEntity(RoleVip.class, RoleVip.ROLE_ID, role.getRoleId());
			roleVip = ensureRoleVipNotNull(role, roleVip);
			roleVip = verifyVipLevel(roleVip);
			addRoleVipMap(roleVip);// roleVipApp
		} catch (Exception e) {
			logger.error("login game, query or save vip info failed", e);
			return 0;
		}
		return 1;
	}

	/*
	 * if vipExp range expand, VIP level shouldn't be reduced else if vipExp
	 * range shrink, preferring increase VIP level instead
	 */
	private RoleVip verifyVipLevel(RoleVip roleVip) {
		int vipExp = roleVip.getVipExp();
		byte expVipLevel = getVipLevelByVipExp(vipExp);
		byte vipLevel = roleVip.getVipLevel();
		if (expVipLevel > vipLevel) {
			roleVip.setVipLevel(expVipLevel);
			GameContext.getBaseDAO().saveOrUpdate(roleVip);
		}
		return roleVip;
	}

	/**
	 * save vip information for vip role
	 */
	@Override
	public int onLogout(RoleInstance role, Object context) {
		LogB.removeRole(role);
		RoleVip roleVip = null;
		try {
			roleVip = getRoleVip(role);
			// no database Operation for non-vip
			if (!isRoleVip(roleVip)) {
				String key = role.getRoleId();
				roleVipMap.remove(key);
				return 1;
			}
			GameContext.getBaseDAO().saveOrUpdate(roleVip);
		} catch (Exception e) {
			if (null != roleVip) {
				Log4jManager.OFFLINE_VIP_DB_LOG.info(roleVip.toString());
			}
			Log4jManager.OFFLINE_ERROR_LOG.error("VipApp.offline error, roleId=" + role.getRoleId() 
					+ ",userId=" + role.getUserId(), e);
			return 0;
		}
		String key = role.getRoleId();
		roleVipMap.remove(key);
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		return 0;
	}

	@Override
	public void setArgs(Object arg0) {

	}

	@Override
	public void start() {
		vipConfProvider = new VipConfigProvider().init();
	}

	@Override
	public void stop() {
	}

	@Override
	public void addVipLevelExp(int roleId, int vipExp) {
		//
		VipLevelUpResult rs = levelUp(roleId, vipExp);
		// viplevelUp
		if (rs.isVipLevelUp()) {
			// online notify
			if (GameContext.getOnlineCenter().isOnlineByRoleId(roleId + "")) {
				byte newVipLevel = rs.getNewVipLevel();
				String info = getText(TextId.VIP_LEVEL_UP_TIP);
				info = MessageFormat.format(info, newVipLevel + "");
				C2514_VipLevelUpNotifyMessage msg = new C2514_VipLevelUpNotifyMessage();
				msg.setNewVipLevel(newVipLevel);
				msg.setRoleId(roleId);
				RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId + "");
				role.getBehavior().sendMessage(msg);
				// 世界广播
				VipLevelUpConfig config = this.vipConfProvider.getVipLevelUpConfig(rs.getNewVipLevel());
				this.broadcast(config.getBroadCastTips(role));
			}
		}
	}
	
	private void broadcast(String message) {
		try {
			if (Util.isEmpty(message)) {
				return ;
			}
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, message, null, null);
		} catch (Exception e) {
			logger.error("VipAppImpl.broadcast error!", e);
		}
	}
	
	private byte getVipLevelByVipExp(int vipExp) {
		byte reVipLev = 0;
		Map<String, VipLevelUpConfig> map = vipConfProvider.getLevelUpConfigMap();
		for (int i = vipConfProvider.getMaxVipLevel(); i > 0; i--) {
			if (map.containsKey(i + "")) {
				VipLevelUpConfig cf = map.get(i + "");
				if (cf.getVipExpMin() <= vipExp) {//13: 10000  20000
					reVipLev = cf.getVipLevel();
					break;
				}
//				if (cf.getVipExpMin() < vipExp) {
//					reVipLev = cf.getVipLevel();
//					break;
//				}
			}
		}
		return reVipLev;
	}

	/**
	 * ensure vipRole returned not null
	 * 
	 * @param role
	 */
	private RoleVip getRoleVipNotNull(RoleInstance role) {
		RoleVip roleVip = getRoleVip(role);
		roleVip = ensureRoleVipNotNull(role, roleVip);
		return roleVip;
	}

	private byte getRoleVipLevel(RoleInstance role) {
		return getRoleVipNotNull(role).getVipLevel();
	}
	@Override
	public int getRoleVipExp(RoleInstance role) {
		return getRoleVipNotNull(role).getVipExp();
	}
	@Override
	public int getVipExp4VipLevelUp(RoleInstance role) {
		RoleVip roleVip = getRoleVipNotNull(role);
		int currentRoleVipLevel = roleVip.getVipLevel();
		if (currentRoleVipLevel >= vipConfProvider.getMaxVipLevel()) {
			return roleVip.getVipExp();
		}
		currentRoleVipLevel++;
		VipLevelUpConfig vipLevelUpConfig = vipConfProvider.getVipLevelUpConfig((byte) currentRoleVipLevel);
		return vipLevelUpConfig.getVipExpMin();
	}

	private int getDiamandsNeeded4VipLevelUp(RoleInstance role) {
		RoleVip roleVip = getRoleVipNotNull(role);
		int currentRoleVipLevel = roleVip.getVipLevel();

		int expNeeded = getVipExpNeeded4NewVipLevel(role, currentRoleVipLevel + 1);
		int rs = (int) (expNeeded / VipConstant.DIMANGDS_VIP_EXP_EXCAHNGE_RATE);
		if ((expNeeded % VipConstant.DIMANGDS_VIP_EXP_EXCAHNGE_RATE) > 0)
			rs++;
		return rs;
	}

	private boolean isLevelUpAwardAvailable(RoleInstance role, byte vipLevel) {
		boolean av = false;
		RoleVip roleVip = getRoleVipNotNull(role);
		int val = roleVip.getVipLevelUpAward();
		if ((1 << (vipLevel - 1) & val) == 0) {
			av = true;
		}
		return av;
	}
	private boolean isVipGiftAvailable(RoleInstance role, byte vipLevel) {
		boolean av = false;
		RoleVip roleVip = getRoleVipNotNull(role);
		int val = roleVip.getVipLevelGift();
		if ((1 << (vipLevel - 1) & val) == 0) {
			av = true;
		}
		return av;
	}
	/**
	 * @param role 获得纪录的功能开启的标记
	 * @param vipLevel 所定位到的某个VIP等级，viplevel的值即为二进制右数第几位
	 * @return
	 * @date 2014-9-2 下午04:22:47
	 */
	private boolean isFunctionOpen(RoleInstance role, byte vipLevel) {
		boolean open = false;
		RoleVip roleVip = getRoleVipNotNull(role);
		int val = roleVip.getVipLevelFunction();
		if ((1 << (vipLevel - 1) & val) !=0) {
			open = true;
		}
		return open;
	}
	/**
	 * when player flip the level-up gallery, we gain t he level, return the
	 * item and set its availability
	 * 
	 * @param role
	 * @param vipLevel
	 */
	private VipLevelUpAwardItem getRoleLevelUpAwardItem4Display(RoleInstance role, byte vipLevel) {
		byte rcvAvailable = 0;

		if (isLevelUpAwardAvailable(role, vipLevel)) {
			rcvAvailable = 1;
		}
		VipLevelUpConfig conf = vipConfProvider.getVipLevelUpConfig(vipLevel);
		VipLevelUpAwardItem vipLvUpItem = buildVipLevelUpAwardItem(conf, rcvAvailable);
		return vipLvUpItem;
	}

	private List<VipLevelUpAwardItem> getRoleLevelUpAwardItemList4Display(RoleInstance role) {
		byte vipLevel = getVipLevel(role);
		List<VipLevelUpAwardItem> list = Lists.newArrayList();
		for (int i = 1; i <= vipConfProvider.getMaxVipLevel(); i++) {
			VipLevelUpAwardItem it = getRoleLevelUpAwardItem4Display(role, (byte) i);
			if (i > vipLevel)
				it.setRcvAvailable((byte) 0);
			list.add(it);
		}
		return list;
	}

	private VipLevelUpAwardItem buildVipLevelUpAwardItem(VipLevelUpConfig conf, byte rcvAvailable) {
		VipLevelUpAwardItem it = new VipLevelUpAwardItem();
		it.setRcvAvailable(rcvAvailable);
		it.setVipLevel(conf.getVipLevel());
		
		List<GoodsLiteNamedItem> its = getVipAwards(conf.getVipLevel());
		it.setGoodsLiteNamedItemList(its);
		return it;
	}

	private List<GoodsLiteNamedItem> getVipAwards(byte vipLevel) {
		List<GoodsLiteNamedItem> its = Lists.newArrayList();
		List<VipLevelAwardConfig> awardConfigs = vipConfProvider.getVipLevelAwardConfigList(vipLevel);
		for (VipLevelAwardConfig cf : awardConfigs) {
			GoodsLiteNamedItem goodsItem = cf.getGoodsLiteNamedItem();
			if(goodsItem != null){
				its.add(cf.getGoodsLiteNamedItem());
			}
		}
		return its;
	}

	private int getVipExpNeeded4NewVipLevel(RoleInstance role, int wantedVipLevel) {
		RoleVip roleVip = getRoleVipNotNull(role);
		int currentRoleVipExp = roleVip.getVipExp();
		VipLevelUpConfig vipLevelUpConfig = vipConfProvider.getVipLevelUpConfig((byte) wantedVipLevel);
		return vipLevelUpConfig.getVipExpMin() - currentRoleVipExp;
	}

	private RoleVip ensureRoleVipNotNull(RoleInstance role, RoleVip roleVip) {
		return ensureRoleVipNotNull(role.getIntRoleId(), roleVip);
	}

	private RoleVip ensureRoleVipNotNull(int roleId, RoleVip roleVip) {
		if (roleVip == null) {
			roleVip = new RoleVip();
			roleVip.setRoleId(roleId);
		}
		return roleVip;
	}
	private boolean isRoleVip(RoleInstance role) {
		RoleVip roleVip = getRoleVip(role);
		if (roleVip != null && roleVip.getVipLevel() > 0) {
			return true;
		}
		return false;
	}

	private boolean isRoleVip(RoleVip roleVip) {
		if (roleVip != null && roleVip.getVipLevel() > 0) {
			return true;
		}
		return false;
	}

	private String buildPrivilegeInfo(byte vipLevel) {
		if (vipLevel == 0)
			vipLevel = 1;
		return vipConfProvider.getPrivilegeInfo(vipLevel);
	}
	private String[] buildPrivilegeInfos(byte vipLevel) {
		if (vipLevel == 0)
			vipLevel = 1;
		return vipConfProvider.getPrivilegeInfos(vipLevel);
	}
	/**
	 * 
	 * @param role
	 * @param vipPriType
	 * @param param
	 * @return 下一VIP等级的描述信息
	 * @date 2014-8-26 下午09:20:00
	 */
	@Override
	public String getNextVipLevelPrivilegeInfo(RoleInstance role, int vipPriType, String param){
		byte vipLevel = getVipLevel(role);
		return getNextVipLevelPrivilegeInfo(vipLevel, vipPriType, param);
	}
	@Override
	public String getNextVipLevelPrivilegeInfo(byte vipLevel, int vipPriType, String param){
		vipLevel++;
		for(;vipLevel <= vipConfProvider.getMaxVipLevel();vipLevel++){
			VipPrivilegeConfig config = vipConfProvider.getVipPrivilegeInfo(vipLevel,vipPriType,param);
			if(config!=null){
				String vipLevelStr = getText(TextId.VIP_LEVEL_BE);
				return MessageFormat.format(vipLevelStr,vipLevel)+config.getVipPriIntroduction();
			}
		}
		if(vipLevel == 1){
			return getText(TextId.VIP_PRIVILEGE_NO_EXIST);
		}
		return "";
	}
	@Override
	public String getVipLevelPrivilegeInfo(RoleInstance role, int vipPriType, String param){
		byte vipLevel = getVipLevel(role);
		VipPrivilegeConfig config = vipConfProvider.getVipPrivilegeInfo(vipLevel,vipPriType,param);
		if(config!=null){
				return config.getVipPriIntroduction();
		}
		return "";
	}
	
	private List<GoodsLiteItem> buildVipLevelUpAwardItems(byte vipLevel) {
		List<GoodsLiteItem> its = Lists.newArrayList();
		List<VipLevelAwardConfig> awardConfigs = vipConfProvider.getVipLevelAwardConfigList(vipLevel);
		for (VipLevelAwardConfig cf : awardConfigs) {
			GoodsLiteItem goodsItem = cf.getGoodsLiteNamedItem();
			if(goodsItem != null){
				its.add(cf.getGoodsLiteNamedItem());
			}
		}
		return its;
	}
	private GoodsLiteItem buildVipGiftAwardItem(byte vipLevel) {
		GoodsLiteItem it = new GoodsLiteItem();
		VipGiftConfig cf = vipConfProvider.getVipGiftConfig(vipLevel);
		it.setBindType(cf.getBind());
		it.setGoodsId(cf.getGoodsId());
		it.setNum(cf.getNum());
		return it;
	}

	@Override
	public boolean isFullVipLevel(RoleInstance role) {
		byte vipLevel = getRoleVipLevel(role);
		return vipLevel >= vipConfProvider.getMaxVipLevel();
	}

	@Override
	public Message getVipInfo(RoleInstance role) {
		byte vipLevel = getRoleVipLevel(role);
		String headerStr = "";
		if (isFullVipLevel(role)) {
			headerStr = vipConfProvider.getMaxLevelHeaderInfo();
		} else {
			int diamansNeeded4LevelUp = getDiamandsNeeded4VipLevelUp(role);
			headerStr = vipConfProvider.getLevelUpHeaderInfo();
			headerStr = MessageFormat.format(headerStr, diamansNeeded4LevelUp, vipLevel+1);
		}
		C2516_VipInfoRespMessage msg = new C2516_VipInfoRespMessage();
		msg.setHeaderStr(headerStr);
		int currentVipExp = getRoleVipExp(role);
		int nextLevelExpNeeded = getVipExp4VipLevelUp(role);
		msg.setCurrentVipExp(currentVipExp);
		msg.setVipLevel(vipLevel);
		msg.setNextLevelExpNeeded(nextLevelExpNeeded);
		return msg;
	}
	@Override
	public Message openVipPanel(RoleInstance role) {
//		boolean vipFlag = isRoleVip(role);
		C2510_VipDisplayRespMessage msg = new C2510_VipDisplayRespMessage();

		byte vipLevel = getRoleVipLevel(role);
		byte priVipLevel = vipLevel == 0 ? 1 : vipLevel;

		List<VipLevelUpAwardItem> vipLvUpItemList = getRoleLevelUpAwardItemList4Display(role);
		String privilegeInfos[] = buildPrivilegeInfos(vipLevel);
		
//		ArrayList<VipLevelFunctionConfig> functionConfigList = vipConfProvider.getVipLevelFunctionList(vipLevel);
//		if(!Util.isEmpty(functionConfigList)){
//			List<VipLevelFunctionItem> functionItemList = buildFunctionItemList(functionConfigList, role);
//			msg.setFunctionItemList(functionItemList);
//		}
		
		msg.setVipMaxLevel(vipConfProvider.getMaxVipLevel());
		msg.setVipLevel(vipLevel);
		msg.setPriVipLevel(priVipLevel);
		msg.setVipLvUpItemList(vipLvUpItemList);
		msg.setPrivilegeInfos(privilegeInfos);
		
		Message infoMsg = getVipInfo(role);
		role.getBehavior().sendMessage(infoMsg);
		return msg;
	}

	private List<VipLevelFunctionItem> buildFunctionItemList(
			ArrayList<VipLevelFunctionConfig> functionConfigList, RoleInstance role) {
		List<VipLevelFunctionItem> functionItemList = Lists.newArrayList();
		for (VipLevelFunctionConfig vipLevelFunctionConfig : functionConfigList) {
			VipLevelFunctionItem item = buildVipLevelFunctionItem(vipLevelFunctionConfig, role);
			if(item != null)
				functionItemList.add(item);
		}
		return functionItemList;
	}

	private VipLevelFunctionItem buildVipLevelFunctionItem(
			VipLevelFunctionConfig config, RoleInstance role) {
		if(config == null){
			return null;
		}
		VipLevelFunctionItem item = new VipLevelFunctionItem();
		item.setFunctionId(config.getVipLevelFucId());
		boolean open = isFunctionOpen(role, config.getVipLevel());
		if(open){
			item.setFunctionButtonRes(config.getVipLevelFucOnButtionRes());
			return item;
		}
		item.setFunctionButtonRes(config.getVipLevelFucOffButtionRes());
		return item;
	}

	/**
	 * @see com.game.draco.app.vip.VipApp#vipGalleryShift(sacred.alliance.magic.vo.RoleInstance, byte)
	 */
	@Override
	public Message vipGalleryShift(RoleInstance role, byte vipLevel) {
		C2511_VipGalleryShiftRespMessage msg = new C2511_VipGalleryShiftRespMessage();
		String privilegeInfos[] = buildPrivilegeInfos(vipLevel);
		msg.setVipLevel(vipLevel);
		msg.setPrivilegeInfos(privilegeInfos);
		return msg;
	}

	@Override
	public Message receiveVipLevelUpAward(RoleInstance role, byte vipLevel) {
		C2513_VipLevelUpAwardReceiveRespMessage msg = new C2513_VipLevelUpAwardReceiveRespMessage();
		RoleVip roleVip = getRoleVipNotNull(role);
		if (roleVip.getVipLevel() == 0) {
			msg.setType((byte) 0);
			msg.setInfo(getText(TextId.NO_VIP_Award));
			return msg;
		} else if (roleVip.getVipLevel() < vipLevel) {
			msg.setType((byte) 0);
			msg.setInfo(getText(TextId.Vip_LEVEL_TOO_LOW));
			return msg;
		} else if (vipLevel < 1) {
			msg.setType((byte) 0);
			msg.setInfo(getText(TextId.Vip_LEVEL_ERR));
			return msg;
		}
		byte type = 0; // 1:success 0:failed
		String info = "";
		if (!isLevelUpAwardAvailable(role, vipLevel)) {
			info = getText(TextId.VIP_LevelUp_Award_Has_Received);
			msg.setType(type);
			msg.setInfo(info);
			return msg;
		} else {
			List<GoodsLiteItem> awardItems = buildVipLevelUpAwardItems(vipLevel);
			sendVipAwards(role, awardItems);
		}
		roleVip.updateVipLevelUpAwardByLevel(vipLevel);
		GameContext.getBaseDAO().saveOrUpdate(roleVip);
		type = 1;
		msg.setType(type);
		msg.setInfo(info);
		return msg;
	}

	private boolean sendVipAward(RoleInstance role, GoodsLiteItem goodsItem) {
		List<GoodsOperateBean> addList = new ArrayList<GoodsOperateBean>();
		GoodsOperateBean bean = new GoodsOperateBean();
		bean.setGoodsId(goodsItem.getGoodsId());
		bean.setGoodsNum(goodsItem.getNum());
		bean.setBindType(BindingType.get(goodsItem.getBindType()));
		addList.add(bean);

		sendAward(role, addList);
		return true;
	}

	private boolean sendAward(RoleInstance role, List<GoodsOperateBean> addList) {
		AddGoodsBeanResult goodsResult = GameContext.getUserGoodsApp().addSomeGoodsBeanForBag(role, addList, OutputConsumeType.VIP_output);
		// if bag is full then send mail
		List<GoodsOperateBean> putFailureList = goodsResult.getPutFailureList();
		try {
			if (!Util.isEmpty(putFailureList)) {
				String context = getText(TextId.VIP_Mail_Context);
				GameContext.getMailApp().sendMail(role.getRoleId(), MailSendRoleType.VIP.getName(), context, MailSendRoleType.VIP.getName(),
						OutputConsumeType.VIP_mail_output.getType(), putFailureList);
			}
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	}
	private boolean sendVipAwards(RoleInstance role, List<GoodsLiteItem> goodsItems) {
		List<GoodsOperateBean> addList = new ArrayList<GoodsOperateBean>();
		for (GoodsLiteItem goodsItem : goodsItems) {
			GoodsOperateBean bean = new GoodsOperateBean();
			bean.setGoodsId(goodsItem.getGoodsId());
			bean.setGoodsNum(goodsItem.getNum());
			bean.setBindType(BindingType.get(goodsItem.getBindType()));
			addList.add(bean);
		}
		sendAward(role, addList);
		return true;
	}

	private String getText(String textId) {
		return GameContext.getI18n().getText(textId);
	}

	@Override
	public byte getVipLevel(String roleId) {
		RoleVip roleVip = roleVipMap.get(roleId);
		if(roleVip != null){
			return roleVip.getVipLevel();
		}
		//ssdb
		AsyncPvpRoleAttr att = GameContext.getAsyncPvpApp().getAsyncPvpRoleAttr(roleId);
		if(att == null){
			return 0;
		}
		return att.getVipLevel();
	}

	public byte getVipLevel(RoleInstance role) {
		String roleId = role.getRoleId();
		return getVipLevel(roleId);
	}

	private void addRoleVipMap(RoleVip roleVip) {
		if (roleVip == null)
			return;
		String key = roleVip.getRoleId() + "";
		roleVipMap.put(key, roleVip);
	}

	private RoleVip getRoleVip(RoleInstance role) {
		String key = role.getRoleId();
		return getRoleVip(key);
	}

	private <T> RoleVip getRoleVip(T roleId) {
		String key = roleId + "";
		return roleVipMap.get(key);
	}

	private VipLevelUpResult levelUp(int roleId, int vipExp) {
		RoleVip roleVip = null;
		// online
		if (GameContext.getOnlineCenter().isOnlineByRoleId(roleId + "")) {
			roleVip = getRoleVip(roleId);
		} else {
			roleVip = GameContext.getBaseDAO().selectEntity(RoleVip.class, RoleVip.ROLE_ID, roleId);
		}
		roleVip = ensureRoleVipNotNull(roleId, roleVip);
		return levelUp(roleVip, vipExp);
	}

	private VipLevelUpResult levelUp(RoleVip roleVip, int vipExp) {
		VipLevelUpResult vipLevelUpResult = new VipLevelUpResult();
		byte curVipLevel = roleVip.getVipLevel();
		// if role is full-level, return;
		if (curVipLevel >= vipConfProvider.getMaxVipLevel()) {
			return vipLevelUpResult;
		}
		int curExp = roleVip.getVipExp();
		int sumExp = curExp + vipExp;
		byte newVipLevel = getVipLevelByVipExp(sumExp);
		roleVip.setVipExp(sumExp);
		if (newVipLevel > curVipLevel) {
			roleVip.setVipLevel(newVipLevel);
			vipLevelUpResult.setNewVipLevel(newVipLevel);
			vipLevelUpResult.setOldVipLevel(curVipLevel);
			vipLevelUpResult.setVipLevelUp(true);
			vipLevelUpResult.setResult((byte) 1);
			vipLevelUpResult.setRoleVip(roleVip);
		}

		try {
			// if online
			if (GameContext.getOnlineCenter().isOnlineByRoleId(roleVip.getRoleId() + ""))
				addRoleVipMap(roleVip);// roleVipApp
			// db
			GameContext.getBaseDAO().saveOrUpdate(roleVip);
			// online notify
		} catch (Exception e) {
			Log4jManager.VIP_DB_LOG.error("VipApp.save error, roleId=" + roleVip.getRoleId(), e);
		}
		return vipLevelUpResult;
	}

	@Override
	public void addDiamands(int roleId, int daimans) {
		int vipExp = (int) VipConstant.DIMANGDS_VIP_EXP_EXCAHNGE_RATE * daimans;
		addVipLevelExp(roleId, vipExp);
	}
	
	@Override
	public int getVipPrivilegeTimes(String roleId, byte privilegeType,
			String param) {
		if(null == param){
			param = "" ;
		}
		return this.getVipPrivilegeTimes(this.getVipLevel(roleId),
				privilegeType, param);
	}
	
	@Override
	public int getVipPrivilegeTimes(int vipLevel, int privilegeType,String param){
		String key = vipLevel + Cat.underline + privilegeType + Cat.underline + param;
		Map<String, Integer> map = vipConfProvider.getVipPrivilegeTimesMap();
		Integer vipPrivilegeTimes = map.get(key);
		if (null == vipPrivilegeTimes) {
			return 0;
		}
		return vipPrivilegeTimes;
	}

	@Override
	public int getOpenVipLevel(int vipPriType,String param) {
		for(int i=1;i <= this.vipConfProvider.maxVipLevel;i++){
			int times = this.getVipPrivilegeTimes(i, vipPriType, param) ;
			if(times > 0){
				return i ;
			}
		}
		return 0;
	}
	
	public int getVipLevelFucOnButtionRes(RoleInstance role, String functionId){
		VipLevelFunctionConfig conf = vipConfProvider.getVipLevelFunctionConfig(functionId);
		return conf.getVipLevelFucOnButtionRes();
	}
	/**
	 * 可能触发 弹出开启功能的购买面板，直接进入功能面板
	 */
	@Override
	public Message callVipFunction(RoleInstance role, String functionId, byte confirm) {
		C2512_VipCallFunctionRespMessage msg = new C2512_VipCallFunctionRespMessage();	
		msg.setType(Result.FAIL);
		
		RoleVip roleVip = getRoleVipNotNull(role);
		VipLevelFunctionConfig conf = vipConfProvider.getVipLevelFunctionConfig(functionId);
		VipFunctionType funcType = VipFunctionType.getVipFunctionType(conf.getVipLevelFucType());
		Result result = new Result().setResult(Result.FAIL);
		if(conf == null||funcType == null)
		{
			String info = getText(TextId.VIP_LEVEL_FUNCTION_NO_EXIST);
			msg.setInfo(info);
			return msg;
		}
		byte roleVipLevel = roleVip.getVipLevel();
		byte fucVipLevel = conf.getVipLevel();
		if(roleVipLevel < fucVipLevel){
			String info = getText(TextId.Vip_LEVEL_TOO_LOW);
			msg.setInfo(info);
			return msg;
		}
		//判断是否已经开启
		boolean open = isFunctionOpen(role, fucVipLevel);
		if(open){//弹出功能面板
			result = enterFunction(role, functionId);
			if(result.isSuccess()){
				return null;
			}else{
				msg.setInfo(result.getInfo());
				return msg;
			}
		}
		//弹出召唤面板，引导购买
		//判断消耗
		int money = conf.getVipLevelFucMoney();
		byte moneyType = conf.getVipLevelFucMoneyType();
		AttributeType attr = AttributeType.get(moneyType);
		
		//二次确认
		if(confirm == 0){
			confirm = 1;
			String tips = getTipformat(TextId.VIP_LEVEL_FUNCTION_CONFIRM_TIPS, attr.getName(), money);
			Message notifyMsg = QuickCostHelper.getMessage(role, CALL_FUNCTION_CMDID, functionId+","+confirm, (short)0, "", money, 0, tips);
			role.getBehavior().sendMessage(notifyMsg);
			return null;
		}
		//【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, attr, money);
		if(ar.isIgnore()){   //弹板
			return null;
		}
		if(!ar.isSuccess()){ //不足
			result = getOpenFunctionMoneyNotEnoughResult(result, money, attr);
			return getResultInfoMessage(result, msg);
		}

//		switch (attr){
//		case goldMoney:
//			if (role.getGoldMoney() < money) {
//				result = getOpenFunctionMoneyNotEnoughResult(result, money, AttributeType.goldMoney);
//				return getResultInfoMessage(result, msg);
//			}
//			break;
//		case bindingGoldMoney:
//			if (role.getBindingGoldMoney() < money) {
//				result = getOpenFunctionMoneyNotEnoughResult(result, money, AttributeType.bindingGoldMoney);
//				return getResultInfoMessage(result, msg);
//			}
//			break;
//		case silverMoney:
//			if (role.getSilverMoney() < money) {
//				result = getOpenFunctionMoneyNotEnoughResult(result, money, AttributeType.silverMoney);
//				return getResultInfoMessage(result, msg);
//			}
//			break;
//		default :
//			result = result.setInfo(getText(TextId.VIP_LEVEL_FUNCTION_NO_ERR));//  del s
//			return getResultInfoMessage(result, msg);
//			
//		}
		GameContext.getUserAttributeApp().changeRoleMoney(role,
				attr, OperatorType.Decrease,
				money, OutputConsumeType.vip_consume);
		//通知用户属性变化
		role.getBehavior().notifyAttribute();
		//记录所激活的功能
		roleVip.updateVipLevelFunctionByLevel((int)fucVipLevel);
		GameContext.getBaseDAO().saveOrUpdate(roleVip);
		
		//更新按钮资源
		msg.setType(Result.SUCCESS);
		msg.setFunctionId(functionId);
		msg.setFunctionButtonRes(conf.getVipLevelFucOnButtionRes());
		return msg;
	}
	private Result enterFunction(RoleInstance role, String functionId) {
		VipLevelFunctionConfig conf = vipConfProvider.getVipLevelFunctionConfig(functionId);
		VipFunctionType funcType = VipFunctionType.getVipFunctionType(conf.getVipLevelFucType());
		Result result = funcType.enterFunction(role, conf.getVipLevelFucParam());
		if(result.isSuccess()){
			return result;
		}
		result.setInfo(getText(TextId.VIP_LEVEL_FUNCTION_ENTER_ERR));
		return result;
	}
	
	private Message getResultInfoMessage(Result result, C2512_VipCallFunctionRespMessage msg){
		msg.setType(result.getResult());
		msg.setInfo(result.getInfo());
		return msg;
	}
	private Result getOpenFunctionMoneyNotEnoughResult(Result result,
			int expendMoney, AttributeType consumeType) {
		result.setResult(Result.FAIL);
		String context = MessageFormat.format(
				   getText(TextId.VIP_LEVEL_OPEN_FUNCTION_MONEY_NOT_ENOUGH),
				   consumeType.getName(),
				   expendMoney);
		result.setInfo(context);
		return result;
	}
	public static String getTipformat(String pattern, Object ... arguments) {
	    String pStr = GameContext.getI18n().getText(pattern);
	    return MessageFormat.format(pStr, arguments);
	}
	// 【商城.运营】
	@Override
	public List<ShopVipGiftItem> getShopVipGiftItems(RoleInstance role) {
		List<ShopVipGiftItem> list = Lists.newArrayList();
		RoleVip roleVip = getRoleVipNotNull(role);
		if (roleVip.getVipLevel() <= 0) {//非VIP不显示
			return list;
		}
		Collection<VipGiftConfig> cfs =  vipConfProvider.getVipGiftMap().values();
		if(Util.isEmpty(cfs)){
			return list;
		}
		for (VipGiftConfig cf : cfs) {
			if(cf == null){
				continue;
			}
			if(!isVipGiftAvailable(role, cf.getVipLevel())){
				continue;
			}
			ShopVipGiftItem item = buildShopVipGiftItem(cf);
				if(item!=null){
					list.add(item);
			}
		}
		return list;
	}

	private ShopVipGiftItem buildShopVipGiftItem(VipGiftConfig cf) {
		if(cf == null){
			return null;
		}
		GoodsLiteNamedItem nameItem = cf.getGoodsLiteNamedItem();
		if(nameItem == null){
			return null;
		}
		ShopVipGiftItem item = new ShopVipGiftItem();
		item.setDiamonds(cf.getDiamonds());
		item.setGiftInfo(cf.getGiftInfo());
		item.setVipLevel(cf.getVipLevel());
		item.setNameItem(nameItem);
		return item;
	}
	//item null
	@Override
	public ShopVipGiftItem getShopVipGiftItemsByVipLevel(RoleInstance role, byte vipLevel) {
		VipGiftConfig cf =  vipConfProvider.getVipGiftConfig(vipLevel);
		RoleVip roleVip = getRoleVipNotNull(role);
		if (roleVip.getVipLevel() <= 0) {//非VIP不显示
			return null;
		}
		if(!isVipGiftAvailable(role, vipLevel)){
			return null;
		}
		ShopVipGiftItem item = buildShopVipGiftItem(cf);
		return item;
	}

	@Override
	public Message receiveShopVipGift(RoleInstance role, byte vipLevel) {
		C2107_VipLevelGiftReceiveRespMessage msg = new C2107_VipLevelGiftReceiveRespMessage();
		RoleVip roleVip = getRoleVipNotNull(role);
		if (roleVip.getVipLevel() == 0) {
			msg.setType((byte) 0);
			msg.setInfo(getText(TextId.NO_VIP_GIFT));
			return msg;
		} else if (roleVip.getVipLevel() < vipLevel) {
			msg.setType((byte) 0);
			msg.setInfo(getText(TextId.Vip_LEVEL_TOO_LOW));
			return msg;
		} else if (vipLevel < 1) {
			msg.setType((byte) 0);
			msg.setInfo(getText(TextId.Vip_LEVEL_ERR));
			return msg;
		}
		byte type = 0; // 1:success 0:failed
		String info = "";
		if (!isVipGiftAvailable(role, vipLevel)) {
			info = format(getText(TextId.VIP_GIFT_HAS_RECEIVED),vipLevel);
			msg.setType(type);
			msg.setInfo(info);
			return msg;
		} else {
			GoodsLiteItem awardItem = buildVipGiftAwardItem(vipLevel);
			sendVipAward(role, awardItem);
		}
		roleVip.updateVipLevelGift(vipLevel);
		GameContext.getBaseDAO().saveOrUpdate(roleVip);
		type = 1;
		msg.setType(type);
		msg.setInfo(info);
		return msg;
	}

	@Override
	public Message vipShopGiftDispaly(RoleInstance role) {
		List<ShopVipGiftItem> list = getShopVipGiftItems(role);
		C2106_VipShopGiftRespMessage msg = new C2106_VipShopGiftRespMessage();
		if(!Util.isEmpty(list)){
			msg.setList(list);
		}
		return msg;
	}
    public String format(String pattern, Object ... arguments) {
        MessageFormat temp = new MessageFormat(pattern);
        return temp.format(arguments);
    }

	@Override
	public void onRoleLevelUp(RoleInstance role) {
		for(VipLevelUpConfig cf : vipConfProvider.getLevelUpConfigs()){
			if(cf.getRoleLevel() <= 0){
				continue;
			}
			if(role.getLevel() == cf.getRoleLevel()){
				byte oldVipLevel = this.getVipLevel(role);
				byte newVipLevel = cf.getVipLevel();
				if(newVipLevel > oldVipLevel){
					int addValue = getVipExpNeeded4NewVipLevel(role, newVipLevel);
					addVipLevelExp(role.getIntRoleId(), addValue);
				}
			}
		}
	}
}
