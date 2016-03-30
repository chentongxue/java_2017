package sacred.alliance.magic.vo;

import java.util.Date;

import sacred.alliance.magic.base.RebornType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;

public abstract class MapCopyInstance extends MapInstance implements java.io.Serializable {
	
	@Override
	protected String createInstanceId() {
		instanceId = "cpm_" + instanceIdGenerator.incrementAndGet();
		return instanceId;
	}

	public MapCopyInstance(sacred.alliance.magic.app.map.Map map) {
		super(map);
	}

	
	protected void updateSub() throws ServiceException {
			super.updateSub();
	}
	
	@Override
	public void broadcastScreenMap(AbstractRole role, Message message) {
		super.broadcastMap(role, message, 0);
	}
	
	@Override
	public void broadcastScreenMap(AbstractRole role, Message message,int expireTime){
		super.broadcastMap(role, message, expireTime);
	}
	
	public boolean canDestroy() {
		if (this.hasPlayer()) {
			// 副本中有用户不能销毁
			return false;
		}
		// 与最后访问时间差大于xx分钟
		Date now = new Date();
		long copyLifeCycle = GameContext.getParasConfig().getCopyLifeCycle();
		if (now.getTime() - this.getLastAccessTime().getTime() < copyLifeCycle) {
			return false;
		}
		return true;
	}

	public void destroy() {
		super.destroy();
	}

	public void addAbstractRole(AbstractRole role) {
		super.addAbstractRole(role);
	}


	@Override
	public boolean canEnter(AbstractRole role) {
		return true;
	}
	
	@Override
	public void npcDeath(NpcInstance npc) {
		super.npcDeath(npc);
	}
	
	@Override
	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
	}

	@Override
	public void useGoods(int goodsId) {
		
	}

	@Override
	protected void deathLog(AbstractRole victim) {
		
	}
	
	@Override
	public Point getRebornPoint(RoleInstance role,RebornType type){
		if(RebornType.soul == type || RebornType.situ == type){
			return role.getCurrentPoint();
		}
		Point p = role.originalCopyBeforePoint();
		//!!!!! 千万不要调用下面方法，会和此方法发生相互调用
		//Point p = role.getCopyBeforePoint() ;
		if(null != p && p.isDefaultMap()){
			return p ;
		}
		return super.getRebornPoint(role, type);
	}
}
