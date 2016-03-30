package sacred.alliance.magic.app.active;

import java.util.Collection;
import java.util.List;

import com.game.draco.message.item.ActiveBaseItem;
import com.game.draco.message.item.ActivePanelDetailBaseItem;
import com.game.draco.message.response.C2301_ActivePanelDetailRespMessage;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.ActiveType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface ActiveApp extends Service{
	
	/**
	 * 获取所有活动
	 * @return
	 */
	public Collection<Active> getAllActive();
	
	/**
	 * 获取活动
	 * @param activeId
	 * @return
	 */
	public Active getActive(short activeId);
	
	public Active getOnlyOneActive(ActiveType activeType);
	
	//public Map<Integer, ActiveSupport> getActiveAppsMap();
	
	public ActiveSupport getActiveSupport(int activeType) ;
	
	/**
	 * 获得活动列表(分类)
	 * */
	public List<ActiveBaseItem> obtainActiveList(RoleInstance role);
	
	/**
	 * 活动详细信息
	 * */
	public C2301_ActivePanelDetailRespMessage obtainActiveDetail(RoleInstance role, short activeId);
	
	/**
	 * 角色下线 活动日志入库
	 * */
	public void saveRoleActive(RoleInstance role);
	
	/**
	 * 上线加载角色活动
	 * */
	public void loadRoleActive(RoleInstance role);
	
	/**
	 * 创建活动面包列表消息
	 * @param role
	 * @param selectedActiveId 默认选中的活动ID
	 * @return
	 */
	public Message createActivePanelListMsg(RoleInstance role, short selectedActiveId);
	
	/**
	 * 给ActivePanelDetailBaseItem赋值
	 * @param item
	 * @param active
	 */
	public void buildActivePanelDetailBaseItem(ActivePanelDetailBaseItem item, Active active);
	
}
