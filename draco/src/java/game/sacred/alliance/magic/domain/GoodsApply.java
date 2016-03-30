package sacred.alliance.magic.domain;


/**
 * 应用道具
 *
 */
public abstract class GoodsApply extends GoodsBase {
	private int intervalId; 
	private int intervalTime;
	private int applyDisappear;
	

	public boolean hasApplyDisappear() {
		return 1 == applyDisappear;
	}

	public int getIntervalId() {
		return intervalId;
	}

	public void setIntervalId(int intervalId) {
		this.intervalId = intervalId;
	}

	public int getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(int intervalTime) {
		this.intervalTime = intervalTime;
	}

	public int getApplyDisappear() {
		return applyDisappear;
	}

	public void setApplyDisappear(int applyDisappear) {
		this.applyDisappear = applyDisappear;
	}

	
}
