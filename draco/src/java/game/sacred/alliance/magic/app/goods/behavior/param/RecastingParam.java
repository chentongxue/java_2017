package sacred.alliance.magic.app.goods.behavior.param;

import lombok.Data;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class RecastingParam extends AbstractParam{

	public final static int RECASTING_TYPE_NORMAL = 0 ; //普通洗练
	public final static int RECASTING_TYPE_GOLD = 1 ; //钻石洗练
	
	public RecastingParam(RoleInstance role) {
		super(role);
	}

	private RoleGoods equipGoods;
	private int type;//0=普通洗练，1=钻石洗练
	//锁定属性序列
	private byte[] lockIndex ;
	private int targetId ;
	
}
