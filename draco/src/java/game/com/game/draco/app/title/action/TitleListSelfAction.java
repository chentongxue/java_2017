package com.game.draco.app.title.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsTitle;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.title.TitleCategory;
import com.game.draco.app.title.domain.TitleRecord;
import com.game.draco.message.item.TitleCategoryItem;
import com.game.draco.message.item.TitleItem;
import com.game.draco.message.request.C2341_TitleListSelfReqMessage;
import com.game.draco.message.response.C2341_TitleListSelfRespMessage;

public class TitleListSelfAction extends BaseAction<C2341_TitleListSelfReqMessage>{

	@Override
	public Message execute(ActionContext context, C2341_TitleListSelfReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C2341_TitleListSelfRespMessage respMsg = new C2341_TitleListSelfRespMessage();
		Map<Integer, TitleRecord> titleMap = role.getTitleMap();
		if(Util.isEmpty(titleMap)){
			return respMsg ;
		}
		int titleNum = titleMap.size() ;
		
		Map<Integer,TitleCategory> categoryMap = GameContext.getTitleApp().getTitleCategoryMap();
		if(Util.isEmpty(categoryMap)){
			return respMsg ;
		}
		
		int titleNowNum = 0 ;
		List<TitleCategoryItem> itemList = new ArrayList<TitleCategoryItem>();
		for(TitleCategory c : categoryMap.values()){
			List<GoodsTitle> titleList = c.getTitleList();
			if(Util.isEmpty(titleList)){
				continue ;
			}
			TitleCategoryItem citem = null ;
			for(GoodsTitle title : titleList){
				int titleId = title.getId();
				TitleRecord record = titleMap.get(titleId);
				if(null == record){
					continue ;
				}
				if(null == citem){
					citem = new TitleCategoryItem();
					citem.setCategoryName(c.getCategoryName());
				}
				TitleItem titleItem = new TitleItem();
				titleItem.setTitleId(titleId);
				titleItem.setTitleName(title.getName());
				titleItem.setStatus(record.getActivateState());
				citem.getTitles().add(titleItem);
				titleNowNum ++ ;
				//提前结束循环
				if(titleNowNum >= titleNum){
					break ;
				}
			}
			if(null != citem){
				itemList.add(citem);
			}
			//提前结束循环
			if(titleNowNum >= titleNum){
				break ;
			}
		}
		respMsg.setItemList(itemList);
		return respMsg;
	}

}
