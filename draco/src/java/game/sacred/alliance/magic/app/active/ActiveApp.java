package sacred.alliance.magic.app.active;

import java.util.Collection;
import java.util.List;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.ActiveType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.hint.HintSupport;
import com.game.draco.message.item.ActiveBaseItem;
import com.game.draco.message.item.ActivePanelDetailBaseItem;

public interface ActiveApp extends Service, AppSupport, HintSupport{
	
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
	
	/**
	 * 获取活动
	 * @param activeType
	 * @return
	 */
	public Active getOnlyOneActive(ActiveType activeType);
	
	/**
	 * 获取活动逻辑
	 * @param activeType
	 * @return
	 */
	public ActiveSupport getActiveSupport(byte activeType) ;
	
	/**
	 * 获得活动列表(分类)
	 * */
	public List<ActiveBaseItem> obtainActiveList(RoleInstance role);
	
	/**
	 * 活动详细信息
	 * */
	public Message obtainActiveDetail(RoleInstance role, short activeId);
	
	/**
	 * 创建活动面包列表消息
	 * @param role
	 * @param selectedActiveId 默认选中的活动ID
	 * @return
	 */
	public Message createActivePanelListMsg(RoleInstance role);
	
	/**
	 * 给ActivePanelDetailBaseItem赋值
	 * @param item
	 * @param active
	 */
	public void buildActivePanelDetailBaseItem(ActivePanelDetailBaseItem item, Active active);
	
	/**
	 * 是否有免费次数的活动（红点提示）
	 * @param role
	 * @return
	 */
	public boolean hasHint(RoleInstance role);
	
}
