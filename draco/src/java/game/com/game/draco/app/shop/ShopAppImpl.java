package com.game.draco.app.shop;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.quickbuy.QuickCostHelper;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.shop.config.PanelInfoConfig;
import com.game.draco.app.shop.config.ShopGoodsConfig;
import com.game.draco.app.shop.domain.RoleShopDailyLimit;
import com.game.draco.message.item.ShopGoodsItem;
import com.game.draco.message.request.C2102_ShopBuyGoodsReqMessage;
import com.game.draco.message.response.C2101_ShopGoodsListRespMessage;
import com.game.draco.message.response.C2105_ShopEnterRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ShopAppImpl implements ShopApp {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    // 面板
    private final short SHOP_BUY_CMDID = new C2102_ShopBuyGoodsReqMessage().getCommandId();
    private Map<Byte, PanelInfoConfig> panelInfoConfigsMap = Maps.newHashMap();

    private Map<Integer, ShopGoodsConfig> allGoodsMap;// 全部商品

    private Map<Integer, ShopGoodsConfig> allGoodsTemp;// 加载中介

    private List<ShopGoodsConfig> dailyLimitGoodsList;

    private List<ShopGoodsConfig> dailyLimitGoodsListTemp;

    // 每日限购
    // cache db
    private Map<String, RoleShopDailyLimit> roleDailyLimitMap = Maps.newConcurrentMap();

    @Override
    public void setArgs(Object arg0) {

    }

    @Override
    public int onCleanup(String roleId, Object context) {
        roleDailyLimitMap.remove(roleId);
        return 1;
    }

    @Override
    public int onLogout(RoleInstance role, Object context) {
        RoleShopDailyLimit roleShop = roleDailyLimitMap.remove(role.getRoleId());
        if (null == roleShop) {
            return 1;
        }
        if (!roleShop.isInDatabase()) {
            //没有在库
            return 1;
        }
        roleShop.preToDatabase();
        if (!roleShop.isEmpty()) {
            return 1;
        }
        //空则删除
        GameContext.getBaseDAO().delete(RoleShopDailyLimit.class, RoleShopDailyLimit.ROLE_ID, role.getRoleId());
        return 0;
    }

    @Override
    public int onLogin(RoleInstance role, Object context) {
        return 0;
    }

    @Override
    public void start() {
        this.loadPanelInfoConfig();
        this.loadShopGoods();

        this.allGoodsMap = this.allGoodsTemp;

        // 清空加载中介
        dailyLimitGoodsListTemp = Lists.newArrayList(allGoodsTemp.values());
        Collections.sort(dailyLimitGoodsListTemp);
        dailyLimitGoodsList = dailyLimitGoodsListTemp;
        dailyLimitGoodsListTemp = null;
        this.allGoodsTemp = null;
    }

    @Override
    public void stop() {

    }

    private RoleShopDailyLimit getRoleShop(String roleId) {
        RoleShopDailyLimit shop = roleDailyLimitMap.get(roleId);
        if (null != shop) {
            shop.resetDay();
            return shop;
        }
        shop = getRoleShopFromDB(roleId);
        if (null != shop) {
            shop.resetDay();
        } else {
            shop = this.generateNewRoleShop(roleId);
        }
        roleDailyLimitMap.put(roleId, shop);
        return shop;
    }

    private RoleShopDailyLimit getRoleShopFromDB(String roleId) {
        RoleShopDailyLimit rs = GameContext.getBaseDAO().selectEntity(
                RoleShopDailyLimit.class, RoleShopDailyLimit.ROLE_ID, roleId);
        if (rs == null) {
            return null;
        }
        rs.postFromDatabase();
        rs.setInDatabase(true);
        return rs;
    }

    private <K, V extends KeySupport<K>> Map<K, V> loadConfigMap(
            XlsSheetNameType xls, Class<V> clazz, boolean linked) {
        String fileName = xls.getXlsName();
        String sheetName = xls.getSheetName();
        String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
        Map<K, V> map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName,
                clazz, linked);
        if (Util.isEmpty(map)) {
            checkFail("not config the " + clazz.getSimpleName() + " ,file="
                    + sourceFile + " sheet=" + sheetName);
        }
        return map;
    }

    private void checkFail(String info) {
        Log4jManager.CHECK.error(info);
        Log4jManager.checkFail();
    }

    private void loadPanelInfoConfig() {
        panelInfoConfigsMap = loadConfigMap(XlsSheetNameType.vip_gift_info,
                PanelInfoConfig.class, true);
    }

    private <T> List<T> loadConfigList(XlsSheetNameType xls, Class<T> t) {
        List<T> list = null;
        String fileName = xls.getXlsName();
        String sheetName = xls.getSheetName();
        String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
        try {
            list = XlsPojoUtil.sheetToList(sourceFile, sheetName, t);
        } catch (Exception e) {
            Log4jManager.CHECK
                    .error("load " + t.getSimpleName() + " error:fileName="
                            + fileName + ",sheetName=" + sheetName);
            Log4jManager.checkFail();
        }
        if (list == null) {
            Log4jManager.CHECK.error("load " + t.getSimpleName()
                    + " error: result is null fileName=" + fileName
                    + ",sheetName=" + sheetName);
            Log4jManager.checkFail();
        }
        return list;
    }

    /**
     * 加载商城物品信息
     */
    private boolean loadShopGoods() {
        boolean loadSuccess = true;
        this.allGoodsTemp = Maps.newHashMap();
        String fileName = XlsSheetNameType.shop_list.getXlsName();
        String sheetName = XlsSheetNameType.shop_list.getSheetName();
        String error = "load excel error:fileName=" + fileName + ",sheetName="
                + sheetName + ".";
        try {
            String sourceFile = GameContext.getPathConfig().getXlsPath()
                    + fileName;
            List<ShopGoodsConfig> shopGoodsList = XlsPojoUtil
                    .sheetToList(sourceFile, sheetName,
                            ShopGoodsConfig.class);
            for (ShopGoodsConfig shopGoods : shopGoodsList) {
                if (null == shopGoods) {
                    continue;
                }
                int goodsId = shopGoods.getGoodsId();
                if (null == GameContext.getGoodsApp().getGoodsBase(goodsId)) {
                    Log4jManager.CHECK.error(error + "goodsId=" + goodsId
                            + ",the goods is not exist!");
                    Log4jManager.checkFail();
                    loadSuccess = false;
                    continue;
                }
                Result result = shopGoods.init();
                if (!result.isSuccess()) {
                    Log4jManager.CHECK.error(error + result.getInfo());
                    Log4jManager.checkFail();
                    loadSuccess = false;
                    continue;
                }
                this.allGoodsTemp.put(goodsId, shopGoods);
            }
        } catch (Exception e) {
            Log4jManager.CHECK.error(error, e);
            Log4jManager.checkFail();
            loadSuccess = false;
        }
        return loadSuccess;
    }

    /**
     * 商城物品排序
     *
     * @param list
     */
    private void sortShopGoods(List<ShopGoodsConfig> list) {
        Collections.sort(list, new Comparator<ShopGoodsConfig>() {

            @Override
            public int compare(ShopGoodsConfig item1,
                               ShopGoodsConfig item2) {
                if (item1.getIndex() < item2.getIndex()) {
                    return -1;
                }
                if (item1.getIndex() > item2.getIndex()) {
                    return 1;
                }
                return 0;
            }
        });
    }

    /**
     * 重新加载配置表
     */
    @Override
    public Result reLoad() {
        Result result = new Result();
        // timelimit shop reload
        if (!this.loadShopGoods() || !GameContext.getShopTimeApp().reLoad()) {
            return result.setInfo(Status.Shop_Load_Failure.getTips());
        }
        this.allGoodsMap = this.allGoodsTemp;
        // 清空加载中介
        this.allGoodsTemp = null;
        return result.success();
    }

    @Override
    public List<ShopGoodsConfig> getShopGoodsList() {
        return dailyLimitGoodsList;
    }

    @Override
    public ShopGoodsConfig getShopGoods(int goodsId) {
        return this.allGoodsMap.get(goodsId);
    }
	private static String getTipformat(String pattern, Object... arguments) {
		String pStr = GameContext.getI18n().getText(pattern);
		return MessageFormat.format(pStr, arguments);
	}
    /**  如果是钻石购买，钻石足够则二次弹板，不够引导到充值 */
    @Override
    public Result shopping(RoleInstance role,byte moneyType, int goodsId, short num,
                           boolean isOneKey, byte confirm) {
        Result result = new Result();
        if (num <= 0) {
            return result.setInfo(Status.Shop_Req_Param_Error.getTips());
        }
        ShopGoodsConfig cf = this.allGoodsMap.get(goodsId);
        GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
        if (null == cf || null == goodsBase) {
            return result.setInfo(Status.Shop_Goods_Not_Exist.getTips());
        }
        if (!isOneKey && !cf.canSell()) {
            return result.setInfo(Status.Shop_Time_Not_Open.getTips());
        }

        // 售卖价格=单价×数量
        int price = cf.getDisPrice();
        int money = price * num;
        if (money <= 0) {
            return result.setInfo(Status.Shop_Req_Param_Error.getTips());
        }

        AttributeType attr = AttributeType.get(cf.getMoneyType());
        if (attr == null) {
            return result.setInfo(Status.Shop_Req_Param_Error.getTips());
        }
        /*
         *  如果是消耗钻石，二次弹板确认
         */
		if (!isOneKey && confirm == 0 && attr.getType() == AttributeType.goldMoney.getType()) {
			confirm = 1;
			String confirmTips = getTipformat(
					TextId.SHOP_BUY_DIAMOND_CONFIRM,
					 attr.getName(), money);
			Message notifyMsg = QuickCostHelper.getMessage(role,
					SHOP_BUY_CMDID, moneyType+","+goodsId+","+num + "," + confirm,
					(short) 0, "", money, 0, confirmTips);
			role.getBehavior().sendMessage(notifyMsg);
			result.setIgnore(true);
			return result;
		}
        
        // 【钻石不足弹板】判断
        Result ar = GameContext.getUserAttributeApp().getEnoughResult(role,
                attr, money);
        if (ar.isIgnore()) {// 弹板
            return ar;
        }
        if (!ar.isSuccess()) {// 不足
            return result.setInfo(Status.Shop_GoldMoney_Not_Enough.getTips());
        }
        // 往背包添加物品
        result = GameContext.getUserGoodsApp().addGoodsForBag(role, goodsId,
                num, BindingType.get(cf.getBind()),
                OutputConsumeType.shop_buy_gold_money);
        if (!result.isSuccess()) {
            return result;
        }
        // 扣除钱
        consumeAttribute(role, money, attr, OutputConsumeType.shop_buy_gold_money);

        // 购买物品日志
        GameContext.getStatLogApp().roleShopBuy(role, goodsId, price, num,
                money, AttributeType.goldMoney,
                OutputConsumeType.shop_buy_gold_money);

        return result.success();
    }

    private void consumeAttribute(RoleInstance role, int money,
                                  AttributeType attr, OutputConsumeType outputConsumeType) {
        switch (attr) {
            case dkp:
                GameContext.getUserAttributeApp().changeRoleDkp(role, attr,
                        OperatorType.Decrease, money,
                        outputConsumeType);
                break;
            default:
                GameContext.getUserAttributeApp().changeRoleMoney(role, attr,
                        OperatorType.Decrease, money,
                        outputConsumeType);
        }
        role.getBehavior().notifyAttribute();
    }

    public void saveOrUpdRoleShop(RoleShopDailyLimit roleShop) {
        if (!roleShop.isModify()) {
            return;
        }
        roleShop.preToDatabase();
        GameContext.getBaseDAO().saveOrUpdate(roleShop);
        roleShop.setModify(false);
        roleShop.setInDatabase(true);
    }


    @Override
    public Message openShop(RoleInstance role) {
        C2105_ShopEnterRespMessage msg = new C2105_ShopEnterRespMessage();
        byte vipLevel = GameContext.getVipApp().getVipLevel(role);
        String info = getPanelInfo(vipLevel);

        int currentVipExp = GameContext.getVipApp().getRoleVipExp(role);
        int nextLevelExpNeeded = GameContext.getVipApp().getVipExp4VipLevelUp(
                role);

        msg.setCurrentVipExp(currentVipExp);
        msg.setNextLevelExpNeeded(nextLevelExpNeeded);
        msg.setVipLevel(vipLevel);
        msg.setInfo(info);

        // 并发送限时抢购的消息
        return msg;
    }

    private String getPanelInfo(byte vipLevel) {
        if (panelInfoConfigsMap.containsKey(vipLevel)) {
            return panelInfoConfigsMap.get(vipLevel).getInfo();
        }
        return "";
    }

    private RoleShopDailyLimit generateNewRoleShop(String roleId) {
        RoleShopDailyLimit roleShopCarrier = new RoleShopDailyLimit();
        roleShopCarrier.setRefreshTime(new Date());
        roleShopCarrier.setRoleId(roleId);
        roleShopCarrier.setModify(false);
        roleShopCarrier.setInDatabase(false);
        return roleShopCarrier;
    }

    // 获取商店列表
    private List<ShopGoodsItem> buildDailySaleItemList(
            String roleId) {
        List<ShopGoodsItem> list = Lists.newArrayList();
        for (ShopGoodsConfig cf : dailyLimitGoodsList) {
            if (null == cf || !cf.canSell()) {
                continue;
            }
            ShopGoodsItem it = cf.getShopGoodsItem();
            if (it == null) {
                continue;
            }
            //购买次数
            list.add(it);
        }
        return list;
    }

    @Override
    public Message getGoodsList(RoleInstance role) {
        C2101_ShopGoodsListRespMessage msg = new C2101_ShopGoodsListRespMessage();
        String roleId = role.getRoleId();
        //获取商店
        msg.setShopGoodsList(buildDailySaleItemList(roleId));
        return msg;
    }
}
