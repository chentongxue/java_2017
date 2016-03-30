package sacred.alliance.magic.app.log.vo;

import java.util.Date;

import lombok.Data;

public @Data class StatRoleGoodsRecord{
	
	private int sourceType;// 来源类型：【1获得、2流通、3消耗】
	private int systemType;// 系统类型：【见文档说明】
	private String remark;//备注信息
	private Date currDate;//  当前时间（获取宝石时的时间）
	private int goodsId;// 物品模版id
	private String goodsName;//物品名称
	private int goodsType;// 物品类型
	private int bindType;// 绑定类型
	private int goodsLevel;// 物品等级
	private int goodsLocation;  //物品位置编号
	private int goodsColor;// 物品颜色（品质）
	private String instanceId;// 物品实例id
	private int instanceNum;// 物品实例数量
	private int changeNum;// 物品实例的变化量
	
}
