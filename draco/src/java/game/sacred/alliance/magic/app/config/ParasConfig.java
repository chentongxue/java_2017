package sacred.alliance.magic.app.config;

import java.util.HashSet;
import java.util.Set;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.Util;

public class ParasConfig extends PropertiesConfig {
	
	private Set<String> logMessagePrintRoleIdSet = new HashSet<String>();//消息日志强制输出的角色ID集合
	
	/**
	 * 获取消息日志强制输出的角色ID集合
	 * @return
	 */
	public Set<String> getLogMessagePrintRoleIdSet(){
		return this.logMessagePrintRoleIdSet;
	}
	
	private String getLogMessagePrintRoleIds(){
		return this.getConfig("logMessagePrintRoleIds");
	}
	
	private Set<String> reloadLogMessageRoleIdSet(){
		Set<String> set = new HashSet<String>();
		String roleIds = this.getLogMessagePrintRoleIds();
		if(Util.isEmpty(roleIds)){
			return set;
		}
		for(String id : roleIds.split(Cat.comma)){
			set.add(id);
		}
		return set;
	}
	
	@Override
	public void reLoad() throws Exception {
		super.reLoad();
		this.logMessagePrintRoleIdSet = this.reloadLogMessageRoleIdSet();
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	
	public int getMaxRoleNum(){
		return Integer.valueOf(getConfig("maxRoleNum"));
	}
	
	/*public int getMaxRoleName() {
		return Integer.parseInt(getConfig("maxRoleName"));
	}*/

	public int getMinRoleName() {
		return Integer.parseInt(getConfig("minRoleName"));
	}

	
	public int getWorldMapGoodsId(){
		return Integer.parseInt(getConfig("worldMapGoodsId"));
	}
	
	public int getWorldMapGoldCost(){
		return Integer.parseInt(getConfig("worldMapGoldCost"));
	}
	
	public int getSearchRoadOutTime(){
		return Integer.parseInt(getConfig("searchRoadOutTime"));
	}
	
	public String getUserStateCommandId() {
		return getConfig("userStateCommandId");
	}
	public String getForbidCommandId() {
		return getConfig("forbidCommandId");
	}
	
	/**
	 * 副本存活时间
	 * @return
	 */
	public long getCopyLifeCycle() {
		return Long.parseLong(getConfig("copyLifeCycle"));
	}
	
	/**
	 * 副本掉线保护时间
	 * @return
	 */
	public long getCopyLostReLogin(){
		return Long.valueOf(this.getConfig("copyLostReLogin"));
	}
	
	//是否启用协议加密
	public boolean getEncryptionFlag(){
		return Boolean.parseBoolean(getConfig("encryptionFlag"));
	}
	
	/** 客服求助时间间隔（秒） */
	public int getKefuHelpGapTime(){
		return Integer.valueOf(getConfig("kefuHelpGapTime"));
	}
	
	
	/** 需要关闭的任务 */
	public String getClosedQuestIds(){
		return this.getConfig("closedQuestIds");
	}
	

	public int getCopyTeamMatchSecond(){
		return Integer.valueOf(this.getConfig("copyTeamMatchSecond"));
	}
	
	public int getSurvivalSecond(){
		return Integer.valueOf(this.getConfig("survivalMatchSecond"));
	}
	
	/**
	 * 聊天消息长度
	 * @return
	 */
	public int getChatMessageSize(){
		return Integer.valueOf(this.getConfig("chatMessageSize"));
	}
	
	/**
	 * pve地图全屏转发位置消息默认人数
	 * @return
	 */
	public int getBroadcastAllMax(){
		return Integer.valueOf(this.getConfig("broadcastAllMax"));
	}
	
	/**
	 * 是否开放语音聊天
	 * @return
	 */
	public boolean isOpenChatVoice(){
		String value = this.getConfig("openChatVoice");
		if(Util.isEmpty(value)){
			return false ;
		}
		value = value.trim();
		return "1".equals(value) || "true".equals(value);
	}


	/**
	 * 神仙福地开宝箱位置偏移量
	 * @return
	 */
	public int getChestDisOffset(){
		return Integer.valueOf(this.getConfig("chestDisOffset"));
	}
	
	/**
	 * 神仙福地开宝箱时间偏移量(毫秒)
	 * @return
	 */
	public int getChestOpenTimeOffset(){
		return Integer.valueOf(this.getConfig("chestOpenTimeOffset"));
	}
	
	public int getChestReadyOpenTimeOffset(){
		return Integer.valueOf(this.getConfig("chestReadyOpenTimeOffset"));
	}
	
	public int getTradingOpenRoleLevel(){
		return Integer.valueOf(this.getConfig("tradingOpenRoleLevel"));
	}
	
	public int getAuctionUpOpenRoleLevel(){
		return Integer.valueOf(this.getConfig("auctionUpOpenRoleLevel"));
	}
	
	public int getAuctionBuyOpenRoleLevel(){
		return Integer.valueOf(this.getConfig("auctionBuyOpenRoleLevel"));
	}
	
	
	public boolean isOpenDoorDog(){
		String value = this.getConfig("openDoorDog");
		if(Util.isEmpty(value)){
			return false ;
		}
		value = value.trim();
		return "1".equals(value) || "true".equals(value);
	}
	
	public int sameIpCanLoginUserCount(){
		return Integer.valueOf(this.getConfig("sameIpCanLoginUserCount"));
	}
	
	public int maxDoorDogErrorNum(){
		return Integer.valueOf(this.getConfig("maxDoorDogErrorNum"));
	}
	
	public int getDoorDogPanelTimeout(){
		return Integer.valueOf(this.getConfig("doorDogPanelTimeout"));
	}
	
	public int getDoorDogPanelInterval(){
		return Integer.valueOf(this.getConfig("doorDogPanelInterval"));
	}
	
	
	public boolean isCheckQuestDistance(){
		String value = this.getConfig("checkQuestDistance");
		if(Util.isEmpty(value)){
			return false ;
		}
		value = value.trim();
		return "1".equals(value) || "true".equals(value);
	}
	

	public byte getSendMailQualityType(){
		return Byte.parseByte(this.getConfig("sendMailQualityType"));
	}
	
	/**
	 * 是否强制更改重名
	 * @return
	 */
	public boolean isMustChangeSameRoleName(){
		String value = this.getConfig("mustChangeSameRoleName");
		if(Util.isEmpty(value)){
			return false ;
		}
		value = value.trim();
		return "1".equals(value) || "true".equals(value);
	}
	
	/**
	 * 关闭拍卖行钱币上架
	 * @return
	 */
	public String getCloseAuctionMoneyChannels(){
		return this.getConfig("closeAuctionMoneyChannels");
	}
	
	
	/**
	 * 好友邀请功能开启的渠道
	 * @return
	 */
	public String getOpenInviteChannelIds(){
		return this.getConfig("openInviteChannelId");
	}
	
	
	public boolean isOpenCaptchaAtFunc(){
		String value = this.getConfig("openCaptchaAtFunc");
		if(Util.isEmpty(value)){
			return false ;
		}
		value = value.trim();
		return "1".equals(value) || "true".equals(value);
	}

	public boolean isOpenVoice2Text(){
		String value = this.getConfig("openVoice2Text");
		if(Util.isEmpty(value)){
			return false ;
		}
		value = value.trim();
		return "1".equals(value) || "true".equals(value);
	}
	
	
	public int getPassCaptchaAtFuncRoleLevel(){
		return Integer.valueOf(this.getConfig("passCaptchaAtFuncRoleLevel"));
	}
	

	public int getPassCaptchaAtFuncRechargeGoldMoney(){
		return Integer.valueOf(this.getConfig("passCaptchaAtFuncRechargeGoldMoney"));
	}
	
	public String getDoorDogBlackIpFile(){
		return this.getConfig("doorDogBlackIpFile");
	}
	
	
}
