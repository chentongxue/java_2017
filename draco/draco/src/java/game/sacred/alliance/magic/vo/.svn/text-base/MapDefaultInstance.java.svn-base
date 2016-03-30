package sacred.alliance.magic.vo;

import sacred.alliance.magic.app.map.Map;

public class MapDefaultInstance extends MapInstance{

	public MapDefaultInstance(Map map){
		super(map);
	}
	
	protected String createInstanceId(){
		instanceId = map.getMapId() ;
		return instanceId;
	}
	
	public boolean canDestroy(){
		return false;
	}
	
	public boolean canEnter(AbstractRole role){
		return true;
	}

	@Override
	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
//		this.notifyNpcAi(victim);
	}

	@Override
	public void useGoods(int goodsId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void deathLog(AbstractRole victim) {
		// TODO Auto-generated method stub
		
	}
	
}
