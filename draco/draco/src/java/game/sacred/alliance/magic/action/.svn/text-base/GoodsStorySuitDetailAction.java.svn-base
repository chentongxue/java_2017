package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.goods.derive.StorySuitConfig;
import sacred.alliance.magic.app.goods.derive.StorySuitEquipConfig;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.request.C0584_GoodsStorySuitDetailReqMessage;
import com.game.draco.message.response.C0584_GoodsStorySuitDetailRespMessage;

public class GoodsStorySuitDetailAction extends BaseAction<C0584_GoodsStorySuitDetailReqMessage>{

	@Override
	public Message execute(ActionContext context, C0584_GoodsStorySuitDetailReqMessage reqMsg) {
		C0584_GoodsStorySuitDetailRespMessage resp = new C0584_GoodsStorySuitDetailRespMessage();
		try {
			RoleInstance role = this.getCurrentRole(context);
			short suitGroupId = reqMsg.getSuitGroupId();
			int level = reqMsg.getGoodsLevel();
			byte equipslotType = reqMsg.getEquipslotType();
			StorySuitEquipConfig config;
			if(level <= 0){
				config = GameContext.getGoodsApp().getStorySuitEquipAutoLevelConfig(role.getLevel(), suitGroupId, equipslotType);
			}else{
				config = GameContext.getGoodsApp().getStorySuitEquipConfig(suitGroupId, level, equipslotType);
			}
			StorySuitConfig suitConfig = GameContext.getGoodsApp().getStorySuitConfig(suitGroupId);
			if(null == suitConfig || null == config){
				return resp;
			}
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(config.getGoodsId());
			if(null != goodsBase){
				GoodsLiteNamedItem targetGoodsItem = goodsBase.getGoodsLiteNamedItem();
				if(null != targetGoodsItem){
					resp.setTargetGoodsItem(targetGoodsItem);
				}
			}
			Map<Integer,Integer> materialMap = config.getMaterialMap();
			if(!Util.isEmpty(materialMap)){
				List<GoodsLiteNamedItem> consumeGoodsList = new ArrayList<GoodsLiteNamedItem>();
				for(Map.Entry<Integer, Integer> entry : materialMap.entrySet()){
					if(null == entry){
						continue;
					}
					GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(entry.getKey());
					if(null == gb){
						continue;
					}
					int number = entry.getValue();
					GoodsLiteNamedItem item = gb.getGoodsLiteNamedItem();
					item.setNum((short) number);
					consumeGoodsList.add(item);
				}
				resp.setConsumeGoodsList(consumeGoodsList);
			}
			if(level <= 0){
				//依赖套装同部位同等级的配置
				StorySuitEquipConfig relyConfig = GameContext.getGoodsApp().getStorySuitEquipConfig(suitConfig.getRelySuitGroupId(), config.getLevel(), equipslotType);
				if(null != relyConfig){
					GoodsBase relygb = GameContext.getGoodsApp().getGoodsBase(relyConfig.getGoodsId());
					if(null != relygb){
						GoodsLiteNamedItem relyGoodsItem = relygb.getGoodsLiteNamedItem();
						if(null != relyGoodsItem){
							resp.setRelyGoodsItem(relyGoodsItem);
						}
					}
				}
			}
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + " execute error: ", e);
		}
		return resp;
	}

}
