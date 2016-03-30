package ac;

import java.util.Collection;

import ac.ahocorasick.trie.Emit;
import ac.ahocorasick.trie.Token;
import ac.ahocorasick.trie.Trie;

public class TestBao {
	public static void main(String args[]){
		System.out.println("start");
		test1();
//		test2();
//		test3();
//		test4();
//		test5();
	}
	//[2:3=he, 1:3=she, 2:5=hers]
	public static void test1(){
		 Trie trie = new Trie();
		    trie.addKeyword("hers");
		    trie.addKeyword("his");
		    trie.addKeyword("she");
		    trie.addKeyword("he");
		    trie.addKeyword("AIDS");
		    trie.addKeyword("傻Ｂ");
		    String s = WordsFilter.getInstance().getFilterWord("aaaa傻Ｂaaaa");
//		    Collection<Emit> emits = trie.parseText("ushers");
		    Collection<Token> tokens = trie.tokenize("aaaa傻Ｂaaaa");
		    for (Token token : tokens) {
		        if (token.isMatch()) {
		        	System.out.println(token.getFragment()+"!!!!!!!!!");
		        }
		    }
	    System.out.println(s);
	}
	//[2:3=he, 1:3=she, 2:5=hers]
//	public static void test2(){
//		 Trie trie = new Trie().removeOverlaps();
//		 trie.addKeyword("hot");
//		 trie.addKeyword("hot chocolate");
//		 Collection<Emit> emits = trie.parseText("hot chocolate");
//		System.out.println(emits);
//	}
//	//[20:24=sugar]
//	public static void test3(){
//	    Trie trie = new Trie().onlyWholeWords();
//	    trie.addKeyword("sugar");
//	    Collection<Emit> emits = trie.parseText("sugarcane sugarcane sugar canesugar");
//		System.out.println(emits);
//	}
//	//[0:5=casing]
//	public static void test4(){
//	    Trie trie = new Trie().caseInsensitive();
//	    trie.addKeyword("casing");
//	    Collection<Emit> emits = trie.parseText("CaSiNg");
//		System.out.println(emits);
//	}
//	//所有关键字进行替换或标记
	public static void test5(String speech){
	    Trie trie = new Trie().caseInsensitive();
	    trie.addKeyword("great question");
	    trie.addKeyword("forty-two");
	    trie.addKeyword("deep thought");
	    Collection<Token> tokens = trie.tokenize(speech);
	    StringBuffer html = new StringBuffer();
	    html.append("<html><body><p>");
	    for (Token token : tokens) {
	        if (token.isMatch()) {
	            html.append("<i>");
	        }
	        html.append(token.getFragment());
	        if (token.isMatch()) {
	            html.append("</i>");
	        }
	    }
	    html.append("</p></body></html>");
	    System.out.println(html);
//
	}
}
