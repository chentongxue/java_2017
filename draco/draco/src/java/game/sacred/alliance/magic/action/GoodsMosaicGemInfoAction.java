package sacred.alliance.magic.action;

import sacred.alliance.magic.app.goods.MosaicHole;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsGem;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsBaseGemItem;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.request.C0559_GoodsMosaicGemInfoReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C0504_GoodsInfoViewRespMessage;

public class GoodsMosaicGemInfoAction extends BaseAction<C0559_GoodsMosaicGemInfoReqMessage> {

	
	@Override
	public Message execute(ActionContext context, C0559_GoodsMosaicGemInfoReqMessage reqMsg) {
		try{
			RoleInstance role = this.getCurrentRole(context);
			RoleGoods roleGoods = GameContext.getUserGoodsApp().getRoleGoods(role, 
					StorageType.get(reqMsg.getBagType()), reqMsg.getGoodsId()) ;
			if(null == roleGoods){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.ERROR_INPUT));
			}
			MosaicHole[] holes = roleGoods.getMosaicHoles();
			int holeId = reqMsg.getHoleId() ;
			if(null == holes || 0 == holes.length || 0>holeId || holeId >= holes.length){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.ERROR_INPUT));
			}
			int goodsId = holes[holeId].getGoodsId();
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(null == goodsBase){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.ERROR_INPUT));
			}
			GoodsBaseItem goodsParItem = goodsBase.getGoodsBaseInfo(null);
			if(null == goodsParItem ){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.ERROR_INPUT));
			}
			GoodsGem gemTempalte = (GoodsGem)goodsBase;
			if(gemTempalte.getBindType() == BindingType.gain_binding.getType()){
				//如果宝石为拾取绑定,则替换为绑定
				GoodsBaseGemItem gemItem = (GoodsBaseGemItem)goodsParItem;
				gemItem.setBindType(BindingType.already_binding.getType());
			}
			
			C0504_GoodsInfoViewRespMessage baseMsg = new C0504_GoodsInfoViewRespMessage();
			baseMsg.setId(String.valueOf(goodsId));
			baseMsg.setBaseItem(goodsParItem);
			return baseMsg ;
		}catch(Exception e){
			logger.error("",e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.SYSTEM_ERROR));
			
		}
		
	}

}
