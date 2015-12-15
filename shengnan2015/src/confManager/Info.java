package confManager;

public class Info {
	private String serverIp;
	private String serverId;
	private String port;
	
	public Info() {
		super();
	}

	public Info(String serverId, String serverIp, String port) {
		super();
		this.serverIp = serverIp;
		this.serverId = serverId;
		this.port = port;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "ServerInfo [serverIp=" + serverIp + ", serverId=" + serverId
				+ ", port=" + port + "]";
	}
	
}
