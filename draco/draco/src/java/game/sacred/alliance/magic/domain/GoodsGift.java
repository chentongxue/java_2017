package sacred.alliance.magic.domain;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.message.item.GoodsBaseGiftItem;
import com.game.draco.message.item.GoodsBaseItem;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.BindingType;


public @Data class GoodsGift extends GoodsBase {
	private final static int DEFAULT_BIND_TYPE = BindingType.template.getType() ;
	private int mustid1;
	private int number1;
	private int bind1 = DEFAULT_BIND_TYPE;
	private int mustid2;
	private int number2;
	private int bind2 = DEFAULT_BIND_TYPE;
	private int mustid3;
	private int number3;
	private int bind3 = DEFAULT_BIND_TYPE;
	private int mustid4;
	private int number4;
	private int bind4 = DEFAULT_BIND_TYPE;
	private int mustid5;
	private int number5;
	private int bind5 = DEFAULT_BIND_TYPE;
	private int mustid6;
	private int number6;
	private int bind6 = DEFAULT_BIND_TYPE;
	private int mustid7;
	private int number7;
	private int bind7 = DEFAULT_BIND_TYPE;
	private int mustid8;
	private int number8;
	private int bind8 = DEFAULT_BIND_TYPE;
	private int mustid9;
	private int number9;
	private int bind9 = DEFAULT_BIND_TYPE;
	private int mustid10;
	private int number10;
	private int bind10 = DEFAULT_BIND_TYPE;
	private int mustid11;
	private int number11;
	private int bind11 = DEFAULT_BIND_TYPE;
	private int mustid12;
	private int number12;
	private int bind12 = DEFAULT_BIND_TYPE;
	private int nextId ;

	private List<GoodsOperateBean> goodsList = new ArrayList<GoodsOperateBean>();
	
	@Override
	public void init(Object initData) {
		this.constructMustMap();
	}
	
	
	private void constructMustMap(){
		this.filterMap(this.mustid1, this.number1, this.bind1,this.goodsList);
		this.filterMap(this.mustid2, this.number2, this.bind2,this.goodsList);
		this.filterMap(this.mustid3, this.number3, this.bind3,this.goodsList);
		this.filterMap(this.mustid4, this.number4, this.bind4,this.goodsList);
		this.filterMap(this.mustid5, this.number5, this.bind5,this.goodsList);
		this.filterMap(this.mustid6, this.number6, this.bind6,this.goodsList);
		this.filterMap(this.mustid7, this.number7, this.bind7,this.goodsList);
		this.filterMap(this.mustid8, this.number8, this.bind8,this.goodsList);
		this.filterMap(this.mustid9, this.number9, this.bind9,this.goodsList);
		this.filterMap(this.mustid10, this.number10, this.bind10,this.goodsList);
		this.filterMap(this.mustid11, this.number11, this.bind11,this.goodsList);
		this.filterMap(this.mustid12, this.number12, this.bind12,this.goodsList);
	}
	
	
	private void filterMap(int goodsId, int num,int bind,List<GoodsOperateBean> mustList){
		if (0 >= goodsId || 0>= num) {
			return;
		}
		for(GoodsOperateBean bean : mustList){
			if(goodsId == bean.getGoodsId() 
					&& bind == bean.getBindType().getType()){
				bean.setGoodsNum(bean.getGoodsNum() + num);
				return ;
			}
		}
		mustList.add(new GoodsOperateBean(goodsId,num,bind));
	}
	
	
	@Override
	public List<AttriItem> getAttriItemList() {
		return null; 
	}


	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseGiftItem item = new GoodsBaseGiftItem();
		this.setGoodsBaseItem(roleGoods, item);
		item.setSecondType((byte)secondType);
		item.setLvLimit((byte)lvLimit);
		item.setDesc(Util.replace(desc));
		return item;
	}
	
}
