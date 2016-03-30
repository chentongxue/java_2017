package com.game.draco.app.title.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTitle;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.title.TitleCategory;
import com.game.draco.app.title.TitleStatus;
import com.game.draco.app.title.domain.TitleRecord;
import com.game.draco.message.item.TitleCategoryItem;
import com.game.draco.message.item.TitleItem;
import com.game.draco.message.request.C2340_TitleListAllReqMessage;
import com.game.draco.message.response.C2340_TitleListAllRespMessage;

public class TitleListAllAction extends BaseAction<C2340_TitleListAllReqMessage>{

	@Override
	public Message execute(ActionContext context, C2340_TitleListAllReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C2340_TitleListAllRespMessage respMsg = new C2340_TitleListAllRespMessage();
		Map<Integer,TitleCategory> categoryMap = GameContext.getTitleApp().getTitleCategoryMap();
		if(Util.isEmpty(categoryMap)){
			return respMsg ;
		}
		/**
		 * 角色背包中存在的称号物品(为了性能这里最多便利一次背包)
		 */
		Set<Integer> roleTitleInBackpack = null ;
		List<TitleCategoryItem> itemList = new ArrayList<TitleCategoryItem>();
		for(TitleCategory c : categoryMap.values()){
			List<GoodsTitle> titleList = c.getTitleList();
			if(Util.isEmpty(titleList)){
				continue ;
			}
			TitleCategoryItem citem = new TitleCategoryItem();
			citem.setCategoryName(c.getCategoryName());
			for(GoodsTitle title : titleList){
				//获得背包内物品
				if(null == roleTitleInBackpack){
					roleTitleInBackpack = this.getRoleTitleInBackpack(role);
				}
				if(!this.canShow(title, role,roleTitleInBackpack)){
					//没有在显示时间内需要判断当前角色是否有此称号（有效）
					//，或者背包内有此称号物品
					continue ;
				}
				TitleItem titleItem = new TitleItem();
				titleItem.setTitleId(title.getId());
				titleItem.setTitleName(title.getName());
				TitleStatus status = GameContext.getTitleApp().getTitleStatus(role,title.getId());
				titleItem.setStatus(status.getType());
				citem.getTitles().add(titleItem);
			}
			//为空时不显示,否则可能会导致客户端崩溃
			if(!Util.isEmpty(citem.getTitles())){
				itemList.add(citem);
			}
		}
		respMsg.setItemList(itemList);
		return respMsg;
	}

	/**
	 * 
	 * @param role
	 * @return 即使没有相关物品，不能返回NULL，否则会多次遍历背包
	 */
	private Set<Integer> getRoleTitleInBackpack(RoleInstance role) {
		Set<Integer> result = new HashSet<Integer>();
		List<RoleGoods> goodsList = role.getRoleBackpack().getAllGoods();
		if (Util.isEmpty(goodsList)) {
			return result;
		}
		for (RoleGoods rg : goodsList) {
			int goodsId = rg.getGoodsId();
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(
					goodsId);
			if (null == goodsBase
					|| goodsBase.getGoodsType() != GoodsType.GoodsTitle
							.getType()) {
				continue;
			}
			result.add(goodsId);
		}
		return result;
	}
	
	private boolean canShow(GoodsTitle title,RoleInstance role,Set<Integer> roleTitleInBackpack){
		if(title.isInShowTime()){
			return true ;
		}
		TitleRecord record = role.getTitleMap().get(title.getId());
		if(null != record && !record.isTimeout()){
			return true ;
		}
		//判断背包中是否存在此类型的物品
		if(null == roleTitleInBackpack){
			return false ;
		}
		return roleTitleInBackpack.contains(title.getId());
	}
}
