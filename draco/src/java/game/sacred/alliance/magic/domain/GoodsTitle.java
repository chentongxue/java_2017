package sacred.alliance.magic.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.GoodsBaseTitleItem;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.DateTimeBean;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.FormatConstant;
import sacred.alliance.magic.util.DateConverter;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;

public @Data class GoodsTitle extends GoodsBase {
	private final static long DEFAULT_COLOR = Long.parseLong("FFFFFFFF",16) ;
	private String nameColor;//称号颜色
	private String strokeColor;//描边颜色
	private short backImageId ; //背景图片
	private int titleEffectId ; //称号特效
	
	private byte pay;//是否可续费
	private String secondTypeName ; //小类名称
	private byte broadcast;//是否需要广播
	private String broadcastInfo;//获得需要广播内容
	private byte killBroadcast;//死亡广播
	private String killBroadcastInfo;//死亡广播的内容
	private int expAddRate;//加经验系数
	private int expMultRate;//乘经验系数

	private String attriTypes;//影响的属性
	private String values;//影响的值
	private String precValues;//影响的百分比
	
	/**
	 * 用来判断是否相同类型，替换以前用的小类判断
	 */
	private int groupId ;
	private String startDateStr;//绝对售卖开始日期
	private String endDateStr;//绝对售卖结束日期
	private int startDay;//相对开始日期（开服的第X天）
	private int endDay;//相对结束日期（开服的第X天）
	
	
	private List<AttriItem> attriList = new ArrayList<AttriItem>();
	private static float percent = 10000f;
	private long longNameColor ;
	private long longStrokeColor ;
	private Date startDate;//开始日期
	private Date endDate;//结束日期
	
	
	public void initBroadCast(){
		try{
			if(!Util.isEmpty(broadcastInfo)){
				broadcastInfo = broadcastInfo.replace(Wildcard.Title_Name, name);
			}
			if(!Util.isEmpty(killBroadcastInfo)){
				killBroadcastInfo = killBroadcastInfo.replace(Wildcard.Title_Name, name);
			}
			
		}catch(Exception e){
			logger.error("",e);
		}
	}
	/**需要广播*/
	public boolean isAllowBroadcast(){
		return broadcast != 0;
	}
	/**需要广播*/
	public boolean isKillAllowBroadcast(){
		return killBroadcast != 0;
	}
	/**被玩家杀死广播*/
	public boolean isAttackerRoleBroadcast(){
		return killBroadcast == 3 || killBroadcast == 1;
	}
	/**被NPC杀死广播*/
	public boolean isAttackerNpcBroadcast(){
		return killBroadcast == 3 || killBroadcast == 2;
	}
	
	/**永久称号*/
	public boolean isPermanent(){
		return deadline == 0;
	}
	
	/**等级*/
	public boolean isAllowLevel(int level) {
		return level >= this.lvLimit;
	}
	
	private long getColor(String color){
		if(Util.isEmpty(color)){
			return DEFAULT_COLOR ;
		}
		return Long.parseLong(color, 16);
	}
	
	public void initExpRate(){
		if(expAddRate > 0){
			attriList.add(new AttriItem(AttributeType.expAddRate.getType(),expAddRate,0f));
		}
		if(expMultRate > 0){
			attriList.add(new AttriItem(AttributeType.expMultRate.getType(),expMultRate,0f));
		}
		
	}
	
	
	private void initAttri(){
		if(Util.isEmpty(attriTypes)){
			return ;
		}
		if(Util.isEmpty(values) && Util.isEmpty(precValues)){
			return ;
		}
		
		String[] attriTypeArray = attriTypes.split(Cat.comma);
		String[] valueArray = null;
		String[] precValueArray = null;
		
		if(!Util.isEmpty(values)){
			valueArray = values.split(Cat.comma);
			if(attriTypeArray.length != valueArray.length){//长度不同则返回
				Log4jManager.CHECK.error("GoodsTitle error: length not same");
				Log4jManager.checkFail();
				return ;
			}
		}
		if(!Util.isEmpty(precValues)){
			precValueArray = precValues.split(Cat.comma);
			if(attriTypeArray.length != precValueArray.length){//长度不同则返回
				Log4jManager.CHECK.error("GoodsTitle error: length not same");
				Log4jManager.checkFail();
				return ;
			}
		}
		this.addAttriList(attriTypeArray, valueArray, precValueArray);
	}
	
	private void addAttriList(String[] attriTypeArray ,String[] valueArray ,String[] precValueArray){
		for(int i = 0 ; i < attriTypeArray.length ; i++){
			byte attriType = Byte.valueOf(attriTypeArray[i]);
			int precValue = 0 ;
			if(null != precValueArray && i < precValueArray.length){
				precValue = Integer.valueOf(precValueArray[i]);
			}
			int value = 0 ;
			if(null != valueArray && i < valueArray.length){
				value = Integer.valueOf(valueArray[i]);
			}
			if(precValue ==0 && value ==0){
				continue ;
			}
			attriList.add(new AttriItem(attriType,value,precValue/percent));
		}
	}
	
	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseTitleItem goodsTitleItem = new GoodsBaseTitleItem();
		this.setGoodsBaseItem(roleGoods, goodsTitleItem);
		goodsTitleItem.setLvLimit((byte)lvLimit);
		goodsTitleItem.setDesc(desc);
		goodsTitleItem.setNameColor((int)this.longNameColor);
		goodsTitleItem.setBackImageId((byte)this.backImageId);
		goodsTitleItem.setTitleEffectId((byte)this.titleEffectId);
		goodsTitleItem.setStrokeColor((int)this.longStrokeColor);
		return goodsTitleItem;
	}
	

	@Override
	public List<AttriItem> getAttriItemList() {
		return attriList ;
	}

	@Override
	public void init(Object initData) {
		this.initAttri();
		this.initBroadCast();
		this.initExpRate();
		this.longNameColor = this.getColor(this.nameColor);
		this.longStrokeColor = this.getColor(this.strokeColor);
		this.initTimes();
	}
	
	/**
	 * 是否在显示期间
	 * @return
	 */
	public boolean isInShowTime() {
		if (this.isForeverShow()) {
			return true;
		}
		long now = System.currentTimeMillis();
		if (null == this.endDate) {
			return now >= this.startDate.getTime();
		}
		if (null == this.startDate) {
			return now < this.endDate.getTime();
		}
		return now >= this.startDate.getTime() && now < this.endDate.getTime();
	}
	
	public boolean isForeverShow(){
		return null == this.startDate && null == this.endDate ;
	}

	private void initTimes(){
		if(this.startDay > 0 || this.endDay > 0 || 
				!Util.isEmpty(this.startDateStr) || 
				!Util.isEmpty(this.endDateStr)){
			DateTimeBean bean = DateConverter.getDateTimeBean(this.startDay, this.endDay, 
					this.startDateStr, this.endDateStr, FormatConstant.DEFAULT_YMD);
			if(null == bean){
				//表示没有时间显示限制
				this.startDate = null ;
				this.endDate = null ;
				return  ;
			}
			this.startDate = bean.getStartDate();
			this.endDate = bean.getEndDate();
		}
	}
}
