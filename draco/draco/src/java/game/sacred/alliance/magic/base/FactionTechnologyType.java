package sacred.alliance.magic.base;

/**   
*    
* 项目名称：MagicAndScience   
* 类名称：FactionTechnologyType   
* 类描述：公会科技类型   
* 创建人：gaojl   
* 创建时间：Mar 18, 2011 5:01:19 PM   
* 修改人：   
* 修改时间：Mar 18, 2011 5:01:19 PM   
* 修改备注：   
* @version    
*    
*/
public enum FactionTechnologyType {
	
	moneyTechnology(101,"货币"),
	expTechnology (102,"经验"),
	questTechnology(103,"任务"),
	donateTechnology(104,"捐献"),
	edificeTechnology(105,"建筑"),
	fallTechnology(106,"掉落"),
	;
	
	private final int type;
	private final String name;
	
	FactionTechnologyType(int type,String name){
		this.type = type;
		this.name = name;
	}
	public final int  getType(){
		return type;
	}
	public String getName(){
		return name;
	}
	/**
	 * 科技类型
	 * @param type
	 * @return
	 */
	public static FactionTechnologyType get(int type){
		switch(type){
			case 101:
				return moneyTechnology;
			case 102:
				return expTechnology;
			case 103:
				return questTechnology;
			case 104:
				return donateTechnology;
			case 105:
				return edificeTechnology;
			case 106:
				return fallTechnology;
			default:
				return null;
		}
	}

 }
