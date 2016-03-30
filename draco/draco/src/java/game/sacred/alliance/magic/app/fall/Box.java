package sacred.alliance.magic.app.fall;

import java.util.List;

import com.game.draco.message.item.FallItem;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

public interface Box {
	boolean isNull();
	boolean isEmpty();
	void put(int goodsId,int num,BindingType bindType);
	void remove(int goodsId);
	List<GoodsOperateBean> list();
	void destory() ;
	void pickup(AbstractRole picker,int goodsId);
	void notifyOwner();
	String getBoxId();
	boolean cache();
	List<FallItem> listFallItem();
	boolean isOwner(RoleInstance role) ;
	RoleInstance getOwner();
}
