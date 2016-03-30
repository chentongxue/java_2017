package sacred.alliance.magic.app.goods.derive;

import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

import com.google.common.collect.Maps;

public @Data class EquipStrengthenVip implements KeySupport<String>{
	private int vipLevel ;
	private int weight1 ;
	private int weight2 ;
	private int weight3 ;
	private int weight4 ;
	private int weight5 ;
	
	private Map<Integer,Integer> weightsMap = Maps.newHashMap() ;
	
	@Override
	public String getKey(){
		return String.valueOf(vipLevel);
	}
	
	public void init(){
		this.init(1,this.weight1);
		this.init(2,this.weight2);
		this.init(3,this.weight3);
		this.init(4,this.weight4);
		this.init(5,this.weight5);
	}
	
	public void init(int level,int weight){
		if(weight <=0){
			return ;
		}
		this.weightsMap.put(level, weight) ;
	}
}
