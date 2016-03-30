package sacred.alliance.magic.app.goods.derive;

import java.util.LinkedHashMap;
import java.util.Map;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

public @Data class EquipUpgradeConfig implements KeySupport<String>{

	private int level ;
	private byte qualityType ;
	private byte equipslotType;
	private int material1;
	private int num1;
	private int material2;
	private int num2;
	private int gameMoney;
	private String broadcast;
	
	private Map<Integer,Integer> materialMap = new LinkedHashMap<Integer,Integer>();
	@Override
	public String getKey() {
		return this.level + Cat.underline + this.qualityType + Cat.underline + this.equipslotType ;
	}
	
	private void initMaterial(int id, int num){
		if(id <=0 || num <=0){
			return ;
		}
		//判断材料是否重复
		if(this.materialMap.containsKey(id)){
			Log4jManager.CHECK.error("equip upgrage config error,have same material id, id=" + id);
			Log4jManager.checkFail();
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(id);
		if(null == gb){
			Log4jManager.CHECK.error("equip upgrage config error, material not exist, id=" + id);
			Log4jManager.checkFail();
		}
		this.materialMap.put(id, num);
	}
	
	public void init(){
		this.initMaterial(material1, num1);
		this.initMaterial(material2, num2);
	}
}
