package sacred.alliance.magic.app.active.siege;

import java.util.Collection;

import sacred.alliance.magic.app.active.ActiveSupport;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface ActiveSiegeApp extends ActiveSupport,Service{
	
	public Active getActive(short activeId);
	
	/**
	 * 價적쉽쟨
	 * @param roleList
	 */
	public void winAward(Active active, Collection<RoleInstance> roleList);

	/**
	 * 呵겨쉽쟨
	 * @param roleList
	 * @param min
	 * @param max
	 */
	public void failAward(Active active, Collection<RoleInstance> roleList, int min, int max);
	
	public SiegeMapConfig getSiegeMapConfig(String mapId) ;
}
