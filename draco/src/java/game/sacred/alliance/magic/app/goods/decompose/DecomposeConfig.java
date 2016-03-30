package sacred.alliance.magic.app.goods.decompose;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.GameContext;

/**
 * 分解配置表
 */
public @Data
class DecomposeConfig implements KeySupport<Integer> {
	public static final byte OUTPUT_ATTRIBUTE_TYPE = 1;
	public static final byte OUTPUT_GOODS_TYPE = 2;

	private int inputGoodsId;
	private byte outputType;// 1：为属性，勇者之魂，狂野之血等,2：为物品，卷轴碎片等
	private int outputId; // 分解后的物品ID或属性
	private int minNum;
	private int maxNum;

	@Override
	public Integer getKey() {
		return inputGoodsId;
	}

	public boolean isGoods() {
		if (outputType != OUTPUT_GOODS_TYPE) {
			return false;
		}
		if (GameContext.getGoodsApp().getGoodsBase(outputId) == null) {
			return false;
		}
		return true;
	}

	public boolean isAttribute() {
		return outputType == OUTPUT_ATTRIBUTE_TYPE
				&& (outputId == AttributeType.braveSoul.getType() || outputId == AttributeType.wildBlood
						.getType());
	}

	public AttributeType getAttribute() {
		if (isAttribute()) {
			AttributeType at = AttributeType.get((byte) outputId);
			return at;
		}
		return null;
	}

	public void init() {
		if (GameContext.getGoodsApp().getGoodsBase(inputGoodsId) == null) {
			checkFail("decomposeConfig init() err: the  inputGoodsId config wrong, inputGoodsId = "
					+ inputGoodsId);
		}
		if (!isGoods() && !isAttribute()) {
			checkFail("decomposeConfig init() err: the  outputType or outputId config wrong, outputType must be 1 or 2, outputId must be goodsId or attributeType");
		}
	}

	private void checkFail(String errInfo) {
		Log4jManager.CHECK.error(errInfo);
		Log4jManager.checkFail();
	}
}
