package sacred.alliance.magic.log.log;

import lombok.Data;
import sacred.alliance.magic.log.Log;

public@Data class Online extends Log{
	private int charNums;
	private int accountNums;
	
	@Override
	public String createLog() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.getTime()).append(cat);
		buffer.append(this.charNums).append(cat);
		buffer.append(this.accountNums).append(cat);
		buffer.append(this.getProductId()).append(cat);
		buffer.append(this.getRegionId());
		return buffer.toString();
	}
}
