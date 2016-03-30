package sacred.alliance.magic.app.attri;
import java.util.Map;

import sacred.alliance.magic.app.attri.config.AttriBattleScore;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.RoleLevelup;
import sacred.alliance.magic.vo.RoleInstance;

public  interface AttriApp extends Service {

	public  RoleLevelup getLevelup(int level);
	
	public int getEffectBattleScore(RoleInstance role);
	
	public int getAttriBattleScore(AttriBuffer attriBuffer) ;
	
	public Map<Byte, AttriBattleScore> getBattleScoreMap();
}
