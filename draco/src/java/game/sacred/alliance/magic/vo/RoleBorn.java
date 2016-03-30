package sacred.alliance.magic.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.GameContext;

public @Data class RoleBorn {
	
	private int level;
	private int equipId1;
	private byte placeType1 = 1;
	private byte bindType1 = -1 ;
	private int equipId2;
	private byte placeType2 = 1;
	private byte bindType2 = -1 ;
	private int equipId3;
	private byte placeType3 = 1;
	private byte bindType3 = -1  ;
	private int equipId4 ;
	private byte placeType4 = 1;
	private byte bindType4 = -1  ;
	private int equipId5 ;
	private byte placeType5  = 1;
	private byte bindType5 = -1 ;
	
	private String bornMapId ;
	private int bornX ;
	private int bornY ;
	
	private List<int[]> noviceEquipList = new ArrayList<int[]>();
	
	public Point getBornPoint(){
		return new Point(this.bornMapId,this.bornX,this.bornY);
	}
	
	private void initNoviceEquipList() {
		noviceEquipList.clear();
		this.initNoviceEquipList(equipId1, placeType1, bindType1);
		this.initNoviceEquipList(equipId2, placeType2, bindType2);
		this.initNoviceEquipList(equipId3, placeType3, bindType3);
		this.initNoviceEquipList(equipId4, placeType4, bindType4);
		this.initNoviceEquipList(equipId5, placeType5, bindType5);
	}
	
	private void initNoviceEquipList(int equipId,byte placeType,byte bindType){
		if (equipId != 0 && placeType != 0) {
			noviceEquipList.add(new int[] { equipId, placeType,bindType});
			if(null == GameContext.getGoodsApp().getGoodsBase(equipId)){
				this.logFail("RoleBorn config error,equipId=" + equipId + " not exist!");
			}
		}
	}
	

	public void init() {
		this.initNoviceEquipList();
		//判断地图id是否存在
		MapConfig mapConfig = GameContext.getMapApp().getMapConfig(this.bornMapId);
		if(null == mapConfig){
			this.logFail("RoleBorn config error,bornMapId=" + this.bornMapId + " not exist!");
		}
	}
	
	private void logFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
    
}
