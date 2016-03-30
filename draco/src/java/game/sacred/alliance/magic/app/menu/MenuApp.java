package sacred.alliance.magic.app.menu;

import java.util.List;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.item.MenuHintItem;
import com.game.draco.message.item.MenuItem;


public interface MenuApp extends Service{

	public List<MenuItem> getMenuList(RoleInstance role);
	
	/**
	 * 刷新所有人的菜单
	 * @param 
	 */
	public void refresh(MenuIdType menuType);
	
	/**
	 * 刷新某角色的菜单
	 * @param role
	 * @param 
	 */
	public void refresh(RoleInstance role, MenuIdType menuType);
	
	/**
	 * 角色升级时刷新菜单
	 * @param role
	 */
	public void onRoleLevelUp(RoleInstance role);
	
	public MenuConfig getMenuConfig(MenuIdType menuType) ;
	
	public MenuConfig getMenuConfigById(short menuId) ;
	
	public List<MenuHintItem> getHintList(RoleInstance role);
	
	/**
	 * 判断角色是否开启功能
	 */
	boolean isOpenFun(RoleInstance role,MenuIdType menuType);

}
