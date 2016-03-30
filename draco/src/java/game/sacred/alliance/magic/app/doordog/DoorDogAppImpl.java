package sacred.alliance.magic.app.doordog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import platform.message.request.C6001_CaptchaGetReqMessage;
import platform.message.response.C6001_CaptchaGetRespMessage;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.RolePayRecord;
import sacred.alliance.magic.util.FileUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C0108_ForcedExitMessage;
import com.game.draco.message.response.C0013_DoorDogNewPanelNotifyMessage;
import com.google.common.collect.Maps;

public class DoorDogAppImpl implements DoorDogApp,Service {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 每个ip当前登录用户数
	 */
	private ConcurrentHashMap<String,AtomicInteger> ipCounts = new ConcurrentHashMap<String, AtomicInteger>();
	private Map<String,RoleDoorDogInfo> roleDoorDogMap = Maps.newConcurrentMap() ;
	
	private Set<String> blackIpSet = new HashSet<String>();
	
	private boolean isBlackIp(String ip){
		if(Util.isEmpty(ip)){
			return false ;
		}
		if(Util.isEmpty(blackIpSet)){
			return false ;
		}
		return blackIpSet.contains(ip);
	}
	
	private String getHostName(RoleInstance role){
		String ipInfo = role.getLoginIp() ;
		if(Util.isEmpty(ipInfo)){
			return "";
		}
		String[] arr = ipInfo.split(Cat.colon);
		if(null == arr || 0 == arr.length){
			return "";
		}
		return arr[0] ;
	}

	/**
	 * 用户登录的时候,将当前ip计数+1
	 * 判断当前ip是否需要回答问题
	 * 如果不要则直接返回null
	 * 否则构建相关问题，将问题回复给客户端
	 */
	@Override
	public void roleLogin(RoleInstance role) {
		try {
			if (!GameContext.getParasConfig().isOpenDoorDog()) {
				// 未开启
				return;
			}
			String hostName = this.getHostName(role);
			if (Util.isEmpty(hostName)) {
				// 没有获得ip不验证
				return;
			}
			AtomicInteger count = ipCounts.get(hostName);
			if (null == count) {
				count = new AtomicInteger(1);
				ipCounts.put(hostName, count);
			} else {
				// 计数+1
				count.incrementAndGet();
			}
			if (count.get() <= GameContext.getParasConfig()
					.sameIpCanLoginUserCount()) {
				return;
			}
			// 将角色设置为未通过验证
			RoleDoorDogInfo info = new RoleDoorDogInfo();
			info.setPassDoorDog(false);
			info.setDoorDogCount((byte)0);
			info.setPassDoorDogTimes((byte)0);
			this.roleDoorDogMap.put(role.getRoleId(), info);
		}catch(Exception ex){
			logger.error("door dog role login error",ex);
		}
	}
	
	@Override
	public RoleDoorDogInfo getRoleDoorDogInfo(String roleId){
		RoleDoorDogInfo info = this.roleDoorDogMap.get(roleId);
		if(null != info){
			return info;
		}
		info = new RoleDoorDogInfo();
		this.roleDoorDogMap.put(roleId, info);
		return info ;
	}
	
	private Question genQuestion(byte type, RoleInstance role){
		RoleDoorDogInfo info = this.getRoleDoorDogInfo(role.getRoleId());
		if(null != info.getDoorDogQuestion()){
			return info.getDoorDogQuestion();
		}
		//根据角色id判断是否需要发发终极验证码
		byte subType = this.getQuestionSubType(role);
		C6001_CaptchaGetReqMessage reqMsg = new C6001_CaptchaGetReqMessage();
		reqMsg.setType(type);
		reqMsg.setSubType(subType);
		try {
			C6001_CaptchaGetRespMessage respMsg = (C6001_CaptchaGetRespMessage)GameContext.getHttpJsonClient().sendMessage(reqMsg,
					GameContext.getPlatformConfig().getDoorDogAddr());
			if (null == respMsg || Result.FAIL == respMsg.getResult()) {
				//失败本次验证跳过
				return null;
			}
			Question question = new Question();
			question.setType(respMsg.getType());
			question.setSubType(respMsg.getSubType());
			List<String> descList = new ArrayList<String>();
			descList.add(0, respMsg.getDescOne());
			String descTwo = respMsg.getDescTwo();
			if(descTwo != null){
				descList.add(1, descTwo);
			}
			question.setDesc(descList);
			List<byte[]> contentList = new ArrayList<byte[]>();
			contentList.add(0, respMsg.getQuestionOne());
			byte[] contentTwo = respMsg.getQuestionTwo();
			if(null != contentTwo){
				contentList.add(1, contentTwo);
			}
			question.setContentList(contentList);
			question.setFormat(respMsg.getFormat());
			question.setAnswer(respMsg.getAnswer());
			if(subType == Question.SUBTYPE_INTELLIGENCE){
				info.setDoorDogQuestion(question);
			}
			return question;
		} catch (Exception e) {
			logger.error("gen doorDog question error", e);
			return null;
		}
	}
	
	/**
	 * 通过分析用户的行为返回验证码类型
	 * @param role
	 * @return
	 */
	private byte getQuestionSubType(RoleInstance role){
		String ip = this.getHostName(role);
		if(!this.isBlackIp(ip)){
			return Question.SUBTYPE_IMAGE_QUESTION ;
		}
		if(this.isWhiteRole(role)){
			return Question.SUBTYPE_IMAGE_QUESTION ;
		}
		return Question.SUBTYPE_INTELLIGENCE;
	}
	
	
	@Override
	public boolean isWhiteRole(RoleInstance role) {
		// 判断角色等级
		int mustRolelevel = GameContext.getParasConfig()
				.getPassCaptchaAtFuncRoleLevel();
		if (mustRolelevel > 0 && role.getLevel() < mustRolelevel) {
			return false;
		}
		int mustPayGold = GameContext.getParasConfig()
				.getPassCaptchaAtFuncRechargeGoldMoney();
		int payGoldMoney = 0;
		RolePayRecord userPay = role.getRolePayRecord();
		if (null != userPay) {
			payGoldMoney = userPay.getPayGold();
		}
		if (mustPayGold > 0 && payGoldMoney < mustPayGold) {
			return false;
		}
		return true;
	}
	

	@Override
	public void setArgs(Object arg0) {
		
	}
	
	@Override
	public Result reloadBlackIp(){
		String fileName = GameContext.getParasConfig().getDoorDogBlackIpFile();
		File file = new File(fileName);
		if(file.exists() && file.isFile()){
			this.blackIpSet = FileUtil.readFileToSet(fileName);
		}else{
			this.blackIpSet = new HashSet<String>();
		}
		return new Result().success();
	}

	@Override
	public void start() {
		this.reloadBlackIp();
	}

	@Override
	public void stop() {
	}

	@Override
	public boolean flagToVerify(RoleInstance role, byte type) {
		RoleDoorDogInfo info = this.getRoleDoorDogInfo(role.getRoleId());
		if(!info.isPassDoorDog()){
			return true;
		}
		Question q = this.genQuestion(type, role);
		if(null == q){
			return false ;
		}
		info.setPassDoorDog(false);
		info.setDoorDogCount((byte)0);
		info.setPassDoorDogTimes((byte)0);
		this.sendVerifyMessage(role,q);
		return true ;
	}
	
	@Override
	public void heartbeat(RoleInstance role) {
		try {
			if (null == role || !GameContext.getParasConfig().isOpenDoorDog()) {
				// 通过或者没开启
				return;
			}
			RoleDoorDogInfo info = this.getRoleDoorDogInfo(role.getRoleId());
			if(info.isPassDoorDog()){
				return ;
			}
			// 没有通过验证
			long now = System.currentTimeMillis();
			if ((now - info.getLastGenQuestionTime()) < GameContext
					.getParasConfig().getDoorDogPanelInterval() * 1000) {
				return;
			}
			if (null != info.getDoorDogAnswer() 
					&& this.isIncrFailNum(role)) {
				// 没有输入
				this.incrFailNum(role);
				this.restRoleQuestion(role);
			}
			if (this.closeNetLink(role)) {
				// 到达最大次数关闭连接
				return;
			}
			// 重新生成验证码
			Question q = this.genQuestion(getVerifyType(role.getRoleId()), role);
			if (null == q) {
				//获得题目错误,得处理下，
				//不能因为验证码系统问题
				//而导致全部玩家因为此问题全部下线
				//TODO:添加最大错误次数，超过次次数，需要将角色设置为通过眼中
				this.decrFailNum(role);
				return;
			}
			this.sendVerifyMessage(role, q);
		} catch (Exception ex) {
			logger.error("door dog heartbeat error", ex);
		}
	}
	
	/**
	 * 根据角色id返回验证码
	 * @param roleId
	 * @return
	 */
	private byte getVerifyType(String roleId){
		return Question.TYPE_IMAGE;
	}
	
	private boolean closeNetLink(RoleInstance role){
		if(null == role){
			return false ;
		}
		RoleDoorDogInfo info = this.getRoleDoorDogInfo(role.getRoleId());
		if(info.getDoorDogCount() < 0 || 
				info.getDoorDogCount() >= GameContext.getParasConfig().maxDoorDogErrorNum()){
			//发送消息给客户端,告知因为验证码失败而下线
			C0108_ForcedExitMessage exitResp = new C0108_ForcedExitMessage();
			exitResp.setInfo(GameContext.getI18n().getText(TextId.DOOR_DOG_MAX_ERROR_TIPS));
			role.getBehavior().sendMessage(exitResp,1000);
			//关闭连接
			role.getBehavior().closeNetLink();
			Log4jManager.SPEEDUP_LOG.info("doorDog fail close NetLink: roleid= "+role.getRoleId()
					+" roleName= "+role.getRoleName()+" userId= "+role.getUserId());
			return true ;
		}
		return false ;
	}
	
	private void incrFailNum(RoleInstance role){
		if(null == role){
			return ;
		}
		RoleDoorDogInfo info = this.getRoleDoorDogInfo(role.getRoleId());
		info.setPassDoorDog(false);
		//次数+1
		info.setDoorDogCount((byte)(info.getDoorDogCount()+1));
		//将当前验证码失效
		info.setDoorDogAnswer(null);
	}
	
	private boolean isIncrFailNum(RoleInstance role){
		RoleDoorDogInfo info = this.getRoleDoorDogInfo(role.getRoleId());
		Question question = info.getDoorDogQuestion();
		if(null == question){
			return true;
		}
		return question.getStep() == Question.DOORDOG_STEP_THREE;
	}
	
	/**
	 * 重置人身上的验证码
	 * @param role
	 */
	private void restRoleQuestion(RoleInstance role){
		RoleDoorDogInfo info = this.getRoleDoorDogInfo(role.getRoleId());
		//重置人身上的验证码问题
		Question question = info.getDoorDogQuestion();
		if(question != null && question.getStep() == Question.DOORDOG_STEP_THREE){
			info.setDoorDogQuestion(null);
			question.setStep(Question.DOORDOG_STEP_ONE);
		}
	}
	
	private void decrFailNum(RoleInstance role){
		if(null == role){
			return ;
		}
		RoleDoorDogInfo info = this.getRoleDoorDogInfo(role.getRoleId());
		info.setPassDoorDog(false);
		//次数-1
		info.setDoorDogCount((byte)(Math.max(0, info.getDoorDogCount()-1)));
		//将当前验证码失效
		info.setDoorDogAnswer(null);
	}
	
	private void sendVerifyMessage(RoleInstance role,Question q){
		RoleDoorDogInfo info = this.getRoleDoorDogInfo(role.getRoleId());
		//设置答案
		info.setDoorDogAnswer(q.getAnswer());
		//设置生成答案时间
		info.setLastGenQuestionTime(System.currentTimeMillis());
		
		C0013_DoorDogNewPanelNotifyMessage notifyMsg = new C0013_DoorDogNewPanelNotifyMessage();
		notifyMsg.setCurrentCount((byte)(info.getDoorDogCount() + 1));
		notifyMsg.setMaxCount((byte)GameContext.getParasConfig().maxDoorDogErrorNum());
		notifyMsg.setTimeout((short)GameContext.getParasConfig().getDoorDogPanelTimeout());
		QuestionDetail detail = q.getRealQuestion(role);
		notifyMsg.setTips(detail.getDesc() + GameContext.getI18n().messageFormat(TextId.DOOR_DOG_QUESTION_STATE,
				notifyMsg.getCurrentCount(),notifyMsg.getMaxCount()));
		notifyMsg.setQuestion(detail.getQuestion());
		notifyMsg.setType(q.getType());
		role.getBehavior().sendMessage(notifyMsg);
	}

	@Override
	public void roleLogout(RoleInstance role) {
		try {
			if (null == role) {
				return;
			}
			//移除
			this.roleDoorDogMap.remove(role.getRoleId());
			
			String hostName = this.getHostName(role);
			if (Util.isEmpty(hostName)) {
				return;
			}
			AtomicInteger count = ipCounts.get(hostName);
			if (null == count) {
				return;
			}
			if (count.decrementAndGet() <= 0) {
				ipCounts.remove(hostName);
			}
		}catch(Exception ex){
			logger.error("door dog role logout error",ex);
		}
	}

	@Override
	public void verifyQuestion(RoleInstance role, String answer) {
		RoleDoorDogInfo info = this.getRoleDoorDogInfo(role.getRoleId());
		if(info.isPassDoorDog()){
			//已经通过验证
			return ;
		}
		//如果验证码的subType=2,
		Question question = info.getDoorDogQuestion();
		if(question != null && question.getStep() == Question.DOORDOG_STEP_TWO){
			//答题阶段是1则直接返回阶段2的题目
			this.sendVerifyMessage(role, question);
			return ;
		}
		String doorDogAnswer = info.getDoorDogAnswer() ;
		if(Util.isEmpty(doorDogAnswer)){
			return ;
		}
		
		this.restRoleQuestion(role);
		if(null != answer && answer.trim().equalsIgnoreCase(doorDogAnswer)){
			//通过验证
			this.flagPassed(role);
			role.getBehavior().sendMessage(new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.DOOR_DOG_ANSWER_RIGHT)));
			return ;
		}
		//没有通过验证
		this.incrFailNum(role);
		if(!this.closeNetLink(role)){
			//没有关闭连接,给用户push验证码错误信息
			role.getBehavior().sendMessage(new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.DOOR_DOG_ANSWER_ERROR)));
		}
	}
	
	private void flagPassed(RoleInstance role){
		RoleDoorDogInfo info = this.getRoleDoorDogInfo(role.getRoleId());
		info.setPassDoorDog(true);
		info.setDoorDogCount((byte)0);
		info.setDoorDogAnswer(null);
		int passCount = info.getPassDoorDogTimes() + 1 ;
		if(passCount > Byte.MAX_VALUE){
			passCount = Byte.MAX_VALUE ;
		}
		//通过验证次数
		info.setPassDoorDogTimes((byte)passCount);
	}

	@Override
	public Result canUserLogin(String ipInfo) {
		Result result = new Result();
		try{
			if (Util.isEmpty(ipInfo)) {
				// 没有获得ip不验证
				return result.success();
			}
			int sameIpCount = 0;
			AtomicInteger count = ipCounts.get(ipInfo);
			if(null != count){
				sameIpCount = count.get();
			}
			int sameIpCanCount = GameContext.getDoorDogConfig().getCountByIp(ipInfo);
			if(sameIpCount < sameIpCanCount){
				return result.success();
			}
			return result.setInfo(GameContext.getI18n().getText(TextId.DOOR_DOG_MAX_IP_COUNT));
		}catch(Exception e){
			logger.error("DoorDogApp.canUserLogin error",e);
		}
		return result.success();
	}

}
