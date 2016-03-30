package sacred.alliance.magic.util;

public class MD5 {
	
	public String getMD5(String inbuf) {
		MD5Algorithm algorithm = new MD5Algorithm();
		return algorithm.getMD5(inbuf);
	}
	
	public String getMD5(byte[] inbuf) {
		MD5Algorithm algorithm = new MD5Algorithm();
		return algorithm.getMD5(inbuf);
	}
}
