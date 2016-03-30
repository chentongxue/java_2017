package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import sacred.alliance.magic.app.goods.derive.StorySuitConfig;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.StorySuitItem;
import com.game.draco.message.request.C0583_GoodsStorySuitListReqMessage;
import com.game.draco.message.response.C0583_GoodsStorySuitListRespMessage;

public class GoodsStorySuitListAction extends BaseAction<C0583_GoodsStorySuitListReqMessage>{

	@Override
	public Message execute(ActionContext context, C0583_GoodsStorySuitListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C0583_GoodsStorySuitListRespMessage respMsg = new C0583_GoodsStorySuitListRespMessage();
		List<StorySuitItem> storySuitList = new ArrayList<StorySuitItem>();
		for(StorySuitConfig config : GameContext.getGoodsApp().getStorySuitConfigList()){
			if(null == config){
				continue;
			}
			short suitGroupId = config.getSuitGroupId();
			StorySuitItem item = new StorySuitItem();
			item.setSuitGroupId(suitGroupId);
			item.setSuitGroupName(config.getName());
			item.setRelySuitGroupId(config.getRelySuitGroupId());
			item.setImageIds(this.buildImageIds(suitGroupId));
			storySuitList.add(item);
		}
		respMsg.setStorySuitList(storySuitList);
		return respMsg ;
	}
	
	private short[] buildImageIds(short suitGroupId){
		TreeMap<Byte,Short> map = GameContext.getGoodsApp().getStorySuitImageMap(suitGroupId);
		int size = map.size();
		short[] images = new short[size];
		int index = 0;
		for(short imageId : map.values()){
			images[index] = imageId;
			index++;
		}
		return images;
	}

}
