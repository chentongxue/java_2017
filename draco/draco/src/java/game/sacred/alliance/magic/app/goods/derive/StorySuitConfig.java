package sacred.alliance.magic.app.goods.derive;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

@Data
public class StorySuitConfig {

	private short suitGroupId;//套装组ID
	private String name;//套装名称
	private int roleLevel;//角色等级
	private short relySuitGroupId;//依赖套装组ID
	
	public void init(String fileInfo){
		String info = fileInfo + "suitGroupId = " + this.suitGroupId + ",";
		if(this.suitGroupId <= 0){
			this.checkFail(info + "suitGroupId must greater than 0");
		}
		if(Util.isEmpty(this.name)){
			this.checkFail(info + "name not config.");
		}
		if(this.roleLevel < 0){
			this.checkFail(info + "roleLevel is error.");
		}
		if(this.relySuitGroupId < 0){
			this.checkFail(info + "relySuitGroupId is error.");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
