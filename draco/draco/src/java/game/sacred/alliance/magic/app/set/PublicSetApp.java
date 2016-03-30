package sacred.alliance.magic.app.set;

import sacred.alliance.magic.core.Service;

public interface PublicSetApp extends Service{

	/**允许交易*/
	public boolean isTrade();

	/**得到此服务器的语言类型*/
	public String getLangName();

	/**最大角色名字长度*/
	public int getMaxRoleNameSize();

}
