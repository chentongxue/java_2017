package sacred.alliance.magic.action;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.request.C0504_GoodsInfoViewReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C0504_GoodsInfoViewRespMessage;

/**
 * 查看物品信息
 */
public class GoodsInfoViewAction extends BaseAction<C0504_GoodsInfoViewReqMessage> {
	
	@Override
	public Message execute(ActionContext context, C0504_GoodsInfoViewReqMessage reqMsg) {
		
		try {
			int targetId = reqMsg.getTargetId() ;
			RoleInstance targetRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(
					String.valueOf(reqMsg.getRoleId()));
			if(null == targetRole){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(), Status.Goods_Targ_Role_Not_Online.getTips());
			}
			StorageType storageType = StorageType.get(reqMsg.getContainerType());
			if(null == storageType){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.ERROR_INPUT));
			}
			String goodsInstanceId = reqMsg.getGoodsInstanceId();
			RoleGoods roleGoods = GameContext.getUserGoodsApp().getRoleGoods(targetRole, storageType, goodsInstanceId,targetId);
			if(null == roleGoods ){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.GOODS_NO_FOUND.getTips());
			}
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
			if(null == gb){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.GOODS_NO_FOUND.getTips());
			}
			//物品模板属性
			GoodsBaseItem goodsParItem = gb.getGoodsBaseInfo(roleGoods);
			if(null == goodsParItem ){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.GOODS_NO_FOUND.getTips());
			}
			
			C0504_GoodsInfoViewRespMessage baseMsg = new C0504_GoodsInfoViewRespMessage();
			baseMsg.setId(roleGoods.getId());
			baseMsg.setBaseItem(goodsParItem);
			return baseMsg ;
			
		} catch (Exception e) {
			logger.error("", e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
					this.getText(TextId.SYSTEM_ERROR));
		}
	}
}
