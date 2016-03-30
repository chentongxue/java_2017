package sacred.alliance.magic.app.config;

import java.util.HashSet;
import java.util.Set;

import sacred.alliance.magic.constant.Cat;

public class QuickBuyConfig extends PropertiesConfig {
	
	private Set<Short> cmdIdSet = null;
	
	/**
	 * 快速购买是否开启
	 * @return
	 */
	public boolean isQuickBuyOpen(){
		return 1 == Integer.valueOf(getConfig("quickBuyIsOpen"));
	}
	
	/**
	 * 快捷购买自动确认的元宝上限
	 * 超过此值，每次都会弹二次确认面板
	 * @return
	 */
	public int getAutoConfirmMaxGold(){
		try {
			return Integer.valueOf(this.getConfig("autoConfirmMaxGold"));
		} catch (Exception e) {
			logger.error(this.getClass().getName() + ".getAutoConfirmMaxGold error: ", e);
			return 0;
		}
	}
	
	/**
	 * 获取快捷购买的命令字集合
	 * @return
	 */
	public Set<Short> getCommandIdSet(){
		if(null == this.cmdIdSet){
			this.cmdIdSet = this.reloadSet();
		}
		return this.cmdIdSet;
	}
	
	private String getQuickBuyCommandIds(){
		return getConfig("quickBuyCommandIds");
	}
	
	private Set<Short> reloadSet(){
		Set<Short> set = new HashSet<Short>();
		try {
			String cmdIdStr = this.getQuickBuyCommandIds();
			for(String item : cmdIdStr.split(Cat.comma)){
				set.add(Short.valueOf(item));
			}
		} catch (NumberFormatException e) {
			logger.error("QuickBuyConfig.reloadSet error: ", e);
		}
		return set;
	}
	
	@Override
	public void reLoad() throws Exception {
		super.reLoad();
		this.cmdIdSet = this.reloadSet();
	}
	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
}
