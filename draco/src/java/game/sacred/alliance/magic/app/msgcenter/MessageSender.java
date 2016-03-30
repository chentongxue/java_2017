package sacred.alliance.magic.app.msgcenter;

public interface MessageSender {
	
	public void start() ;
	
	public void stop() ;
	
	public void sendMessage(MessageEntry entry);
	
	public void messageEntryHandle(MessageEntry entry);
}
