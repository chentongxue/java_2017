package sacred.alliance.magic.app.goods.behavior;

import java.util.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BroadcastType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsBox;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class UseGoodsBox extends AbstractGoodsBehavior {
    public UseGoodsBox() {
        this.behaviorType = GoodsBehaviorType.Use;
    }

    @Override
    public GoodsResult operate(AbstractParam param) {
        UseGoodsParam useGoodsParam = (UseGoodsParam) param;
        RoleInstance role = useGoodsParam.getRole();
        RoleGoods boxGoods = useGoodsParam.getRoleGoods();
        int useCount = useGoodsParam.getUseCount();

        GoodsResult result = new GoodsResult();
        if (useCount <= 0 || useCount > boxGoods.getCurrOverlapCount()) {
            return result.setInfo(GameContext.getI18n().getText(TextId.Sys_Param_Error));
        }

        GoodsBox goodsBox = GameContext.getGoodsApp().getGoodsTemplate(GoodsBox.class, boxGoods.getGoodsId());
        if (goodsBox == null) {
            return result.setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
        }

        if (goodsBox.getLvLimit() > role.getLevel()) {
            return result.setInfo(GameContext.getI18n().messageFormat(TextId.USE_GOODS_BOX_LEVEL_NOT_ENOUGH,
                    goodsBox.getLvLimit()));
        }

        int keyNum = 0;
        //判断是否有钥匙
        int keyId = goodsBox.getKeyId();
        if (keyId > 0) {
            keyNum = role.getRoleBackpack().countByGoodsId(keyId);
            if (keyNum <= 0) {
                GoodsBase key = GameContext.getGoodsApp().getGoodsBase(keyId);
                if (null == key) {
                    return result.setInfo(GameContext.getI18n().getText(TextId.USE_GOODS_BOX_KEY_NOT_ENOUGH));
                }
                return result.setInfo(GameContext.getI18n().messageFormat(TextId.USE_GOODS_BOX_NAME_KEY_NOT_ENOUGH, key.getName()));
            }
        }

        int maxUseCount = this.maxCanUseNum(role, useCount, goodsBox);
        if (maxUseCount <= 0) {
            return result.setInfo(GameContext.getI18n().messageFormat(TextId.USE_GOODS_BOX_GRID_NOT_ENOUGH, goodsBox.getNeedGoodsGridCount()));
        }

        Map<Integer, Integer> delMap = new HashMap<Integer, Integer>();
        if (keyId > 0) {
            maxUseCount = Math.min(keyNum, maxUseCount);
            delMap.put(keyId, maxUseCount);
        }

        Map<String, GoodsOperateBean> beanMap = Maps.newHashMap();
        for (int i = 0; i < maxUseCount; i++) {
            List<GoodsOperateBean> goodsList = goodsBox.getGoodsList();
            if (Util.isEmpty(goodsList)) {
                continue;
            }
            for (GoodsOperateBean bean : goodsList) {
                String key = bean.getGoodsId() + "_" + bean.getBindType().getType();
                GoodsOperateBean existBean = beanMap.get(key);
                if (null == existBean) {
                    beanMap.put(key, bean);
                } else {
                    existBean.setGoodsNum(existBean.getGoodsNum() + bean.getGoodsNum());
                }
            }
        }

        List<GoodsOperateBean> awardsList = Lists.newArrayList();
        awardsList.addAll(beanMap.values());

        result = GameContext.getUserGoodsApp().addDelGoodsForBag(
                role, awardsList, OutputConsumeType.treasure_box_output,
                boxGoods, maxUseCount, delMap, OutputConsumeType.treasure_box_use);
        if (!result.isSuccess()) {
            return result;
        }

        if (goodsBox.getGoldMoney() > 0) {
            GameContext.getUserAttributeApp().changeRoleMoney(role,
                    AttributeType.goldMoney, OperatorType.Add, goodsBox.getGoldMoney() * maxUseCount,
                    OutputConsumeType.treasure_box_output);
        }
        if (goodsBox.getSilverMoney() > 0) {
            GameContext.getUserAttributeApp().changeRoleMoney(role,
                    AttributeType.gameMoney, OperatorType.Add, goodsBox.getSilverMoney() * maxUseCount,
                    OutputConsumeType.treasure_box_output);
        }
        if (goodsBox.getPotential() > 0) {
            GameContext.getUserAttributeApp().changeRoleMoney(role,
                    AttributeType.potential, OperatorType.Add, goodsBox.getPotential() * maxUseCount,
                    OutputConsumeType.treasure_box_output);
        }

        role.getBehavior().notifyAttribute();

        sendGoodsBoxInfo(role, awardsList, goodsBox, maxUseCount);

        this.broadcast(role.getRoleName(), awardsList, boxGoods.getGoodsId());
        return result.setResult(GoodsResult.SUCCESS);
    }

    private int maxCanUseNum(RoleInstance role, int useCount, GoodsBox goodsBox) {
        int freeGoodsGridCount = role.getRoleBackpack().freeGridCount();
        int needGoodsGridCount = goodsBox.getNeedGoodsGridCount();
        if (needGoodsGridCount <= 1) {
            return useCount;
        }
        return Math.min(useCount, freeGoodsGridCount / needGoodsGridCount);
    }


    private void sendGoodsBoxInfo(RoleInstance role, Collection<GoodsOperateBean> goodsList, GoodsBox goodsBox, int count) {
        Converter.pushIncomeMessage(role, goodsList, goodsBox.getPotential() * count,
                goodsBox.getSilverMoney() * count, goodsBox.getGoldMoney() * count);
    }


    private void broadcast(String roleName, Collection<GoodsOperateBean> list, int boxId) {
        try {
            if (Util.isEmpty(list)) {
                return;
            }
            for (GoodsOperateBean bean : list) {
                if (null == bean) {
                    continue;
                }
                int goodsId = bean.getGoodsId();
                GameContext.getBroadcastApp().broadCast(roleName, goodsId, String.valueOf(boxId), BroadcastType.box);
            }
        } catch (Exception e) {
            logger.error("UseGoodsBox.broadcast error:", e);
        }
    }
}
