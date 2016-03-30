package sacred.alliance.magic.app.arena.top;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.message.item.AwardAttrItem;
import com.game.draco.message.item.GoodsLiteItem;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;

public @Data class TopRewardConfig {

	private int minLevel ;
	private int maxLevel ;
	private int startRank ;
	private int endRank ;
	private int exp ;
	private int gameMoney ;
	private int goodsId1 ;
	private int goodsNum1 ;
	private byte bindType1 ;
	
	private int goodsId2 ;
	private int goodsNum2 ;
	private byte bindType2 ;
	
	private int goodsId3 ;
	private int goodsNum3 ;
	private byte bindType3 ;
	
	private List<GoodsOperateBean> addGoods = new ArrayList<GoodsOperateBean>() ;
	//奖励显示信息
	private List<AwardAttrItem> showAttrList = new ArrayList<AwardAttrItem>();//属性奖励
	private List<GoodsLiteItem> showGoodsList = new ArrayList<GoodsLiteItem>();//物品奖励
	
	
	public void init(){
		this.init(goodsId1, goodsNum1, bindType1);
		this.init(goodsId2, goodsNum2, bindType2);
		this.init(goodsId3, goodsNum3, bindType3);
		if(this.addGoods.size() > Mail.MaxAccessoryNum){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("TopRewardConfig config error,goods num >" +  Mail.MaxAccessoryNum);
		}
		//构建属性奖励显示信息
		if(this.exp > 0){
			this.showAttrList.add(new AwardAttrItem(AttributeType.exp.getType(), this.exp));
		}
		if(this.gameMoney > 0){
			this.showAttrList.add(new AwardAttrItem(AttributeType.gameMoney.getType(), this.gameMoney));
		}
	}
	
	private void init(int goodsId,int goodsNum,int bindType){
		if(goodsId <=0 || goodsNum <=0){
			return ;
		}
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null == goodsBase){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("TopRewardConfig config error,goods not exist,goodsId=" + goodsId);
			return ;
		}
		addGoods.add(new GoodsOperateBean(goodsId,goodsNum,bindType));
		GoodsLiteItem item = goodsBase.getGoodsLiteItem() ;
		item.setNum((short)goodsNum);
		item.setBindType((byte)bindType);
		this.showGoodsList.add(item);
	}
}
