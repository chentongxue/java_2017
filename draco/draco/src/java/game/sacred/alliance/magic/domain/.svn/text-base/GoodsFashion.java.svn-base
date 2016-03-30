package sacred.alliance.magic.domain;


/**
 * 时装模板类
 * @author Ljm
 *
 */
public abstract class GoodsFashion extends GoodsBase  {/*
	
	private int conditionType;
	private float expBonus;
	private float moneyBonus;
	private float prestigeBonus;
	private int sex;//-1:通用0：男1：女
	
	//基础血量上限(护甲专用)
	private int baseMaxHP;//基本HP上限
	private int baseMaxMP;//基本MP上限
	
	//5个属性
	private byte attrType1;	//属性类型1
	private float  attrValue1;//属性值1
	private byte attrType2;	
	private float attrValue2;
	private byte attrType3;	
	private float  attrValue3;	
	private byte attrType4;	
	private float attrValue4;
	private byte attrType5;	
	private float  attrValue5;	

	
	private Map<Byte,Float> attriMap = Maps.newConcurrentMap();

	

	@Override
	public List<AttriItem> getAttriItemList() {
		List<AttriItem> attrList = new ArrayList<AttriItem>();
		for(Iterator<Byte> it = this.attriMap.keySet().iterator();it.hasNext();){
			byte key = it.next();
			float value = attriMap.get(key);
			attrList.add(new AttriItem(key,0,value/100f));
		}
		return attrList;
	}
	
	private void addAttri(byte attriType,float value){
		if(0 >= attriType || 0 >= value){
			return;
		}
		Float val = attriMap.get(attriType);
		if(null != val){
			value += val;
		}
		attriMap.put(attriType, value);
	}
	
	*//**
	 * 获得显示用属性
	 * @return 属性列表 <属性类型，属性值>
	 *//*
	public List<AttriShortTypeValueItem> getAttriItem(){
		List<AttriShortTypeValueItem> attriList = new ArrayList<AttriShortTypeValueItem>();
		for(Iterator<Byte> it = this.attriMap.keySet().iterator();it.hasNext();){
			AttriShortTypeValueItem item = new AttriShortTypeValueItem();
			byte key = it.next();
			int value = attriMap.get(key).intValue();
			item.setAttriType(Short.parseShort(String.valueOf(key)));
			item.setValue(value);
			attriList.add(item);
		}
		return attriList;
	}

	@Override
	public void init(Object initData) {
		this.addAttri(AttributeType.expAddRate.getType(), expBonus);
		this.addAttri(AttributeType.moneyAddRate.getType(), moneyBonus);
		this.addAttri(AttributeType.prestigeAddRate.getType(), prestigeBonus);
		
		this.addAttri(AttributeType.maxHP.getType(), baseMaxHP);
		this.addAttri(AttributeType.maxMP.getType(), baseMaxMP);
		this.addAttri(attrType1, attrValue1);
		this.addAttri(attrType2, attrValue2);
		this.addAttri(attrType3, attrValue3);
		this.addAttri(attrType4, attrValue4);
		this.addAttri(attrType5, attrValue5);
	}

	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods){
		GoodsBaseFashionItem it = new GoodsBaseFashionItem();
		try{
			this.setGoodsBaseItem(roleGoods, it);
			it.setAttriList(this.getAttriItem());// 属性列表
			it.setConditionType((byte) this.getConditionType());// 装备位置
			it.setDesc(this.getDesc());
			it.setDeadline(this.getDeadline());// 存在时间
			it.setSex(((Integer) this.getSex()).byteValue());
			it.setExpired((byte) (roleGoods.isExpired()?1:0));// 时装是否已过期
			it.setActivateType(this.getActivateType());
			it.setGroupId((short) this.getComposeGroupId());
			it.setPay(this.getPay());
			it.setDeadline(this.getDeadline());
			if(null != roleGoods){
				if(!Util.isEmpty(roleGoods.getExpiredTime())){
					it.setExpiredTime(DateUtil.getGoodsCalendarByTime(roleGoods.getExpiredTime().getTime()));
				}
				if(roleGoods.getDeadline()>0){
					it.setDeadline(roleGoods.getDeadline());
				}
			}
			it.setExpireType(this.getExpireType());
			it.setResId((short) this.getResId());
			it.setFashionname(this.getName());
		}catch(Exception e){
			logger.error("GoodsFashion",e);
		}
		return it;
	}
	
	*//** 封装物品详细信息Item *//*
	@Override
	public GoodsDetailItem getGoodsDetailItem(RoleGoods roleGoods){
		return null;
	}
*/}
