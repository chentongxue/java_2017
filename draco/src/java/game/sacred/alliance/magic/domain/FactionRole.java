//package sacred.alliance.magic.domain;
//
//import java.util.Date;
//
//import lombok.Data;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import sacred.alliance.magic.base.FactionPositionType;
//import sacred.alliance.magic.constant.Cat;
//import sacred.alliance.magic.constant.FormatConstant;
//import sacred.alliance.magic.util.DateUtil;
//import sacred.alliance.magic.util.Log4jManager;
//
//public @Data class FactionRole {
//	private final Logger logger = LoggerFactory.getLogger(this.getClass());
//	private int roleId;//角色ID
//	private String factionId;//公会ID
//	private String roleName;//角色名称
//	private int roleLevel;//角色等级
//	private byte career;//角色职业
//	private byte sex;//性别
//	private String nickName;//公会昵称
//	private byte position;//公会职务
//	private Date createDate;//加入公会时间
//	private String signature = "爱生活";//签名
//	private Date offlineTime;//最后一次下线时间
//	private int prestige;//公会声望
//	private int contribution;//贡献度
//	private int totalContribution;//贡献度
//	private String userId;
//	
//	public void offlineLog(){
//		StringBuffer sb = new StringBuffer();
//		sb.append(roleId);
//		sb.append(Cat.pound);
//		sb.append(factionId);
//		sb.append(Cat.pound);
//		sb.append(roleName);
//		sb.append(Cat.pound);
//		sb.append(roleLevel);
//		sb.append(Cat.pound);
//		sb.append(career);
//		sb.append(Cat.pound);
//		sb.append(nickName);
//		sb.append(Cat.pound);
//		sb.append(position);
//		sb.append(Cat.pound);
//		sb.append(DateUtil.getTimeByDate(createDate));
//		sb.append(Cat.pound);
//		sb.append(signature);
//		sb.append(Cat.pound);
//		sb.append(DateUtil.getTimeByDate(offlineTime));
//		sb.append(Cat.pound);
//		sb.append(prestige);
//		sb.append(Cat.pound);
//		sb.append(contribution);
//		sb.append(Cat.pound);
//		Log4jManager.FACTION_LOG.info(sb.toString());
//	}
//	
//	/**
//	 * 获得帮众的职位
//	 * @return
//	 */
//	public FactionPositionType getPositionType(){
//		return FactionPositionType.getPosition(this.position);
//	}
//	
//	public void setPositionType(FactionPositionType positionType){
//		if(null != positionType){
//			this.position = positionType.getType();
//		}
//	}
//	
//	/**
//	 * 加入门派时间（或申请入会时间）
//	 * @return
//	 */
//	public String getCreatTime(){
//		return DateUtil.date2FormatDate(this.createDate, FormatConstant.FactionRoleCreateFormat);
//	}
//	
//	/** 门派成员最后下线时间 */
//	public String getLastOfflineTime(){
//		if(null == this.offlineTime){
//			return "";
//		}
//		return DateUtil.date2FormatDate(this.offlineTime, FormatConstant.FactionRoleOfflineFormat);
//	}
//}
