package remote_call;

public class ServiceProxy {
	public final class EnumCall{
		public static final int GETTEAM_STRING = 1;
	}
	
	private Port localPort;
	private ServiceProxy(){}
	public static ServiceProxy newInstance(){
		return new ServiceProxy();
	}
	public void listenResult(GofFunction2<String, String> method, Object...context) {
		listenResult(method, newString(context));
	}
	public void listenResult(GofFunction2<String, String> method, String context) {
		localPort.listenResult(method, context);
	}
	
	
	public void getTeam(String teamId) {
		localPort.call( EnumCall.GETTEAM_STRING, new Object[]{ teamId });
	}
	
	
	public String newString(Object...context){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < context.length; i++) {
			sb.append(context[i]);
		}
		return sb.toString();
	}
}
