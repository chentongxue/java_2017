package sacred.alliance.magic.domain;

import java.util.List;

import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.GoodsBaseTaskPropsItem;

import sacred.alliance.magic.app.attri.AttriItem;

/**   
*    
* 项目名称：MagicAndScience   
* 类名称：GoodsTaskprops   
* 类描述：任务道具   
* 创建人：gaojl   
* 创建时间：Sep 1, 2010 11:15:36 AM   
* 修改人：   
* 修改时间：Sep 1, 2010 11:15:36 AM   
* 修改备注：   
* @version    
*    
*/
public class GoodsTaskprops extends GoodsBase {
	private int taskId;

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	
	
	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseTaskPropsItem item = new GoodsBaseTaskPropsItem();
		this.setGoodsBaseItem(roleGoods,item);
		item.setSecondType((byte)this.getSecondType());
		item.setLvLimit((byte)this.getLvLimit());
		item.setDesc(this.getDesc());
		return item;
	}

	



	@Override
	public List<AttriItem> getAttriItemList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void init(Object initData) {
	} 
}
