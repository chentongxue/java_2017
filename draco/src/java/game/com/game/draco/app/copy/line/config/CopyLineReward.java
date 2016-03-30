package com.game.draco.app.copy.line.config;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteItem;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;

@Data
public class CopyLineReward {
	
	private byte chapterId;//章节序列
	private short starNum;//星级数量
	private int goodsId;//物品ID
	private short goodsNum;//物品数量
	private int gold;//元宝
	private int silver;//游戏币
	
	public void checkInit(String fileInfo){
		String info = fileInfo + "chapterId = " + this.chapterId + ", ";
		if(this.starNum <= 0){
			this.checkFail(info + "starNum must be greater than 0.");
		}
		if(this.goodsId > 0){
			if(this.goodsNum <= 0){
				this.checkFail(info + "goodsNum config error.");
			}
			if(null == GameContext.getGoodsApp().getGoodsBase(this.goodsId)){
				this.checkFail(info + "goodsId is error, the goods is not exist.");
			}
		}
		if(this.gold < 0){
			this.checkFail(info + "gold config error.");
		}
		if(this.silver < 0){
			this.checkFail(info + "silver config error.");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	public List<AttriTypeValueItem> getAttrTypeValueList(){
		List<AttriTypeValueItem> attrList = new ArrayList<AttriTypeValueItem>();
		if(this.gold > 0){
			AttriTypeValueItem goldItem = new AttriTypeValueItem();
			goldItem.setAttriType(AttributeType.goldMoney.getType());
			goldItem.setAttriValue(this.gold);
			attrList.add(goldItem);
		}
		if(this.silver > 0){
			AttriTypeValueItem silverItem = new AttriTypeValueItem();
			silverItem.setAttriType(AttributeType.gameMoney.getType());
			silverItem.setAttriValue(this.silver);
			attrList.add(silverItem);
		}
		return attrList;
	}
	
	public GoodsLiteItem getGoodsLiteItem(){
		if(this.goodsId <= 0){
			return null;
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(this.goodsId);
		if(null == gb){
			return null;
		}
		GoodsLiteItem liteItem = gb.getGoodsLiteItem();
		liteItem.setNum(this.goodsNum);
		return null;
	}
	
}
