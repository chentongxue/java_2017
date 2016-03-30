package sacred.alliance.magic.app.recoup;

import java.util.Collection;

import com.game.draco.app.AppSupport;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface RecoupApp extends Service, AppSupport {

	public boolean reload() ;
	
	public void receiveRecoup(RoleInstance role) ;
	
	public void insertRecoup(Recoup recoup) ;
	
	public void deleteRecoup(int id) ;
	
	public Collection<Recoup> getAllRecoup();
	
}
