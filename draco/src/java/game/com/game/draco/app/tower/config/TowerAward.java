package com.game.draco.app.tower.config;


import com.game.draco.GameContext;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.google.common.collect.Lists;
import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Initable;
import sacred.alliance.magic.util.Log4jManager;

import java.util.List;

public @Data abstract
class  TowerAward implements Initable{

    protected int goods1;
    protected short num1;
    protected byte bind1 = -1 ;

    protected int goods2;
    protected short num2;
    protected byte bind2 = -1 ;

    protected int goods3;
    protected short num3;
    protected byte bind3 = -1 ;

    protected byte attributeType1;
    protected int attributeValue1;

    protected byte attributeType2;
    protected int attributeValue2;

    private List<GoodsLiteItem> goodsAwards = Lists.newArrayList();
    private List<AttriTypeValueItem> attriAwards = Lists.newArrayList();
    private List<GoodsOperateBean> goodsOperateList = Lists.newArrayList() ;

    @Override
    public void init() {
        initGoodsAward(goodsAwards, goods1, num1, bind1);
        initGoodsAward(goodsAwards, goods2, num2, bind2);
        initGoodsAward(goodsAwards, goods3, num3, bind3);

        initAttrAward(attriAwards, attributeType1, attributeValue1);
        initAttrAward(attriAwards, attributeType2, attributeValue2);
    }

    public List<GoodsOperateBean> multGoodsOperateList(int mult){
        List<GoodsOperateBean> ret = Lists.newArrayList() ;
        for(GoodsOperateBean bean : goodsOperateList){
            ret.add(new GoodsOperateBean(bean.getGoodsId(),bean.getGoodsNum(),bean.getBindType())) ;
        }
        return ret ;
    }

    public List<GoodsLiteItem> multGoodsAwards(int mult){
        List<GoodsLiteItem> ret = Lists.newArrayList() ;
        for(GoodsLiteItem item : goodsAwards){
            GoodsLiteItem newItem = new GoodsLiteItem();
            org.springframework.beans.BeanUtils.copyProperties(item,newItem);
            newItem.setNum((short)(newItem.getNum()*mult));
            ret.add(newItem);
        }
        return ret ;
    }

    public List<AttriTypeValueItem> multAttriAwards(int mult){
        List<AttriTypeValueItem> ret = Lists.newArrayList() ;
        for(AttriTypeValueItem item : attriAwards){
            AttriTypeValueItem newItem = new AttriTypeValueItem();
            newItem.setAttriType(item.getAttriType());
            newItem.setAttriValue(item.getAttriValue()*mult);
            ret.add(newItem);
        }
        return ret ;
    }

    private void initAttrAward(List<AttriTypeValueItem> attAwardList, byte attrType, int value) {
        if(0 == attrType || value <=0 ){
            return ;
        }
        if (AttributeType.get((byte) attrType) == null) {
            checkFail(this.getErrorPrefixTips() + " attributeType=" + attrType + " is error");
            return ;
        }
        AttriTypeValueItem at = new AttriTypeValueItem();
        at.setAttriType((byte) attrType);
        at.setAttriValue(value);
        attAwardList.add(at);
    }

    private void initGoodsAward(List<GoodsLiteItem> awardsList, int goodsId, short num, byte bind) {
        if (goodsId <= 0 || num <=0) {
            return;
        }
        GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
        if (null == goodsBase) {
            checkFail(this.getErrorPrefixTips() + " goodsId=" + goodsId + " not exist");
            return;
        }
        GoodsLiteItem goodsLiteNamedItem = goodsBase.getGoodsLiteItem();
        goodsLiteNamedItem.setNum(num);
        goodsLiteNamedItem.setBindType(bind);
        awardsList.add(goodsLiteNamedItem);

        goodsOperateList.add(new GoodsOperateBean(goodsId,num,bind));
    }

    protected void checkFail(String errInfo) {
        Log4jManager.CHECK.error(errInfo);
        Log4jManager.checkFail();
    }

    public abstract String getErrorPrefixTips() ;
}
