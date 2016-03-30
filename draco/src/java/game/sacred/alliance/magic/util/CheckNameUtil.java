package sacred.alliance.magic.util;



/**
 * 合服改名、注册角色名、公会名时的关键字检查
 */
public class CheckNameUtil{
	private static String matches = "[\\s\\S]*[sS](\\d){1,4}$";
	
	/**
	 * 以s/S+数字结尾
	 * @param name
	 * @return
	 */
	public static boolean isMatchChangeName(String name){
		int index = name.indexOf("@") ;
		if(index >=0){
			return true ;
		}
		return StringUtil.match(matches, name);
	}
	
	public static void main(String[] args) {
		String str = "卩ǒS烟鬼s1111";
		System.out.println("%%%%%%%%%===="+CheckNameUtil.isMatchChangeName(str));
		
	}
}
