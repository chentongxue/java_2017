package sacred.alliance.magic.app.recall;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.message.internal.C0080_RecallSendAwardReqMessage;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

public class RecallAppImpl implements RecallApp, Service {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private final static String CAT_LOG = "#";
	public final static String LOG_REWARD_SUCESS = "sucess";
	public final static String LOG_REWARD_FAIL = "fail";
	private List<RecallAward> allAwardList = null;
	@Override
	public void setArgs(Object arg0) {

	}

	@Override
	public void start() {
		this.loadAwardCfg();
	}

	@Override
	public void stop() {

	}
	
	private void loadAwardCfg(){
		String fileName = XlsSheetNameType.recall_award_config.getXlsName();
		String sheetName = XlsSheetNameType.recall_award_config.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allAwardList = XlsPojoUtil.sheetToList(sourceFile, sheetName, RecallAward.class);
		}catch (Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		
		if(Util.isEmpty(allAwardList)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
		}
		
		for(RecallAward awardConfig : allAwardList){
			if(null == awardConfig){
				continue;
			}
			awardConfig.init(fileName, sheetName);
		}
	}

	@Override
	public void sendRecallAward(int roleId, long lastLoginTime) {
		if(Util.isEmpty(allAwardList)){
			return;
		}
		Date now = new Date();
		String nowStr = DateUtil.date2Str(now, DateUtil.format_yyyy_MM_dd);
		Date lastLoginDate = new Date(lastLoginTime);
		String lastLoginDateStr = DateUtil.date2Str(lastLoginDate, DateUtil.format_yyyy_MM_dd);
		int offlineDay = DateUtil.dateDiffDay(now, lastLoginDate);
		for(RecallAward awardConfig : allAwardList){
			if(null == awardConfig){
				continue;
			}
			//过期
			if(!DateUtil.dateInRegion(now, awardConfig.getStartDate(), awardConfig.getEndDate())){
				continue;
			}
			if(offlineDay < awardConfig.getMinDay() || offlineDay > awardConfig.getMaxDay()){
				continue;
			}
			this.sendAwardByMail(roleId, lastLoginDateStr, nowStr, offlineDay, awardConfig);
		}
	}
	
	
	private void sendAwardByMail(int roleId, String lastLoginDate, String now, int offlineDay, RecallAward awardConfig){
		try{
			String content = awardConfig.getMailDesc();
			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
			mail.setTitle(awardConfig.getMailTitle());
			mail.setSendRole(awardConfig.getMailSender());
			mail.setRoleId(String.valueOf(roleId));
			mail.setSendSource(OutputConsumeType.recall_award_mail.getType());
			mail.setContent(content);
			GoodsOperateBean awardGoods = awardConfig.getAwardGoods();
			mail.addMailAccessory(awardGoods.getGoodsId(), awardGoods.getGoodsNum(), awardGoods.getBindType());
			GameContext.getMailApp().sendMail(mail);//发送邮件
			Log4jManager.RECALL_AWARD_LOG.info(LOG_REWARD_SUCESS + CAT_LOG + roleId
					+ CAT_LOG + lastLoginDate + CAT_LOG + now
					+ CAT_LOG + offlineDay + CAT_LOG + awardConfig.getId()
					+ CAT_LOG + awardGoods.getGoodsId() + CAT_LOG + awardGoods.getGoodsNum());
			
		}catch (Exception e){
			this.logger.error("recallApp sendAwardByMail error:", e);
			Log4jManager.RECALL_AWARD_LOG.info(LOG_REWARD_FAIL + CAT_LOG + roleId
					+ CAT_LOG + awardConfig.getId());
		}
		
	}

	@Override
	public void sendRecallAwardMsg(RoleInstance role, Date lastLoginTime) {
		C0080_RecallSendAwardReqMessage message = new C0080_RecallSendAwardReqMessage();
		message.setRoleId(role.getIntRoleId());
		message.setLastLoginTime(lastLoginTime.getTime());
		role.getBehavior().addEvent(message);
	}
	

}
