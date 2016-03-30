package sacred.alliance.magic.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.FileSystemResource;

import sacred.alliance.magic.app.config.ForbiddenWordsConfig;
import sacred.alliance.magic.app.config.IllegalWordsConfig;
import sacred.alliance.magic.service.IllegalWordsService;
import sacred.alliance.magic.util.StringUtil;

public class IllegalWordsServiceImpl implements IllegalWordsService {

	private IllegalWordsConfig illegalWordsConfig;
	private ForbiddenWordsConfig forbiddenWordsConfig;

	public void setIllegalWordsConfig(IllegalWordsConfig illegalWordsConfig) {
		this.illegalWordsConfig = illegalWordsConfig;
	}

	public void setForbiddenWordsConfig(ForbiddenWordsConfig forbiddenWordsConfig) {
		this.forbiddenWordsConfig = forbiddenWordsConfig;
	}

	public static void main(String[] args) {
		IllegalWordsConfig config = new IllegalWordsConfig();
		config
				.setResource(new FileSystemResource(
						"D:\\workspace\\MagicAndScience\\conf\\spring\\properties\\dirty-word.txt"));
		try {
			config.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		IllegalWordsServiceImpl impl = new IllegalWordsServiceImpl();
		impl.setIllegalWordsConfig(config);
//		System.out.println(impl.doFilter("他 妈 的xxxde 乳房", 'a'));
//		System.out.println(impl.doFilter("a"));
//		System.out.println(impl.doFilter("他妈的李洪志"));
//		System.out.println(impl.doFilter("大家好"));
//		System.out.println(impl
//				.doFilter("你ChinaLiberal好民猪吧AsDf21世纪中国基金会的新疆独民猪"));
		System.out.println(impl.findIllegalChar("他 妈 的xxxde 乳房FEKS feKS"));
//		System.out.println("a他妈的A".length());
		
//	    System.out.println(impl.isCNorENorFigure("aye9093ERWQ%#%$+_"));
//	    System.out.println(impl.lengthFaceCN("zho中国"));
		System.out.println(impl.isLow("中a", 4));
		System.out.println("========"+impl.isENorFigure("ert3456,中国"));
	}

	@Override
	public String doFilter(String text) {
		if (StringUtil.nullOrEmpty(text)) {
			return "";
		}
		text = text.trim();
		if (1 == text.length()
				&& (StringUtil.isNumber(text) || StringUtil.isCharacter(text))) {
			// 单个字母,数字,通过
			return text;
		}
		List<String> words = illegalWordsConfig.getList();
		StringBuffer buffer = new StringBuffer(text);
		for (String keyword : words) {
			if (null == keyword || keyword.trim().length() == 0) {
				continue;
			}
			int index = 0;
			while (true) {
				index = buffer.indexOf(keyword, index);
				if (index < 0) {
					break;
				}
				for (int len = 0; len < keyword.length(); len++) {
					buffer.setCharAt(index + len, '*');
				}
			}
		}
		return buffer.toString();
	}

	@Override
	public String doFilter(String text, char newStr) {
		if (StringUtil.nullOrEmpty(text)) {
			return "";
		}
		text = text.trim();
		if (1 == text.length()
				&& (StringUtil.isNumber(text) || StringUtil.isCharacter(text))) {
			// 单个字母,数字,通过
			return text;
		}
		List<String> words = illegalWordsConfig.getList();
		StringBuffer buffer = new StringBuffer(text);
		for (String keyword : words) {
			if (null == keyword || keyword.trim().length() == 0) {
				continue;
			}
			int index = 0;
			while (true) {
				index = buffer.indexOf(keyword, index);
				if (index < 0) {
					break;
				}
				for (int len = 0; len < keyword.length(); len++) {
					buffer.setCharAt(index + len, newStr);
				}
			}
		}
		return buffer.toString();
	}

	@Override
	public String findIllegalChar(String text) {
		return this.findChar(text, this.illegalWordsConfig.getList());
	}
	
	@Override
	public String findForbiddenChar(String text) {
		return this.findChar(text, this.forbiddenWordsConfig.getList());
	}
	
	/**
	 * 在文本中查找字库中的字符
	 * @param text 文本
	 * @param words 字库
	 * @return
	 */
	private String findChar(String text, List<String> words){
		if (StringUtil.nullOrEmpty(text)) {
			return null;
		}
		text = text.trim();
		if (1 == text.length()
				&& (StringUtil.isNumber(text) || StringUtil.isCharacter(text))) {
			// 单个字母,数字,通过
			return null;
		}
		List<String> forbid = new ArrayList<String>();
		StringBuffer outBuffer = new StringBuffer(text);
		text = text.toLowerCase();
		StringBuffer buffer = new StringBuffer(text);
		
		for (String keyword : words) {
			if (null == keyword || keyword.trim().length() == 0) {
				continue;
			}
			int index = 0;
			while (true) {
				//System.out.println("keyword = "+keyword);
				String bakKeyWord = keyword.toLowerCase();
				index = buffer.indexOf(bakKeyWord, index);
				if (index < 0) {
					break;
				}
				StringBuffer sb = new StringBuffer();
				for (int len = 0; len < bakKeyWord.length(); len++) {
					sb.append(outBuffer.charAt(index+len));
					buffer.setCharAt(index + len, '*');
				}
				//System.out.println("sb  = "+sb );
				String outStr = sb.toString();
				if (!forbid.contains(outStr)) {
					forbid.add(outStr);
				}
			}
		}
		if (0 == forbid.size()) {
			return null;
		}
		StringBuffer bufferForbid = new StringBuffer();
		for (String str : forbid) {
			bufferForbid.append(str);
		}
		return bufferForbid.toString();
	}

	@Override
	public boolean isExceed(String str, int maxNum) {
		if (0 >= maxNum) {
			throw new java.lang.IllegalArgumentException("The num(" + maxNum
					+ ") is not illegal");
		}
		if (null == str || 0 == str.length()) {
			throw new java.lang.IllegalArgumentException(
					"The (str=null) is not illegal");
		}
		
		int actual = this.lengthFaceCN(str);
		
		if (maxNum < actual) {
			return true;

		}
		return false;
	}
	
	public boolean isLow(String str,int minNum){
		if (0 >= minNum) {
			throw new java.lang.IllegalArgumentException("The num(" + minNum
					+ ") is not illegal");
		}
		if (null == str || 0 == str.length()) {
			throw new java.lang.IllegalArgumentException(
					"The (str=null) is not illegal");
		}
		
		int actual = this.lengthFaceCN(str);
		
		if (minNum > actual) {
			return true;

		}
		return false;
	}

	@Override
	public boolean isNullOrEmpty(String str) {
		return null == str || 0 == str.trim().length();
	}

	@Override
	public boolean isCNorENorFigure(String str) {
		int len = 0;
		char c;
		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);
			if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z')
					|| (c >= 'A' && c <= 'Z')) {
				len++;
				continue;
			}
			if (Character.isLetter(c)) {
				len += 2;
				continue;
			}
			return false;
		}
		return true;
	}
	
	@Override
	public boolean isENorFigure(String str) {
		int len = 0;
		char c;
		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);
			if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z')
					|| (c >= 'A' && c <= 'Z')) {
				len++;
				continue;
			}
			return false;
		}
		return true;
	}

	@Override
	public int lengthFaceCN(String str) {
		int len = 0;
		char c;
		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);
			if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z')
					|| (c >= 'A' && c <= 'Z')) {
				len++;
			} else {
				if (Character.isLetter(c)) {
					len += 2;
				} else {
					len++;
				}
			}
		}
		return len;
	}
	
}
