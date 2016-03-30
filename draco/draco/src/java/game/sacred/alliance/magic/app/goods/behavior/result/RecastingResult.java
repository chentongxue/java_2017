package sacred.alliance.magic.app.goods.behavior.result;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.derive.EquipRecatingConfig;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;

public @Data class RecastingResult extends Result{
	
	private EquipRecatingConfig config ;
	private GoodsEquipment template ;
	private RoleGoods equipGoods ;
	private List<AttriItem> lockAttriItems ;
	private ArrayList<AttriItem> newAttris;
	private int delGoodsId;//消耗道具ID
	private int delGoodsNum;//消耗道具数量
	private int delGameMoney;//消耗游戏币
	private int delGoldMoney;//消耗元宝
}
