package sacred.alliance.magic.app.arena.top;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.buff.Buff;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;

public @Data class TopMapConfig {

	private String mapId ;
	private short buffId1 ;
	private short buffId2 ;
	private int interval ;
	private int topRank ;
	private int maxRoleNum ;
	private List<Short> buffList = new ArrayList<Short>();
	
	public void init(){
		List<Short> list =  new ArrayList<Short>(); ;
		this.initBuff(list, this.buffId1);
		this.initBuff(list, this.buffId2);
		this.buffList = list ;
	}
	
	private void initBuff(List<Short> list,short buffId){
		if(buffId <=0){
			return ;
		}
		Buff buff = GameContext.getBuffApp().getBuff(buffId);
		if(null == buff){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("TopMapConfig config error,buff not exist.buffId=" + buffId);
			return ;
		}
		list.add(buffId);
	}
}
