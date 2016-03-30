package com.game.draco.app.giftcode;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import platform.message.request.C5500_VertifyCodeReqMessage;
import platform.message.response.C5500_VertifyCodeRespMessage;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.giftcode.config.GiftCodeConfig;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.google.common.collect.Maps;


public class GiftCodeAppImpl implements GiftCodeApp{
	
	private Map<String, GiftCodeConfig> giftCodeMap = Maps.newLinkedHashMap();
	private final int cdTime = 10 * 1000;//领取CD 
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final int GIFT_CODE_ACTIVE_ID_LEN = 4 ;
	
	@Override
	public void start() {
		this.loadGiftCode();
	}
	
	private Result loadGiftCode(){
		String fileName = "" ;
		String sheetName = "" ;
		Result result = new Result();
		String errorInfo = "" ;
		try{
			String path = GameContext.getPathConfig().getXlsPath();
			fileName = path + XlsSheetNameType.gift_code.getXlsName();
			sheetName = XlsSheetNameType.gift_code.getSheetName();
			Map<String,GiftCodeConfig> map  = XlsPojoUtil.sheetToLinkedMap(fileName, sheetName, GiftCodeConfig.class);
			StringBuffer buffer = new StringBuffer("");
			boolean initOk = true ;
			if(!Util.isEmpty(map)){
				for(GiftCodeConfig config : map.values()){
					String str = config.init() ;
					if(!Util.isEmpty(str)){
						initOk = false ;
						buffer.append(str).append(" ");
					}
				}
			}
			if(initOk){
				this.giftCodeMap = map ;
				result.success();
				return result ;
			}
			
		}catch(Exception e){
			Log4jManager.CHECK.error("load gift Code error,fileName=" + fileName + " sheetName=" + sheetName,e);
		}
		
		Log4jManager.CHECK.error("load gift Code error,fileName=" + fileName + " sheetName=" + sheetName + " info=" + errorInfo);
		Log4jManager.checkFail() ;
		
		result.setInfo(errorInfo);
		return result ;
	}
	

	
	@Override
	public Collection<GiftCodeConfig> getAllGiftCodeConfig(){
		if(null == this.giftCodeMap){
			return null ;
		}
		return this.giftCodeMap.values() ;
	}

	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void stop() {
		
	}
	
	private int getActiveId(String codeNumber){
		codeNumber = codeNumber.trim();
		int appId = GameContext.getAppId();
		int appIdLen = String.valueOf(appId).length() ;
		if(codeNumber.length() <= (appIdLen + GIFT_CODE_ACTIVE_ID_LEN)){
			return 0 ;
		}
		String activeIdStr = codeNumber.substring(
					appIdLen, appIdLen + GIFT_CODE_ACTIVE_ID_LEN);
		if(!Util.isNumeric(activeIdStr)){
			return 0 ;
		}
		return Integer.parseInt(activeIdStr);
	}
	
	
	private GiftCodeConfig getGiftCodeConfig(String activeId){
		if(null == giftCodeMap){
			return null ;
		}
		return giftCodeMap.get(activeId);
	}

	@Override
	public Result takeCdkey(RoleInstance role,String codeNumber) {
		Result result = new Result();
		if(!this.vertifyRoleCd(role)){
			return result.setInfo(Status.Sys_Operate_Frequently.getTips());
		}
		if(Util.isEmpty(codeNumber)){
			return result.setInfo(Status.Sys_Input_Act_Code.getTips());
		}
		//遍历系统当前相关活动是否有效
		int activeId = this.getActiveId(codeNumber);
		if(activeId <=0){
			return result.setInfo(Status.Sys_Input_Act_Code.getTips());
		}
		GiftCodeConfig config = this.getGiftCodeConfig(String.valueOf(activeId));
		if(null == config || !config.nowOpen(role.getChannelId(),GameContext.getServerId())){
			//活动已经结束
			return result.setInfo(Status.Sys_Sorry_Active_End.getTips());
		}
		C5500_VertifyCodeReqMessage vertifyReq = new C5500_VertifyCodeReqMessage();
		vertifyReq.setCode(codeNumber);
		vertifyReq.setAppId(GameContext.getAppId());
		vertifyReq.setUserId(role.getUserId());
		vertifyReq.setUserName(role.getUserName());
		vertifyReq.setRoleId(role.getRoleId());
		vertifyReq.setRoleName(role.getRoleName());
		vertifyReq.setChannelId(role.getChannelId());
		vertifyReq.setActiveId((short)activeId);
		vertifyReq.setServerId(GameContext.getServerId());
		vertifyReq.setChannelUserId(role.getChannelUserId());
		//vertifyReq.setChannelUserName("");//TODO:
		try {
			//获取激活码验证的HTTP地址
			String url = GameContext.getPlatformConfig().getActiveCodeHttpUrl();
			C5500_VertifyCodeRespMessage resp = (C5500_VertifyCodeRespMessage) GameContext.getHttpJsonClient().sendMessage(vertifyReq, url);
			byte status = resp.getStatus();
			String info = resp.getInfo();
			if(status != RespTypeStatus.SUCCESS){
				return result.setInfo(info);
			}
			int rewardGoodsId = config.getRewardGoodsId();
			int silverMoney = config.getSilverMoney();
			int goldMoney = config.getGoldMoney();
			int bindingGoldMoney = config.getBindingGoldMoney();
			String content = config.getContext();
			
			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
			mail.setTitle(Status.Sys_Act_Code_Center.getTips());
			mail.setSendRole(MailSendRoleType.Active_Code.getName());
			mail.setSendSource(OutputConsumeType.active_code_award.getType());
			mail.setRoleId(role.getRoleId());
			mail.setBindGold(bindingGoldMoney);
			mail.setContent(content);
			mail.setSilverMoney(silverMoney);
			mail.setGold(goldMoney);
			mail.setBindGold(bindingGoldMoney);
			if(rewardGoodsId > 0){
				mail.addMailAccessory(rewardGoodsId, 1, BindingType.already_binding);
			}
			GameContext.getMailApp().sendMail(mail);
			result.setInfo(Status.Sys_Act_Code_Success_Mail_Reward.getTips());
			return result.success();
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".takeCdkey error: ", e);
			return result.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
		}
	}
	
	private boolean vertifyRoleCd(RoleInstance role){
		long activationDateTime = role.getActivationDateTime();
		if(activationDateTime <= 0){
			role.setActivationDateTime(System.currentTimeMillis());
			return true;
		}
		if((System.currentTimeMillis() - activationDateTime) > this.cdTime){
			role.setActivationDateTime(System.currentTimeMillis());
			return true;
		}
		return false;
	}
	
}
