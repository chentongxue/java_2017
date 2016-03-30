package com.game.draco.app.vip;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.app.hint.HintId;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.vip.config.VipLevelUpConfig;
import com.game.draco.app.vip.domain.RoleVip;
import com.game.draco.app.vip.domain.VipConstant;
import com.game.draco.app.vip.vo.VipLevelUpResult;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.item.VipLevelUpAwardItem;
import com.game.draco.message.push.C2514_VipLevelUpNotifyMessage;
import com.game.draco.message.response.C2510_VipDisplayRespMessage;
import com.game.draco.message.response.C2511_VipGalleryShiftRespMessage;
import com.game.draco.message.response.C2512_VipDailyAwardReceiveRespMessage;
import com.game.draco.message.response.C2513_VipLevelUpAwardReceiveRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
/**
 * new vip
 */
public class VipAppImpl implements VipApp {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	//roleVipApp
	private Map<String,RoleVip> roleVipMap = Maps.newConcurrentMap();
	private VipConfigProvider vipConfProvider; 

	@Override
	public void login(RoleInstance role) {
		try {
			RoleVip roleVip = GameContext.getBaseDAO().selectEntity(RoleVip.class, RoleVip.ROLE_ID, role.getRoleId());
			roleVip = ensureRoleVipNotNull(role, roleVip);
			roleVip = verifyVipLevel(roleVip);
			addRoleVipMap(roleVip);//roleVipApp
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("login game, query or save vip info failed", e);
		}
	}
	/*
	 * if vipExp range expand, VIP level shouldn't be reduced
	 * else if vipExp range shrink, preferring increase VIP level instead
	 */
	private RoleVip verifyVipLevel(RoleVip roleVip){
		int vipExp = roleVip.getVipExp();
		byte expVipLevel = getVipLevelByVipExp(vipExp);
		byte vipLevel = roleVip.getVipLevel();
		if(expVipLevel>vipLevel){
			roleVip.setVipLevel(expVipLevel);
			GameContext.getBaseDAO().saveOrUpdate(roleVip);
		}
		return roleVip;
	}

	/**
	 * save vip information for vip role
	 */
	@Override
	public void offline(RoleInstance role) {
		RoleVip roleVip = null;
		try {
			roleVip = getRoleVip(role);
			// no database Operation for non-vip
			if (!isRoleVip(roleVip)) {
				String key = role.getRoleId();
				roleVipMap.remove(key);
				return;
			}
			GameContext.getBaseDAO().saveOrUpdate(roleVip);
		} catch (Exception e) {
			e.printStackTrace();
			if (null != roleVip) {
				Log4jManager.OFFLINE_VIP_DB_LOG.info(roleVip.toString());
			}
			Log4jManager.OFFLINE_ERROR_LOG.error(
					"VipApp.offline error, roleId=" + role.getRoleId()
							+ ",userId=" + role.getUserId(), e);
		}
		String key = role.getRoleId();
		roleVipMap.remove(key);
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
	public Set<HintId> getHintIdSet(RoleInstance role) {
		Set<HintId> set = new HashSet<HintId>();
		// if(this.haveVipReward(role)){
		// set.add(HintId.VIP_Reward);
		// }
		return set;
	}

	@Override
	public void hintChange(RoleInstance role, HintId hintId) {
		try {
			// GameContext.getHintApp().hintChange(role, hintId,
			// this.haveVipReward(role));
		} catch (Exception e) {
			this.logger.error("RoleVipApp.hintChange error: ", e);
		}
	}
	@Override
	public void addVipLevelExp(int roleId, int vipExp) {
		//
		VipLevelUpResult rs = levelUp(roleId, vipExp);
		//viplevelUp
		if(rs.isVipLevelUp()){
			//online notify
			if(GameContext.getOnlineCenter().isOnlineByRoleId(roleId+"")){
				byte newVipLevel = rs.getNewVipLevel();
				String info = getText(TextId.VIP_LEVEL_UP_TIP);
				info = MessageFormat.format(info,newVipLevel+"");
				C2514_VipLevelUpNotifyMessage msg = new C2514_VipLevelUpNotifyMessage();
				msg.setNewVipLevel(newVipLevel);
				msg.setRoleId(roleId);
				RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId+"");
				role.getBehavior().sendMessage(msg);
			}
		}
	}
	private byte getVipLevelByVipExp(int vipExp) {
		byte reVipLev = 0;
		Map<String, VipLevelUpConfig> map = vipConfProvider.getLevelUpConfigMap();
		for(int i = vipConfProvider.getMaxVipLevel(); i>0; i--){
			if(map.containsKey(i+"")){
				VipLevelUpConfig cf = map.get(i+"");
				if(cf.getVipExpMin()<vipExp){
					reVipLev = cf.getVipLevel();
					break;
				}
			}
		}
		return reVipLev;
	}

	/**
	 * ensure vipRole returned not null
	 * @param role
	 */
	private RoleVip getRoleVipNotNull(RoleInstance role) {
		RoleVip roleVip = getRoleVip(role);
		roleVip = ensureRoleVipNotNull(role, roleVip);
		return roleVip;
	}

	private byte getDailyAwardReceivedFlag(RoleInstance role) {
		RoleVip roleVip = getRoleVipNotNull(role);
		Date d = roleVip.getLastReceiveAwardTime();
		if(DateUtil.sameDay(d,new Date())){
			return 0;
		}
		return 1;
	}
	private byte getRoleVipLevel(RoleInstance role) {
		return getRoleVipNotNull(role).getVipLevel();
	}

	private int getRoleVipExp(RoleInstance role) {
		return getRoleVipNotNull(role).getVipExp();
	}

	public int getVipExp4VipLevelUp(RoleInstance role){
		RoleVip roleVip = getRoleVipNotNull(role);
		int currentRoleVipLevel = roleVip.getVipLevel();
		if(currentRoleVipLevel>=vipConfProvider.getMaxVipLevel()){
			return roleVip.getVipExp();
		}
		currentRoleVipLevel++;
		VipLevelUpConfig vipLevelUpConfig = vipConfProvider.getVipLevelUpConfig((byte)currentRoleVipLevel);
		return vipLevelUpConfig.getVipExpMin();
	}

	private int getDiamandsNeeded4VipLevelUp(RoleInstance role) {
		RoleVip roleVip = getRoleVipNotNull(role);
		int currentRoleVipLevel = roleVip.getVipLevel();

		int expNeeded = getVipExpNeeded4NewVipLevel(role,
				currentRoleVipLevel + 1);
		int rs = (int)(expNeeded / VipConstant.DIMANGDS_VIP_EXP_EXCAHNGE_RATE);
		if((expNeeded % VipConstant.DIMANGDS_VIP_EXP_EXCAHNGE_RATE)>0)
			rs++;
		return rs;
	}

	private boolean isLevelUpAwardAvailable(RoleInstance role, byte vipLevel) {
		boolean av = false;
		RoleVip roleVip = getRoleVipNotNull(role);
		int val = roleVip.getVipLevelUpAward();
		if ((1<<(vipLevel - 1)&val) == 0) {
			av = true;
		}
		return av;
	}

	/**
	 * when player flip the level-up gallery, we gain t
	 * he level, return the item
	 * and set its availability
	 * 
	 * @param role
	 * @param vipLevel
	 */
	private VipLevelUpAwardItem getRoleLevelUpAwardItem4Display(
			RoleInstance role, byte vipLevel) {
		byte rcvAvailable = 0;

		if (isLevelUpAwardAvailable(role, vipLevel)) {
			rcvAvailable = 1;
		}
		VipLevelUpConfig conf = vipConfProvider.getVipLevelUpConfig(vipLevel);
		VipLevelUpAwardItem vipLvUpItem = buildVipLevelUpAwardItem(conf,
				rcvAvailable);
		return vipLvUpItem;
	}

	private List<VipLevelUpAwardItem> getRoleLevelUpAwardItemList4Display(
			RoleInstance role) {
		byte vipLevel = getVipLevel(role);
		List<VipLevelUpAwardItem> list = Lists.newArrayList(); 
		for (int i = 1; i <= vipConfProvider.getMaxVipLevel(); i++) {
			VipLevelUpAwardItem it = getRoleLevelUpAwardItem4Display(role, (byte)i);
			if(i > vipLevel)
				it.setRcvAvailable((byte)0);
			list.add(it);
		}
		return list;
	}

	private VipLevelUpAwardItem buildVipLevelUpAwardItem(VipLevelUpConfig conf,
			byte rcvAvailable) {
		VipLevelUpAwardItem vipLvUpItem = new VipLevelUpAwardItem();
		vipLvUpItem.setRcvAvailable(rcvAvailable);
		vipLvUpItem.setVipLevel(conf.getVipLevel());//
		vipLvUpItem.setVipLevelUpAwardId(conf.getVipLevelUpAwardId());
		vipLvUpItem.setVipLevelUpAwardInfo(conf.getVipLevelUpAwardInfo());
		vipLvUpItem.setVipLevelUpAwardInfo1(conf.getVipLevelUpAwardInfo1());
		vipLvUpItem.setVipLevelUpAwardImageId(conf.getVipLevelUpAwardImageId());
		return vipLvUpItem;
	}

	private int getVipExpNeeded4NewVipLevel(RoleInstance role,
			int wantedVipLevel) {
		RoleVip roleVip = getRoleVipNotNull(role);
		int currentRoleVipExp = roleVip.getVipExp();
		VipLevelUpConfig vipLevelUpConfig = vipConfProvider.getVipLevelUpConfig((byte)wantedVipLevel);
		return vipLevelUpConfig.getVipExpMin() - currentRoleVipExp;
	}

	private RoleVip ensureRoleVipNotNull(RoleInstance role, RoleVip roleVip) {
		return ensureRoleVipNotNull(role.getIntRoleId(),roleVip);
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
		if(vipLevel==0)
			vipLevel = 1;
		return vipConfProvider.getPrivilegeInfo(vipLevel);
	}


	private GoodsLiteItem buildDailyAwardItem(byte vipLevel) {
		if(vipLevel==0)
			vipLevel = 1;
		GoodsLiteItem dailyAwardItem = new GoodsLiteItem();
		VipLevelUpConfig conf = vipConfProvider.getVipLevelUpConfig(vipLevel);
		if(conf==null){
			return dailyAwardItem;
		}
		dailyAwardItem.setBindType(conf.getVipDailyAwardBind());
		dailyAwardItem.setGoodsId(conf.getVipDailyAwardId());
		dailyAwardItem.setNum(conf.getVipDailyAwardNum());
		return dailyAwardItem;
	}
	private GoodsLiteItem buildVipLevelUpAwardItem(byte vipLevel) {
		GoodsLiteItem awardItem = new GoodsLiteItem();
		VipLevelUpConfig conf = vipConfProvider.getVipLevelUpConfig(vipLevel);
		awardItem.setBindType(conf.getVipLevelUpAwardBind());
		awardItem.setGoodsId(conf.getVipLevelUpAwardId());
		awardItem.setNum(conf.getVipLevelUpAwardNum());
		return awardItem;
	}

	public boolean isFullVipLevel(RoleInstance role){
		byte vipLevel = getRoleVipLevel(role);
		return vipLevel >= vipConfProvider.getMaxVipLevel();
	}
	// check daily award available
	@Override
	public Message openVipPanel(RoleInstance role) {
		// 4 daily award
		boolean vipFlag = isRoleVip(role);
		// configure the return msg
		C2510_VipDisplayRespMessage msg = new C2510_VipDisplayRespMessage();

		byte vipLevel = getRoleVipLevel(role);
		byte priVipLevel = vipLevel==0?1:vipLevel;

		byte dailyAwardReceived = 0;
		if(vipFlag){
			dailyAwardReceived = getDailyAwardReceivedFlag(role);
		}
		String headerStr = "";
		if(isFullVipLevel(role)){
			headerStr = vipConfProvider.getMaxLevelHeaderInfo();
		} 
		else{
			int diamansNeeded4LevelUp = getDiamandsNeeded4VipLevelUp(role);
			headerStr = vipConfProvider.getLevelUpHeaderInfo();
			headerStr = MessageFormat.format(headerStr, diamansNeeded4LevelUp);
		}
		
		int currentVipExp = getRoleVipExp(role);
		int nextLevelExpNeeded = getVipExp4VipLevelUp(role);
		List<VipLevelUpAwardItem> vipLvUpItemList = getRoleLevelUpAwardItemList4Display(role);
		String privilegeInfo = buildPrivilegeInfo(vipLevel);
		GoodsLiteItem dailyAwardItem = buildDailyAwardItem(vipLevel);
		msg.setVipMaxLevel(vipConfProvider.getMaxVipLevel());
		msg.setVipLevel(vipLevel);
        msg.setDailyAwardReceived(dailyAwardReceived);
		msg.setHeaderStr(headerStr);
		msg.setCurrentVipExp(currentVipExp);
		msg.setNextLevelExpNeeded(nextLevelExpNeeded);
		msg.setPriVipLevel(priVipLevel);
		msg.setVipLvUpItemList(vipLvUpItemList);
		msg.setPrivilegeInfo(privilegeInfo);
		msg.setDailyAwardItem(dailyAwardItem);
		
		return msg;
	}
	/**
	 * @see com.game.draco.app.vip.VipApp#vipGalleryShift(sacred.alliance.magic.vo.RoleInstance, byte)
	 */
	@Override
	public Message vipGalleryShift(RoleInstance role, byte vipLevel) {
		C2511_VipGalleryShiftRespMessage msg = new C2511_VipGalleryShiftRespMessage();
		String privilegeInfo = buildPrivilegeInfo(vipLevel);
		msg.setVipLevel(vipLevel);
		msg.setPrivilegeInfo(privilegeInfo);
		return msg;
	}
	/**
	 * vipLevelUpReward 
	 */
	public Date getLastReceiveAwardTime(RoleInstance role){
		RoleVip roleVip = getRoleVipNotNull(role);
		Date lastReceiveAwardTime = roleVip.getLastReceiveAwardTime();
		return lastReceiveAwardTime;
	}
	@Override
	public Message vipDailyAwardReceive(RoleInstance role) {
		C2512_VipDailyAwardReceiveRespMessage msg = new C2512_VipDailyAwardReceiveRespMessage();
		RoleVip roleVip = getRoleVipNotNull(role);
		byte type = 0;      //1:success 0:failed
		String info = "";   
		Date dt = getLastReceiveAwardTime(role);
		if(DateUtil.sameDay(new Date(),dt)){
			info = getText(TextId.VIP_Daily_Award_Has_Received);
			msg.setType(type);
			msg.setInfo(info);
			return msg;
		}
		byte roleVipLevel = getRoleVipLevel(role);
		if(roleVipLevel == 0){
			info = getText(TextId.NO_VIP_Award);
			msg.setType(type);
			msg.setInfo(info);
		}
		GoodsLiteItem dailyAwardItem = buildDailyAwardItem(roleVipLevel);
		sendVipAward(role, dailyAwardItem);
		roleVip.setLastReceiveAwardTime(new Date());
		GameContext.getBaseDAO().saveOrUpdate(roleVip);
		type = 1;
		msg.setType(type);
		msg.setInfo(info);
		return msg;
	}
	@Override
	public Message vipLevelUpAwardReceive(RoleInstance role, byte vipLevel) {
		C2513_VipLevelUpAwardReceiveRespMessage msg = new C2513_VipLevelUpAwardReceiveRespMessage();
		RoleVip roleVip = getRoleVipNotNull(role);
		if(roleVip.getVipLevel()==0){
				msg.setType((byte)0);
				msg.setInfo(getText(TextId.NO_VIP_Award));
				return msg;
		}else if(roleVip.getVipLevel()<vipLevel){
			msg.setType((byte)0);
			msg.setInfo(getText(TextId.Vip_LEVEL_TOO_LOW));
			return msg;
		}else if(vipLevel<1){
			msg.setType((byte)0);
			msg.setInfo(getText(TextId.Vip_LEVEL_ERR));
			return msg;
		}
		byte type = 0;      //1:success 0:failed
		String info = "";   
		if(!isLevelUpAwardAvailable(role,vipLevel)){
			info = getText(TextId.VIP_LevelUp_Award_Has_Received);
			msg.setType(type);
			msg.setInfo(info);
			return msg;
		}else{
			GoodsLiteItem awardItem = buildVipLevelUpAwardItem(vipLevel);
			sendVipAward(role, awardItem);
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
		// 
		AddGoodsBeanResult goodsResult = GameContext.getUserGoodsApp().addSomeGoodsBeanForBag(role, addList,OutputConsumeType.VIP_output);
		//if bag is full then send mail
		List<GoodsOperateBean> putFailureList = goodsResult.getPutFailureList();
		try {
			if(!Util.isEmpty(putFailureList)){
				String context = getText(TextId.VIP_Mail_Context);
				GameContext.getMailApp().sendMail(role.getRoleId(),
							MailSendRoleType.VIP.getName(), 
							context,
							MailSendRoleType.VIP.getName(), 
							OutputConsumeType.VIP_mail_output
							.getType(),
							putFailureList);
			}
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	}
	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}
	
	@Override
	public byte getVipLevel(String roleId) {
		RoleVip roleVip = roleVipMap.get(roleId);
		return roleVip==null?0:roleVip.getVipLevel();
	}
	public byte getVipLevel(RoleInstance role) {
		String roleId = role.getRoleId();
		return getVipLevel(roleId);
	}
	private void addRoleVipMap(RoleVip roleVip){
		if(roleVip==null)
			return;
		String key = roleVip.getRoleId()+"";
		roleVipMap.put(key, roleVip);
	}
	private RoleVip getRoleVip(RoleInstance role) {
		String key = role.getRoleId();
		return getRoleVip(key);
	}
	private <T> RoleVip getRoleVip(T roleId) {
		String key = roleId+"";
		return roleVipMap.get(key);
	}
	private VipLevelUpResult levelUp(int roleId, int vipExp) {
		RoleVip roleVip = null;
		//online
		if(GameContext.getOnlineCenter().isOnlineByRoleId(roleId+"")){
			roleVip = getRoleVip(roleId);
		}else{
			roleVip = GameContext.getBaseDAO().selectEntity(RoleVip.class, RoleVip.ROLE_ID, roleId);
		}
		roleVip = ensureRoleVipNotNull(roleId, roleVip);
		return levelUp(roleVip, vipExp);
	}
	private VipLevelUpResult levelUp(RoleVip roleVip, int vipExp){
		VipLevelUpResult vipLevelUpResult = new VipLevelUpResult();
		byte curVipLevel = roleVip.getVipLevel();
		//if role is full-level, return;
		if(curVipLevel>=vipConfProvider.getMaxVipLevel()){
			return vipLevelUpResult;
		}
		int curExp = roleVip.getVipExp();
		int sumExp = curExp + vipExp;
		byte newVipLevel = getVipLevelByVipExp(sumExp);
		roleVip.setVipExp(sumExp);
		if(newVipLevel>curVipLevel){
			roleVip.setVipLevel(newVipLevel);
			vipLevelUpResult.setNewVipLevel(newVipLevel);
			vipLevelUpResult.setOldVipLevel(curVipLevel);
			vipLevelUpResult.setVipLevelUp(true);
			vipLevelUpResult.setResult((byte)1);
			vipLevelUpResult.setRoleVip(roleVip);
		}
		
		try{
			//if online
			if(GameContext.getOnlineCenter().isOnlineByRoleId(roleVip.getRoleId()+""))
				addRoleVipMap(roleVip);//roleVipApp
			//db
			GameContext.getBaseDAO().saveOrUpdate(roleVip);
			//online notify
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
}
