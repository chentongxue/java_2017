package sacred.alliance.magic.app.benefit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.hint.HintId;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.BenefitLoginCountItem;
import com.game.draco.message.item.BenefitOfflineExpItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.response.C1903_BenefitPanelRespMessage;

public class BenefitAppImpl implements BenefitApp {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	/** 离线经验配置信息：KEY=等级,VALUE=离线经验配置 */
	private Map<Integer, OfflineExpConfig> offlineExpMap = new HashMap<Integer, OfflineExpConfig>();
	/** 离线经验奖励配置 */
	private List<OfflineExpAward> expAwardList = new ArrayList<OfflineExpAward>();
	
	/** 每日登录每轮最大天数 */
	private static final int Max_Index = 7;
	/** 第二轮及其之后的奖励配置：KEY=天数索引,VALUE=奖励配置 */
	private Map<Integer,List<LoginCount>> rewardMap = new HashMap<Integer,List<LoginCount>>();
	/** 第一轮的奖励配置：KEY=天数索引,VALUE=奖励配置 */
	private Map<Integer,List<LoginCount>> specialRewardMap = new HashMap<Integer,List<LoginCount>>();
	private int popupLevel;//登录弹出面板的角色等级
	private int popupTime;//登录弹出面板的离线时间
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadOfflineExpConfig();
		this.loadOfflineExpAward();
		this.loadPopupParamConfig();
		this.loadLoginCountConfig();
	}

	@Override
	public void stop() {
		
	}
	
	/** 加裁离线经验配置 */
	private void loadOfflineExpConfig() {
		String fileName = XlsSheetNameType.benefit_offline_exp.getXlsName();
		String sheetName = XlsSheetNameType.benefit_offline_exp.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<OfflineExpConfig> expList = XlsPojoUtil.sheetToList(sourceFile, sheetName, OfflineExpConfig.class);
			for (OfflineExpConfig config : expList) {
				if(null == config){
					continue;
				}
				this.offlineExpMap.put(config.getLevel(), config);
			}
		}catch(Exception e){
			Log4jManager.CHECK.error("load excel error: fileName=" + fileName + ",sheetName=" + sheetName + ".", e);
			Log4jManager.checkFail();
		}
	}

	/** 加裁离线经验奖励 */
	private void loadOfflineExpAward() {
		String fileName = XlsSheetNameType.benefit_offline_award.getXlsName();
		String sheetName = XlsSheetNameType.benefit_offline_award.getSheetName();
		String info = "load excel error: fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			this.expAwardList = XlsPojoUtil.sheetToList(sourceFile, sheetName, OfflineExpAward.class);
			if (Util.isEmpty(this.expAwardList)) {
				Log4jManager.CHECK.error(info + "award has no config");
				Log4jManager.checkFail();
			}
			//判断是否有序
			int curMultiple = 0;
			boolean noOrder = false;
			for(OfflineExpAward award : this.expAwardList){
				if(null == award){
					continue ;
				}
				//初始化配置
				award.init();
				int multiple = award.getMultiple();
				if(multiple < curMultiple){
					noOrder = true;
					break;
				}
				curMultiple = multiple;
				if (!this.expAwardList.contains(award)) {
					this.expAwardList.add(award);
				}
			}
			if(noOrder){
				Log4jManager.CHECK.error(info + "item has no order");
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			Log4jManager.CHECK.error(info, e);
			Log4jManager.checkFail();
		}
	}
	
	private void loadPopupParamConfig() {
		String fileName = XlsSheetNameType.benefit_popup_param.getXlsName();
		String sheetName = XlsSheetNameType.benefit_popup_param.getSheetName();
		String info = "load excel error: fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<BenefitPopupParam> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, BenefitPopupParam.class);
			BenefitPopupParam param = list.get(0);
			this.popupLevel = param.getLevel();
			this.popupTime = param.getTime();
			if(this.popupLevel <= 0 || this.popupTime <= 0){
				Log4jManager.CHECK.error(info + "popupLevel or popupTime is error!");
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			Log4jManager.CHECK.error(info, e);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 加载每日登录的配置
	 */
	private void loadLoginCountConfig(){
		//先加载首轮奖励
		this.loadReward(XlsSheetNameType.benefit_login_count_special_reward.getXlsName(), 
				XlsSheetNameType.benefit_login_count_special_reward.getSheetName(), 
				this.specialRewardMap);
		//再加载普通奖励
		this.loadReward(XlsSheetNameType.benefit_login_count_reward.getXlsName(), 
				XlsSheetNameType.benefit_login_count_reward.getSheetName(), 
				this.rewardMap);
	}
	
	/**
	 * 加载每日登录的奖励
	 * @param fileName
	 * @param sheetName
	 * @param rdMap 保存奖励的Map
	 */
	private void loadReward(String fileName, String sheetName, Map<Integer,List<LoginCount>> rdMap){
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<LoginCount> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, LoginCount.class);
			for(LoginCount config : list){
				if(null == config){
					continue;
				}
				//初始化配置数据
				Result result = config.checkAndInit();
				if(!result.isSuccess()){
					Log4jManager.CHECK.error(info + result.getInfo());
					Log4jManager.checkFail();
					continue;
				}
				int index = config.getIndex();
				if(!rdMap.containsKey(index)){
					rdMap.put(index, new ArrayList<LoginCount>());
				}
				rdMap.get(index).add(config);
			}
		}catch(Exception e){
			Log4jManager.CHECK.error(info);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 获取每日登录的奖励配置
	 * @param role
	 * @param index 第几天
	 * @param rdMap 奖励Map
	 * @return
	 */
	private LoginCount getLoginCount(RoleInstance role, int index, Map<Integer,List<LoginCount>> rdMap){
		List<LoginCount> list = rdMap.get(index);
		if(Util.isEmpty(list)){
			return null;
		}
		for(LoginCount item : list){
			if(null == item || !item.isSuitLevel(role)){
				continue;
			}
			return item;
		}
		return null;
	}
	
	/**
	 * 获取每日登录的奖励列表
	 * @param role
	 * @return
	 */
	private List<LoginCount> getLoginCountList(RoleInstance role) {
		//每日登录的天数
		int index = this.getLoginCountDays(role);
		//前7天
		if(index <= Max_Index){
			return this.getRewardList(role, this.specialRewardMap);
		}
		//第8天开始，之后的天数
		return this.getRewardList(role, this.rewardMap);
	}
	
	/**
	 * 获取每日登录的奖励列表
	 * @param role
	 * @param rdMap 奖励Map
	 * @return
	 */
	private List<LoginCount> getRewardList(RoleInstance role, Map<Integer,List<LoginCount>> rdMap){
		List<LoginCount> list = new ArrayList<LoginCount>();
		for(int i=1; i<=Max_Index; i++){
			LoginCount item = this.getLoginCount(role, i, rdMap);
			if(null == item){
				continue;
			}
			list.add(item);
		}
		return list;
	}
	
	/**
	 * 获取每日登录的当前奖励配置
	 * @param role
	 * @return
	 */
	private LoginCount getLoginCount(RoleInstance role){
		int index = this.getLoginCountDays(role);
		if(index <= Max_Index){
			return this.getLoginCount(role, index, this.specialRewardMap);
		}
		int newIndex = index % Max_Index;
		if(0 == newIndex){
			newIndex = Max_Index;
		}
		return this.getLoginCount(role, newIndex, this.rewardMap);
	}
	
	@Override
	public Result takeLoginCountReward(RoleInstance role) {
		Result result = new Result();
		//判断今日是否可领奖
		if(LoginCountStatus.Can_Receive != this.getTodayLoginCountStatus(role)){
			return result.setInfo(Status.Login_Not_Can_Reward.getTips());
		}
		LoginCount loginCount = this.getLoginCount(role);
		if(null == loginCount){
			return result.setInfo(Status.Login_Req_Param_Error.getTips());
		}
		//奖励物品
		GoodsResult goodsResult = GameContext.getUserGoodsApp().addGoodsBeanForBag(role, loginCount.getGoodsList(), OutputConsumeType.active_serial_login);
		if(!goodsResult.isSuccess()){
			return goodsResult;
		}
		//累计天数增加
		int index = role.getDailyLoginCount() + 1;
		role.setDailyLoginCount(index);
		//设置领奖时间
		role.setDailyLoginRewardDate(new Date());
		
		//更改可领取提示
		this.hintChange(role, HintId.Benefit);
		return result.success();
	}
	
	/**
	 * 获取每日登录的天数
	 * @param role
	 * @return
	 */
	@Override
	public int getLoginCountDays(RoleInstance role){
		int index = role.getDailyLoginCount();//今日所在的序列
		Date lastRewardTime = role.getDailyLoginRewardDate();
		//未领取过奖励或不是今天领取的
		if(null == lastRewardTime || !DateUtil.sameDay(lastRewardTime, new Date())){
			index += 1;
		}
		return index;
	}
	
	/**
	 * 获取奖励索引
	 * @param role
	 * @return
	 */
	private int getLoginCountIndex(RoleInstance role){
		int index = this.getLoginCountDays(role);
		index = index % Max_Index;
		if(0 == index){
			index = Max_Index;
		}
		return index;
	}
	
	/**
	 * 获取每日登录的领奖状态
	 * 今天是否可领奖
	 * @param role
	 * @return
	 */
	private LoginCountStatus getTodayLoginCountStatus(RoleInstance role){
		//如果领奖时间是今天，则状态为已完成；领奖时间非今天，则状态为可领取
		Date lastRewardTime = role.getDailyLoginRewardDate();
		if(null != lastRewardTime && DateUtil.sameDay(lastRewardTime, new Date())){
			return LoginCountStatus.Have_Received;
		}
		return LoginCountStatus.Can_Receive;
	}
	
	/**
	 * 获取福利面板上每日登录的信息
	 * @param role
	 * @return
	 */
	private List<BenefitLoginCountItem> getLoginCounPanelInfo(RoleInstance role) {
		int todayIndex = this.getLoginCountIndex(role);
		LoginCountStatus todayStatus = this.getTodayLoginCountStatus(role);
		List<BenefitLoginCountItem> displayList = new ArrayList<BenefitLoginCountItem>();
		for(LoginCount loginCount : this.getLoginCountList(role)){
			if(null == loginCount){
				continue;
			}
			int index = loginCount.getIndex();
			LoginCountStatus status = LoginCountStatus.Cannot_Receive;
			if(index < todayIndex){
				status = LoginCountStatus.Have_Received;
			}else if(index > todayIndex){
				status = LoginCountStatus.Cannot_Receive;
			}else{
				status = todayStatus;
			}
			BenefitLoginCountItem item = new BenefitLoginCountItem();
			item.setStatus(status.getType());
			List<GoodsLiteItem> goodsList = new ArrayList<GoodsLiteItem>();
			for(GoodsOperateBean bean : loginCount.getGoodsList()){
				if(null == bean){
					continue;
				}
				int goodsId = bean.getGoodsId();
				GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
				if(null == goodsBase){
					continue;
				}
				GoodsLiteItem goodsItem = goodsBase.getGoodsLiteItem();
				goodsItem.setBindType(bean.getBindType().getType());
				goodsItem.setNum((short)bean.getGoodsNum());
				goodsList.add(goodsItem);
			}
			item.setGoodsList(goodsList);
			displayList.add(item);
		}
		return displayList;
	}

	@Override
	public C1903_BenefitPanelRespMessage getBenefitPanelRespMessage(RoleInstance role) {
		C1903_BenefitPanelRespMessage resp = new C1903_BenefitPanelRespMessage();
		resp.setLoginCountList(this.getLoginCounPanelInfo(role));
		//离线经验面板信息
		OfflineExpConfig config = this.getRoleOfflineExp(role.getLevel());
		if (null != config) {
			resp.setFull((byte) 0);
			int offlineTime = role.getOfflineTime();
			if(0 == offlineTime){
				resp.setFull((byte) 2);
			}else if(offlineTime >= config.getAddRateMinute(role)) {
				resp.setFull((byte) 1);
			}
			resp.setExp(config.getTimeExp(role, offlineTime));
			resp.setTime(offlineTime);
			resp.setZp(config.getTimeZp(role, offlineTime));
			List<BenefitOfflineExpItem> offlineExpList = new ArrayList<BenefitOfflineExpItem>();
			for (OfflineExpAward award : this.expAwardList) {
				if(null == award){
					continue;
				}
				BenefitOfflineExpItem item = new BenefitOfflineExpItem();
				item.setMultiple(award.getMultiple());
				item.setAttriType(award.getAttriType());
				item.setMoney(award.getDeductMoney(config, offlineTime));
				item.setBtnName(award.getBtnName());
				offlineExpList.add(item);
			}
			resp.setOfflineExpList(offlineExpList);
		}
		return resp;
	}
	
	@Override
	public Result takeOfflineExp(RoleInstance role, byte index){
		Result result = new Result();
		try {
			int offlineTime = role.getOfflineTime();
			if (offlineTime == 0) {
				return result.setInfo(Status.Offline_Is_Null.getTips());
			}
			if(index < 0 || index >= this.expAwardList.size()){
				return result.setInfo(Status.FAILURE.getTips());
			}
			OfflineExpAward award = this.expAwardList.get(index);
			if(null == award){
				return result.setInfo(Status.Offline_No_Multiple.getTips());
			}
			OfflineExpConfig config = this.getRoleOfflineExp(role.getLevel());
			if (null == config) {
				return result.setInfo(Status.Offline_Obj_Null.getTips());
			}
			//需要付费领取的，扣相应的钱币
			if(award.needPayMoney()){
				AttributeType attrType = award.getAttributeType();
				int needMoney = award.getDeductMoney(config.getBaseMoney(attrType), offlineTime);
				if(role.get(attrType) < needMoney){
					return result.setInfo(Status.Offline_Money_Less.getTips());
				}
				GameContext.getUserAttributeApp().changeRoleMoney(role, attrType,
						OperatorType.Decrease, needMoney, OutputConsumeType.offline_consume);
				role.getBehavior().notifyAttribute();
			}
			//领取离线经验时，使用实际的倍数，然后乘以领取倍数
			int multiple = (int) award.getRealMultiple();
			int exp = this.multiplication(config.getTimeExp(role, offlineTime), multiple);
			int zp = this.multiplication(config.getTimeZp(role, offlineTime), multiple);
			//发离线经验奖励
			this.sendOfflineAward(role, exp, zp);
			//离线时间清零
			role.setOfflineTime(0);
			//更改可领取提示
			this.hintChange(role, HintId.Benefit);
			return result.success();
		} catch (Exception e) {
			this.logger.error("BenefitApp.takeOfflineExp error: ", e);
			return result.setInfo("");
		}
	}
	
	/**
	 * 乘法
	 * @param time
	 * @param attrValue
	 * @return
	 */
	private int multiplication(int attrValue, float multiple){
		long result = (long) (attrValue * multiple);
		if(result > Integer.MAX_VALUE){
			result = Integer.MAX_VALUE;
		}
		return (int) result;
	}
	
	private OfflineExpConfig getRoleOfflineExp(int level) {
		return this.offlineExpMap.get(level);
	}
	
	/**
	 * 发离线经验奖励
	 * @param role
	 * @param exp
	 * @param zp
	 */
	private void sendOfflineAward(RoleInstance role, int exp, int zp) {
		try {
			if(exp > 0){
				GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.exp, 
						OperatorType.Add, exp, OutputConsumeType.offline_output);
			}
			if(zp > 0){
				GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.potential, 
						OperatorType.Add, zp, OutputConsumeType.offline_output);
			}
			if(exp > 0 || zp > 0){
				role.getBehavior().notifyAttribute();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		
	}
	
	@Override
	public void initRoleOfflineTime(RoleInstance role) {
		try{
			Date lastOfflineTime = role.getLastOffTime();
			if(null == lastOfflineTime){
				return ;
			}
			OfflineExpConfig config = this.getRoleOfflineExp(role.getLevel());
			if (null == config) {
				return;
			}
			Date now = new Date();
			int time = (int) DateUtil.dateDiffMinute(now, lastOfflineTime);
			
			if (time < config.getBaseMinute()) {
				return;
			}
			time += role.getOfflineTime();
			int maxMinute = config.getAddRateMinute(role);
			if(time > maxMinute) {
				time = maxMinute;
			}
			role.setOfflineTime(time);
		}catch(Exception e){
			logger.error("BenefitApp.initRoleOfflineTime error: ",e);
		}
		
	}

	@Override
	public C1903_BenefitPanelRespMessage popupBenefitPanel(RoleInstance role) {
		//判断角色等级
		if(role.getLevel() < this.popupLevel){
			return null;
		}
		if(!this.benefitCanTake(role)){
			return null;
		}
		return this.getBenefitPanelRespMessage(role);
	}
	
	private boolean benefitCanTake(RoleInstance role){
		return role.getOfflineTime() >= this.popupTime || LoginCountStatus.Can_Receive == this.getTodayLoginCountStatus(role);
	}
	
	@Override
	public Set<HintId> getHintIdSet(RoleInstance role) {
		Set<HintId> set = new HashSet<HintId>();
		if(this.benefitCanTake(role)){
			set.add(HintId.Benefit);
		}
		return set;
	}
	
	@Override
	public void hintChange(RoleInstance role, HintId hintId){
		try {
			GameContext.getHintApp().hintChange(role, hintId, this.benefitCanTake(role));
		} catch (Exception e) {
			this.logger.error("BenefitApp.hintChange error: ", e);
		}
	}
	
}
