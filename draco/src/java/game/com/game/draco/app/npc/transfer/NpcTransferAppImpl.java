package com.game.draco.app.npc.transfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.ChangeMapResult;
import sacred.alliance.magic.base.GoodsUseType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTitle;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.StringUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.type.NpcFuncShowType;
import com.game.draco.message.item.NpcFunctionItem;
import com.game.draco.message.request.C1606_NpcTransferReqMessage;

public class NpcTransferAppImpl implements NpcTransferApp{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	/**key=Id value=Npc传送信息*/
	private java.util.Map<String,NpcTransfer> npcTransferMap1 = new HashMap<String,NpcTransfer>();
	/**key=npcId value=Npc传送信息集合*/
	private java.util.Map<String,List<NpcTransfer>> npcTransferMap2 = new HashMap<String,List<NpcTransfer>>();
	private static final short TRANSFERCMD = new C1606_NpcTransferReqMessage().getCommandId();
	
	@Override
	public Result transferRole(RoleInstance role, String param){
		Result rs = new Result().failure();
		try {
			String transferId = analysisTransferId(param);
			NpcTransfer npcTransfer = npcTransferMap1.get(transferId);
			if(null == npcTransfer){
				return rs.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			}
			//条件不满足(玩家长时间停留对话框)
			boolean suit = npcTransfer.suitAllCondition(role);
			if(!suit){
				return rs.setInfo(GameContext.getI18n().getText(TextId.TRANSFER_NOTSUIT_CONDITION)); 
			}
			//消耗不足
			int cost = npcTransfer.getSilver();
			//【游戏币/潜能/钻石不足弹板】 判断
			Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.gameMoney, cost);
			if(ar.isIgnore()){
				return ar;
			}
			if(!ar.isSuccess()){
				return rs.setInfo(GameContext.getI18n().getText(TextId.TRANSFER_MONEY_NOT_ENOUGH));
			}
//			if(role.getSilverMoney() < cost){
//				return GameContext.getI18n().getText(TextId.TRANSFER_MONEY_NOT_ENOUGH);
//			}
			int titleId = npcTransfer.getAskTitleId();
			//是否有对应称号
			if(titleId != 0 && 
					!GameContext.getTitleApp().isExistEffectiveTitle(role, titleId)){
				GoodsTitle gt = GameContext.getGoodsApp().getGoodsTemplate(GoodsTitle.class, titleId);
				String name = "" ;
				if(null != gt){
					name = gt.getName();
				}
				return rs.setInfo(GameContext.getI18n().messageFormat(TextId.TRANSFER_ASK_TITLE,name));
			}
			//是否有对应的物品
			int goodsId = npcTransfer.getNeedGoodsId();
			if(goodsId > 0){
				int ownNum = role.getRoleBackpack().countByGoodsId(goodsId);
				int goodsNum = npcTransfer.getNeedGoodsNum();
				if(ownNum < goodsNum){
					String goodsName = "";
					GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
					if(null != gb){
						goodsName = gb.getColorName();
					}
					return rs.setInfo(GameContext.getI18n().messageFormat(TextId.TRANSFER_GOODS_NOT_ENOUGH, goodsNum, goodsName));
				}
			}
			String tarMapId = npcTransfer.getMapId();
			int tarMapX = npcTransfer.getX();
			int tarMapY = npcTransfer.getY();
			Point tarPoint = new Point(tarMapId, tarMapX, tarMapY);
			ChangeMapResult result = null ;
			try {
				result = GameContext.getUserMapApp().changeMap(role, tarPoint);
			} catch (ServiceException e) {
				this.logger.error(this.getClass().getName() + ".transferRole error: ", e);
			}
			if(!result.isSuccess()) {
				return rs.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR)) ;
			}
			//扣除物品
			if(goodsId > 0){
				GoodsUseType goodsUseType = npcTransfer.getGoodsUseType();
				int goodsNum = npcTransfer.getNeedGoodsNum();
				if(GoodsUseType.Consume == goodsUseType && goodsNum > 0){
					GameContext.getUserGoodsApp().deleteForBag(role, goodsId, goodsNum, OutputConsumeType.npc_transmit);
				}
			}
			//扣除消耗
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney
					,OperatorType.Decrease, cost, OutputConsumeType.npc_transmit);
			role.getBehavior().notifyAttribute();
//			return null;
			return rs.success();
		} catch (RuntimeException e) {
			this.logger.error(this.getClass().getName() + ".transferRole error: ", e);
			return rs.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
		}
	}
	
	/**
	 * 解析参数获取TransferId
	 * */
	private String analysisTransferId(String param){
		if(StringUtil.nullOrEmpty(param)){
			return null;
		}
		String[] params = param.split(Cat.colon);
		if(params.length != 1){
			return null;
		}
		String transferId = params[0];
		return transferId;
	}
	
	public void start(){
		loadNpcTransferXls();
	}
	
	private void loadNpcTransferXls(){
		String fileName = XlsSheetNameType.npc_transfer.getXlsName();
		String sheetName = XlsSheetNameType.npc_transfer.getSheetName();
		String info = "load xls error: fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try{
			String path = GameContext.getPathConfig().getXlsPath();
			npcTransferMap1 = XlsPojoUtil.sheetToLinkedMap(path+fileName, sheetName, NpcTransfer.class);
			for(NpcTransfer npcTransfer : this.npcTransferMap1.values()){
				if(null == npcTransfer){
					continue;
				}
				//初始化数据
				npcTransfer.init(info);
				String npcId = npcTransfer.getNpcId();
				if(!npcTransferMap2.containsKey(npcId)){
					npcTransferMap2.put(npcId, new ArrayList<NpcTransfer>());
				}
				npcTransferMap2.get(npcId).add(npcTransfer);
			}
		}catch(Exception e){
			Log4jManager.CHECK.error(info);
			Log4jManager.checkFail();
		}
	}
	
	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void stop() {
	}

	@Override
	public void reload() throws ServiceException {
		npcTransferMap1.clear();
		npcTransferMap2.clear();
		loadNpcTransferXls();	
	}

	@Override
	public List<NpcFunctionItem> getNpcFunction(RoleInstance role, NpcInstance npc) {
		List<NpcTransfer> transfers = npcTransferMap2.get(npc.getNpcid());
		if(Util.isEmpty(transfers)){
			return null;
		}
		List<NpcFunctionItem> list = new ArrayList<NpcFunctionItem>();
		for(NpcTransfer transfer:transfers){
			if(!transfer.suitAllCondition(role)){
				continue;
			}
			Map map = GameContext.getMapApp().getMap(transfer.getMapId());
			if(null == map){
				continue;
			}
			NpcFunctionItem item = new NpcFunctionItem();
			item.setType(NpcFuncShowType.Confirm.getType()); //二次确认
			item.setCommandId(TRANSFERCMD);
			String mapName = map.getMapConfig().getMapdisplayname();
			item.setTitle(transfer.getTitle());
			item.setParam(String.valueOf(transfer.getId()));
			if(transfer.getSilver()>0){
				String txt = GameContext.getI18n().messageFormat(TextId.NPC_TRANSFER_FEE_TIPS,mapName,
						String.valueOf(transfer.getSilver()));
				item.setContent(txt);
			}else{
				//免费不需要二次确认
				item.setType(NpcFuncShowType.Default.getType()); 
			}
			list.add(item);
		}
		return list;
	}
	
}
