package sacred.alliance.magic.app.arena.config;

import com.game.draco.GameContext;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.google.common.collect.Lists;
import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Initable;
import sacred.alliance.magic.util.Log4jManager;

import java.util.List;

public @Data class Reward1V1Finish implements Initable {
	
	private int minLevel ;
	private int maxLevel ;
	private int startRank ;
	private int endRank ;
    private short imageId ;

	private int exp ;
	private int gameMoney ;

	private int goodsId1 ;
	private int goodsNum1 ;
	private byte bindType1 = -1 ;
    private int goodsId2 ;
    private int goodsNum2 ;
    private byte bindType2 = -1 ;
    private int goodsId3 ;
    private int goodsNum3 ;
    private byte bindType3 = -1 ;


    private List<GoodsLiteNamedItem> goodsList = Lists.newArrayList() ;
    private List<AttriTypeValueItem> attriList = Lists.newArrayList() ;

    private void initAttri(byte type,int value){
        if(value <=0){
            return ;
        }
        AttriTypeValueItem item = new AttriTypeValueItem() ;
        item.setAttriType(type);
        item.setAttriValue(value);
        attriList.add(item);
    }

    private void initGoods(int goodsId,int goodsNum,byte bindType){
        if(goodsId <=0 || goodsNum <=0){
            return ;
        }
        GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId) ;
        if(null == gb){
            Log4jManager.CHECK.error("Reward1V1Finish config error,goods not exist,goodsId={}",goodsId);
            Log4jManager.checkFail();
            return ;
        }
        GoodsLiteNamedItem item = gb.getGoodsLiteNamedItem();
        item.setNum((short)goodsNum);
        item.setBindType(bindType);
        this.goodsList.add(item);
    }

    @Override
    public void init() {
        this.initAttri(AttributeType.exp.getType(),exp);
        this.initAttri(AttributeType.gameMoney.getType(),gameMoney);

        this.initGoods(this.goodsId1,this.goodsNum1, this.bindType1);
        this.initGoods(this.goodsId2,this.goodsNum2,this.bindType2);
        this.initGoods(this.goodsId3,this.goodsNum3,this.bindType3);
    }
}
