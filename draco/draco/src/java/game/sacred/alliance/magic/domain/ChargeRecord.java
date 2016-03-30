package sacred.alliance.magic.domain;

import java.util.Date;

import lombok.Data;
import sacred.alliance.magic.app.charge.ChargeStatus;
import sacred.alliance.magic.util.Log4jManager;

public @Data class ChargeRecord {
	
	public static final String CHANNELID = "channelId";
	public static final String CHANNELORDERID = "channelOrderId";
	
	private String roleId;
	private int channelId;//渠道ID
	private String channelOrderId;//渠道订单号
	private String orderId;//订单号
	private Date recordTime;
	private String userId;
	private int feeValue;
	private int state;
	private int errCode = -1;
	private int payGold;
	private int gameMoney;//游戏虚拟币 大于0表示使用虚拟币充值
	
	//private String userName;
	private String roleName;
	
	public ChargeStatus getchargeStatus(){
		return ChargeStatus.get(this.state);
	}
	
	/**
	 * 打印充值日志
	 */
	public void printUserPayLog(){
		StringBuffer sb = new StringBuffer();
		sb.append("$,").append(this.roleId)
			//.append(",").append(this.userName)
			.append(",").append(this.userId)
			.append(",").append(this.roleName)
			.append(",").append(this.feeValue)
			.append(",").append(this.payGold)
			.append(",").append(this.channelId)
			.append(",").append(this.channelOrderId)
			.append(",").append(this.orderId)
			.append(",").append(this.state)
			.append(",").append(this.gameMoney);
		Log4jManager.USER_PAY.info(sb.toString());
	}
	
}
