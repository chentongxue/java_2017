package ac;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import ac.ahocorasick.trie.Token;
import ac.ahocorasick.trie.Trie;

public class WordsFilter {
	private WordsFilter(){
		trie = new Trie();
		BufferedReader br;
		try {
			File file = new File("dirty-word.txt");
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				trie.addKeyword(line);
			   }
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private final static WordsFilter instance = new WordsFilter();
	private final Trie trie;
	public static WordsFilter getInstance(){
		return instance;
	}
	
	public static void main(String args[]){
		test1();
		
		
	}
	public static void test1(){
		System.out.println("start");
		BufferedReader br;
		long time = System.currentTimeMillis();
		try {
			File file = new File("dirty-word.txt");
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				line = WordsFilter.getInstance().getFilterWord(line);
				System.out.println(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		time = System.currentTimeMillis() -time;
		System.out.println(time);
		
		
	}
	public static void test2(){
		System.out.println("start");
		BufferedReader br;
		long time = System.currentTimeMillis();
		try {
			File file = new File("dirty-word.txt");
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				line = WordsFilter.getInstance().getFilterWord(line);
				System.out.println(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		time = System.currentTimeMillis() -time;
		System.out.println(time);
		
		
	}
	//所有关键字进行替换或标记
	public String getFilterWord(final String speech){
	    Collection<Token> tokens = trie.tokenize(speech);
	    StringBuffer sb = new StringBuffer();
	    for (Token token : tokens) {
	        if (token.isMatch()) {
	            sb.append("*");
	            continue;
	        }
	        sb.append(token.getFragment());
	    }
	    return sb.toString();
	}
}
