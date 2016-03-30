package sacred.alliance.magic.service;

public interface IllegalWordsService {

	public String doFilter(String str);
	
	public String doFilter(String str,char newStr);
	
	/**
	 * 查找其中包含的敏感字符
	 * @param text
	 * @return
	 */
	public String findIllegalChar(String text);
	
	public boolean isNullOrEmpty(String str);
	
	public boolean isExceed(String str,int maxNum);
	
	public boolean isLow(String str,int minNum);
	
	public boolean isCNorENorFigure(String str);
	
	public boolean isENorFigure(String str);
	
	public int lengthFaceCN(String str);
	
	/**
	 * 查找其中包含的被禁用字符
	 * @param text
	 * @return
	 */
	public String findForbiddenChar(String text);
	
}
