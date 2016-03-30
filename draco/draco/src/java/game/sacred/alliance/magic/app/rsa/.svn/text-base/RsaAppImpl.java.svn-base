package sacred.alliance.magic.app.rsa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.Base64Util;
import sacred.alliance.magic.util.FileUtil;
import sacred.alliance.magic.util.RSAUtil;

public class RsaAppImpl implements RsaApp{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String decryptStr;
	private String fileName;

	@Override
	public String decryptByPublicKey() {
		try{
			String publicKey = FileUtil.readFileToString(fileName);
			byte[] decryptArr = Base64Util.decode(decryptStr);
			byte[] decodedData = RSAUtil.decryptByPrivateKey(decryptArr, publicKey);
	        return new String(decodedData);
		}catch(Exception e){
			logger.error("RsaApp.decryptByPublicKey error",e);
		}
		return null;
	}

	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		
	}

	@Override
	public void stop() {
		
	}

	public void setDecryptStr(String decryptStr) {
		this.decryptStr = decryptStr;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
