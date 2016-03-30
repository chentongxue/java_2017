package sacred.alliance.magic.log.log;

import lombok.Data;
import sacred.alliance.magic.log.Log;

public @Data class Active extends Log{
	
	@Override
	public String createLog() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.getUserId()).append(cat);
		buffer.append(this.getCharId()).append(cat);
		buffer.append(this.getUserIp()).append(cat);
		buffer.append(this.getTime()).append(cat);
		buffer.append(this.getUserName()).append(cat);
		buffer.append(this.getProductId()).append(cat);
		buffer.append(this.getRegionId());
		return buffer.toString();
	}
}
