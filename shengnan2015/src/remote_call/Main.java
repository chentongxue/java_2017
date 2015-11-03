package remote_call;

public class Main {

	public static void main(String[] args) {
		ServiceProxy p = ServiceProxy.newInstance();
		p.getTeam("a");
	}

}
