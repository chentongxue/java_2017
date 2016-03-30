package sacred.alliance.magic.domain;

/**   
*    
* 项目名称：MagicAndScience   
* 类名称：FactionTechnology   
* 类描述：公会科技信息   
* 创建人：gaojl   
* 创建时间：Mar 18, 2011 3:13:30 PM   
* 修改人：   
* 修改时间：Mar 18, 2011 3:13:30 PM   
* 修改备注：   
* @version    
*    
*/
public class FactionTechnology {
	//公会科技类型
	private int technologyType;
	//公会科技名称
	private String technologyName;
	//公会科技的等级
	private int technologyLevel;
	//公会科技金条
	private int technologyGoldNum;
	//公会科技金币
	private int technologySilverNum;
	public int getTechnologyType() {
		return technologyType;
	}
	public void setTechnologyType(int technologyType) {
		this.technologyType = technologyType;
	}
	public String getTechnologyName() {
		return technologyName;
	}
	public void setTechnologyName(String technologyName) {
		this.technologyName = technologyName;
	}
	public int getTechnologyLevel() {
		return technologyLevel;
	}
	public void setTechnologyLevel(int technologyLevel) {
		this.technologyLevel = technologyLevel;
	}
	public int getTechnologyGoldNum() {
		return technologyGoldNum;
	}
	public void setTechnologyGoldNum(int technologyGoldNum) {
		this.technologyGoldNum = technologyGoldNum;
	}
	public int getTechnologySilverNum() {
		return technologySilverNum;
	}
	public void setTechnologySilverNum(int technologySilverNum) {
		this.technologySilverNum = technologySilverNum;
	}
	
	
}
