package sacred.alliance.magic.servlet;

import org.eclipse.jetty.continuation.Continuation;

import sacred.alliance.magic.channel.servlet.ServletParameter;

public class ContinuationParameter implements ServletParameter{

	private Continuation continuation ;
	public ContinuationParameter(Continuation continuation){
		this.continuation = continuation ;
	}
	@Override
	public void complete() {
		if(null != this.continuation){
			this.continuation.complete() ;
		}
	}

}
