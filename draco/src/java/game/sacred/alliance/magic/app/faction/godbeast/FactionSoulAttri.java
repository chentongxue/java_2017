package sacred.alliance.magic.app.faction.godbeast;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.calct.FormulaCalct;
import sacred.alliance.magic.base.AttributeType;

import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.message.item.AttriTypeStrValueItem;

public @Data class FactionSoulAttri {
	
	private static List<AttributeType> list = new ArrayList<AttributeType>();
	
	private int id;//神兽ID
	private int level;//等级
	private int growValue;//成长值
	private String npcId;
	private String desc;//描述
	private List<AttriTypeStrValueItem> showAttrItemes = new ArrayList<AttriTypeStrValueItem>();
	public final static String NULL_SIGN = "N";
	
	public boolean isUpLevel(int growValue){
		return growValue >= this.growValue;
	}
	
	public boolean isFull(int curGrowValue){
		return curGrowValue >= growValue; 
	}
	
	public void initShowAttr(NpcTemplate npc){
		if (null == npc) {
			return ;
		}
		for (AttributeType attriType : list) {
			AttriTypeStrValueItem item = new AttriTypeStrValueItem();
			item.setType(attriType.getType());
			
			int attValue = npc.getAttriValue(attriType.getType());
			if (AttributeType.hit == attriType) {
				// 面板显示：实际值-10000
				attValue -= FormulaCalct.DEFAULT_HIT_VALUE;
			}
			item.setValue(AttributeType.formatValue(attriType.getType(), attValue));
			showAttrItemes.add(item);
		}
	}
	
	static {
		list.add(AttributeType.maxHP);//生命值上限
		list.add(AttributeType.atk);//攻击力
		list.add(AttributeType.rit);//防御力
		list.add(AttributeType.critAtk);//暴击值
		list.add(AttributeType.critRit);//免暴击值
		list.add(AttributeType.hit);//命中值
		list.add(AttributeType.dodge);//闪避值
		
		list.add(AttributeType.critAtkProb);//暴击伤害倍率
		list.add(AttributeType.sacredAtk);//神圣伤害
	}
}
