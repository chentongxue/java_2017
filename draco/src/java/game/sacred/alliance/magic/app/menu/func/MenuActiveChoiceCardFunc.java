package sacred.alliance.magic.app.menu.func;

import sacred.alliance.magic.app.menu.MenuFunc;
import sacred.alliance.magic.app.menu.MenuIdType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.item.MenuItem;
import com.game.draco.message.request.C2810_CardGoldReqMessage;
import com.game.draco.message.request.C2811_CardGemReqMessage;
import com.game.draco.message.request.C2812_CardActivityReqMessage;

public class MenuActiveChoiceCardFunc extends MenuFunc{

	public MenuActiveChoiceCardFunc() {
		super(MenuIdType.ChoiceCard);
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
		C2810_CardGoldReqMessage reqGoldMsg = new C2810_CardGoldReqMessage();
		role.getBehavior().addCumulateEvent(reqGoldMsg);
		C2811_CardGemReqMessage reqGemMsg = new C2811_CardGemReqMessage();
		role.getBehavior().addCumulateEvent(reqGemMsg);
//		C2812_CardActivityReqMessage reqActivityMsg = new C2812_CardActivityReqMessage();
//		role.getBehavior().addCumulateEvent(reqActivityMsg);
		return null;
	}


}
