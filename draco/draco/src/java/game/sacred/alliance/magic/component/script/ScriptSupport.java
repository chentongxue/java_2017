package sacred.alliance.magic.component.script;

import sacred.alliance.magic.core.Service;

public interface ScriptSupport extends Service{
	
	public int loadScript(String pathname, boolean includeSubPath);
	
	public int loadScript(String pathname);
	
	/*public void invokeMethod(ScriptType scriptType, String fileName,
			String chooseName, AbstractRole role, String arg) throws ServiceException;*/
}
