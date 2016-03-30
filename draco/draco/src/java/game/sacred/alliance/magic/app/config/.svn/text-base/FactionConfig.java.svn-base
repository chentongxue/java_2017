package sacred.alliance.magic.app.config;

/**   
*    
* 项目名称：MagicAndScience   
* 类名称：FactionConfig   
* 类描述：读取公会配置文件   
* 创建人：gaojl   
* 创建时间：Jun 7, 2010 4:49:50 PM   
* 修改人：   
* 修改时间：Jun 7, 2010 4:49:50 PM   
* 修改备注：   
* @version    
*    
*/
public class FactionConfig extends PropertiesConfig{

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * 获取角色头顶公会显示的颜色
	 * @return
	 */
	public String getViewColor() {
		return getConfig("viewColor");
	}
	/**
	 * 获得公会名称最大长度
	 * @return
	 */
	public int getFactionNameLength(){
		return Integer.parseInt(getConfig("factionNameLength"));
	}
	
	/** 获得公会宗旨长度 */
	public int getFactionDescLength(){
		return Integer.parseInt(getConfig("factionDescLength"));
	}
	
	/** 公会公告最大长度 */
	public int getFactionNoticeLength(){
		return Integer.parseInt(getConfig("factionNoticeLength"));
	}
	
	/** 公会成员签名的最大长度 */
	public int getFactionRoleSignatureLength(){
		return Integer.parseInt(getConfig("factionRoleSignature"));
	}
	
	/**
	 * 申请加入的队列长度
	 */
	public int getApplyQueueLenth(){
		return Integer.valueOf(getConfig("applyQueueLenth"));
	}
	/**
	 * 公会成员缓存时间（毫秒）
	 */
	public int getCacheMillis(){
		return Integer.valueOf(getConfig("cacheMinutes"))*60*1000;
	}
}
