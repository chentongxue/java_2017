package sacred.alliance.magic.app.goods.derive;

import lombok.Data;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.GameContext;

public @Data class EquipRecatingConfig implements KeySupport<String>{

	private int quality;
	private int star ;
	private int material;
	private int num;
	private int gameMoney;
	private int goldMoney;
	
	@Override
	public String getKey(){
		return genKey(this.quality,this.star) ;
	}
	
	public static String genKey(int quality,int star){
		return quality + "_" + star ;
	}
	
	
	public void init(String fileInfo){
		String info = fileInfo + "quality=" + this.quality + " star=" +star +" ";
		if(this.material > 0 ){
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(this.material);
			if(null == gb){
				this.checkFail(info + "material="+ this.material + ", goods not exist.");
			}
			if(this.num <= 0){
				this.checkFail(info + "num must be greater than 0");
			}
		}
		if(this.gameMoney < 0){
			this.checkFail(info + "gameMoney is error.");
		}
		if(this.goldMoney <= 0){
			this.checkFail(info + "goldMoney is not config.");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
