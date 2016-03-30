package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.debug.message.item.GoodsAddItem;
import com.game.draco.debug.message.request.C10005_GoodsAddReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.vo.RoleInstance;

/*添加物品*/
public class GoodsAddAction extends ActionSupport<C10005_GoodsAddReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10005_GoodsAddReqMessage reqMsg) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		try{
			String roleName = reqMsg.getRoleName();
			List<GoodsAddItem> items = reqMsg.getItems();
			if(items!=null && items.size()>0) {
				List<GoodsOperateBean> goodsList = new ArrayList<GoodsOperateBean>();
				for(GoodsAddItem item : items){
					goodsList.add(new GoodsOperateBean(item.getGoodsId(), item.getNumber(), BindingType.template));
				}
				RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleName(roleName);
				if(null == role){
					resp.setInfo(GameContext.getI18n().getText(TextId.ROLE_OFFLINE_FAIL));
					return resp;
				}
				GameContext.getFallApp().fallBox(role, goodsList,OutputConsumeType.gm_output,
						role.getMapX(),role.getMapY(),false);
				resp.setType((byte)RespTypeStatus.SUCCESS);
			}
			return resp;
		}catch(Exception e){
			logger.error("GoodsAddAction error: ",e);
			resp.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return resp;
		}
	}
	
}
