package sacred.alliance.magic.app.map.point;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.message.item.FallItem;

import sacred.alliance.magic.domain.CollectPoint;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

public abstract class CollectablePoint<T extends AbstractRole> extends EventPoint<T>{
	public static Logger logger = LoggerFactory.getLogger(CollectablePoint.class);
	protected String instanceId ;
	protected List<FallItem> fallItemList ;
	protected CollectPoint collectPoint ;

	public abstract List<FallItem> getFallItemList(RoleInstance role);

	/**
	 * ʰȡ
	 * @param role
	 * @param itemId
	 */
	public abstract void pickup(RoleInstance role,int itemId);
	
	public String getInstanceId() {
		return instanceId;
	}

	public CollectPoint getCollectPoint() {
		return collectPoint;
	}
	
	
}
