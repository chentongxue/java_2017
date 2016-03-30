package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.AuctioneBigTypeItem;
import com.game.draco.message.item.AuctioneSmallTypeItem;
import com.game.draco.message.request.C0858_AuctionMenuReqMessage;
import com.game.draco.message.response.C0858_AuctionMenuRespMessage;

import sacred.alliance.magic.app.auction.AuctionMenu;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class AuctionMenuAction extends BaseAction<C0858_AuctionMenuReqMessage>{

	@Override
	public Message execute(ActionContext context, C0858_AuctionMenuReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		List<AuctionMenu> menuList = GameContext.getAuctionApp().getAuctionMenuList();
		List<AuctioneBigTypeItem> bigTypeList = new ArrayList<AuctioneBigTypeItem>();
		for(AuctionMenu menu : menuList){
			AuctioneBigTypeItem bigItem = new AuctioneBigTypeItem();
			bigItem.setBigType(menu.getType());
			bigItem.setBigTypeName(menu.getName());
			bigItem.setImageId(menu.getImageId());
			
			List<AuctioneSmallTypeItem> smallTypeList = new ArrayList<AuctioneSmallTypeItem>();
			for(AuctionMenu sub : menu.getSubMenu()){
				AuctioneSmallTypeItem smallItem = new AuctioneSmallTypeItem();
				smallItem.setSmallType(sub.getType());
				smallItem.setSmallTypeName(sub.getName());
				smallTypeList.add(smallItem);
			}
			bigItem.setSmallTypeList(smallTypeList);
			bigTypeList.add(bigItem);
		}
		C0858_AuctionMenuRespMessage resp = new C0858_AuctionMenuRespMessage();
		resp.setBigTypeList(bigTypeList);
		return resp ;
	}
	
}
