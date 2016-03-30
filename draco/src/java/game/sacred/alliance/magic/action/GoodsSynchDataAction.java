package sacred.alliance.magic.action;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.goods.DefaultBackpack;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0503_GoodsSynchDataReqMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GoodsSynchDataAction extends BaseAction<C0503_GoodsSynchDataReqMessage> {
	private List<StorageType> storageTypeList = Lists.newArrayList(StorageType.bag) ;
	
	@Override
	public Message execute(ActionContext context, C0503_GoodsSynchDataReqMessage reqMessage) {
		try {
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null ;
			}
			//!!! 客户端要求先装备 后背包
			//<容器类型，物品集合>
			Map<Integer,List<RoleGoods>> map = Maps.newLinkedHashMap();
			for(StorageType st : storageTypeList){
				DefaultBackpack pack = GameContext.getUserGoodsApp().getStorage(role, st,0);
				if(null == pack){
					continue ;
				}
				map.put(Integer.valueOf(st.getType()),pack.getAllGoods());
			}
			GameContext.getUserGoodsApp().syncAllGoodsGridMessage(role, map);
			
		} catch (Exception e) {
			logger.error("", e);
		}
		return null ;
	}
	
	
	
}
