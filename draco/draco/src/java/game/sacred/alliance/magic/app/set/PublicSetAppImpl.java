package sacred.alliance.magic.app.set;

import java.util.List;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.config.PathConfig;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.XlsPojoUtil;

public @Data class PublicSetAppImpl implements PublicSetApp{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private PublicSetInfo publicSetObj = null;
	private static final String default_lang = "cn";
	private static final int default_max_name_size = 14;
	private PathConfig pathConfig;
	
	@Override
	public int getMaxRoleNameSize(){
		if(null == publicSetObj){
			return default_max_name_size;
		}
		return publicSetObj.getMaxRoleNameSize();
	}
	
	@Override
	public String getLangName(){
		if(null == publicSetObj){
			return default_lang ;
		}
		return publicSetObj.getLangName();
	}
	
	@Override
	public boolean isTrade(){
		if(null == publicSetObj){
			return true;
		}
		return publicSetObj.hasTrade();
	}
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadPublicSetInfo();
	}

	@Override
	public void stop() {
	}
	
	private void loadPublicSetInfo(){
		String path = pathConfig.getXlsPath();
		String fileName = XlsSheetNameType.public_set.getXlsName();
		String sheetName = XlsSheetNameType.public_set.getSheetName();
		String info = "load excel error: fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			String sourceFile = path + fileName;
			List<PublicSetInfo> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, PublicSetInfo.class);
			if(null == list || 0 == list.size()){
				this.checkFail(info);
				return ;
			}
			publicSetObj = list.get(0);
		} catch (Exception e) {
			logger.error("loadPublicSetInfo",e);
			this.checkFail(info);
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

}
