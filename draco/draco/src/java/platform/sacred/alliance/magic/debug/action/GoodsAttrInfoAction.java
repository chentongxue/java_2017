package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.debug.message.item.GoodsAttrInfoItem;
import com.game.draco.debug.message.request.C10046_GoodsAttrInfoReqMessage;
import com.game.draco.debug.message.response.C10046_GoodsAttrInfoRespMessage;
import com.game.draco.message.item.GoodsBaseItem;

import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsAttrInfoAction extends ActionSupport<C10046_GoodsAttrInfoReqMessage> {
	
	@Override
	public Message execute(ActionContext context, C10046_GoodsAttrInfoReqMessage req) {
		C10046_GoodsAttrInfoRespMessage resp = new C10046_GoodsAttrInfoRespMessage();
		try{
			String roleId = req.getRoleId();
			String goodsInstanceId = req.getGoodsInstanceId();
			RoleInstance role = GameContext.getUserRoleApp().getRoleByRoleId(roleId);
			if(null == role){
				return resp;
			}
			StorageType storageType = StorageType.get(req.getContainerType());
			if(null == storageType){
				return resp ;
			}
			RoleGoods roleGoods = GameContext.getUserGoodsApp().getRoleGoods(role,storageType,goodsInstanceId);
			if(null == roleGoods ){
				return resp;
			}
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
			if(null == gb){
				return resp ;
			}
			List<GoodsAttrInfoItem> goodsAttrInfoItemList = new ArrayList<GoodsAttrInfoItem>();
			//物品模板属性
			GoodsBaseItem goodsParItem = gb.getGoodsBaseInfo(roleGoods);
			this.buildItem(goodsAttrInfoItemList, goodsParItem.toAttrString());
			/*GoodsDetailItem goodsInfoItem = gb.getGoodsDetailItem(roleGoods);
			if(null !=  goodsInfoItem){
				this.buildItem(goodsAttrInfoItemList, goodsInfoItem.toAttrString());
			}*/
			resp.setGoodsInfoList(goodsAttrInfoItemList);
			return resp;
		}catch(Exception e){
			logger.error("GoodsAttrInfoAction error: ",e);
			return resp;
		}
	}
	
	private void buildItem(List<GoodsAttrInfoItem> goodsAttrInfoItemList, String infoStr){
		try{
			for(String goodsInfo : infoStr.split(Cat.comma)){
				if(goodsInfo.isEmpty()){
					continue;
				}
				String[] attrInfo = goodsInfo.split(Cat.equ);
				GoodsAttrInfoItem item = new GoodsAttrInfoItem();
				item.setName(attrInfo[0]);
				item.setValue(attrInfo[1]);
				goodsAttrInfoItemList.add(item);
			}
		}catch(Exception e){
			logger.error("GoodsAttrInfoAction buildItem error: ",e);
		}
	}
	
}
