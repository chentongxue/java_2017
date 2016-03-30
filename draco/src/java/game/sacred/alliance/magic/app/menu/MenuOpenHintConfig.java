package sacred.alliance.magic.app.menu;

import sacred.alliance.magic.util.Log4jManager;
import lombok.Data;

public @Data class MenuOpenHintConfig {
	
	private int level;
	private short imageId;
	private byte type;// 0.上飘 1.下飘
	private short menuId;
	private String info;
	
	public void init(String fileInfo) {
		if (0 == type && null == MenuIdType.get(menuId)) {
			this.checkFail(fileInfo + " level " + this.level + " menuId not exist!");
		}
	}
	
	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

}
