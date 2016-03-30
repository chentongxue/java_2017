package sacred.alliance.magic.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileUtil {

	
	public static Set<String> readFileToSet(String fileName){
    	Set<String> set = new HashSet<String>();
    	BufferedReader br=null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)),"utf8"));
			for(String line = br.readLine(); line != null;  line = br.readLine()){  
				 if(null != line && !line.trim().startsWith("#") && line.trim().length() > 0){
					 set.add(line);
				 } 
			  }
		}catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(null != br){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    	return set ;
    }
	
	public static List<String> readFileToList(String fileName){
    	List<String> list = new ArrayList<String>();
    	BufferedReader br=null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)),"utf8"));
			for(String line = br.readLine(); line != null;  line = br.readLine()){  
				 if(null != line && !line.trim().startsWith("#") && line.trim().length() > 0){
					 list.add(line);
				 } 
			  }
		}catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(null != br){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    	return list ;
    }
	
	public static Map<String,String> readFileToMap(String fileName){
		Map<String,String> map = new HashMap<String, String>();
    	BufferedReader br=null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName))));
			for(String line = br.readLine(); line != null;  line = br.readLine()){  
				 if(null != line && line.trim().length() > 0){
					 String str[] = line.split(" ");
					 map.put(str[0], str[1]);
				 } 
			  }
		}catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(null != br){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    	return map ;
    }
	
	public static String splitFileName(String original, char regex){
		int beginIndex = original.indexOf(regex)+1;
		int endIndex = original.length();
		String splitFileName = original.substring(beginIndex, endIndex);
		return splitFileName;
	}
	
	public static File getFile(String path, String prefix, String ext){
		File dir = new File(path);
		File[] files = dir.listFiles();
		if(files == null){
	        return null;
	    }
		
		File file = null;
		for(File f : files){
			String filename = f.getName().toLowerCase();
			if(prefix != null && filename.startsWith(prefix) == false){
	            continue;
	        }
	        
	        if(ext != null && filename.endsWith("." + ext) == false){
	            continue;
	        }
	        file = f;
		}
		return file;
	}
	
	public static ArrayList getDirFiles(String path, String prefix, String ext){
	    File dir = new File(path);
	    if(!dir.exists() || !dir.isDirectory()){
	    	return new ArrayList();
	    }
	    File[] files = dir.listFiles();
	    if(files == null){
	        return new ArrayList();
	    }
	    
	    ArrayList list = new ArrayList(); 
	    for(int i=0; i<files.length; i++){
	        String filename = files[i].getName().toLowerCase();
	        
	        if(prefix != null && filename.startsWith(prefix) == false){
	            continue;
	        }
	        
	        if(ext != null && filename.endsWith("." + ext) == false){
	            continue;
	        }
	        
	        list.add(files[i]);	    
	    }
	    
	    return list;
	}
	
	public static byte[] readByteData(File file){
		byte[] buffer = null ; 
		FileInputStream in = null ;
		try{			
			buffer = new byte[1024*500];
			in = new FileInputStream(file);
			int totallen = 0;
			int len;
			int offset = 0;
			while((len = in.read(buffer, offset, 1024)) != -1){
				totallen += len;
				offset += 1024;
			}

			byte[] data = new byte[totallen];
			for(int i=0; i<totallen; i++){
				data[i] = buffer[i];
			}
			
			in.close();
			return data;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			if(null != buffer){
				buffer = null ;
			}
			if(null != in){
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static String readFileToString(String fileName){
    	BufferedReader br=null;
    	StringBuffer buffer = new StringBuffer();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName))));
			for(String line = br.readLine(); line != null;  line = br.readLine()){  
				 if(null != line && line.trim().length() > 0){
					 buffer.append(line);
				 } 
			  }
		}catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(null != br){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    	return buffer.toString() ;
    }
}
