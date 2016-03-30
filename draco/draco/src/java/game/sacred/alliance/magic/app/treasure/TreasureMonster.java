package sacred.alliance.magic.app.treasure;

import java.util.HashMap;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class TreasureMonster implements KeySupport<Integer>{
	private int id;
	private String monster1;
	private short num1;
	private String monster2;
	private short num2;
	private String monster3;
	private short num3;
	private String broadcastInfo;
	private HashMap<String, Short> monstersMap = new HashMap<String, Short>();
	@Override
	public Integer getKey() {
		return this.id;
	}
	

	private void init(String monster,short num,String r_name,String r_num){
		if(!Util.isEmpty(monster) && num > 0){
			monstersMap.put(monster, num);
			//解析走马灯信息
			if(!Util.isEmpty(broadcastInfo)){
				broadcastInfo = broadcastInfo.replace(r_name, Wildcard.getNpcName(monster)).replace(r_num, ""+num);
			}
		}
	}
	
	public void init(){
		this.init(monster1, num1, Wildcard.Monster1_Name, Wildcard.Monster1_Num);
		this.init(monster2, num2, Wildcard.Monster2_Name, Wildcard.Monster2_Num);
		this.init(monster3, num3, Wildcard.Monster3_Name, Wildcard.Monster3_Num);
	}
	
	
	public String getBroadcastTips(RoleInstance role, String treasureName){
		return broadcastInfo.replace(Wildcard.Role_Name, role.getRoleName()).replace(Wildcard.Treasure_Name, treasureName);
	}
	
	
	private boolean haveCheck = false ;
	public void check(){
		if(haveCheck){
			return ;
		}
		haveCheck = true ;
		//判断配置的npc是否存在
		for(String monsterId : this.getMonstersMap().keySet()){
			if(null == GameContext.getNpcApp().getNpcTemplate(monsterId)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("treasure monsterid = "+ monsterId + " no exsit,id=" + this.getId() );
			}
		}
	}
	
}
