package sacred.alliance.magic.filter;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;


/**
 * 
 * 屏蔽路由头标识
 * 此filter必须放到codec filter前面
 *
 */
public class RouteHeadFilter extends IoFilterAdapter{
	 private static final String IS_ROUTING = RouteHeadFilter.class.getName()+".IsRouting" ;
	 private static final String IS_ROUTED = RouteHeadFilter.class.getName()+".IsRouted" ;
	 private static final String ROUTE_HEAD_BUFFER = RouteHeadFilter.class.getName()+".RouteHeadBuffer" ;
	// private static final String ROUTE_DATA = RouteHeadFilter.class.getName() + ".RouteData" ;
	 
	private boolean hasRouted(IoSession session) {
		return ((AtomicBoolean)session.getAttribute(IS_ROUTED)).get();
	}

	private AtomicBoolean getRouting(IoSession session) {
		return ((AtomicBoolean)session.getAttribute(IS_ROUTING));
	}
	
	private void setRouted(IoSession session) {
		((AtomicBoolean)session.getAttribute(IS_ROUTED)).compareAndSet(false, true);
	}
	
	/*private void setRouteData(IoSession session,RouteHead head) {
		session.setAttribute(ROUTE_DATA, head);
	}*/
	
	private IoBuffer getRouteHeadBuffer( IoSession session){
		return (IoBuffer)session.getAttribute(ROUTE_HEAD_BUFFER);
	}
	
	private void removeRouteHeadBuffer(IoSession session){
		session.removeAttribute(ROUTE_HEAD_BUFFER);
	}
	
	public void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session)
			throws Exception {
		//是否正在做路由操作
		session.setAttribute(IS_ROUTING, new AtomicBoolean(false));
		session.setAttribute(IS_ROUTED, new AtomicBoolean(false));
		IoBuffer routeBuffer = IoBuffer.allocate(1);
		routeBuffer.setAutoExpand(true);
		routeBuffer.flip();
		session.setAttribute(ROUTE_HEAD_BUFFER,routeBuffer);
		super.sessionOpened(nextFilter, session);
	}
	
	private boolean isCompleteRouteHead(IoBuffer buffer){
		return RouteHead.isCompleteRouteHead(buffer);
	}
	
	
	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session,
			Object message) {
		if(this.hasRouted(session)){
			//已经路由,直接将消息往后发送即可
			nextFilter.messageReceived(session, message);
			return ;
		}
		IoBuffer recvBuffer = (IoBuffer) message;
		IoBuffer routeBuffer = this.getRouteHeadBuffer(session);
		routeBuffer.put(recvBuffer);
		routeBuffer.flip();
		if(this.isCompleteRouteHead(routeBuffer) 
				&& this.getRouting(session).compareAndSet(false, true)){
			this.doRoute(nextFilter, session, routeBuffer);
		}
	}
	
	private void doRoute(NextFilter nextFilter, IoSession session,IoBuffer routeBuffer){
		//处理路由操作
		RouteHead head = new RouteHead(routeBuffer);
		//将剩余消息发送到后面filter
		nextFilter.messageReceived(session, routeBuffer);
		//标识路由完成
		setRouted(session);
		//设置路由数据
		//setRouteData(session,head);
		removeRouteHeadBuffer(session);
	}
	
}
