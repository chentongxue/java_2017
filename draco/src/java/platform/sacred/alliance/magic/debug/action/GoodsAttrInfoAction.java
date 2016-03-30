package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.goods.HeroEquipBackpack;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.rune.domain.MosaicRune;
import com.game.draco.debug.message.item.GoodsAttrInfoItem;
import com.game.draco.debug.message.request.C10046_GoodsAttrInfoReqMessage;
import com.game.draco.debug.message.response.C10046_GoodsAttrInfoRespMessage;
import com.game.draco.message.item.GoodsBaseItem;

public class GoodsAttrInfoAction extends ActionSupport<C10046_GoodsAttrInfoReqMessage> {
	public static final byte CARRY_TYPE_HERO_EQUIP = 1;//英雄装备
	public static final byte CARRY_TYPE_PET_RUNE = 2;// 宠物符文
	
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
			RoleGoods roleGoods = null;
			if(null == storageType){
				byte carryType = req.getCarryType();
				int carrierId = req.getCarrierId();
				switch (carryType) {
				case CARRY_TYPE_HERO_EQUIP:
					roleGoods = getHeroEquipRoleGoods(roleId, carrierId, goodsInstanceId);
					break;
				case CARRY_TYPE_PET_RUNE:
					roleGoods = getPetRuneRoleGoods(roleId, carrierId, goodsInstanceId);
					break;
				default:
					return resp ;
				}
			}else{
				roleGoods = GameContext.getUserGoodsApp().getRoleGoods(role,storageType,goodsInstanceId,0);
			}
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
	
	private RoleGoods getPetRuneRoleGoods(String roleId, int carrierId,
			String goodsInstanceId) {
		RolePet pet = null;
		int petId = carrierId;
		if(GameContext.getOnlineCenter().isOnlineByRoleId(roleId)){
			pet = GameContext.getUserPetApp().getRolePet(roleId, petId);
		}else{
			pet = GameContext.getBaseDAO().selectEntity(RolePet.class, RolePet.MASTER_ID, roleId, RolePet.PET_ID, petId);
			if(pet == null){
				return null;
			}
		}
		//符文
		for(MosaicRune r:pet.getMosaicRuneList()){
			RoleGoods rg = r.getRoleGoods(roleId);
			if(rg.getId() == goodsInstanceId){}
			return rg;
		}
		return null;
	}

	private RoleGoods getHeroEquipRoleGoods(String roleId, int carrierId,
			String instanceId) {
		int heroId = carrierId;
		//获得英雄的装备
		Map<String,List<RoleGoods>> equipMap = GameContext.getHeroApp().buildHeroEquipMap(roleId);
		if(Util.isEmpty(equipMap)){
			return null;
		}
		List<RoleGoods>  list = equipMap.get(String.valueOf(heroId));
		
		if(Util.isEmpty(list)){
			return null;
		}
		RoleInstance role;
		try {
			role = GameContext.getUserRoleApp().getRoleByRoleId(roleId);
		} catch (ServiceException e) {
			e.printStackTrace();
			return null;
		}
		HeroEquipBackpack equippack = new HeroEquipBackpack(role,
				ParasConstant.HERO_EQUIP_MAX_NUM,heroId);
		equippack.initGoods(list);
		return equippack.getRoleGoodsByInstanceId(instanceId);
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
