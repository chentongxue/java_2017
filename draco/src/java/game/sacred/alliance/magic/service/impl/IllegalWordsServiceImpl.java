package sacred.alliance.magic.service.impl;

import java.util.Collection;

import sacred.alliance.magic.app.config.WordsConfig;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.service.IllegalWordsService;
import sacred.alliance.magic.util.StringUtil;
import sacred.alliance.magic.util.ahocorasick.trie.Emit;
import sacred.alliance.magic.util.ahocorasick.trie.Token;
import sacred.alliance.magic.util.ahocorasick.trie.Trie;

public class IllegalWordsServiceImpl implements IllegalWordsService {

	private static final char DEFAULT_FILER_COVER_CHAR = '*';
	
	private WordsConfig illegalWordsConfig,forbiddenWordsConfig;
	
	public void setIllegalWordsConfig(WordsConfig illegalWordsConfig) {
		this.illegalWordsConfig = illegalWordsConfig;
	}

	public void setForbiddenWordsConfig(WordsConfig forbiddenWordsConfig) {
		this.forbiddenWordsConfig = forbiddenWordsConfig;
	}
    //聊天过滤
	@Override
	public String doFilter(String text) {
		return doFilter(text,DEFAULT_FILER_COVER_CHAR);
	}
	//un
	@Override
	public String doFilter(String text, char newStr) {
		if(Util.isEmpty(text)){
			return text;
		}
		Trie trie = illegalWordsConfig.getTrie();
	    Collection<Token> tokens = trie.tokenize(text);
	    StringBuilder sb = new StringBuilder();
	    for (Token token : tokens) {
	        if (token.isMatch()) {
	            sb.append(newStr);
	            continue;
	        }
	        sb.append(token.getFragment());
	    }
	    return sb.toString();
	}

	@Override
	public String findIllegalChar(String text) {
		return this.findChar(text, this.illegalWordsConfig.getTrie());
	}
	
	@Override
	public String findForbiddenChar(String text) {
		return this.findChar(text, this.forbiddenWordsConfig.getTrie());
	}
	
	/**
	 *
	 * 在文本中查找字库中的字符
	 * @param text 文本
	 * @param words 字库
	 * @return not match:NULL,match: keywords
	 */
	private String findChar(String text, Trie trie){
		if(Util.isEmpty(text)){
			return null;
		}
		//如果是一个数字或字母返回null
		if (1 == text.length()&& (StringUtil.isCharacter(text))) {
			return null;
		}
		Collection<Emit> emits = trie.parseText(text);
		if(emits.isEmpty()){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (Emit et : emits) {
			sb.append(et.getKeyword());
		}
		return sb.toString();
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
