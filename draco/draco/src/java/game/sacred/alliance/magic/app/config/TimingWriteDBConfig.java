package sacred.alliance.magic.app.config;

public class TimingWriteDBConfig extends PropertiesConfig {

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	/** 定时入库时间间隔 */
	public int getTimingWriteDBTime() {
		return Integer.parseInt(getConfig("timingWriteDBTime"));
	}
	
	/** 定时入库开关 */
	public boolean getTimingWriteDBOn(){
		return Boolean.parseBoolean(getConfig("timingWriteDBOn"));
	}

}
