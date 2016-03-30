
package sacred.alliance.magic.app.config;

public interface Configurable {
	
	public String getName() ;
	public void init() throws Exception;
	public void reLoad() throws Exception;
	
}
