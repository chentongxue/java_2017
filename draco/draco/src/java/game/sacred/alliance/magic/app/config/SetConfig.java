package sacred.alliance.magic.app.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.springframework.core.io.Resource;

public class SetConfig implements Configurable{
	
	private Resource resource;
	private String encoding = "UTF-8";
	
	private File configFile;
	private Set<String> set = new HashSet<String>();

	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public File getConfigFile() {
		return configFile;
	}

	public void setConfigFile(File configFile) {
		this.configFile = configFile;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	public void init() throws Exception {
		this.reLoad();
		ConfigCollection.add(this);
	}
	
	@Override
	public void reLoad() throws Exception{
		if(null != this.resource){
			configFile = resource.getFile();
		}
		if(null == configFile || !configFile.exists() || ! configFile.isFile()){
			throw new java.lang.IllegalArgumentException("file not config ") ;
		}
		Set<String> set = this.readFile();
		this.set = set ;
	}

	public Set<String> getValue() {
		return set ;
	}
	
	
	private Set<String> readFile(){
		Set<String> words = new HashSet<String>();
    	BufferedReader br=null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(configFile),encoding));
			for(String line = br.readLine(); line != null;  line = br.readLine()){  
				 if(null != line && !line.trim().startsWith("#") && line.trim().length() > 0 && !words.contains(line)){
					 words.add(line);
				 } 
			  }
		}catch (FileNotFoundException e1) {
			throw new java.lang.RuntimeException("file: " + configFile.getName() + " not found") ;
		}catch(Exception ex){
			throw new java.lang.RuntimeException("read  file:" + configFile.getName() + " exception ",ex);
		}finally{
			if(null != br){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return words ;
	}

}
