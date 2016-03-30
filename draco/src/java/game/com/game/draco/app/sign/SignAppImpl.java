package com.game.draco.app.sign;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.sign.config.SignConfig;
import com.game.draco.app.vip.type.VipPrivilegeType;
import com.google.common.collect.Maps;

public class SignAppImpl implements SignApp, Service {

	private Map<String, SignConfig> configMap = Maps.newLinkedHashMap();

	// * (2) 取int型变量a的第k位(k=0,1,2……sizeof(int))，即a>>k&1
	// * (3) 将int型变量a的第k位清0，即a=a&~(1<<k)
	// * (4) 将int型变量a的第k位置1， 即a=a|(1<<k)
	private static final int ONE = 1;
	private static final int INT_SIZE = 32;
	private int MAX_SIGN_TIMES = 31;

	// 签到
	private int flagSign(int signValue, int day) {
		return signValue | (ONE << day);
	}

	// 判断是否签到
	@Override
	public boolean isSigned(int signValue, int day) {
		return (signValue >> day & ONE) == 1;
	}

	// 取得当前已签到次数
	@Override
	public int getCurrSignTimes(RoleInstance role) {
		RoleCount count = role.getRoleCount();
		if (null == count) {
			return 0;
		}
		return count.getRoleTimesToInt(CountType.CurrSignTimes);//getCurrSignTimes();
	}

	//取得当前领奖信息
	@Override
	public byte getRecvState(int awardValue, int totalSignTimes, int times) {
		if (totalSignTimes < times) {
			return ReceiveState.canot_receive.getType();
		}
		if (!this.configMap.containsKey(String.valueOf(times))) {
			return ReceiveState.canot_receive.getType();
		}
		if (this.isRecvAward(awardValue, times)) {
			return ReceiveState.already_receive.getType();
		}
		return ReceiveState.can_receive.getType();
	}

	/**
	 * 补签
	 */
	@Override
	public Result signRepair(RoleInstance role) {
		Result result = new Result();
		RoleCount count = role.getRoleCount();
		count.resetDay();
		
		//判断是否已经到达最大配置签到数，如果是不允许补签并提示玩家领奖
		int currSignTimes = count.getRoleTimesToInt(CountType.CurrSignTimes);//getCurrSignTimes();		
		if (currSignTimes >= this.MAX_SIGN_TIMES) {
			result.setInfo(GameContext.getI18n().getText(TextId.Sign_sign_max_times));
			return result;
		}
		
		//判断是否还拥有补签次数，如果否不允许补签并提示玩家升级VIP等级
		int repairSignTimes = count.getRoleTimesToInt(CountType.RepairSignTimes);//.getRepairSignTimes();
		int canRepairSignTimes = this.getCanRepairSignTimes(role.getRoleId());
		if (repairSignTimes >= canRepairSignTimes) {
			result.setInfo(GameContext.getI18n().getText(TextId.Sign_repair_not_hava_times));
			return result;
		}
		
		int signValue = count.getRoleTimesToInt(CountType.MonthSign);//getMonthSign();
		Date now = new Date();
		int day = DateUtil.getDay(now);
		int repairDay = 0;
		for (int i = 1; i < day; i++) {
			if (this.isSigned(signValue, i)) {
				continue;
			}
			signValue = this.flagSign(signValue, i);
			currSignTimes ++;
			repairSignTimes ++;
			repairDay ++;
			
			if (currSignTimes >= this.MAX_SIGN_TIMES) {
				result.setInfo(GameContext.getI18n().getText(TextId.Sign_sign_max_times));
				break;
			}
			if (repairSignTimes >= canRepairSignTimes) {
				result.setInfo(GameContext.getI18n().getText(TextId.Sign_repair_not_hava_times));
				break;
			}
		}
		
		//判断是否需要补签
		if (repairDay <= 0) {
			result.setInfo(GameContext.getI18n().getText(TextId.Sign_repair_not_need_todo));
			return result;
		}
		
		//实时入库
		count.setDayTime(now);
		count.changeTimes(CountType.MonthSign,signValue);//setMonthSign(signValue);
		count.changeTimes(CountType.CurrSignTimes,currSignTimes);//setCurrSignTimes(currSignTimes);
		count.changeTimes(CountType.RepairSignTimes,repairSignTimes);//setRepairSignTimes(repairSignTimes);
		GameContext.getCountApp().saveRoleCount(count);
		return result;
	}

	/**
	 * 签到
	 */
	@Override
	public Result sign(RoleInstance role) {
		Result result = new Result();
		RoleCount count = role.getRoleCount();
		count.resetDay();
		Date now = new Date();
		int day = DateUtil.getDay(now);
		int signValue = count.getRoleTimesToInt(CountType.MonthSign);//getMonthSign();
		int currSignTimes = count.getRoleTimesToInt(CountType.CurrSignTimes);//getCurrSignTimes();

		if (this.isSigned(signValue, day)) {
			// 已经签到
			result.setInfo(GameContext.getI18n().getText(TextId.Sign_have_sign_this_day));
			return result;
		}
		if (currSignTimes >= this.MAX_SIGN_TIMES) {
			result.setInfo(GameContext.getI18n().getText(TextId.Sign_sign_max_times));
			return result;
		}
		// 签到
		count.setDayTime(now);
		count.changeTimes(CountType.MonthSign,this.flagSign(signValue, day));//setMonthSign(this.flagSign(signValue, day));
		// 将签名次数+1
		count.changeTimes(CountType.CurrSignTimes,count.getRoleTimesToInt(CountType.CurrSignTimes)+1);//setCurrSignTimes(count.getCurrSignTimes() + 1);
		// 实时入库
		GameContext.getCountApp().saveRoleCount(count);
		result.setInfo(GameContext.getI18n().getText(TextId.Sign_sign_success));
		result.success();
		return result;
	}

	// 获得当前领取奖励
	@Override
	public Result recvAward(RoleInstance role, int times) {
		Result result = new Result();
		RoleCount count = role.getRoleCount();
		count.resetDay();

		int awardValue = count.getRoleTimesToInt(CountType.CurrSignRecv);//getCurrSignRecv();
		int totalSignTimes = this.getCurrSignTimes(role);
		byte status = this.getRecvState(awardValue, totalSignTimes, times);
		if (ReceiveState.already_receive.getType() == status) {
			result.setInfo(GameContext.getI18n().getText(TextId.Sign_award_already_receive));
			return result;
		}
		if (ReceiveState.canot_receive.getType() == status) {
			result.setInfo(GameContext.getI18n().getText(TextId.Sign_award_canot_receive));
			return result;
		}
		SignConfig config = this.configMap.get(String.valueOf(times));
		if (null == config) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 添加物品
		GoodsResult gr = GameContext.getUserGoodsApp().addGoodsForBag(role, config.getGoodsId(), config.getGoodsNum(), BindingType.get(config.getBindType()),
				OutputConsumeType.sign_award_output);
		if (!gr.isSuccess()) {
			return gr;
		}
		awardValue = this.flagSign(awardValue, times);
		count.setDayTime(new Date());
		count.changeTimes(CountType.CurrSignRecv, awardValue);//setCurrSignRecv(awardValue);
		if (config.getTimes() >= this.MAX_SIGN_TIMES) {
			// 已经到达累计最大签名次数
			// 将签名次数以及领取奖励次数都重置
			count.changeTimes(CountType.CurrSignRecv, 0);//setCurrSignRecv(0);
			count.changeTimes(CountType.CurrSignTimes, 0);//setCurrSignTimes(0);
		}
		// 实时入库
		GameContext.getCountApp().saveRoleCount(count);
		return result;
	}

	// 取得取得当前月的签到数据
	@Override
	public int getMonthSignValue(RoleInstance role) {
		RoleCount count = role.getRoleCount();
		count.resetDay();
		return count.getRoleTimesToInt(CountType.MonthSign);//getMonthSign();
	}

	// 取得当前的奖励数据
	@Override
	public int getCurrSignRecv(RoleInstance role) {
		RoleCount count = role.getRoleCount();
		if (null == count) {
			return 0;
		}
		return count.getRoleTimesToInt(CountType.CurrSignRecv);//getCurrSignRecv();
	}

	private boolean isRecvAward(int awardValue, int times) {
		return (awardValue >> times & ONE) == 1;
	}

	public Collection<SignConfig> getAllSignConfig() {
		return this.configMap.values();
	}

	@Override
	public void setArgs(Object arg0) {

	}

	/**
	 * 按等级升序
	 */
	private void sortByLevel(List<SignConfig> list) {
		Collections.sort(list, new Comparator<SignConfig>() {
			@Override
			public int compare(SignConfig s1, SignConfig s2) {
				int level1 = s1.getTimes();
				int level2 = s2.getTimes();
				if (level1 < level2) {
					return -1;
				}
				if (level1 > level2) {
					return 1;
				}
				return 0;
			}
		});
	}

	private void loadConfig() {
		String fileName = XlsSheetNameType.sign_config.getXlsName();
		String sheetName = XlsSheetNameType.sign_config.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		Map<String, SignConfig> map = Maps.newLinkedHashMap();
		List<SignConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, SignConfig.class);
		if (Util.isEmpty(list)) {
			this.configMap = map;
			return;
		}
		this.sortByLevel(list);
		for (SignConfig config : list) {
			map.put(String.valueOf(config.getTimes()), config);
		}
		// 设置某配置是否为最大次数

		this.configMap = map;
		this.MAX_SIGN_TIMES = list.get(list.size() - 1).getTimes();
	}

	@Override
	public void start() {
		this.loadConfig();
	}

	@Override
	public void stop() {

	}

	/**
	 * 获取当前用户的每月可补签次数
	 */
	private int getCanRepairSignTimes(String roleId) {	
		return GameContext.getVipApp().getVipPrivilegeTimes(roleId,VipPrivilegeType.SIGN_REFILL_TIMES.getType(),"");
	}
}
