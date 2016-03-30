package com.game.draco.app.copy.action;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.copy.config.AttrConfig;
import com.game.draco.app.copy.config.CopyConfig;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.request.C0257_CopyDetailedInfoReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C0257_CopyDetailedInfoRespMessage;
import com.google.common.collect.Lists;

public class CopyDetailedInfoAction extends BaseAction<C0257_CopyDetailedInfoReqMessage>{

	@Override
	public Message execute(ActionContext context, C0257_CopyDetailedInfoReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if (role == null) {
			return null;
		}
		CopyConfig copyConfig = GameContext.getCopyLogicApp().getCopyConfig(req.getCopyId());
		if (copyConfig == null) {
			// 如果该副本不存在提示
			C0002_ErrorRespMessage resp = new C0002_ErrorRespMessage();
			resp.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return resp;
		}
		C0257_CopyDetailedInfoRespMessage resp = new C0257_CopyDetailedInfoRespMessage();
		resp.setCopyId(copyConfig.getCopyId());
		resp.setCopyType(copyConfig.getCountType());
		resp.setDifficulty(copyConfig.getDifficulty());
		byte maxCount = (byte)GameContext.getCopyLogicApp().getCopyMaxCount(role, req.getCopyId());
		resp.setRemCount((byte) (maxCount - GameContext.getCopyLogicApp().getCopyCurrCount(role, req.getCopyId())));
		resp.setMaxCount(maxCount);
		resp.setCountType(copyConfig.getCountType());
		resp.setMaxRoleNum((byte) copyConfig.getMaxEnterCount());
		resp.setMinRoleNum((byte) copyConfig.getMinEnterCount());
		resp.setShowRaids(GameContext.getCopyLogicApp().copyShowRaids(role, req.getCopyId()));
		resp.setShowBuy(GameContext.getCopyLogicApp().copyShowBuyNumber(req.getCopyId()));
		resp.setCopyContent(copyConfig.getContent());
		resp.setGoodsLiteList(this.getCopyFallItem(req.getCopyId()));
		resp.setAttrItemList(this.getCopyAttrItemList(req.getCopyId()));
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
	
	/** 
	 * 封装副本掉落信息
	 * @param copyId
	 * @return
	 */
	private List<AttriTypeValueItem> getCopyAttrItemList(short copyId){
		List<AttriTypeValueItem> list = Lists.newArrayList();
		List<AttrConfig> attrList = GameContext.getCopyLogicApp().getCopyAttrList(copyId);
		if(Util.isEmpty(attrList)){
			return list;
		}
		for(AttrConfig config : attrList){
			AttriTypeValueItem item = new AttriTypeValueItem();
			item.setAttriType(config.getAttrType());
			list.add(item);
		}
		return list;
	}
	
}
