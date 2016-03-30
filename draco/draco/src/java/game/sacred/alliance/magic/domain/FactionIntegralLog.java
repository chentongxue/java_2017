package sacred.alliance.magic.domain;

import java.util.Date;

import sacred.alliance.magic.constant.FormatConstant;
import sacred.alliance.magic.util.DateUtil;

import lombok.Data;

public @Data class FactionIntegralLog {
	
	public static final String FACTIONID = "factionId";
	
	private int id;//唯一标识
	private String factionId;//公会ID
	private int roleId;//角色ID
	private String roleName;//角色名称
	private byte operateType;//操作类型
	private int integral;//积分变化值
	private int remainIntegral;//剩余积分
	private Date operateTime;//报名时间
	private String info;//备注信息
	
	/**
	 * 获取积分日志的时间
	 * @return
	 */
	public String getIntegralLogTime(){
		return DateUtil.date2FormatDate(this.operateTime, FormatConstant.IntegralLogFormat);
	}
	
	/**
	 * 获取积分日志的内容
	 * @return
	 */
	public String getIntegralLogContent(){
		String opType = this.operateType == 0 ? "消耗" : "增加";
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.roleName).append(this.info)
			.append(opType).append(this.integral).append("积分")
			.append("，门派剩余").append(this.remainIntegral).append("积分");
		return buffer.toString();
	}
	
}
