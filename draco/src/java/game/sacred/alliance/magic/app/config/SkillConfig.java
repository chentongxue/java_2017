package sacred.alliance.magic.app.config;

public class SkillConfig extends PropertiesConfig{

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * 获得级别差的命中率
	 * @return
	 */
	/*public int getLevelHitProp(){
		return Integer.parseInt(getConfig("levelhitprop"));
	}*/
	
	/**
	 * 获得最小命中率
	 * @return
	 */
	/*public int getMinHitProp(){
		return Integer.parseInt(getConfig("minhitprop"));
	}*/
	
	/**
	 * 获得最大命中率
	 * @return
	 */
	/*public int getMaxHitProp(){
		return Integer.parseInt(getConfig("maxhitprop"));
	}*/
	
	/**
	 * 人,怪的公共CD一样
	 */
	public int getSkillGlobalCd(){
		return Integer.parseInt(getConfig("skillglobalcd"));
	}
	
	public int getChangeOwerTime(){
		return Integer.parseInt(getConfig("changeownertime"));
	}
	/** 获得改变外形buffid */
	public short getShapeBuffId() {
		return Short.parseShort(getConfig("shapebuffid"));
	}
	/** 获得变色buffid */
	public short getColorBuffId() {
		return Short.parseShort(getConfig("colorbuffid"));
	}
	/** 获得缩放buffid */
	public short getZoomBuffId() {
		return Short.parseShort(getConfig("zoombuffid"));
	}
	
}
