package sacred.alliance.magic.domain;

import java.util.List;

import com.game.draco.message.item.GoodsBaseFactionItem;
import com.game.draco.message.item.GoodsBaseItem;

import sacred.alliance.magic.app.attri.AttriItem;

public class GoodsFaction extends GoodsBase{
	private int factionContribute ;//公会贡献
	private int factionIntegral ;// 公会积分
	private int applyDisappear; //使用是否消失
	
	@Override
	public void init(Object initData) {
	}

	@Override
	public List<AttriItem> getAttriItemList() {
		return null;
	}

	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseFactionItem item = new GoodsBaseFactionItem();
		this.setGoodsBaseItem(roleGoods, item);
		item.setLvlimit((byte)this.getLvLimit());
		item.setDesc(desc);
		return item;
	}



	public int getFactionContribute() {
		return factionContribute;
	}

	public void setFactionContribute(int factionContribute) {
		this.factionContribute = factionContribute;
	}

	public int getFactionIntegral() {
		return factionIntegral;
	}

	public void setFactionIntegral(int factionIntegral) {
		this.factionIntegral = factionIntegral;
	}

	public int getApplyDisappear() {
		return applyDisappear;
	}

	public void setApplyDisappear(int applyDisappear) {
		this.applyDisappear = applyDisappear;
	}

	
}
