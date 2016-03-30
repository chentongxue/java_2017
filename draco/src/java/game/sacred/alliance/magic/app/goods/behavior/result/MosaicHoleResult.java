package sacred.alliance.magic.app.goods.behavior.result;

import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.GoodsRune;
import sacred.alliance.magic.domain.RoleGoods;

import com.game.draco.app.rune.domain.MosaicRune;

public class MosaicHoleResult extends GoodsResult {

	private RoleGoods roleGoods;// 装备
	private GoodsEquipment goodsTemplate;// 装备模版
	private RoleGoods roleRune;// 符文
	private GoodsRune runeTemplate;// 符文模版
	private int matchHoleId = -1;// 镶嵌孔位
	private int mosaicMoney = 0;// 镶嵌金币
	private MosaicRune mosaicRune;// 镶嵌符文

	public RoleGoods getRoleGoods() {
		return roleGoods;
	}

	public void setRoleGoods(RoleGoods roleGoods) {
		this.roleGoods = roleGoods;
	}

	public GoodsEquipment getGoodsTemplate() {
		return goodsTemplate;
	}

	public void setGoodsTemplate(GoodsEquipment goodsTemplate) {
		this.goodsTemplate = goodsTemplate;
	}

	public static MosaicHoleResult newSuccess() {
		MosaicHoleResult result = new MosaicHoleResult();
		result.setResult(GoodsResult.SUCCESS);
		return result;
	}

	public static MosaicHoleResult newFail() {
		MosaicHoleResult result = new MosaicHoleResult();
		result.setResult(GoodsResult.FAIL);
		return result;
	}

	public static MosaicHoleResult newFail(String info) {
		MosaicHoleResult result = new MosaicHoleResult();
		result.setInfo(info);
		return result;
	}

	public int getMatchHoleId() {
		return matchHoleId;
	}

	public void setMatchHoleId(int matchHoleId) {
		this.matchHoleId = matchHoleId;
	}

	public int getMosaicMoney() {
		return mosaicMoney;
	}

	public void setMosaicMoney(int mosaicMoney) {
		this.mosaicMoney = mosaicMoney;
	}

	public MosaicRune getMosaicRune() {
		return mosaicRune;
	}

	public void setMosaicRune(MosaicRune mosaicRune) {
		this.mosaicRune = mosaicRune;
	}

	public RoleGoods getRoleRune() {
		return roleRune;
	}

	public void setRoleRune(RoleGoods roleRune) {
		this.roleRune = roleRune;
	}

	public GoodsRune getRuneTemplate() {
		return runeTemplate;
	}

	public void setRuneTemplate(GoodsRune runeTemplate) {
		this.runeTemplate = runeTemplate;
	}

}
