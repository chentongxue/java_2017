package sacred.alliance.magic.app.benefit;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class OfflineExpConfig {
	
	private static final int minute = 60;//一小时
	private int level;//等级
	private float exp;//每分钟获得的经验
	private float zp;//每分钟获得的真气
	private float baseGold;//消耗金条基础数
	private float baseBindGold;//消耗绑金基础数
	private float baseSilver;//消耗银币基数
	private int maxMinute;//最大计算经验的分钟数
	private int baseMinute;//开始计算经验的基本分钟数
	private int temporaryExp = 0;//临时经验
	private int temporaryMinute = 0;//临时最大分钟数
	
	/**VIP加成后的最大计算经验的分钟数*/
	public int getAddRateMinute(RoleInstance role){
		return maxMinute;
		//TODO:去掉VIP影响的最大分钟数
		/*if(temporaryMinute == 0){
			PriValue pv = GameContext.getRoleVipApp().getPriValue(role, PrivType.offlineExpMaxTimeLimit);
			if(null != pv){
				temporaryMinute = pv.getValue();
			}
		}
		return maxMinute + temporaryMinute;*/
	}
	
	/**
	 * 得到时间内积累的经验值
	 * @param role
	 * @param time
	 * @return
	 */
	public int getTimeExp(RoleInstance role, float time){
		return this.multiplication(time, this.exp);
	}
	
	/**
	 * 获取时间内积累的真气值
	 * @param role
	 * @param time
	 * @return
	 */
	public int getTimeZp(RoleInstance role, float time){
		return this.multiplication(time, this.zp);
	}
	
	/**
	 * 乘法
	 * @param time
	 * @param attrValue
	 * @return
	 */
	private int multiplication(float time, float attrValue){
		long result = (long) (time * attrValue);
		if(result > Integer.MAX_VALUE){
			result = Integer.MAX_VALUE;
		}
		return (int) result;
	}
	
	public float getBaseMoney(AttributeType attrType){
		if(null == attrType){
			return 0;
		}
		switch(attrType){
		case goldMoney:
			return this.baseGold;
		case bindingGoldMoney:
			return this.baseBindGold;
		case silverMoney:
			return this.baseSilver;
		}
		return 0;
	}
	
}
