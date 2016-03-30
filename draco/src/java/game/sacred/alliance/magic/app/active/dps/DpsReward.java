package sacred.alliance.magic.app.active.dps;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.AwardAttrItem;
import com.game.draco.message.item.GoodsLiteItem;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Util;

public @Data class DpsReward {
	
	private int rewardId;//奖励ID
	private int exp;//经验
	private int bindMoney;//绑金
	private int silverMoney;//游戏币
	private int goodsId1;//奖励物品1
	private int goodsNum1;//奖励1数量
	private byte bind1 = BindingType.template.getType();//绑定类型
	private int goodsId2;
	private int goodsNum2;
	private byte bind2 = BindingType.template.getType();
	private int goodsId3;
	private int goodsNum3;
	private byte bind3 = BindingType.template.getType();
	private int goodsId4;
	private int goodsNum4;
	private byte bind4 = BindingType.template.getType();
	private int goodsId5;
	private int goodsNum5;
	private byte bind5 = BindingType.template.getType();
	private String senderName;//邮件发件人
	private String title;//邮件标题
	private String content;//邮件内容
	//奖励物品
	private List<GoodsOperateBean> rewardGoodsList = new ArrayList<GoodsOperateBean>();
	//奖励显示信息
	private List<AwardAttrItem> showAttrList = new ArrayList<AwardAttrItem>();//属性奖励
	private List<GoodsLiteItem> showGoodsList = new ArrayList<GoodsLiteItem>();//物品奖励
	
	public List<GoodsLiteItem> getShowGoodsList(){
		return this.showGoodsList ;
	}
	
	public Result checkAndInit(){
		Result result = new Result();
		String info = "rewardId=" + this.rewardId + ".";
		try {
			if(this.bindMoney < 0 || this.silverMoney < 0){
				return result.setInfo(info + "The bindMoney and silverMoney must be greater than 0.");
			}
			if(Util.isEmpty(this.senderName)){
				return result.setInfo(info + "The senderName is null.");
			}
			if(Util.isEmpty(this.title)){
				return result.setInfo(info + "The title is null.");
			}
			this.addRewardGoods(this.goodsId1, this.goodsNum1, this.bind1);
			this.addRewardGoods(this.goodsId2, this.goodsNum2, this.bind2);
			this.addRewardGoods(this.goodsId3, this.goodsNum3, this.bind3);
			this.addRewardGoods(this.goodsId4, this.goodsNum4, this.bind4);
			this.addRewardGoods(this.goodsId5, this.goodsNum5, this.bind5);
			//验证物品是否存在，构建物品奖励显示信息
			for(GoodsOperateBean bean : this.rewardGoodsList){
				if(null == bean){
					continue;
				}
				int goodsId = bean.getGoodsId();
				GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
				if(null == goodsBase){
					return result.setInfo(info + "goodsId=" + goodsId + ",reward goods not exist!");
				}
				GoodsLiteItem item = goodsBase.getGoodsLiteItem() ;
				item.setNum((short) bean.getGoodsNum());
				item.setBindType(bean.getBindType().getType());
				this.showGoodsList.add(item);
			}
			//构建属性奖励显示信息
			if(this.exp > 0){
				this.showAttrList.add(new AwardAttrItem(AttributeType.exp.getType(), this.exp));
			}
			if(this.silverMoney > 0){
				this.showAttrList.add(new AwardAttrItem(AttributeType.gameMoney.getType(), this.silverMoney));
			}
			return result.success();
		} catch (Exception e) {
			return result.setInfo("warlords reward error: " + info + e.toString());
		}
	}
	
	private void addRewardGoods(int goodsId, int goodsNum, byte bind){
		if(goodsId <= 0 || goodsNum <= 0){
			return;
		}
		this.rewardGoodsList.add(new GoodsOperateBean(goodsId, goodsNum, BindingType.get(bind)));
	}
	
}
