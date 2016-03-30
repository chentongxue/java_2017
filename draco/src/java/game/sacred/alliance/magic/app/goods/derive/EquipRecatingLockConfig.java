package sacred.alliance.magic.app.goods.derive;

import lombok.Data;

public @Data class EquipRecatingLockConfig {
	
	public static final float LOCK_RATIO_BASE_VALUE = 100f;
	
	private byte lockNum;//锁的数量
	private short ratio;//消耗倍数
	
}
