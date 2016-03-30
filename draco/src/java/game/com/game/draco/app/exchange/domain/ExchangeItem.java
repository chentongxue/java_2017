package com.game.draco.app.exchange.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.FrequencyType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.condition.Condition;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteNamedItem;

/**
 * npc_exchange.xls -> item
 */
public
@Data
class ExchangeItem implements KeySupport<Integer> {
    private static final int CONSUME_NUM = 3;
    private int menuId;
    private int id;
    private String name;
    private String startDate;
    private String endDate;
    private byte frequencyType;
    private byte frequencyValue;
    private short enterResetCopyId;
    private String conditionIds;

    private String consumeType;        //<=3 属性消耗类型
    private String consumeValue;    //<=3

    private String consumeGoodIds;
    private String consumeGoodCount;
    private int gainGoodsId;    //<=3
    private int gainGoodsCount;    //<=3
    private byte bindType;
    //拿conditionIds来初始化列表
    private List<Condition> conditionList = new ArrayList<Condition>();
    //消耗的物品<goodId, goodCount>
    private Map<Integer, Integer> consumeGoods = new LinkedHashMap<Integer, Integer>();
    //获得的物品
    private GoodsOperateBean gainGoods;
    private List<GoodsOperateBean> gainGoodsList = new ArrayList<GoodsOperateBean>();//只放入一个
    private Date start;
    private Date end;
    private boolean timeOpen = false;//是否有时间控制
    private String broadcast;
    private int firstConsumeGoodsId = 0;//第一个消耗品的物品ID，喊话用
    private Map<Byte, Integer> consumeAttrs = new LinkedHashMap<Byte, Integer>();

    private List<AttriTypeValueItem> consumeAttrItems = new ArrayList<AttriTypeValueItem>();//属性消耗
    private List<GoodsLiteNamedItem> consumeGoodsItems = new ArrayList<GoodsLiteNamedItem>();//物品消耗

    public List<GoodsLiteNamedItem> getConsumeGoodsItems() {
        return consumeGoodsItems;
    }

    public List<AttriTypeValueItem> getConsumeAttrItems() {
        return consumeAttrItems;
    }
    public boolean isDiamondConusume(){
    	if(!Util.isEmpty(consumeGoodsItems)){
    		return false;
    	}
    	if(Util.isEmpty(consumeAttrItems)){
    		return false;
    	}
    	if(consumeAttrItems.size() > 1){
    		return false;
    	}
    	return consumeAttrItems.get(0).getAttriType() == AttributeType.goldMoney.getType();
    }
    
    /**
     * 初始化兑换的实例
     *
     * @return false:兑换过期,else true
     */
    public boolean init() {
        String fileName = XlsSheetNameType.exchange_item.getXlsName();
        String sheetName = XlsSheetNameType.exchange_item.getSheetName();
        //日期
        if (timeOpen) {
            start = DateUtil.getDateZero(DateUtil.strToDate(startDate, DateUtil.format_yyyy_MM_dd));
            end = DateUtil.getDateEndTime(DateUtil.strToDate(endDate, DateUtil.format_yyyy_MM_dd));
            if (isOutDate()) {
                return false;
            }
        }
        //消耗物品
        String[] tGoodIds = Util.splitString(consumeGoodIds);
        String[] tGoodCount = Util.splitString(consumeGoodCount);
        if (tGoodIds.length == tGoodCount.length) {
            int goodsId;
            for (int i = 0; i < tGoodIds.length; i++) {
                goodsId = Integer.valueOf(tGoodIds[i]);
                if (goodsId <= 0) {
                    continue;
                }
                if (firstConsumeGoodsId == 0) {
                    firstConsumeGoodsId = goodsId;
                }
                GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
                if (null == gb) {
                    Log4jManager.checkFail();
                    Log4jManager.CHECK.error("sourceFile = " + fileName + ", sheetName = " + sheetName + ", exchange id= " + this.id + "  consume goodId=" + tGoodIds[i] + " is not exsit!");
                    continue;
                }
                consumeGoods.put(Integer.valueOf(tGoodIds[i]), Integer.valueOf(tGoodCount[i]));
                GoodsLiteNamedItem consumeGoodItem = gb.getGoodsLiteNamedItem();
                // 设置数量
                consumeGoodItem.setNum(Short.valueOf(tGoodCount[i]));
                consumeGoodsItems.add(consumeGoodItem);
            }
        } else {
            Log4jManager.checkFail();
            Log4jManager.CHECK.error("sourceFile = " + fileName + ", sheetName = " + sheetName + ", exchange id= " + this.id + " consume goodId num is not equal goodCount num");
        }
        //获得物品
        if (null == GameContext.getGoodsApp().getGoodsBase(gainGoodsId)) {
            Log4jManager.checkFail();
            Log4jManager.CHECK.error("sourceFile = " + fileName + ", sheetName = " + sheetName + ", exchange id=" + this.id + " gain goodId=" + gainGoodsId + " is not exsit!");
        }
        if (gainGoodsId <= 0 || gainGoodsCount <= 0) {
            Log4jManager.checkFail();
            Log4jManager.CHECK.error("sourceFile = " + fileName + ", sheetName = " + sheetName + ", exchange id=" + this.id + " gain goodId num is not equal goodCount num or gain goodId num is not equal bindType num");
        }
        gainGoods = new GoodsOperateBean(gainGoodsId, gainGoodsCount, bindType);
        gainGoodsList.add(gainGoods);

        //检查消耗
        String[] consumeTypes = Util.splitString(consumeType);
        String[] consumeValues = Util.splitString(consumeValue);
        if (consumeTypes.length + tGoodIds.length > CONSUME_NUM) {
            Log4jManager.checkFail();
            Log4jManager.CHECK.error("sourceFile = " + fileName + ", sheetName = " + sheetName + ", exchange id=" + this.id
                    + "consumptions exceed the limit number :3");
        }
        if (consumeTypes.length == consumeValues.length) {
            for (int i = 0; i < consumeTypes.length; i++) {
                byte attr = Byte.valueOf(consumeTypes[i]);
                int val = Integer.valueOf(consumeValues[i]);
                consumeAttrs.put(attr, val);
                AttriTypeValueItem it = new AttriTypeValueItem();
                it.setAttriType(attr);
                it.setAttriValue(Integer.valueOf(consumeValues[i]));
                consumeAttrItems.add(it);
            }
        } else {
            Log4jManager.checkFail();
            Log4jManager.CHECK.error("sourceFile = " + fileName + ", sheetName = " + sheetName + ", exchange id=" + this.id
                    + "consumeTypes'size not eqauls consumeValues' size");
        }
        return true;
    }

    @Override
    public Integer getKey() {
        return this.id;
    }

    /**
     * 判断是否能兑换,判断项:兑换日期，每天兑换时间段，兑换次数，兑换条件，消耗物品
     * @return
     */
    public Result isMeet(RoleInstance role, short num) {
    	Result result = new Result();
        if (!isInDate()) {
        	result.setInfo(Status.Exchange_Not_InDate.getTips());
            return result;
        }
        if (!isHasTimes(role)) {
        	result.setInfo(Status.Exchange_Frequency_Not_Enough.getTips());
            return result;
        }
        if (!isMeetConditions(role)) {
        	result.setInfo(Status.Exchange_Condition_Not_Meet.getTips());
            return result;
        }
        if (!isHasConsumeGoods(role, num)) {
        	result.setInfo(Status.Exchange_ConsumeGood_Not_Enough.getTips());
            return result;
        }
        return this.getHasEnoughNumStatus(role, num);
    }

    /**
     * 是否在兑换日期内
     *
     * @return
     */
    public boolean isInDate() {
        if (start == null && end == null) {
            return true;
        }
        return DateUtil.dateInRegion(new Date(), start, end);
    }

    /**
     * 是否满足兑换次数条件
     *
     * @return
     */
    public boolean isHasTimes(RoleInstance role) {
        if (frequencyType == FrequencyType.FREQUENCY_TYPE_NONE.getType()) {
            return true;
        }
        ExchangeDbInfo exchangeDbInfo = role.getExchangeDbInfo().get(id);
        if (null == exchangeDbInfo) {
            return true;
        }
        //重置
        this.resetExchange(exchangeDbInfo);
        //比较次数
        return exchangeDbInfo.getTimes() < frequencyValue;
        //比较时间和次数
    }

    public void resetExchange(ExchangeDbInfo info) {
        if (null == info) {
            return;
        }
        FrequencyType ft = FrequencyType.get(this.frequencyType);
        if (null == ft || FrequencyType.FREQUENCY_TYPE_NONE == ft) {
            //没有限制
            return;
        }
        Date now = new Date();
        if (ft.isInCycle(now, info.getLastExTime())) {
            //在同一周期
            return;
        }
        //重置次数
        info.setLastExTime(now);
        info.setTimes(0);
    }

    /**
     * @param role
     * @param num  兑换的次数
     * @date 2014-12-31 下午09:31:30
     */
    public void updateDbInfo(RoleInstance role, short num) {
        FrequencyType ft = FrequencyType.get(this.frequencyType);
        if (null == ft || FrequencyType.FREQUENCY_TYPE_NONE == ft) {
            //没有限制
            return;
        }
        ExchangeDbInfo exchangeDbInfo = role.getExchangeDbInfo().get(id);
        if (null == exchangeDbInfo) {
            exchangeDbInfo = new ExchangeDbInfo(id, role.getRoleId(), num, new Date());
            exchangeDbInfo.setExistRecord(false);
            role.getExchangeDbInfo().put(id, exchangeDbInfo);
            return;
        }
        this.resetExchange(exchangeDbInfo);
        //次数+num
        exchangeDbInfo.setTimes(exchangeDbInfo.getTimes() + num);
    }


    public int getFrequencyInfo(RoleInstance role) {
        ExchangeDbInfo exchangeDbInfo = role.getExchangeDbInfo().get(id);
        if (null != exchangeDbInfo) {
            return exchangeDbInfo.getTimes();
        }
        return 0;
    }


    /**
     * 是否满足所有条件
     */
    public boolean isMeetConditions(RoleInstance role) {
        if (null == conditionList || conditionList.size() == 0) {
            return true;
        }
        for (Condition exchangeConditon : conditionList) {
            if (null == exchangeConditon) {
                continue;
            }
            if (!exchangeConditon.isMeet(role)) {
                return false;
            }
        }
        return true;
    }

    public boolean isMeetConditionsAndDis(RoleInstance role) {
        //如果是兑换次数是永久的那种，如果已经兑换过了就不显示
        if (frequencyType == FrequencyType.FREQUENCY_TYPE_FOREVER.getType()) {
            ExchangeDbInfo exchangeDbInfo = role.getExchangeDbInfo().get(id);
            if (null != exchangeDbInfo && exchangeDbInfo.getTimes() >= frequencyValue) {
                return false;
            }
        }
        if (Util.isEmpty(conditionList)) {
            return true;
        }
        //条件不满足的话兑换项不显示
        for (Condition exchangeConditon : conditionList) {
            if (!exchangeConditon.isMeet(role) && !exchangeConditon.isDisplay()) {
                return false;
            }
        }

        return true;
    }

    /**
     * 消耗的物品是否满足
     *
     * @param role
     * @return
     */
    public boolean isHasConsumeGoods(RoleInstance role, short num) {
        if (null == consumeGoods || consumeGoods.size() == 0)
            return true;
        for (Integer key : consumeGoods.keySet()) {
            if (role.getRoleBackpack().countByGoodsId(key) < consumeGoods.get(key) * num) {
                return false;
            }
        }
        return true;
    }

    /**
     * 消耗的属性是否满足
     * @param role
     * @param num
     * @return
     * @date 2015-1-4 下午04:07:37
     */
    public Result getHasEnoughNumStatus(RoleInstance role, short num) {
    	Result result = new Result();
        if (!Util.isEmpty(consumeAttrs)) {
            for (Map.Entry<Byte, Integer> entry : consumeAttrs.entrySet()) {
                Byte attr = entry.getKey();
                Integer value = entry.getValue();
                AttributeType type = AttributeType.get(attr);
                if (null != type) {
                    int roleHasNum = role.get(type);
                    if (roleHasNum < value * num) {
                    	result.setInfo(GameContext.getI18n().messageFormat(TextId.Exchange_Attribute_Not_Enough, type.getName()));
                        return result;
                    }
                }
            }
        }
        result.setInfo(GameContext.getI18n().getText(TextId.Exchange_Can_Exchange));
    	result.success();
        return result;
    }

    /**
     * 获取不足的属性
     * @param role
     * @param num
     * @return
     */
    public AttributeType isHasEnoughNum(RoleInstance role, short num) {
        if (Util.isEmpty(consumeAttrs)) {
            return null;
        }
        for (Map.Entry<Byte, Integer> entry : consumeAttrs.entrySet()) {
            Byte attr = entry.getKey();
            Integer value = entry.getValue();
            AttributeType type = AttributeType.get(attr);
            if (null != type) {
                int roleHasNum = role.get(type);
                if (roleHasNum < value * num) {
                    return type;
                }
            }
        }
        return null;
    }

    public void consumeAttribute(RoleInstance role, short num) {
        if (Util.isEmpty(consumeAttrs)) {
            return;
        }
        for (Map.Entry<Byte, Integer> entry : consumeAttrs.entrySet()) {
            Byte attr = entry.getKey();
            Integer value = entry.getValue();
            AttributeType type = AttributeType.get(attr);
            if (null != type) {
                int roleHasNum = role.get(type);
                if (roleHasNum >= value * num) {
                	if (type.isMoney()) {
                		GameContext.getUserAttributeApp().changeRoleMoney(role, type, OperatorType.Decrease, value * num, OutputConsumeType.goods_exchange_consume);
                	} else {
                		GameContext.getUserAttributeApp().changeAttribute(role, type, OperatorType.Decrease, value * num, OutputConsumeType.goods_exchange_consume);
                	}
                }
            }
        }
    }

    /**
     * 是否含有属性消耗
     * @return
     * @date 2015-1-4 下午04:50:34
     */
    public boolean isHasAttributeConsumption() {
        return !Util.isEmpty(consumeAttrs);
    }

    /**
     * 活动是否过期
     */
    public boolean isOutDate() {
        if (end == null) {
            return false;
        }
        return System.currentTimeMillis() >= end.getTime();
    }

}
