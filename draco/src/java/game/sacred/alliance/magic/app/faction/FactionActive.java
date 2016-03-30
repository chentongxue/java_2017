//package sacred.alliance.magic.app.faction;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import lombok.Data;
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.constant.Cat;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.domain.GoodsBase;
//import sacred.alliance.magic.util.KeySupport;
//import sacred.alliance.magic.util.Log4jManager;
//import sacred.alliance.magic.util.Util;
//
//import com.game.draco.GameContext;
//
///**
// * 门派活动
// */
//public @Data class FactionActive implements KeySupport<Integer> {
//	private int type;
//	
//	private String activeName;
//	
//	private int openLevel;
//	
//	private String desc;
//	
//	private String goodsId;
//	
////	private int count;
//	
//	private short param;
//	
//	private List<Integer> gooodsIdList = new ArrayList<Integer>();
//
//	@Override
//	public Integer getKey() {
//		return this.type;
//	}
//	
//	public void init(){
//		if(Util.isEmpty(this.goodsId)){
//			return;
//		}
//		String[] ids = this.goodsId.split(Cat.comma);
//		for(String id : ids){
//			if(!Util.isNumeric(id)){
//				this.checkFail("FactionActive: goodsId=" + id + ",it's not numeric.");
//				continue ;
//			}
//			int goodsId = Integer.parseInt(id);
//			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
//			if(null == gb){
//				this.checkFail("FactionActive: goodsId=" + id + ",it's not exist.");
//				continue ;
//			}
//			this.gooodsIdList.add(goodsId);
//		}
//	}
//	
//	public Result condition(Faction faction){
//		Result result = new Result();
//		if(faction.getFactionLevel() < openLevel) {
//			String str = GameContext.getI18n().messageFormat(TextId.Faction_ACTIVE_OPEN_LEVEL, this.openLevel);
//			return result.setInfo(str);
//		}
//		return result.success();
//	}
//	
//	private void checkFail(String info){
//		Log4jManager.CHECK.error(info);
//		Log4jManager.checkFail();
//	}
//}
