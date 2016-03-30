package sacred.alliance.magic.component.script;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.python.util.PythonInterpreter;

import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.ParallelRun;

import com.game.draco.GameContext;

public class PyScriptSupport implements ScriptSupport{

	private final static String PY = ".py" ;
	private String defaultEncoding = "utf8" ;
	private PythonInterpreter interp = new PythonInterpreter(); 
    //private Log loadLogger = LogFactory.getLog(this.getClass());
    
    @Override
	public int loadScript(String pathname) {
    	return loadScript(pathname,false);
    }
    
	@Override
	public int loadScript(String pathname, boolean includeSubPath) {
		File path = new File(pathname);
		if (!path.exists() || path.isHidden()) {
			return 0;
		}
		if (!path.isDirectory() && !pathname.endsWith(PY)) {
			return 0;
		}
		if (pathname.endsWith(PY)) {
			try {
				interp.execfile(pathname);
				return 1;
			} catch (Exception ex) {
				ex.printStackTrace();
				Log4jManager.CHECK.error("Python File :" + pathname
						+ " is Error", ex);
				Log4jManager.checkFail();
			}
			return 0;
		}
		File[] files = path.listFiles();
		if (null == files) {
			return 1;
		}
		int nThreads = GameContext.getThreadsPools();
		if(nThreads <=1){
			for (File f : files) {
				this.load(f, includeSubPath);
			}
		}else{
			final boolean subPath = includeSubPath ;
			List<Runnable> runList = new ArrayList<Runnable>();
			for (File f : files) {
				final File file = f ;
				Runnable rn = new Runnable() {
					public void run() {
						load(file,subPath);
					}
				};
				runList.add(rn);
			}
			ParallelRun.execute(nThreads,runList);
		}
		
		return 1;
	}
	
	private void load(File f,boolean includeSubPath){
		if (f.isHidden() || f.getName().endsWith(".svn")) {
			return;
		}
		if (f.isFile() && f.getPath().endsWith(PY)) {
			try {
				interp.execfile(f.getPath());
			} catch (Exception ex) {
				ex.printStackTrace();
				Log4jManager.CHECK.error("Python File :" + f.getPath()
						+ " is Error", ex);
				Log4jManager.checkFail();
			}
			return;
		}
		if (includeSubPath && f.isDirectory()) {
			loadScript(f.getPath(), includeSubPath);
		}
	}

	
	@Override
	public void start() {
		org.python.core.codecs.setDefaultEncoding(defaultEncoding);
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	@Override
	public void setArgs(Object args) {
		// TODO Auto-generated method stub
		
	}

	/*public void invokeMethod(ScriptType scriptType,
			String fileName, String chooseName,
			AbstractRole role, String arg) throws ServiceException {

		String path = "";
		
		if(scriptType==ScriptType.AI){
			path = pathConfig.getAiPath();
		}else if(scriptType==ScriptType.BUFF){
			path = pathConfig.getBuffPath();
		}else if(scriptType==ScriptType.GOODS_LOGIC){
			path = pathConfig.getGoodsLogicPath();
		}else if(scriptType==ScriptType.QUEST){
			path = pathConfig.getQuestPath();
		}else if(scriptType==ScriptType.SKILL_LOGiC){
			path = pathConfig.getSkillPath();
		}
		
		PyObject obj = JythonQuest.jfactory.getPyObjectFromJythonFile(chooseName, path+fileName+".py");
		
		if(obj==null)System.out.println("obj=null");else System.out.println("obj!=null");
		
		InvokeJythonMethod invokeMethod = (InvokeJythonMethod)obj.__tojava__(InvokeJythonMethod.class);
		
		invokeMethod.invokeMethod(role, arg);
		
	}

	public void setPathConfig(PathConfig pathConfig) {
		this.pathConfig = pathConfig;
	}
*/


}
