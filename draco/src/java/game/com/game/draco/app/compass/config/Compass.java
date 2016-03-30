package com.game.draco.app.compass.config;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.DateTimeBean;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.*;
import sacred.alliance.magic.vo.RoleInstance;

import java.util.*;

public @Data class Compass {
	private static final String DateFormat = "yyyy-MM-dd";

	private short taobaoId;//ID
	private byte taobaoType ;
    private short goodsImageId ;
	private String name;//淘宝名称
	private int minLevel;//需要角色等级
	private int goodsId;//消耗道具ID
	private byte consumeAttriType;
	private int consume1 ;
    private short consumeGoods1;
	private int consume10 ;
    private short consumeGoods10;
	private int consume50 ;
    private short consumeGoods50;
	private String startDateAbs;//绝对开启日期
	private String endDateAbs;//绝对结束日期
	private int startDateRel;//相对开启日期
	private int endDateRel;//相对结束日期
	private String desc;//活动说明
	private int award1;//奖励1的ID
	private int num1;//奖励1的数量
	private int odds1;//奖励1的概率
	private byte bind1 = BindingType.template.getType();//奖励1绑定类型
	private String broadcastInfo1;//广播消息（有则发）
	private int award2;
	private int num2;
	private int odds2;
	private byte bind2 = BindingType.template.getType();
	private String broadcastInfo2;
	private int award3;
	private int num3;
	private int odds3;
	private byte bind3 = BindingType.template.getType();
	private String broadcastInfo3;
	private int award4;
	private int num4;
	private int odds4;
	private byte bind4 = BindingType.template.getType();
	private String broadcastInfo4;	
	private int award5;
	private int num5;
	private int odds5;
	private byte bind5 = BindingType.template.getType();
	private String broadcastInfo5;
	private int award6;
	private int num6;
	private int odds6;
	private byte bind6 = BindingType.template.getType();
	private String broadcastInfo6;
	private int award7;
	private int num7;
	private int odds7;
	private byte bind7 = BindingType.template.getType();
	private String broadcastInfo7;
	private int award8;
	private int num8;
	private int odds8;
	private byte bind8 = BindingType.template.getType();
	private String broadcastInfo8;
	private int award9;
	private int num9;
	private int odds9;
	private byte bind9 = BindingType.template.getType();
	private String broadcastInfo9;
	private int award10;
	private int num10;
	private int odds10;
	private byte bind10 = BindingType.template.getType();
	private String broadcastInfo10;
	private int award11;
	private int num11;
	private int odds11;
	private byte bind11 = BindingType.template.getType();
	private String broadcastInfo11;
	private int award12;
	private int num12;
	private int odds12;
	private byte bind12 = BindingType.template.getType();
	private String broadcastInfo12;
	private int award13;
	private int num13;
	private int odds13;
	private byte bind13 = BindingType.template.getType();
	private String broadcastInfo13;
	private int award14;
	private int num14;
	private int odds14;
	private byte bind14 = BindingType.template.getType();
	private String broadcastInfo14;
	private String extraId = "" ;
	
	
	//开启时间（启动时构建）
	private Date startDate;
	private Date endDate;
	//奖励列表
	private List<CompassAward> awardList = new ArrayList<CompassAward>();
	private Map<Integer,Integer> placeMap = new HashMap<Integer,Integer>();//抽奖map【KEY:位置 VALUE:概率】
	//额外的奖励列表
	private List<Short> extraIdList = new ArrayList<Short>();

	private void initExtraId(){
		try{
			if(Util.isEmpty(extraId)){
				return;
			}
			String[] array = extraId.split(Cat.comma);
			if(null == array || 0 == array.length){
				return ;
			}
			for(String value : array){
				short _extraId = Short.valueOf(value);
				extraIdList.add(_extraId);
			}
		}catch(Exception e){
			this.checkFail("the extra config error ! taobaoId=" + taobaoId);
		}
	}

	private void checkConsume(String info ,int value,short goodsNum){
		if(value <= 0 && goodsNum <=0){
            this.checkFail(info + " consumeValue=" + value + " goodsNum=" + goodsNum + ", it is error!");
		}

	}

	/**
	 * 加载时初始化奖励信息
	 */
	public void init(){
		String info = "load compass error: id=" + this.taobaoId + ".";
		//抽奖消耗金钱是否配置
		this.checkConsume(info,this.consume1,this.consumeGoods1);
		this.checkConsume(info,this.consume10,this.consumeGoods10);
		this.checkConsume(info,this.consume50,this.consumeGoods50);

		AttributeType consumeType = AttributeType.get(this.consumeAttriType);
		if(null == consumeType){
			this.checkFail(info + " consumeAttriType:" + consumeAttriType + " is error");
		}
		//支持道具抽奖的，验证道具是否存在
		if(this.goodsId > 0){
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(this.goodsId);
			if(null == gb){
				this.checkFail(info + " goodsId=" + this.goodsId + ",this goods is not exist!");
			}
		}
		//转换时间格式
		DateTimeBean bean = DateConverter.getDateTimeBean(this.startDateRel, this.endDateRel, this.startDateAbs, this.endDateAbs, DateFormat);
		if(null == bean){
			this.checkFail(info + " The startDateRel/endDateRel/startDateAbs/endDateAbs is error.");
		}
		this.startDate = bean.getStartDate();
		this.endDate = bean.getEndDate();
		if(null == this.startDate || null == this.endDate){
			this.checkFail(info + " Please config the start time or the end time.");
		}
		//给时间字符串赋值
		this.startDateAbs = DateUtil.date2Str(this.startDate, DateFormat);
		this.endDateAbs = DateUtil.date2Str(this.endDate, DateFormat);
		
		//初始化奖励对象
		this.buildAwardList(this.award1, this.num1, this.odds1, this.bind1, this.broadcastInfo1);
		this.buildAwardList(this.award2, this.num2, this.odds2, this.bind2, this.broadcastInfo2);
		this.buildAwardList(this.award3, this.num3, this.odds3, this.bind3, this.broadcastInfo3);
		this.buildAwardList(this.award4, this.num4, this.odds4, this.bind4, this.broadcastInfo4);
		this.buildAwardList(this.award5, this.num5, this.odds5, this.bind5, this.broadcastInfo5);
		this.buildAwardList(this.award6, this.num6, this.odds6, this.bind6, this.broadcastInfo6);
		this.buildAwardList(this.award7, this.num7, this.odds7, this.bind7, this.broadcastInfo7);
		this.buildAwardList(this.award8, this.num8, this.odds8, this.bind8, this.broadcastInfo8);
		this.buildAwardList(this.award9, this.num9, this.odds9, this.bind9, this.broadcastInfo9);
		this.buildAwardList(this.award10, this.num10, this.odds10, this.bind10, this.broadcastInfo10);
		this.buildAwardList(this.award11, this.num11, this.odds11, this.bind11, this.broadcastInfo11);
		this.buildAwardList(this.award12, this.num12, this.odds12, this.bind12, this.broadcastInfo12);
		this.buildAwardList(this.award13, this.num13, this.odds13, this.bind13, this.broadcastInfo13);
		this.buildAwardList(this.award14, this.num14, this.odds14, this.bind14, this.broadcastInfo14);
		//构建奖励的权重Map
		this.buildPlaceMap();
		//检测物品配置
		this.checkAwardGoods();
		//判断额外奖励是否存在
		//1
		this.initExtraId();
		//2
		for(short id : extraIdList){
			TaobaoExtra extra = GameContext.getCompassApp().getTaobaoExtra(id);
			if(null == extra){
				this.checkFail(info + " the extra not exist ! extraId=" + id);
			}
		}
		
	}
	
	/**
	 * 构建奖励列表
	 * @param awardId
	 * @param num
	 * @param odds
	 * @param bind
	 * @param broadcastInfo
	 */
	private void buildAwardList(int awardId, int num, int odds, byte bind, String broadcastInfo){
		String info = "load compass error: id=" + this.taobaoId + ".";
		if(awardId <= 0 || num <= 0 || odds <= 0){
			this.checkFail(info + "awardId=" + awardId + ",awardId or num or odds config error.");
		}
		this.awardList.add(new CompassAward(awardId, num, odds, bind, this.getBroadcastInfo(broadcastInfo, awardId)));
	}
	
	/**
	 * 构建广播消息（替换通配符）
	 * @param broadcastInfo
	 * @param awardId
	 * @return
	 */
	private String getBroadcastInfo(String broadcastInfo, int awardId){
		if(Util.isEmpty(broadcastInfo)){
			return "";
		}
		return broadcastInfo.replace(Wildcard.Compass_Name, this.name).replace(Wildcard.GoodsName,
				Wildcard.getChatGoodsContent(awardId, ChannelType.Publicize_Personal));
	}
	
	/**
	 * 初始化抽奖map
	 */
	private void buildPlaceMap(){
		this.placeMap.put(1, this.odds1);
		this.placeMap.put(2, this.odds2);
		this.placeMap.put(3, this.odds3);
		this.placeMap.put(4, this.odds4);
		this.placeMap.put(5, this.odds5);
		this.placeMap.put(6, this.odds6);
		this.placeMap.put(7, this.odds7);
		this.placeMap.put(8, this.odds8);
		this.placeMap.put(9, this.odds9);
		this.placeMap.put(10, this.odds10);
		this.placeMap.put(11, this.odds11);
		this.placeMap.put(12, this.odds12);
		this.placeMap.put(13, this.odds13);
		this.placeMap.put(14, this.odds14);
	}
	
	/**
	 * 可以配置相同的物品模板，但绑定类型必须相同
	 */
	private void checkAwardGoods(){
		Map<Integer, Integer> awardMap = new HashMap<Integer, Integer>();//验证物品集合
		for(CompassAward award : this.awardList){
			int goodsId = award.getAward();
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(null == goodsBase){
				this.checkFail("load compass error: goodsId=" + goodsId + ".the goods is not exist.");
			}
			int bindType = award.getBindType();
			if(awardMap.containsKey(goodsId) && awardMap.get(goodsId) != bindType){
				this.checkFail("load compass error: goodsId=" + goodsId + ".the same goods have different bind.");
			}
			awardMap.put(goodsId, bindType);
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	/**
	 * 是否满足等级要求
	 * @param role
	 * @return
	 */
	public boolean isSuitLevel(RoleInstance role){
		if(null == role){
			return false;
		}
		return role.getLevel() >= this.minLevel;
	}
	
	/**
	 * 活动时间是否开启
	 */
	public boolean isTimeOpen(){
		Date now = new Date();
		return now.after(this.startDate) && now.before(this.endDate);
	}
	
	/**
	 * 上古法阵抽奖
	 * @return
	 */
	public CompassRoleAward getAward(){
		Integer key = Util.getWeightCalct(this.placeMap);
		if(null == key){
			//重置概率
			this.resetOdds();
			key = Util.getWeightCalct(this.placeMap);//重新抽奖
		}
		int index = key - 1;
		CompassAward award = this.awardList.get(index);
		if(null == award){
			return null;
		}
		//减权重
		int odds = award.getOdds() - 1;
		if(odds < 0){
			odds = 0;
		}
		award.setOdds(odds);
		this.placeMap.put(key, odds);
		//抽中的奖励
		CompassRoleAward roleAward = new CompassRoleAward();
		roleAward.setId(this.taobaoId);
		roleAward.setPlace((byte) index);
		roleAward.setGoodsId(award.getAward());
		roleAward.setGoodsNum(award.getNum());
		roleAward.setBindType(award.getBindType());
		roleAward.setBroadcastInfo(award.getBroadcastInfo());
		return roleAward;
	}
	
	/**
	 * 重置概率
	 */
	private void resetOdds(){
		this.awardList.get(0).setOdds(this.odds1);
		this.awardList.get(1).setOdds(this.odds2);
		this.awardList.get(2).setOdds(this.odds3);
		this.awardList.get(3).setOdds(this.odds4);
		this.awardList.get(4).setOdds(this.odds5);
		this.awardList.get(5).setOdds(this.odds6);
		this.awardList.get(6).setOdds(this.odds7);
		this.awardList.get(7).setOdds(this.odds8);
		this.awardList.get(8).setOdds(this.odds9);
		this.awardList.get(9).setOdds(this.odds10);
		this.awardList.get(10).setOdds(this.odds11);
		this.awardList.get(11).setOdds(this.odds12);
		this.awardList.get(12).setOdds(this.odds13);
		this.awardList.get(13).setOdds(this.odds14);
		this.buildPlaceMap();
	}
	
}
