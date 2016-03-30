package sacred.alliance.magic.app.faction.godbeast;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

public @Data class FactionSoulFlyConfig {
	private int id;//神兽ID
	private int flyNum;//飞升次数
	private int flyLevel;//飞升需要的等级
	private int flyGoodsId;//飞升需要的物品ID
	private int flyGoodsNum;//飞升需要的物品数量
	private short skillId;//飞升获得的技能
	private short resId;//资源ID
	private List<Short> skillList = new ArrayList<Short>();//当前飞升等级拥有的技能List，根据等级不同，有多个技能
}
