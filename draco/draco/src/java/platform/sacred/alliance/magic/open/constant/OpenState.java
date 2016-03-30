package sacred.alliance.magic.open.constant;

public class OpenState {
	
	public static final int SUCCESS = 0;//成功
	public static final int ERROR = 1;//系统错误
	public static final int PARAM_ERROR = 2;//参数格式错误
	public static final int SIGN_FAIL = 3;//签名验证失败
	public static final int TIME_ERROR = 4;//时间非法
	public static final int SERVER_MAINTAIN = 5;//服务器正在维护
	public static final int SERVER_LIST_EMPTY = 101;//服务器列表为空
	public static final int ROLE_NAME_NOT_EXIST = 201;//角色名不存在
	public static final int ROLE_NAME_CHANNELUID_NOT_MATCH = 202;//角色名和渠道uid不匹配
	public static final int CDKEY_TAKE_FAIL = 203;//激活码礼包领取失败
	
}
