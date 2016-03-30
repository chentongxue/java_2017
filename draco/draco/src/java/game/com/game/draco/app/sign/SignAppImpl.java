package com.game.draco.app.sign;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.google.common.collect.Maps;

public class SignAppImpl implements SignApp,Service{
	
	private Map<String,SignConfig> configMap = Maps.newLinkedHashMap();
	
	// * (2) 取int型变量a的第k位(k=0,1,2……sizeof(int))，即a>>k&1 
	// * (3) 将int型变量a的第k位清0，即a=a&~(1<<k) 
	// * (4) 将int型变量a的第k位置1， 即a=a|(1<<k) 
	private static final int ONE = 1 ;
	private static final int INT_SIZE = 32 ;
	
	
	private int flagSign(int signValue,int day){
		return signValue|(ONE<<day) ;
	}
	
	@Override
	public boolean isSigned(int signValue, int day) {
		return (signValue>>day&ONE) == 1;
	}

	@Override
	public int getCurrSignTimes(RoleInstance role) {
		RoleCount count = role.getRoleCount() ;
		if(null == count){
			return 0 ;
		}
		return count.getCurrSignTimes();
	}

	@Override
	public byte getRecvState(int awardValue, int totalSignTimes, int times) {
		if(totalSignTimes < times){
			return ReceiveState.canot_receive.getType();
		}
		if(!this.configMap.containsKey(String.valueOf(times))){
			return ReceiveState.canot_receive.getType();
		}
		if(this.isRecvAward(awardValue, times)){
			return ReceiveState.already_receive.getType() ;
		}
		return ReceiveState.can_receive.getType() ;
	}

	
	@Override
	public Result signRepair(RoleInstance role) {
		//TODO:判断VIP条件
		Result result = new Result();
		RoleCount count = role.getRoleCount() ;
		count.resetDay();
		Date now = new Date();
		int day = DateUtil.getDay(now);
		int signValue = count.getMonthSign() ;
		int repairDay = 0 ;
		for(int i=1;i<day;i++){
			if(this.isSigned(signValue, i)){
				continue ;
			}
			signValue = this.flagSign(signValue, i);
			repairDay++ ;
		}
		if(repairDay<=0){
			result.setInfo(GameContext.getI18n().getText(TextId.Sign_repair_not_need_todo));
			return result ;
		}
		//签到
		count.setDayTime(now);
		count.setMonthSign(signValue);
		//加当前签名次数
		count.setCurrSignTimes(count.getCurrSignTimes()+repairDay);
		//实时入库
		GameContext.getCountApp().saveRoleCount(count);
		return result;
	}

	@Override
	public Result sign(RoleInstance role) {
		Result result = new Result();
		RoleCount count = role.getRoleCount() ;
		count.resetDay();
		Date now = new Date();
		int day = DateUtil.getDay(now);
		int signValue = count.getMonthSign() ;
		if(this.isSigned(signValue, day)){
			//已经签到
			result.setInfo(GameContext.getI18n().getText(TextId.Sign_have_sign_this_day));
			return result ;
		}
		//签到
		count.setDayTime(now);
		count.setMonthSign(this.flagSign(signValue, day));
		//将签名次数+1
		count.setCurrSignTimes(count.getCurrSignTimes()+1);
		//实时入库
		GameContext.getCountApp().saveRoleCount(count);
		result.setInfo(GameContext.getI18n().getText(TextId.Sign_sign_success));
		result.success();
		return result;
	}
	
	@Override
	public Result recvAward(RoleInstance role, int times) {
		Result result = new Result();
		RoleCount count = role.getRoleCount() ;
		count.resetDay();
		
		int awardValue = count.getCurrSignRecv() ;
		int totalSignTimes = this.getCurrSignTimes(role);
		byte status = this.getRecvState(awardValue, totalSignTimes, times);
		if(ReceiveState.already_receive.getType() == status){
			result.setInfo(GameContext.getI18n().getText(TextId.Sign_award_already_receive));
			return result ;
		}
		if(ReceiveState.canot_receive.getType() == status){
			result.setInfo(GameContext.getI18n().getText(TextId.Sign_award_canot_receive));
			return result ;
		}
		SignConfig config = this.configMap.get(String.valueOf(times));
		if(null == config){
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result ;
		}
		//添加物品
		GoodsResult gr = GameContext.getUserGoodsApp().addGoodsForBag(role,
				config.getGoodsId(), config.getGoodsNum(),
				BindingType.get(config.getBindType()), OutputConsumeType.sign_award_output);
		if(!gr.isSuccess()){
			return gr ;
		}
		awardValue = this.flagSign(awardValue, times);
		count.setDayTime(new Date());
		count.setCurrSignRecv(awardValue);
		if(config.isMaxConfigTimes()){
			//已经到达累计最大签名次数
			//将签名次数以及领取奖励次数都重置
			count.setCurrSignRecv(0);
			count.setCurrSignTimes(0);
		}
		//实时入库
		GameContext.getCountApp().saveRoleCount(count);
		return result;
	}

	
	@Override
	public int getMonthSignValue(RoleInstance role) {
		RoleCount count = role.getRoleCount();
		count.resetDay();
		return count.getMonthSign() ;
	}
	
	@Override
	public int getCurrSignRecv(RoleInstance role) {
		RoleCount count = role.getRoleCount();
		if(null == count){
			return 0 ;
		}
		return count.getCurrSignRecv() ;
	}
	
	private boolean isRecvAward(int awardValue, int times) {
		return (awardValue>>times&ONE) == 1;
	}
	 
	
	public Collection<SignConfig> getAllSignConfig(){
		return this.configMap.values();
	}

	@Override
	public void setArgs(Object arg0) {
		
	}
	
	/**
	 * 按等级升序
	 */
	private void sortByLevel(List<SignConfig> list){
		Collections.sort(list, new Comparator<SignConfig>(){
			@Override
			public int compare(SignConfig s1, SignConfig s2) {
				int level1 = s1.getTimes();
				int level2 = s2.getTimes();
				if(level1 < level2){
					return -1;
				}
				if(level1 > level2){
					return 1;
				}
				return 0;
			}
		});
	}
	
	private void loadConfig(){
		String fileName = XlsSheetNameType.sign_config.getXlsName();
		String sheetName = XlsSheetNameType.sign_config.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		Map<String,SignConfig> map = Maps.newLinkedHashMap();
		List<SignConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, SignConfig.class);
		if(Util.isEmpty(list)){
			this.configMap = map ;
			return ;
		}
		this.sortByLevel(list);
		for(SignConfig config : list){
			map.put(String.valueOf(config.getTimes()), config);
		}
		//设置某配置是否为最大次数
		list.get(list.size()-1).setMaxConfigTimes(true);
		this.configMap = map ;
	}

	@Override
	public void start() {
		this.loadConfig();
	}

	@Override
	public void stop() {
		
	}

}
