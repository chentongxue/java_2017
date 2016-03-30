package sacred.alliance.magic.app.config;

public class PathConfig extends PropertiesConfig {

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	private String currentPath;
	
	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}

	public String getQuestPath(){
		return currentPath+getConfig("questPath");
	}
	

	public String getAiPath(){
		return currentPath+getConfig("aiPath");
	}
	
	public String getSkillPath(){
		return currentPath+getConfig("skillPath");
	}
	
	public String getBuffPath(){
		return currentPath+getConfig("buffPath");
	}
	
	
	public String getMapDataPath(){
		return currentPath+getConfig("mapDataPath");
	}

	
	public String getXlsPath(){
		return currentPath+getConfig("xlsPath");
	}
	
	public String getMapLogicPath(){
		return currentPath+getConfig("mapLogicPath");
	}
	
	public String getDataPath(){
		return currentPath+getConfig("dataPath");
	}
}
