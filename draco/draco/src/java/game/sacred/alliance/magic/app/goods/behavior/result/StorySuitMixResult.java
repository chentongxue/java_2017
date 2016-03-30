package sacred.alliance.magic.app.goods.behavior.result;

import lombok.Data;
import sacred.alliance.magic.app.goods.derive.StorySuitConfig;
import sacred.alliance.magic.app.goods.derive.StorySuitEquipConfig;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;

import com.game.draco.message.item.GoodsBaseItem;

@Data
public class StorySuitMixResult extends Result{

	private StorySuitConfig storySuitConfig;//套装配置
	private StorySuitEquipConfig storySuitEquipConfig;//套装消耗配置
	private RoleGoods equipGoods;//当前所选装备
	private GoodsEquipment equipment;//当前所选装备模版
	private GoodsEquipment targetEquipment;//目标装备模版信息
	private GoodsBaseItem targetBaseItem;//目标装备的物品信息
	
}
