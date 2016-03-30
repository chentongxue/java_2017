package sacred.alliance.magic.app.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.ahocorasick.trie.Trie;

public class WordsConfig  implements Configurable{
	private String encoding = "UTF-8";
	//
	private Trie trie;
	private String path;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public void setPath(String path){
		this.path = path;
	}
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	public void init() throws Exception {
		trie = new Trie();
		BufferedReader br = null ;
		try {
			FileInputStream fs = new FileInputStream(path); 
		    InputStreamReader reader = new InputStreamReader(fs, encoding); 
			br = new BufferedReader(reader);
			
			String line = null;
			while ((line = br.readLine()) != null) {
				trie.addKeyword(line);
			}
		} catch (IOException e) {
			logger.error("FilterWord file not config ",e);
		}finally{
			if(null != br){
				br.close();
			}
		}
	}
	
	@Override
	public void reLoad() throws Exception{
		init();
	}

	public Trie getTrie() {
		return trie;
	}
}
