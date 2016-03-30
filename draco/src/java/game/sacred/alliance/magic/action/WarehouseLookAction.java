package sacred.alliance.magic.action;

import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0565_WarehouseLookReqMessage;

import sacred.alliance.magic.app.goods.WarehousePack;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;


public class WarehouseLookAction extends BaseAction<C0565_WarehouseLookReqMessage>{

	@Override
	public Message execute(ActionContext context, C0565_WarehouseLookReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		WarehousePack pack = role.getWarehousePack();
		if(null == pack) {
			GameContext.getUserWarehouseApp().loadWarehouseGoods(role);
		}
		List<RoleGoods> warehouseGoods = role.getWarehousePack().getAllGoods();
		
		//int warehouseType = StorageType.warehouse.getType();
		//Map<Integer,List<RoleGoods>> map = new LinkedHashMap<Integer,List<RoleGoods>>();
		//map.put(warehouseType, warehouseGoods);
		
		GameContext.getUserGoodsApp().syncAllGoodsGridMessage(role, warehouseGoods,StorageType.warehouse.getType());
		return null;
	}
}
