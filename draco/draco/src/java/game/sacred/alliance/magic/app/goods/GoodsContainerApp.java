package sacred.alliance.magic.app.goods;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 
 *교관윈념宮밑쌈왯
 *
 */
public interface GoodsContainerApp extends Service{

	public void expand(RoleInstance role) ;
	
	public void expandExec(RoleInstance role,String info) ;
}
