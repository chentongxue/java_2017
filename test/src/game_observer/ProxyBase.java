package game_observer;

import function.GofFunction2;
import function.GofFunction3;


/**
 * 代理基类
 */
public abstract class ProxyBase {
	/**
	 * 异步注册监听返回值
	 * @param obj
	 * @param methodName
	 * @param context
	 */
	abstract public void listenResult(GofFunction2<Param, Param> method, Object...context);
	abstract public void listenResult(GofFunction3<Boolean, Param, Param> method, Object...context);
	
	/**
	 * 同步等待返回值
	 * @return
	 */
	abstract public Param waitForResult();
	
	abstract public <T> T getMethodFunction(Service serv, int methodKey);
}