/**
 * 
 */
package sacred.alliance.magic.app.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * @author a
 * 
 */
public abstract class PropertiesConfig implements Configurable {

	protected static Logger logger = LoggerFactory.getLogger(PropertiesConfig.class);
	
	protected Properties prop = new Properties();

	private File configFile;

	private Resource resource;

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public File getConfigFile() {
		return configFile;
	}

	public void setConfigFile(File configFile) {
		this.configFile = configFile;
	}

	public void init() throws Exception {
		this.reLoad();
		ConfigCollection.add(this);
	}

	private Properties load() throws Exception{
		FileInputStream stream = null ;
		try {
			Properties pop = new Properties();
			stream = new FileInputStream(configFile);
			pop.load(stream);
			return pop ;
		} catch (IOException e) {
			throw e ;
		}finally{
			if(null != stream){
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	@Override
	public void reLoad() throws Exception{
		if(null != this.resource){
			configFile = resource.getFile();
		}
		Properties p = load();
		prop = p ;
	}

	public String getConfig(String key) {
		return prop.getProperty(key);

	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		Enumeration e = prop.keys();
		while(e.hasMoreElements())
		{
			String key = (String)e.nextElement();
			String value = (String)prop.get(key);
			sb.append("{"+key+"="+value+"}");
		}
		return sb.toString();
	}
}
