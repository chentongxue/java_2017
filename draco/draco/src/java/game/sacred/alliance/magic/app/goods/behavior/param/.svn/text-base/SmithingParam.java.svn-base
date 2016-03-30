package sacred.alliance.magic.app.goods.behavior.param;

import lombok.Data;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class SmithingParam extends AbstractParam{
	public final static int INFO_TYPE = -1 ; //获得信息
	public final static int SMITHING_TYPE = 0 ; //洗炼
	public final static int SMITHING_EX_TYPE = 1 ;//精炼
	
	public SmithingParam(RoleInstance role) {
		super(role);
	}

	private byte bagType ;
	private String instanceId ;
	private int type ;
	private int index ;
	private String reqInfo ;
	
	private RoleGoods equipGoods ;
	private GoodsEquipment template ;
	private boolean confirm ;
	private boolean buyShortcut ;
}
