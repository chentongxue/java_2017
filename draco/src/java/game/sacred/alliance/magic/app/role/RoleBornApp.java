package sacred.alliance.magic.app.role;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleBorn;
import sacred.alliance.magic.vo.RoleBornGuide;
import sacred.alliance.magic.vo.RoleBornHero;

import com.game.draco.message.item.UserLoginHeroShowItem;

public interface RoleBornApp extends Service{
	
	
	/**
	 * 根据职业获取RoleBorn（角色出生模版）
	 * @param careerId
	 * @return
	 */
	public RoleBorn getRoleBorn();
	
	public RoleBornGuide getRoleBornGuide() ;
	
	public List<UserLoginHeroShowItem> getBornHeroInfoList();
	
	public boolean isBornHero(int heroId);
	
	public Map<Integer,RoleBornHero> getRoleBornHeroMap() ;
	
}
