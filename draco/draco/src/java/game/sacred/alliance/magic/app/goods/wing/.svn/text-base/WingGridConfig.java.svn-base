package sacred.alliance.magic.app.goods.wing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.message.item.AttriTypeStrValueItem;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;

public @Data class WingGridConfig {

	private byte id;//命格ID
	private String name;//命格名称
	private int level;//等级
	private int maxExp;//升级所需经验
	private byte type1;//属性类型1
	private int value1;//属性值1
	private byte type2;//属性类型2
	private int value2;//属性值2
	private byte type3;//属性类型3
	private int value3;//属性值3
	private int wingLevel;//需要翅膀等级
	private int goodsId;//培养道具ID
	private int goodsNum;//道具数量
	private int gameMoney;//消耗游戏币
	private int addExp;//培养增加经验
	private short imageId;//图片
	private byte qualityType;//品质
	private String broadcast;//广播
	private String info;//材料不足提示信息
	private String openInfo;
	private Map<Byte, Integer> attrMap = new HashMap<Byte, Integer>();
	private List<AttriItem> attrList = new ArrayList<AttriItem>();
	private List<AttriTypeStrValueItem> displayAttriItemList = new ArrayList<AttriTypeStrValueItem>();
	
	public boolean isUpLevel(int exp){
		return exp >= this.maxExp;
	}
	
	public void init(){
		if(type1 > 0 && value1 > 0) {
			attrMap.put(type1, value1);
			attrList.add(new AttriItem(type1,value1,0f));
		}
		if(type2 > 0 && value2 > 0) {
			attrMap.put(type2, value2);
			attrList.add(new AttriItem(type2,value2,0f));
		}
		if(type3 > 0 && value3 > 0) {
			attrMap.put(type3, value3);
			attrList.add(new AttriItem(type3,value3,0f));
		}
		
		for (Map.Entry<Byte, Integer> entry : attrMap.entrySet()) {
			AttriTypeStrValueItem item = new AttriTypeStrValueItem();
			item.setType(entry.getKey());
			item.setValue(AttributeType.formatValue(entry.getKey(), entry.getValue()));
			displayAttriItemList.add(item);
		}
	}
}
