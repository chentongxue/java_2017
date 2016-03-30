package com.game.draco.app.mail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.dao.impl.MailDAOImpl;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.domain.MailAccessory;
import com.game.draco.app.mail.domain.MailAttriBean;
import com.game.draco.app.mail.domain.MailGoods;
import com.game.draco.app.mail.type.MailFreezeType;
import com.game.draco.message.internal.C0084_MailAsyncSendInternalMessage;
import com.game.draco.message.item.MailListItem;
import com.game.draco.message.push.C0007_ConfirmationNotifyMessage;
import com.game.draco.message.push.C1007_MailNoticeNotifyMessage;
import com.game.draco.message.request.C1008_MailTruePickReqMessage;
import com.game.draco.message.response.C1001_MailListRespMessage;
import com.game.draco.message.response.C1004_MailMoreAccessoryRespMessage;
import com.game.draco.message.response.C1006_MailMoreDelRespMessage;

public class MailAppImpl implements MailApp{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private MailDAOImpl mailDAO;
	private Map<String,MailDisplayContextLogic> mailContextLogicMap = new HashMap<String,MailDisplayContextLogic>();
	private final static short TRUE_PICK = new C1008_MailTruePickReqMessage().getCommandId();
	private final static int Out_Date_Days = 30;//邮件过期时间
	
	public void setMailDAO(MailDAOImpl mailDAO) {
		this.mailDAO = mailDAO;
	}

	//得到允许显示的邮件时间
	private Date getAllowFeedDate(){
		Date d = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DAY_OF_MONTH, -Out_Date_Days);
		return cal.getTime();
	}
	
	//得到超时的时间
	private Date getOutTimeDate(){
		Date d = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DAY_OF_MONTH, -Out_Date_Days);
		cal.add(Calendar.MINUTE, 10);
		return cal.getTime();
	}
	
	/**
	 * 获取邮件剩余有效期
	 * @param sendTime 邮件发送时间
	 * @return 分钟
	 */
	private int getRemainMinutes(Date sendTime){
		try{
			Date endTime = DateUtil.addDayToDate(sendTime, Out_Date_Days);
			return (int) DateUtil.dateDiffMinute(endTime, new Date());
		}catch(Exception e) {
			this.logger.error("MailApp.getRemainMinutes error: ", e);
			return 0;
		}
	}
	
	@Override
	public void insertMailAccessory(List<MailAccessory> maList){
		if(null == maList || 0 == maList.size()){
			return ;
		}
		for(MailAccessory ma : maList){
			this.insertMailAccessory(ma);
		}
	}
	
	@Override
	public void insertMailGoods(List<MailGoods> mgList){
		if(null == mgList || 0 == mgList.size()){
			return ;
		}
		for(MailGoods mg : mgList){
			this.insertMailGoods(mg);
		}
	}
	
	@Override
	public void insertMail(Mail mail){
		this.mailDAO.insert(mail);
	}
	
	private void insertMailGoods(MailGoods mg){
		this.mailDAO.insert(mg);
	}
	
	private void insertMailAccessory(MailAccessory ma){
		this.mailDAO.insert(ma);
	}
	
	@Override
	public Mail getMailInfoByDB(String mailId){
		return this.mailDAO.selectEntity(Mail.class, "mailId", mailId);
	}
	
	private List<MailAccessory> getMailAccessoryList(RoleInstance role,String mailId){
		return this.mailDAO.selectList(MailAccessory.class, "roleId", role.getRoleId(),"mailId",mailId);
	}
	
	@Override
	public List<MailAccessory> getMailAccessoryListByDB(String roleId,String mailId){
		return this.mailDAO.selectList(MailAccessory.class, "roleId",roleId,"mailId", mailId);
	}
	
	private List<Mail> getMailListByParam(String roleId, int startIndex, int pageSize){
		return this.mailDAO.selectListByParms(Mail.class, roleId, getAllowFeedDate(), startIndex, pageSize, 0, 0, 0);
	}
	
	private int getMailSum(String roleId){
		return this.mailDAO.count(Mail.class, "roleId",roleId, "sendTime",getAllowFeedDate());
	}
	
	private int getUnreadMailCount(String roleId){
		return this.mailDAO.count(Mail.class, "roleId",roleId, "look",0,"sendTime",getAllowFeedDate());
	}
	
	@Override
	public boolean isExistUnreadMail(String roleId){
		int count = getUnreadMailCount(roleId);
		if(count > 0){
			return true;
		}
		return false;
	}
	
	@Override
	public Status delMoreMail(RoleInstance role,String[] mailIds){
		if(null == mailIds || 0 == mailIds.length){
			return Status.Mail_Not_Mail_Id;
		}
		C1006_MailMoreDelRespMessage resp = new C1006_MailMoreDelRespMessage();
		List<String> successMailIds = new ArrayList<String>();
		Status status = null;
		try{
			for(String mailId : mailIds){
				status = this.delMail(role, mailId);
				if(!status.isSuccess()){
					continue ;
				}
				successMailIds.add(mailId);
			}
		}catch(Exception e){
			
		}
		if(0 == successMailIds.size()){
			return Status.Mail_Del_FAILURE;
		}
		resp.setType(Status.SUCCESS.getInnerCode());
		resp.setInfo(Status.Mail_Del_Success.getTips());
		resp.setSuccessMailIds(listToArray(successMailIds));
		role.getBehavior().sendMessage(resp);
		return Status.SUCCESS;
	}
	
	private String[] listToArray(List<String> list){
		String[] array = new String[list.size()];
		for(int i = 0;i<list.size();i++){
			array[i] = list.get(i);
		}
		return array;
	}
	
	@Override
	public Result pickMoreMailAccessory(RoleInstance role,String[] mailIds){
		Result result = new Result();
		if(null == mailIds || 0 == mailIds.length){
			return result.setInfo(Status.Mail_Not_Mail_Id.getTips());
		}
		List<Mail> mailList = this.mailDAO.getBatchMail(mailIds);
		if(Util.isEmpty(mailList)){
			return result.setInfo(Status.Mail_Not_Mail_Id.getTips());
		}
		if(!this.hasAccessory(mailList)){
			return result.setInfo(Status.Mail_Goods_Taken.getTips());
		}
		List<String> successMailIds = new ArrayList<String>();
		try{
			for(Mail mail : mailList){
				if(null == mail){
					continue;
				}
				Result res = this.pickMailAccessory(role, mail, false);
				if(!res.isSuccess()||res.isIgnore()){//0913 ->if(!res.isSuccess())?
					continue ;
				}
				successMailIds.add(mail.getMailId());
			}
		}catch(Exception e){
			logger.error("pickMoreMailAccessory",e);
		}
		if(0 == successMailIds.size()){
			return result.setInfo(Status.Mail_Pack_Full.getTips());
		}
		C1004_MailMoreAccessoryRespMessage resp = new C1004_MailMoreAccessoryRespMessage();
		resp.setType(Status.SUCCESS.getInnerCode());
		resp.setSuccessMailIds(this.listToArray(successMailIds));
		role.getBehavior().sendMessage(resp);
		return result.setResult(Status.SUCCESS.getInnerCode());
	}
	
	/**
	 * 判断一批邮件中是否存在附件
	 * @param mailList
	 * @return
	 */
	private boolean hasAccessory(List<Mail> mailList){
		for(Mail mail : mailList){
			if(null == mail){
				continue;
			}
			if(mail.isExistGoods() || mail.isExistAttri()){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Status delMail(RoleInstance role,String mailId)throws ServiceException{
		try{
			Status status = this.roleDelMail(mailId, role.getRoleId());
			if(status.isSuccess()){
				//邮件日志
				GameContext.getStatLogApp().mailDelInfoDelLog(mailId, role);
			}
			return status;
		}catch(Exception e){
			throw new ServiceException("del mail error",e);
		}
	}
	
	@Override
	public Result pickMailAccessory(RoleInstance role, String mailId, boolean isPayGold)throws ServiceException{
		Result result = new Result();
		Mail mail = this.getMailInfoByDB(mailId);
		if(null == mail || !mail.getRoleId().equals(role.getRoleId())){
			return result.setInfo(Status.Mail_Not_Exist.getTips());
		}
		return this.pickMailAccessory(role, mail, isPayGold);
	}
	
	/**
	 * 提取附件
	 * @param role
	 * @param mail
	 * @param isPayGold
	 * @return
	 * @throws ServiceException
	 */
	private Result pickMailAccessory(RoleInstance role, Mail mail, boolean isPayGold)throws ServiceException{
		try{
			Result result = new Result();
			if(null == mail || !mail.getRoleId().equals(role.getRoleId())){
				return result.setInfo(Status.Mail_Not_Exist.getTips());
			}
			//被冻结
			if(mail.isFreeze()){
				return result.setInfo(GameContext.getI18n().getText(TextId.Mail_Pick_Freeze));
			}
			if(!mail.isExistGoods() && !mail.isExistAttri()){
				//无附件
				return result.setInfo(Status.Mail_Goods_Taken.getTips());
			}
			String mailId = mail.getMailId();
			if(mail.isPayMail()){
				if(!isPayGold){
					//发送二次确认
					String params = "1" + Cat.comma + mailId;
					String info = Status.Mail_Pick_Pay_Money.getTips().replace(Wildcard.Number, String.valueOf(mail.getPayGold()));
					role.getBehavior().sendMessage(this.pickConfirmationNotifyMessage(TRUE_PICK, params, info));
					result.setIgnore(true);
					return result.setResult(Status.Mail_Consume_Gold.getInnerCode());
				}
				//【游戏币/潜能/钻石不足弹板】 判断
				Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.goldMoney, role.getGoldMoney());
				if(ar.isIgnore()){
					return ar;
				}
				if(!ar.isSuccess()){
					return result.setInfo(Status.Mail_Less_Gold.getTips());
				}

//				if(mail.isPayGoldEnough(role.getGoldMoney())){
//					return result.setInfo(Status.Mail_Less_Gold.getTips());
//				}
			}
			List<MailAccessory> maList = this.getMailAccessoryList(role, mailId);
			//有物品的，提取物品
			if(!Util.isEmpty(maList)){
				List<RoleGoods> addRgList = new ArrayList<RoleGoods>();
				List<GoodsOperateBean> addGoodsBeanList = new ArrayList<GoodsOperateBean>();
				for(MailAccessory ma : maList){
					if(null != ma.getInstanceId()){
						//实例ID的物品
						GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(ma.getTemplateId());
						if(null == gb){
							continue;
						}
						MailGoods mg = this.mailDAO.selectEntity(MailGoods.class, "id", ma.getInstanceId());
						if(null == mg){
							continue;
						}
						mg.setRoleId(role.getRoleId());
						RoleGoods rg = mg.createRoleGoods();
						if(null == rg){
							continue ;
						}
						addRgList.add(rg);
					}else{
						//模版ID的物品
						GoodsOperateBean agb = new GoodsOperateBean();
						agb.setGoodsId(ma.getTemplateId());
						agb.setGoodsNum(ma.getNum());
						agb.setBindType(BindingType.get(ma.getBind()));
						addGoodsBeanList.add(agb);
					}
				}
				if(addGoodsBeanList.size() > 0){
					Result goodsRes = GameContext.getUserGoodsApp().addGoodsBeanForBag(role, addGoodsBeanList, OutputConsumeType.getType(mail.getSendSource()));
					if(!goodsRes.isSuccess()){
						return goodsRes;
					}
				}
				if(addRgList.size() > 0){
					Result addRes = GameContext.getUserGoodsApp().addGoodsForBag(role, addRgList, OutputConsumeType.getType(mail.getSendSource()));
					if(!addRes.isSuccess()){
						return addRes;
					}
				}
			}
			//扣除付费钱数
			this.consume(role, mail.getPayGold());
			mail.setExistGoods(0);//已经提取附件
			mail.setLook(1);//已经读
			//更新附件数据库
			this.delMailInfo(mailId, role.getRoleId());
			//提取金钱和经验
			int exp = mail.getExp();
			int gold = mail.getGold();
			int bindGold = mail.getBindGold();
			int silverMoney = mail.getSilverMoney();
			int potential = mail.getPotential() ;
			int dkp = mail.getDkp();
			if(mail.isExistAttri()){
				//提取金钱、经验
				this.getMailAttri(mail, role);
			}
			//更新数据库
			this.mailDAO.update(mail);
			//提取邮件日志
			GameContext.getStatLogApp().mailSendInfoPickLog(role, mail, maList, exp, gold, bindGold, silverMoney, 0, potential, 0,dkp);
			return result.setResult(Status.SUCCESS.getInnerCode());
		}catch(Exception e){
			throw new ServiceException("",e);
		}
	}
	
	private C0007_ConfirmationNotifyMessage pickConfirmationNotifyMessage(short confirmCmdId,String params ,String info){
		//发送二次确认消息
		C0007_ConfirmationNotifyMessage confirmMsg = new C0007_ConfirmationNotifyMessage();
		confirmMsg.setAffirmCmdId(confirmCmdId);
		confirmMsg.setAffirmParam(params);
		confirmMsg.setCancelCmdId((short)0);
		confirmMsg.setCancelParam("");
		confirmMsg.setInfo(info);
		confirmMsg.setTime((byte)0);
		confirmMsg.setTimeoutCmdId((short)0);
		confirmMsg.setTimeoutParam("");
		return confirmMsg;
	}
	
	//消耗金条
	private void consume(RoleInstance role, int num) {
		if (num <= 0) {
			return;
		}
		GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, 
				OperatorType.Decrease, num, OutputConsumeType.mail_pick_consume);
		role.getBehavior().notifyAttribute();
	}
	
	//提取邮件 的钱或经验
	private Result getMailAttri(Mail mail, RoleInstance role){
		Result result = new Result();
		try{
			OutputConsumeType ocType = OutputConsumeType.getType(mail.getSendSource());
			int exp = mail.getExp();
			int gold = mail.getGold();
			int silverMoney = mail.getSilverMoney();
			int potential = mail.getPotential();
			int dkp = mail.getDkp();
			int honor = mail.getHonor();
			boolean notify = false ;
			if(exp > 0){
				GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.exp, OperatorType.Add, exp, ocType);
				notify = true ;
			}
			if(gold > 0){
				GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, OperatorType.Add, gold, ocType );
				notify = true ;
			}
			if(silverMoney > 0){
				GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, OperatorType.Add, silverMoney, ocType);
				notify = true ;
			}
			if(potential > 0){
				GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.potential, OperatorType.Add, potential, ocType);
				notify = true ;
			}
			if(dkp > 0){
				GameContext.getUserAttributeApp().changeRoleDkp(role, AttributeType.dkp, OperatorType.Add, dkp,ocType);
				notify = true ;
			}
			if (honor > 0) {
				GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.honor, OperatorType.Add, honor, ocType);
			}
			//提取过的金钱和经验，重置为0
			mail.setBindGold(0);
			mail.setGold(0);
			mail.setSilverMoney(0);
			mail.setExp(0);
			mail.setPotential(0);
			mail.setDkp(0);
			mail.setHonor(0);
			if(notify){
				//属性同步
				role.getBehavior().notifyAttribute();
			}
			result.setInfo(Status.Mail_Goods_Success.getTips());
			return result.success();
		}catch(Exception e ){
			this.logger.error("",e);
			return result.setInfo(Status.Mail_FAILURE.getTips());
		}
	}
	
	//删除提取的邮件信息
	private Status delMailInfo(String mailId, String roleId){
		int delNum = this.mailDAO.delete(MailAccessory.class, "mailId", mailId,"roleId", roleId);
		if(delNum > 0){
			this.mailDAO.delete(MailGoods.class, "mailId", mailId,"roleId", roleId);
		}
		return Status.Mail_Del_Success;
	}
	
	/**
	 * 角色请求删除邮件
	 * @param mailId
	 * @param roleId
	 * @return
	 */
	private Status roleDelMail(String mailId, String roleId){
		//int delNum = this.mailDAO.delete(Mail.class, "mailId", mailId,"roleId", roleId);
		//玩家请求删除邮件时，只删除未被冻结且没有附件的邮件。避免误删、错删等还得找回。
		int delNum = this.mailDAO.deleteEmptyMail(roleId, mailId);
		if(delNum == 0){
			return Status.Mail_Delete_Not_Empty_Or_Freeze;
		}
		return this.delMailInfo(mailId, roleId);
	}
	
	@Override
	public Status getMailList(RoleInstance role,int currentPage, int pageSize){
		if(currentPage < 0){
			currentPage = 0;
		}
		C1001_MailListRespMessage resp = new C1001_MailListRespMessage();
		int sumPage = this.getSumPage(role, pageSize);
		if(0 == sumPage){
			resp.setType(Status.SUCCESS.getInnerCode());
			role.getBehavior().sendMessage(resp);
			return Status.SUCCESS;
		}
		//超过最大页数时设置为最大页数（当前页数从0开始）
		if (currentPage >= sumPage) {
			currentPage = sumPage-1;
		}
		int startIndex = currentPage * pageSize;
		List<Mail> mailList = this.getMailListByParam(role.getRoleId(), startIndex, pageSize);
		if(null == mailList || 0 == mailList.size()){
			resp.setType(Status.SUCCESS.getInnerCode());
			role.getBehavior().sendMessage(resp);
			return Status.SUCCESS;
		}
		List<MailListItem> itemList = new ArrayList<MailListItem>();
		for(Mail mail : mailList){
			MailListItem item = new MailListItem();
			item.setMailId(mail.getMailId());
			item.setLook((byte) mail.getLook());
			item.setTitle(mail.getTitleFreeze());
			item.setSendRoleName(mail.getSendRole());
			//String date = DateUtil.getMinFormat(mail.getSendTime());
			//item.setSendTime(date.substring(5,11));
			item.setSendTime(DateUtil.date2FormatDate(mail.getSendTime(), "MM-dd"));
			if(mail.isExistAttri() || mail.isExistGoods()){
				if(mail.isPayMail()){
					item.setAccessory((byte)2);
				}else{
					item.setAccessory((byte)1);
				}
			}
			item.setRemainTime(this.getRemainMinutes(mail.getSendTime()));
			itemList.add(item);
		}
		resp.setSumPage((short) sumPage);
		resp.setCurrentPage((short) currentPage);
		resp.setListItem(itemList);
		resp.setType(Status.SUCCESS.getInnerCode());
		role.getBehavior().sendMessage(resp);
		return Status.SUCCESS;
	}
	
	/** *得到总页数**** */
	private int getSumPage(RoleInstance role, int pageSize) {
		int totalSize = this.getMailSum(role.getRoleId());
		if (totalSize <= pageSize) {
			return 1;
		}
		int mod = totalSize % pageSize;
		int sumPage = mod == 0 ? totalSize / pageSize : totalSize / pageSize + 1;
		return sumPage;
	}
	
	@Override
	public void clearOutTimeMail(){
		Date delDate = this.getOutTimeDate();
		this.mailDAO.delete(Mail.class, "sendTime", delDate);
		this.mailDAO.delete(MailAccessory.class, "sendTime", delDate);
		this.mailDAO.delete(MailGoods.class, "sendTime", delDate);
		
	}
	
	@Override
	public void addMailDisplayConextLogic(MailDisplayContextLogic logic) {
		if(null == logic){
			return ;
		}
		OutputConsumeType source = logic.getMailSource();
		if(null == source){
			return ;
		}
		String key = String.valueOf(source.getType());
		if(this.mailContextLogicMap.containsKey(key)){
			Log4jManager.CHECK.error("the mailContext Logic id=" + key + " is exist now ");
			Log4jManager.checkFail();
		}
		this.mailContextLogicMap.put(key, logic);
	}
	
	@Override
	public String getDisplayContext(Mail mail, RoleInstance role) {
		if(null == mail){
			return "" ;
		}
		try {
			MailDisplayContextLogic logic = this.mailContextLogicMap.get(String
					.valueOf(mail.getSendSource()));
			if (null == logic) {
				return mail.getContent();
			}
			return logic.getDisplayContext(mail, role);
		}catch(Exception ex){
			logger.error("",ex);
		}
		return mail.getContent();
	}
	
	@Override
	public Result pickTrueMailAccessory(RoleInstance role, String mailId)throws ServiceException{
		try{
			Result result = new Result();
			if(mailId.indexOf(Cat.comma) == -1){
				return result.setResult(Status.Mail_Params_Err.getInnerCode());
			}
			mailId = mailId.split(Cat.comma)[1];
			return this.pickMailAccessory(role, mailId, true);
		}catch(Exception e){
			throw new ServiceException("",e);
		}
	}

	@Override
	public int getUnreadMailNumber(RoleInstance role) {
		try{
			return this.mailDAO.getUnreadMailNum(role.getRoleId());
		}catch(Exception e){
			this.logger.error("MailApp.getUnreadMailNumber error: ", e);
			return 0;
		}
	}

	@Override
	public Result modifyMailFreeze(String roleId, String mailId, MailFreezeType freezeType) {
		Result result = new Result();
		try {
			if(Util.isEmpty(roleId) || Util.isEmpty(mailId) || null == freezeType){
				return result.setInfo(GameContext.getI18n().getText(TextId.Mail_Freeze_Param_Error));
			}
			int res = this.mailDAO.modifyMailFreeze(roleId, mailId, freezeType.getType());
			if(res > 0){
				return result.success();
			}
			return result.setInfo(GameContext.getI18n().getText(TextId.Mail_Freeze_Fail));
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".modifyMailFreeze error: ", e);
			return result.setInfo(GameContext.getI18n().getText(TextId.Mail_Freeze_Param_Error));
		}
	}

	@Override
	public List<Mail> selectMailListForDebug(String roleId) {
		return this.mailDAO.selectList(Mail.class, "roleId", roleId, "sendTime", this.getAllowFeedDate());
	}



	public Status sendMail(Mail mail) throws ServiceException{
		try{
			if(null == mail){
				return Status.Mail_Send_Failure;
			}
			Status status = mail.save();
			if(!status.isSuccess()){
				return status;
			}
		}catch(Exception e){
			throw new ServiceException("sendMail error",e);
		}
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(mail.getRoleId());
		//邮件日志
		GameContext.getStatLogApp().mailSendInfoLog(mail, role);
		if(null == role){
			return Status.Mail_Send_Success;
		}
		if(role.isLoginCompleted()){
			//!!!!!!!!!! 登录完成后才发送，否则旧版本的客户端未处理，会导致客户端崩溃
			role.getBehavior().sendMessage(new C1007_MailNoticeNotifyMessage());
		}
		return Status.Mail_Send_Success;
	}
	
	@Override
	public void sendMail(String toRoleId, String title, String content, String sendRole,
			int sendSource, List<GoodsOperateBean> goodsList, MailAttriBean attriBean){
		this.generalSendMail(toRoleId, title, content, sendRole, sendSource, goodsList, null,attriBean, false);
	}
	
	@Override
	public void sendMail(String toRoleId, String title, String content, 
			String sendRole, int sendSource, int goldMoney,
			int silverMoney, List<GoodsOperateBean> goodsList) {
		MailAttriBean bean = null ;
		if(goldMoney != 0  || silverMoney != 0){
			bean = new MailAttriBean();
			bean.setGold(goldMoney);
			bean.setSilverMoney(silverMoney);
		}
		this.sendMail(toRoleId, title, content, sendRole, sendSource, goodsList, bean);
	}
	
	@Override
	public void sendMail(String toRoleId,String title,String content,
			String sendRole, int sendSource, List<GoodsOperateBean> goodsList) {
		 this.sendMail(toRoleId, title, content, sendRole, sendSource,goodsList,null); 
	}
	
	/**
	 * 同步、异步发邮件
	 */
	private void generalSendMail(String toRoleId, String title, String content, 
			String sendRole,int sendSource, 
			List<GoodsOperateBean> goodsList, 
			List<RoleGoods> goodsInstanceList ,
			MailAttriBean attriBean, boolean isAsync){
		try {
			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
			mail.setTitle(title);
			mail.setSendRole(sendRole);
			mail.setContent(content);
			mail.setRoleId(toRoleId);
			mail.setSendSource(sendSource);
			if(null != attriBean){
				mail.setGold(attriBean.getGold());
				mail.setSilverMoney(attriBean.getSilverMoney());
				//mail.setBindGold(attriBean.getBindGold());
				mail.setPotential(attriBean.getPotential());
				mail.setPayGold(attriBean.getPayGold());
				mail.setDkp(attriBean.getDkp());
				mail.setHonor(attriBean.getHonor());
			}
			if (Util.isEmpty(goodsList) && Util.isEmpty(goodsInstanceList)) {
				this.generalSendMail(mail, isAsync);
				return;
			}
			int affixNum = 0;
			if(!Util.isEmpty(goodsList)){
				for (GoodsOperateBean bean : goodsList) {
					if (affixNum >= Mail.MaxAccessoryNum) {
						this.generalSendMail(mail, isAsync);// 发送邮件
						mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
						mail.setTitle(title);
						mail.setSendRole(sendRole);
						mail.setContent(content);
						mail.setRoleId(toRoleId);
						mail.setSendSource(sendSource);
						affixNum = 0;// 重置附件物品数量
					}
					affixNum++;// 累计附件物品
					mail.addMailAccessory(bean.getGoodsId(), bean.getGoodsNum(),
							bean.getBindType());
				}
			}
			if(!Util.isEmpty(goodsInstanceList)){
				for(RoleGoods rg : goodsInstanceList){
					if (affixNum >= Mail.MaxAccessoryNum) {
						this.generalSendMail(mail, isAsync);// 发送邮件
						mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
						mail.setTitle(title);
						mail.setSendRole(sendRole);
						mail.setContent(content);
						mail.setRoleId(toRoleId);
						mail.setSendSource(sendSource);
						affixNum = 0;// 重置附件物品数量
					}
					affixNum++;// 累计附件物品
					mail.addRoleGoods(rg);
				}
			}
			if (affixNum > 0) {// 发送附件不满的邮件
				this.generalSendMail(mail, isAsync);
			}
		} catch (Exception ex) {
			logger.error(this.getClass().getName() + ".sendMail error", ex);
		}
	}
	
	/**
	 * 通用发邮件
	 * @param mail 邮件
	 * @param isAsync 是否是异步发邮件
	 * @throws ServiceException
	 */
	private void generalSendMail(Mail mail, boolean isAsync) throws ServiceException {
		if(isAsync){
			this.sendMailAsync(mail);
		}else{
			this.sendMail(mail);
		}
	}
	
	@Override
	public void sendMailAsync(Mail mail) {
		try {
			if(null == mail){
				return;
			}
			C0084_MailAsyncSendInternalMessage message = new C0084_MailAsyncSendInternalMessage();
			message.setMail(mail);
			GameContext.getUserSocketChannelEventPublisher().publish(
					"", message, GameContext.emptyChannelSession, true);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".sendMailAsync error: ", e);
		}
	}

	@Override
	public void sendMailAsync(String toRoleId, String title, String content,
			String sendRole, int sendSource, int goldMoney,int silverMoney,int exp, List<GoodsOperateBean> goodsList) {
		MailAttriBean bean = null ;
		if(goldMoney != 0 || silverMoney != 0 || exp != 0){
			bean = new MailAttriBean();
			bean.setGold(goldMoney);
			bean.setSilverMoney(silverMoney);
			bean.setExp(exp);
		}
		this.sendMailAsync(toRoleId, title, content, sendRole, sendSource, goodsList, bean);
	}

	@Override
	public void sendMailAsync(String toRoleId, String title, String content,
			String sendRole, int sendSource, List<GoodsOperateBean> goodsList,
			List<RoleGoods> goodsInstanceList) {
		this.generalSendMail(toRoleId, title, content, sendRole, sendSource, goodsList,goodsInstanceList, null, true);
	}
	
	public void sendMailAsync(String toRoleId,String title,String content,
			String sendRole, int sendSource, List<GoodsOperateBean> goodsList){
		this.generalSendMail(toRoleId, title, content, sendRole, sendSource, goodsList,null, null, true);
	}

	@Override
	public void sendMailAsync(String toRoleId, String title, String content, String sendRole, 
			int sendSource, List<GoodsOperateBean> goodsList, MailAttriBean attriBean) {
		this.generalSendMail(toRoleId, title, content, sendRole, sendSource, goodsList,null, attriBean, true);
	}

}
