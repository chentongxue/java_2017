package sacred.alliance.magic.app.role.reward;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.message.push.C1902_RoleOnlineRewardTimeNotifyMessage;

import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

public class OnlineRewardAppImpl implements OnlineRewardApp {
	
	private final Logger logger = LoggerFactory.getLogger(OnlineRewardAppImpl.class);
	private static final String RESET_TIME = "00:00";
	private static final int DefultIndex = 0;//领奖索引的默认值
	private static final int DefultRemainTime = -1;//领奖倒计时默认值（没有下次领奖时，领奖倒计时的值）
	private Map<Integer,List<OnlineReward>> rewardMap = new HashMap<Integer,List<OnlineReward>>();
	private int openLevel ; //活动开启级别
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadRewardConfig();
	}

	@Override
	public void stop() {
		
	}
	
	private void loadRewardConfig(){
		String fileName = XlsSheetNameType.online_reward.getXlsName();
		String sheetName = XlsSheetNameType.online_reward.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<OnlineReward> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, OnlineReward.class);
			int minLevel = Integer.MAX_VALUE ;
			for(OnlineReward config : list){
				if(null == config){
					continue;
				}
				if(config.getMinLevel() < minLevel){
					minLevel = config.getMinLevel() ;
				}
				//初始化配置数据
				config.init();
				int index = config.getIndex();
				if(!this.rewardMap.containsKey(index)){
					this.rewardMap.put(index, new ArrayList<OnlineReward>());
				}
				this.rewardMap.get(index).add(config);
			}
			this.openLevel = minLevel ;
		}catch(Exception e){
			Log4jManager.CHECK.error("load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".");
			Log4jManager.checkFail();
		}
	}
	
	
	private void doInit(RoleInstance role){
		try {
			// 判断是否需要重置，需要重置则刷新奖励信息
			if (this.checkAndReset(role)) {
				this.notifyRewardTimeByReset(role);
				return;
			}
			OnlineReward reward = this.getRoleCanTakeReward(role);
			if (null == reward) {
				return;
			}
			// 从来没有领过奖的角色
			Date lastTime = role.getOnlineRewardLastTime();
			if (null == lastTime) {
				role.setOnlineRewardLastTime(new Date());
				this.initRoleRewardValue(role, reward);
			} else {
				role.setOnlineReward(reward);
				role.setOnlineRewardNextTime(DateUtil.addSecond(new Date(),
						role.getOnlineRewardRemainTime()));
			}
			// 通知下次领奖倒计时
			this.notifyNextRewardTime(role);
		}catch(Exception ex){
			logger.error("",ex);
		}
	}
	
	
	public void roleLevelUpgrade(RoleInstance role) {
		if(!this.isEqualOpenLevel(role)){
			return ;
		}
		this.doInit(role);
	}
	
	
	@Override
	public void login(RoleInstance role) {
		if(!this.isReachOpenLevel(role)){
			return ;
		}
		this.doInit(role);
	}
	
	/**
	 * 判断角色等级是否已经达到开启等级
	 * @param role
	 * @return
	 */
	private boolean isReachOpenLevel(RoleInstance role){
		return role.getLevel() >= this.openLevel ;
	}
	
	private boolean isEqualOpenLevel(RoleInstance role){
		return role.getLevel() == this.openLevel ;
	}
	
	/**
	 * 通知角色领奖倒计时
	 * @param role
	 */
	private void notifyNextRewardTime(RoleInstance role){
		int time = role.getOnlineRewardRemainTime();
		C1902_RoleOnlineRewardTimeNotifyMessage message = new C1902_RoleOnlineRewardTimeNotifyMessage();
		message.setTime(time);
		role.getBehavior().sendMessage(message);
	}
	
	/**
	 * 系统重置后通知角色领奖倒计时
	 * @param role
	 */
	private void notifyRewardTimeByReset(RoleInstance role){
		OnlineReward reward = this.getRoleCanTakeReward(role);
		if(null != reward){
			this.initRoleRewardValue(role, reward);
		}
		this.notifyNextRewardTime(role);
	}
	
	/**
	 * 检测是否需要重置
	 * @param role
	 * @return
	 */
	private boolean checkAndReset(RoleInstance role){
		Date lastTime = role.getOnlineRewardLastTime();
		if(null == lastTime){
			lastTime = new Date();
			//容错，修改了最后领奖时间要入库
			role.setOnlineRewardLastTime(lastTime);
		}
		if(!DateUtil.isTimeOver(GameContext.systemStartTime, lastTime, 1, RESET_TIME)){
			return false;
		}
		this.resetRoleRewardValue(role);
		return true;
	}
	
	/**
	 * 重置角色的领奖信息
	 * @param role
	 */
	private void resetRoleRewardValue(RoleInstance role){
		role.setOnlineRewardIndex(DefultIndex);
		role.setOnlineRewardRemainTime(DefultRemainTime);
		role.setOnlineRewardLastTime(new Date());
		role.setOnlineRewardNextTime(null);
		role.setOnlineReward(null);
	}
	
	/**
	 * 初始化角色的奖励信息
	 * @param role
	 * @param reward
	 */
	private void initRoleRewardValue(RoleInstance role, OnlineReward reward){
		int second = reward.getTime();
		role.setOnlineRewardIndex(reward.getIndex());
		role.setOnlineRewardRemainTime(second);
		role.setOnlineRewardNextTime(DateUtil.addSecond(new Date(), second));
		role.setOnlineReward(reward);//把奖励放到角色身上
	}

	@Override
	public void logout(RoleInstance role) {
		if(!this.isReachOpenLevel(role)){
			return ;
		}
		int remainTime = role.getOnlineRewardRemainTime();
		Date now = new Date();
		Date endDate = role.getOnlineRewardNextTime();
		if(DefultRemainTime != remainTime && null != endDate){
			role.setOnlineRewardRemainTime(DateUtil.dateDiffSecond(now, endDate));
		}
	}

	@Override
	public void systemRefresh() {
		for(RoleInstance role : GameContext.getOnlineCenter().getAllOnlineRole()){
			if(!this.isReachOpenLevel(role)){
				continue ;
			}
			//当日领奖结束的角色才重置
			if(DefultRemainTime != role.getOnlineRewardRemainTime()){
				continue;
			}
			this.resetRoleRewardValue(role);
			this.notifyRewardTimeByReset(role);
		}
	}
	
	@Override
	public Result takeReward(RoleInstance role) {
		Result result = new Result();
		OnlineReward reward = role.getOnlineReward();
		if(null == reward){
			return result.setInfo(GameContext.getI18n().getText(TextId.ONLINE_REWARD_NOT_HAS));
		}
		Date now = new Date();
		Date nextTime = role.getOnlineRewardNextTime();
		int remainTime = DateUtil.dateDiffSecond(now, nextTime);
		if(now.before(nextTime)){
			role.setOnlineRewardRemainTime(remainTime);
			return result.setInfo(GameContext.getI18n().getText(TextId.ONLINE_REWARD_CD_NOT_OVER));
		}
		//往背包添加奖励物品，失败则返回错误信息
		GoodsResult goodsResult = GameContext.getUserGoodsApp().addGoodsBeanForBag(role, reward.getGoodsList(), OutputConsumeType.online_reward);
		if(!goodsResult.isSuccess()){
			role.setOnlineRewardRemainTime(remainTime);
			return goodsResult;
		}
		//金钱奖励
		int bindMoney = reward.getBindMoney();
		int silver = reward.getSilverMoney();
		if(bindMoney > 0){
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.bindingGoldMoney, 
					OperatorType.Add, bindMoney, OutputConsumeType.online_reward);
		}
		if(silver > 0){
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.silverMoney, 
					OperatorType.Add, silver, OutputConsumeType.online_reward);
		}
		//奖励经验
		int exp = reward.getExp();
		if(exp > 0){
			GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.exp, 
					OperatorType.Add, exp, OutputConsumeType.online_reward);
		}
		//通知属性改变
		if(bindMoney > 0 || silver > 0 || exp > 0){
			role.getBehavior().notifyAttribute();
		}
		int index = role.getOnlineRewardIndex() + 1;
		role.setOnlineRewardIndex(index);
		//判断是否需要重置，到时间则刷新
		this.checkAndReset(role);
		//领奖成功，设置最后领奖时间
		role.setOnlineRewardLastTime(now);
		OnlineReward nextReward = this.getRoleCanTakeReward(role);
		//没有下次领奖，设置标识
		if(null == nextReward){
			role.setOnlineRewardRemainTime(DefultRemainTime);//下次领奖时间为-1
			role.setOnlineRewardNextTime(null);
			role.setOnlineReward(null);
			return result.success();
		}
		//有下次领奖信息，给角色赋值
		this.initRoleRewardValue(role, nextReward);
		return result.success();
	}
	
	/**
	 * 获取角色当前可领取的奖励信息
	 * @param role
	 * @return
	 */
	private OnlineReward getRoleCanTakeReward(RoleInstance role) {
		int index = role.getOnlineRewardIndex();
		if(DefultIndex == index){
			index = DefultIndex + 1;
		}
		int level = role.getLevel();
		List<OnlineReward> rewardList = this.rewardMap.get(index);
		if(Util.isEmpty(rewardList)){
			return null;
		}
		for(OnlineReward reward : rewardList){
			if(null == reward){
				continue;
			}
			if(level <= reward.getMaxLevel() && level >= reward.getMinLevel()){
				return reward;
			}
		}
		return null;
	}
	
}
