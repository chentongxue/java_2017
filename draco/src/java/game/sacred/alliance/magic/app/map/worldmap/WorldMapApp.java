package sacred.alliance.magic.app.map.worldmap;

import java.util.List;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public interface WorldMapApp extends Service {
	
	public Result transfer(RoleInstance role,Point point,int cost);
	
	public Result transferConfirm(RoleInstance role,String confirmInfo) ;
	
	public List<WorldMapInfo> getAllWorldMapInfo();
	
	
}
