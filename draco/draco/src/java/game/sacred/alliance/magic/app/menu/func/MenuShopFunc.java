package sacred.alliance.magic.app.menu.func;

import sacred.alliance.magic.app.menu.MenuFunc;
import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.shop.type.ShopShowType;
import com.game.draco.message.item.MenuItem;
import com.game.draco.message.request.C2101_ShopGoodsListReqMessage;

public class MenuShopFunc extends MenuFunc{

	public MenuShopFunc() {
		super(MenuIdType.Shop);
	}

	
	@Override
	protected MenuItem createMenuItem(RoleInstance role) {
		if(role.getLevel() < this.menuConfig.getRoleLevel()){
			//没有达到等级
			return null ;
		}
		MenuItem item = new MenuItem();
		//其他赋值在外面
		return item;
	}


	@Override
	public Message createFuncReqMessage(RoleInstance role) {
		C2101_ShopGoodsListReqMessage reqMsg = new C2101_ShopGoodsListReqMessage();
		reqMsg.setType(ShopShowType.Hot.getType());
		return reqMsg;
	}


	@Override
	protected byte getMenuCountNotify(MenuIdType menuType) {
		// TODO Auto-generated method stub
		return 0;
	}

}
