package com.game.draco.app.target.config;

import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteItem;
import com.google.common.collect.Lists;

public @Data class TargetConfig {
	public final static short DEFAULT_TARGET = -1;
	public final static short DEFAULT_ICON_ID = -1;
	public final static byte line1 = 1;
	public final static byte line2 = 2;
	public final static byte line3 = 3;
	public final static byte line4 = 4; //等同于A线,记录当前为完成的目标
	private short targetId;	//目标id
	private String name;	//目标名字
	private byte line;	//目标组0：A线,1:B线,2:C线
	private short hintIcon = DEFAULT_ICON_ID ;	//提示图标
	private String hintText;	//提示文本（只有A线需要填）
	private short nextTargetId;	//后续目标id
	private short conditionId;	//条件id
	private int gameMoney;	//游戏币
	private int goldMoney;	//钻石
	private int potential;	//潜能
	private int goods1;	//物品id
	private short num1;	//物品数量
	private byte bind1;	//绑定类型
	private int goods2;	//物品id
	private short num2;	//物品数量
	private byte bind2;	//绑定类型
	private int goods3;	//物品id
	private short num3;	//物品数量
	private byte bind3;	//绑定类型
	
	//变量
	private List<GoodsOperateBean> goodsList = Lists.newArrayList();
	private TargetConfig nextTarget = null;
	private TargetCond targetCond;
	
	//public static Map<Byte, Short> lineStartIdMap = Maps.newHashMap();
	
	public void init(String fileInfo) {
		this.addToGoodsList(fileInfo, this.goods1, this.num1, this.bind1);
		this.addToGoodsList(fileInfo, this.goods2, this.num2, this.bind2);
		this.addToGoodsList(fileInfo, this.goods3, this.num3, this.bind3);
	}
	
	private void addToGoodsList(String info, int goodsId, short goodsNum, byte bindType){
		if(goodsId <= 0 || goodsNum <= 0){
			return;
		}
		if(null == GameContext.getGoodsApp().getGoodsBase(goodsId)){
			this.checkFail(info + " goodsId=" + goodsId + ", it's not exist!");
			return;
		}
		this.goodsList.add(new GoodsOperateBean(goodsId, goodsNum, bindType));
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	public List<GoodsLiteItem> getGoodsLiteItemList() {
		if(Util.isEmpty(goodsList)) {
			return null;
		}
		List<GoodsLiteItem> glItemList = Lists.newArrayList();
		for(GoodsOperateBean goodsBean : goodsList) {
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsBean.getGoodsId());
			if(null == gb) {
				continue;
			}
			GoodsLiteItem item = gb.getGoodsLiteItem() ;
			item.setBindType(goodsBean.getBindType().getType());
			item.setNum((short)goodsBean.getGoodsNum());
			glItemList.add(item);
		}
		return glItemList;
	}
	
}
