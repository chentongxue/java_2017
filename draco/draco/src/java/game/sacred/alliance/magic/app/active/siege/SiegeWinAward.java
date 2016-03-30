package sacred.alliance.magic.app.active.siege;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.BindingType;

public class SiegeWinAward {
	private String mapId;// 地图ID
	private int minLevel;// 最小等级
	private int maxLevel;// 最大等级
	private int bindGold;// 绑金
	private int silverMoney;// 银币
	private int exp;// 经验
	private int goodsId1;// 物品id
	private int num1;// 数量
	private int bind1;// 绑定类型
	private int goodsId2;// 物品id
	private int num2;// 数量
	private int bind2;// 绑定类型
	private int goodsId3;// 物品id
	private int num3;// 数量
	private int bind3;// 绑定类型

	private List<GoodsOperateBean> addGoodsList = new ArrayList<GoodsOperateBean>();

	public void init() {
		this.addGoods(goodsId1, num1, bind1);
		this.addGoods(goodsId2, num2, bind2);
		this.addGoods(goodsId3, num3, bind3);
	}

	private void addGoods(int goodsId, int num, int bind) {
		if (goodsId <= 0 || num <= 0) {
			return;
		}
		GoodsOperateBean gob = new GoodsOperateBean();
		gob.setGoodsNum(num);
		gob.setBindType(BindingType.get(bind));
		gob.setGoodsId(goodsId);
		this.addGoodsList.add(gob);
	}

	public boolean isLevel(int level) {
		if (level >= minLevel && level <= maxLevel) {
			return true;
		}
		return false;
	}

	public String getMapId() {
		return mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public int getBindGold() {
		return bindGold;
	}

	public void setBindGold(int bindGold) {
		this.bindGold = bindGold;
	}

	public int getSilverMoney() {
		return silverMoney;
	}

	public void setSilverMoney(int silverMoney) {
		this.silverMoney = silverMoney;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getGoodsId1() {
		return goodsId1;
	}

	public void setGoodsId1(int goodsId1) {
		this.goodsId1 = goodsId1;
	}

	public int getNum1() {
		return num1;
	}

	public void setNum1(int num1) {
		this.num1 = num1;
	}

	public int getBind1() {
		return bind1;
	}

	public void setBind1(int bind1) {
		this.bind1 = bind1;
	}

	public int getGoodsId2() {
		return goodsId2;
	}

	public void setGoodsId2(int goodsId2) {
		this.goodsId2 = goodsId2;
	}

	public int getNum2() {
		return num2;
	}

	public void setNum2(int num2) {
		this.num2 = num2;
	}

	public int getBind2() {
		return bind2;
	}

	public void setBind2(int bind2) {
		this.bind2 = bind2;
	}

	public int getGoodsId3() {
		return goodsId3;
	}

	public void setGoodsId3(int goodsId3) {
		this.goodsId3 = goodsId3;
	}

	public int getNum3() {
		return num3;
	}

	public void setNum3(int num3) {
		this.num3 = num3;
	}

	public int getBind3() {
		return bind3;
	}

	public void setBind3(int bind3) {
		this.bind3 = bind3;
	}

	public List<GoodsOperateBean> getAddGoodsList() {
		return addGoodsList;
	}

	public void setAddGoodsList(List<GoodsOperateBean> addGoodsList) {
		this.addGoodsList = addGoodsList;
	}

}
