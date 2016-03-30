package sacred.alliance.magic.app.role.systemset;

import java.util.List;

import com.game.draco.message.item.SystemSetItem;

import sacred.alliance.magic.vo.RoleInstance;

public interface SystemSetApp {
	
	/**
	 * 初始化玩家的系统设置
	 * @param role
	 */
	public void initRoleSysSet(RoleInstance role);
	
	/**
	 * 玩家更改系统设置
	 * @param role
	 * @param sysSetList
	 * @return
	 */
	public boolean modifyRoleSysSet(RoleInstance role, List<SystemSetItem> sysSetList);
	
	/**
	 * 保存玩家的系统设置
	 * @param role
	 */
	public void saveSysSet(RoleInstance role);
	
	/**
	 * 获得角色的系统设置
	 * @param role
	 * @return
	 */
	public List<SystemSetItem> getSystemSetList(RoleInstance role);

	/**
	 * 系统设置入库失败后的日志
	 * @param role
	 */
	public void offlineLog(RoleInstance role);
	
}
