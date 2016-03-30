package sacred.alliance.magic.app.broadcast;

import sacred.alliance.magic.base.BroadcastType;
import sacred.alliance.magic.core.Service;

public interface BroadcastApp extends Service{
	/**
	 * 物品掉落广播或打开宝箱获得物品广播
	 * @param roleName
	 * @param goodsId
	 * @param targetId
	 * @param type
	 */
	public void broadCast(String roleName, int goodsId, String targetId, BroadcastType type);
}
