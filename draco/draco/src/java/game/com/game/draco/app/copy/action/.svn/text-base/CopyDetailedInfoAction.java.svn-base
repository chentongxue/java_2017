package com.game.draco.app.copy.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.copy.CopyConfig;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.request.C0257_CopyDetailedInfoReqMessage;
import com.game.draco.message.response.C0257_CopyDetailedInfoRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class CopyDetailedInfoAction extends BaseAction<C0257_CopyDetailedInfoReqMessage>{

	@Override
	public Message execute(ActionContext context, C0257_CopyDetailedInfoReqMessage req) {
		short copyId = req.getCopyId();
		C0257_CopyDetailedInfoRespMessage resp = new C0257_CopyDetailedInfoRespMessage();
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return resp;
		}
		CopyConfig copyConfig = GameContext.getCopyLogicApp().getCopyConfig(copyId);
		if(copyConfig == null){
			return resp;
		}
		resp.setCopyId(copyConfig.getCopyId());
		resp.setCopyType(copyConfig.getCountType());
		resp.setDifficulty(copyConfig.getDifficulty());
		resp.setCurrCount((byte) GameContext.getCopyLogicApp().getCopyCurrCount(role, copyId));
		resp.setMaxCount((byte) copyConfig.getCount());
		resp.setCountType(copyConfig.getCountType());
		resp.setPower(copyConfig.getPower());
		resp.setCopyContent(copyConfig.getContent());
		resp.setFallItems(this.getCopyFallItem(copyId));
		return resp;
	}

	/** 
	 * 封装副本掉落信息
	 * @param copyId
	 * @return
	 */
	private List<GoodsLiteItem> getCopyFallItem(short copyId){
		List<GoodsLiteItem> list = new ArrayList<GoodsLiteItem>();
		List<Integer> goodsList = GameContext.getCopyLogicApp().getCopyFalls(copyId);
		if(Util.isEmpty(goodsList)){
			return list;
		}
		for(int goodsId : goodsList){
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(goodsBase == null){
				continue ;
			}
			list.add(goodsBase.getGoodsLiteItem());
		}
		return list;
	}
	
}
