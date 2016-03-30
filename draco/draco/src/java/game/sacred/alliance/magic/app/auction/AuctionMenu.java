package sacred.alliance.magic.app.auction;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

public @Data class AuctionMenu {
	
	private short id;//节点ID
	private byte parentId;//父节点ID[顶级节点则为0]
	private String name;//节点名字
	private byte type;//节点类型值
	private byte imageId ; // 图片ID
	
	//子菜单列表
	private List<AuctionMenu> subMenu = new ArrayList<AuctionMenu>();
	
	public void addSubMenu(AuctionMenu sub){
		if(null == sub){
			return ;
		}
		this.subMenu.add(sub);
	}
	
	public boolean isParentMenu(){
		return 0 == this.parentId ;
	}
}
