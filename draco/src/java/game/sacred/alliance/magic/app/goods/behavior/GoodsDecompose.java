package sacred.alliance.magic.app.goods.behavior;

import java.util.ArrayList;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.GoodsDecomposeParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.goods.decompose.DecomposeConfig;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsMaterial;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
/**
 * 分解
 */
public class GoodsDecompose extends AbstractGoodsBehavior {

	public GoodsDecompose() {
		this.behaviorType = GoodsBehaviorType.Decompose;
	}

	@Override
	public GoodsResult operate(AbstractParam param) {

		GoodsResult result = new GoodsResult();
		result.failure();
		
		GoodsDecomposeParam deParam = (GoodsDecomposeParam) param;
		RoleInstance role = deParam.getRole();
		RoleGoods src = deParam.getRolegoods();
		GoodsBase gb = deParam.getGoodsBase();
		short num = deParam.getNum();//要分解的物品数量
		
		DecomposeConfig cf = GameContext.getGoodsApp().getDecomposeConfig(gb.getId());
		if(cf == null){
			return result.setInfo(GameContext.getI18n().getText(
					TextId.GOODS_CANOT_DECOMPOSE));
		}
		
		int tarNum = 0;
		for (int i = 0; i < num; i++) {
			tarNum += RandomUtil.randomInt(cf.getMinNum(), cf.getMaxNum());
		}
		
		if(cf.isAttribute()){
			AttributeType att = cf.getAttribute();
			if(att == null){
				return result.setInfo(GameContext.getI18n().getText(
						TextId.GOODS_CANOT_DECOMPOSE));
			}
			//删除物品
			result = GameContext.getUserGoodsApp().deleteForBagByInstanceId(role, src.getId(), num, OutputConsumeType.goods_decompose);
			if (!result.isSuccess()) {
				return result.setInfo(GameContext.getI18n().getText(
						TextId.GOODS_CANOT_DECOMPOSE));
			}
			GameContext.getUserAttributeApp().changeAttribute(role, att, OperatorType.Add, tarNum, OutputConsumeType.goods_decompose_reward);
			role.getBehavior().notifyAttribute();
			
			//发送广播
			String tips = GameContext.getI18n().messageFormat(TextId.DECOMPOSE_REWARD_TIPS, att.getName(),tarNum);
			C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage(tips);
			role.getBehavior().sendMessage(msg);
			
			result.success();
			return result;
		}
		GoodsBase tar = GameContext.getGoodsApp().getGoodsBase(cf.getOutputId());
		if(tar == null){
			return result.setInfo(GameContext.getI18n().getText(
					TextId.GOODS_CANOT_DECOMPOSE));
		}
		
		ArrayList<GoodsOperateBean> operateList = new ArrayList<GoodsOperateBean>();
		GoodsOperateBean operatebean = new GoodsOperateBean();
		operatebean.setGoodsNum(tarNum);
		operatebean.setGoodsId(cf.getOutputId());
		operatebean.setBindType(gb.getBindingType());
		operateList.add(operatebean);

		result = GameContext.getUserGoodsApp().addDelGoodsForBag(role,
				operateList, OutputConsumeType.goods_decompose_reward,
				src, num, null,
				OutputConsumeType.goods_decompose);
		if (!result.isSuccess()) {
			return result.setInfo(GameContext.getI18n().getText(
					TextId.GOODS_CANOT_DECOMPOSE));
		}
		
		String tips = GameContext.getI18n().messageFormat(TextId.DECOMPOSE_REWARD_TIPS, tar.getName(),tarNum);
		C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage(tips);
		role.getBehavior().sendMessage(msg);

		result.success();

		return result;
	}

}
