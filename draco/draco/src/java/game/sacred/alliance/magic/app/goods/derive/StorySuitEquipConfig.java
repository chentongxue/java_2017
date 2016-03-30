package sacred.alliance.magic.app.goods.derive;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.base.EquipslotType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.GameContext;

@Data
public class StorySuitEquipConfig implements KeySupport<String> {

	private short suitGroupId;//套装组ID
	private int level;//套装等级
	private byte equipslotType;//装备部位
	private int goodsId;//装备模版ID
	private int materialId1;//材料1ID
	private int materialNum1;//材料1数量
	private int materialId2;//材料2ID
	private int materialNum2;//材料2数量
	private int goldMoney;//元宝获得
	
	private Map<Integer,Integer> materialMap = new HashMap<Integer,Integer>();
	
	@Override
	public String getKey() {
		return this.suitGroupId + Cat.underline + this.level + Cat.underline + this.equipslotType;
	}
	
	public void init(String fileInfo){
		String info = fileInfo + "suitGroupId = " + this.suitGroupId + ", level = " + this.level + ", equipslotType = " + this.equipslotType + ".";
		if(this.suitGroupId <= 0){
			this.checkFail(info + "suitGroupId must greater than 0");
		}
		if(this.level < 0){
			this.checkFail(info + "level is error.");
		}
		if(null == EquipslotType.get(this.equipslotType)){
			this.checkFail(info + "equipslotType not exist.");
		}
		GoodsEquipment eq = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, goodsId);
		if(null == eq){
			this.checkFail(info + "goodsId = " + this.goodsId + ",goods is not exist or not Equipment.");
		}
		if(this.suitGroupId != eq.getSuitGroupId()){
			this.checkFail(info + "suitGroupId is not equal to Equipment's suitGroupId.");
		}
		if(this.equipslotType != eq.getEquipslotType()){
			this.checkFail(info + "equipslotType is not equal to Equipment's equipslotType.");
		}
		if(this.materialId1 > 0 && this.materialNum1 > 0){
			if(null == GameContext.getGoodsApp().getGoodsBase(this.materialId1)){
				this.checkFail(info + "materialId1 is not exist.");
			}
			this.materialMap.put(this.materialId1, this.materialNum1);
		}
		if(this.materialId2 > 0 && this.materialNum2 > 0){
			if(null == GameContext.getGoodsApp().getGoodsBase(this.materialId2)){
				this.checkFail(info + "materialId2 is not exist.");
			}
			this.materialMap.put(this.materialId2, this.materialNum2);
		}
		if(this.goldMoney < 0){
			this.checkFail(info + "goldMoney must be greater than 0.");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
