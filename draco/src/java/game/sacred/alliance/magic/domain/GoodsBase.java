package sacred.alliance.magic.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.goods.decompose.DecomposeConfig;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.QualityType;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.util.DateUtil;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.item.GoodsLiteNamedExItem;
import com.game.draco.message.item.GoodsLiteNamedItem;

public abstract class GoodsBase  {
	protected final static Logger logger = LoggerFactory.getLogger(GoodsBase.class);
	protected int id;
	protected String name;
	protected short imageId;// 图片ID
	protected byte bindType;// 绑定
	protected int overlapCount = 1;// 叠加数
	protected int only;// 唯一
	protected int recycling = 1 ;//是否可回收
	protected int recyclePrice;// 回收价
	protected int offlineDie;// 下线消失
	protected int discard;// 可否丢弃
	protected int level ;
	protected int effectId ;
	protected String desc;// 说明
	protected int goodsType;// 物品类型
	protected int printlog;// 是否打印日志0:不打印 1:商城 2:绑金商城 3.一般物品
	protected int resId;//道具资源ID
	/** 有几个表没有此字段,默认为零 **/
	protected byte qualityType;
	//星
	protected byte star ;
	/**物品小类*/
	protected byte secondType ;
	/**物品使用等级*/
	protected int lvLimit  = 1 ;
	protected byte career = -1 ;
	
	protected byte activateType;//激活类型0：使用激活1：进入背包激活
	protected int deadline;//有效期限（分钟）
	protected byte expireType;//到期处理类型
	
	protected String decomposeDesc;//分解信息
	public static Logger getLogger() {
		return logger;
	}

	public abstract void init(Object initData);
	
	protected Object initData = null ;
	
	/**
	 * 是否职业匹配
	 * @param careerId
	 * @return
	 */
	public boolean isCareerMatch(int careerId){
		return this.career < 0 || this.career == careerId ;
	}
	
	/**
	 * 加载完后初始化
	 * @param initData
	 * @date 2014-11-4 下午04:58:08
	 */
	public void setInitData(Object initData) {
		this.initData = initData;
	}
	
	public Object getInitData() {
		return initData;
	}
	
	public boolean isTimeOver(){
		return false;
	}
	
	/**
	 * 下线是否消失
	 * 
	 * @return
	 */
	public boolean hasOfflineDie() {
		return this.offlineDie == 1;
	}

	/**
	 * 可否回收
	 * 
	 * @return
	 */
	public boolean hasRecycling() {
		return this.recycling == 1 ;
	}

	/**
	 * 判断是否可以丢弃
	 * 
	 * @return
	 */
	public boolean hasDiscard() {
		if (discard == 1) {
			return true;
		}
		if (discard == 0) {
			return false;
		}
		return false;
		// throw new ConfigDataException();
	}

	/**
	 * 判断是否能叠放
	 * 
	 * @return
	 */
	public boolean hasOverlap() {
		if (overlapCount <= 0) {
			return false;
		}
		if (overlapCount == 1) {
			return false;
		}
		return true;
	}

	

	/**
	 * 判断是否能拆分
	 * 
	 * @return
	 */
	public boolean isCanSplit() {
		return overlapCount >= 1;
	}
	/**
	 * 是否为装备
	 * @return
	 */
	public boolean isEquipment() {
		return goodsType == GoodsType.GoodsEquHuman.getType();
	}
	
	
	/**判断是否达到叠放上限**/

	public boolean isMaxOverlapCount(int currOverlapCount) {
		return currOverlapCount >= this.overlapCount;
	}
	
	
	/**
	 * 获得物品属性列表
	 * @return 属性列表 list
	 * 20110726
	 */
	public abstract List<AttriItem> getAttriItemList();

	
	//2012-4-1 Wang.K add
	/**
	 * 创建物品实例 
	 * @param roleId
	 * @param createNum 物品数目
	 * @return
	 */
	public List<RoleGoods> createRoleGoods(String roleId, int createNum){
		
		List<RoleGoods> list = new ArrayList<RoleGoods>();
		if(Util.isEmpty(roleId) || createNum <= 0 || this.getOverlapCount() <= 0){
			return list;
		}
		
		int count = createNum / this.getOverlapCount();
		for(int i = 0; i < count; i++){
			RoleGoods roleGoods = this.createSingleRoleGoods(roleId, this.getOverlapCount());
			if(roleGoods != null){
				list.add(roleGoods);
			}
		}
		
		int remainder = createNum % this.getOverlapCount();
		if(remainder>0){
			RoleGoods roleGoods = this.createSingleRoleGoods(roleId, remainder);
			if(roleGoods != null){
				list.add(roleGoods);
			}
		}
		return list;
		
	}
	
	//2012-4-1 Wang.K add
	/**
	 * 创建物品实例 
	 * @param roleId
	 * @param overlapCount 叠放数
	 * @return
	 */
	public RoleGoods createSingleRoleGoods(String roleId, int overlapCount){
		
		String instanceId = GameContext.getGoodsApp().newGoodsInstanceId();
		if(Util.isEmpty(roleId) || instanceId == null || overlapCount <= 0){
			return null;
		}
		
		if(overlapCount > this.getOverlapCount()){
			return null;
		}
		 
		RoleGoods roleGoods = new RoleGoods();
		roleGoods.setId(instanceId);
		roleGoods.setRoleId(roleId);
		roleGoods.setGoodsId(this.getId());
		roleGoods.setBind(bindType);
		roleGoods.setCurrOverlapCount((short)overlapCount);
		roleGoods.setStorageType(StorageType.bag.getType());
		if(this.isForever()){
			return roleGoods;
		}
		if(activateType == 1){
			roleGoods.setExpiredTime(DateUtil.add(new Date(),Calendar.MINUTE, deadline));
		}
		roleGoods.setDeadline(deadline);
		
		return roleGoods;
		
	}
	/**永久物品*/
	public boolean isForever(){
		return deadline == 0;
	}
	
	//2012-4-1 Wang.K add
	/** 物品基本信息 */ 
	public abstract GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods);
	
	
	//2012-4-1 Wang.K add
	/** 封装公用的基本属性 */
	protected GoodsBaseItem setGoodsBaseItem(RoleGoods roleGoods, GoodsBaseItem goodsBaseItem){
		goodsBaseItem.setImageId(imageId);
		goodsBaseItem.setBindType((byte) bindType);
		//goodsBaseItem.setCallback((byte)recycling);
		goodsBaseItem.setName(name);
		goodsBaseItem.setQualityType((byte) qualityType);
		goodsBaseItem.setPriceSilver(recyclePrice);
		goodsBaseItem.setType((byte) goodsType);
		goodsBaseItem.setGoodsId(id);
		goodsBaseItem.setLevel((byte)this.level);
		//goodsBaseItem.setEffectId((byte)this.effectId);
		goodsBaseItem.setDesc(this.getDesc());
		//获得分解信息，如果分解信息不为空则置不可回收，且回收价格为0
		goodsBaseItem.setDecomposeDesc(this.decomposeDesc);//分解信息
		if(roleGoods != null){
			goodsBaseItem.setBindType((byte)roleGoods.getBind());
		}
		return goodsBaseItem;
	}
	
	public BindingType getBindingType() {
		return BindingType.get(this.bindType);
	}
	
	
	
	
/******************************   getter/setter   *******************************************/
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public short getImageId() {
		return imageId;
	}

	public void setImageId(short imageId) {
		this.imageId = imageId;
	}

	public byte getBindType() {
		return bindType;
	}

	public void setBindType(byte bindType) {
		this.bindType = bindType;
	}

	public int getOverlapCount() {
		if(overlapCount <=0 ){
			overlapCount = 1 ;
		}
		return overlapCount;
	}

	public void setOverlapCount(int overlapCount) {
		this.overlapCount = overlapCount;
	}


	public String getDesc() {
		return desc;
	}
	
	public String getDecomposeDesc(){
		return decomposeDesc;
	}
	
	public void setDecomposeDesc(String ds){
		decomposeDesc = ds;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(int goodsType) {
		this.goodsType = goodsType;
	}

	public int getRecyclePrice() {
		return recyclePrice;
	}

	public void setRecyclePrice(int recyclePrice) {
		this.recyclePrice = recyclePrice;
	}


	public String toString() {
		StringBuffer buffer = new StringBuffer(GoodsBase.class.getSimpleName());
		buffer.append("[");
		buffer.append("id=").append(id).append(" ");
		buffer.append("mame=").append(this.name).append(" ");
		buffer.append("mame=").append(this.desc).append(" ");
		buffer.append("bindType=").append(this.bindType).append(" ");
		buffer.append("goodsType=").append(this.goodsType).append(" ");
		buffer.append("imageId=").append(this.imageId).append(" ");
		buffer.append("isDiscard=").append(this.discard).append(" ");
		buffer.append("isOfflineDie=").append(this.offlineDie).append(" ");
		buffer.append("isOnly=").append(this.only).append(" ");
		buffer.append("overlapCount=").append(this.overlapCount).append(" ");
		buffer.append("recyclePrice=").append(this.recyclePrice).append(" ");
		buffer.append("]");
		return buffer.toString();
	}


	public byte getQualityType() {
		return qualityType;
	}

	public void setQualityType(byte qualityType) {
		this.qualityType = qualityType;
	}

	public int getPrintlog() {
		return printlog;
	}

	public void setPrintlog(int printlog) {
		this.printlog = printlog;
	}

	public int getResId() {
		return resId;
	}
	public void setResId(int resId) {
		this.resId = resId;
	}
	public byte getSecondType() {
		return secondType;
	}
	public void setSecondType(byte secondType) {
		this.secondType = secondType;
	}
	
	public int getLvLimit() {
		if(this.lvLimit <=0){
			this.lvLimit = 1 ;
		}
		return lvLimit;
	}
	
	public void setLvLimit(int lvLimit) {
		this.lvLimit = lvLimit;
	}
	public int getOnly() {
		return only;
	}

	public void setOnly(int only) {
		this.only = only;
	}

	public int getRecycling() {
		return recycling;
	}

	public void setRecycling(int recycling) {
		this.recycling = recycling;
	}

	public int getOfflineDie() {
		return offlineDie;
	}

	public void setOfflineDie(int offlineDie) {
		this.offlineDie = offlineDie;
	}

	public int getDiscard() {
		return discard;
	}

	public void setDiscard(int discard) {
		this.discard = discard;
	}

	public byte getCareer() {
		return career;
	}
	public void setCareer(byte career) {
		this.career = career;
	}

	public byte getActivateType() {
		return activateType;
	}

	public void setActivateType(byte activateType) {
		this.activateType = activateType;
	}

	public int getDeadline() {
		return deadline;
	}

	public void setDeadline(int deadline) {
		this.deadline = deadline;
	}

	public byte getExpireType() {
		return expireType;
	}

	public void setExpireType(byte expireType) {
		this.expireType = expireType;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getEffectId() {
		return effectId;
	}

	public void setEffectId(int effectId) {
		this.effectId = effectId;
	}

	public GoodsLiteItem getGoodsLiteItem(){
		return getGoodsLiteItem(null);
	}
	
	public GoodsLiteNamedItem getGoodsLiteNamedItem(){
		return getGoodsLiteNamedItem(null);
	}
	
	public GoodsLiteNamedExItem getGoodsLiteNamedExItem(){
		return getGoodsLiteNamedExItem(null);
	}
	
	public GoodsLiteItem getGoodsLiteItem(RoleGoods roleGoods){
		GoodsLiteItem item = new GoodsLiteItem();
		this.setGoodsLiteItem(item,roleGoods);
		return item ;
	}
	
	public GoodsLiteNamedExItem getGoodsLiteNamedExItem(RoleGoods roleGoods){
		GoodsLiteNamedExItem item = new GoodsLiteNamedExItem();
		this.setGoodsLiteItem(item,roleGoods);
		item.setGoodsName(this.getName());
		item.setResId((short)this.getResId());
		return item ;
	}
	
	public GoodsLiteNamedItem getGoodsLiteNamedItem(RoleGoods roleGoods){
		GoodsLiteNamedItem item = new GoodsLiteNamedItem();
		this.setGoodsLiteItem(item,roleGoods);
		item.setGoodsName(this.getName());
		return item ;
	}
	
	protected void setGoodsLiteItem(GoodsLiteItem item,RoleGoods roleGoods){
		item.setGoodsId(this.id);
		item.setGoodsType((byte)this.getGoodsType());
		item.setGoodsImageId(this.imageId);
		item.setGoodsLevel((byte)this.level);
		item.setQualityType(this.qualityType);
		item.setStar(this.getStar());
		item.setBindType(this.bindType);
		item.setSecondType(this.secondType);
		if(null != roleGoods){
			item.setQualityType(roleGoods.getQuality());
			item.setStar(roleGoods.getStar());
			item.setBindType(roleGoods.getBind());
		}
	}
	
	/**
	 * 获取带颜色的物品名称
	 * 颜色就是物品品质颜色
	 * @return
	 */
	public String getColorName(){
		return ("[\\C]" + QualityType.get(this.qualityType).getColor() + "[C]" + this.name);
	}

	public byte getStar() {
		return star;
	}

	public void setStar(byte star) {
		this.star = star;
	}
	
}
