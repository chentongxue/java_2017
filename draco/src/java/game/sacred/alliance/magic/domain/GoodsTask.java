package sacred.alliance.magic.domain;

import java.util.List;

import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.GoodsBaseTaskItem;

import sacred.alliance.magic.app.attri.AttriItem;

/**   
*    
* 项目名称：MagicAndScience   
* 类名称：GoodsTask   
* 类描述：任务物品信息类   
* 创建人：gaojl   
* 创建时间：Sep 1, 2010 11:13:55 AM   
* 修改人：   
* 修改时间：Sep 1, 2010 11:13:55 AM   
* 修改备注：   
* @version    
*    
*/
public class GoodsTask extends GoodsBase {
	
	
	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseTaskItem item = new GoodsBaseTaskItem();
		this.setGoodsBaseItem(roleGoods,item);
		item.setSecondType((byte)this.getSecondType());
		item.setLvLimit((byte)this.getLvLimit());
		item.setDesc(this.getDesc());
		return item;
	}

	
	

	@Override
	public List<AttriItem> getAttriItemList() {
		return null;
	}
	
	@Override
	public void init(Object initData) {
	} 
}
