package tree;

import java.util.Arrays;

import javax.naming.spi.DirStateFactory.Result;

public class Calculator {
	public static void main(String[] args) {
		System.out.println("shengnan");
		byte[] a = {6,5,7,8,4,3,9,0,1};
		Arrays.sort(a);
		System.out.println(Arrays.toString(a));
	}
}

class TreeNode {
	double data;
	char operation;
	TreeNode left;
	TreeNode right;

	/**
	 * recursively construct the tree
	 * @param expression
	 */
	public TreeNode(String expression) {
//		char[] exc = 
	}
	/**
	 * trim the out most useless parentheses and return a char array
	 */
	public char[] toCharArrayTrimOutParenthes(String src){
		if(src.length() == 0){
			return null;
		}
		String result = src;
		while(result.charAt(0)=='('&&result.charAt(result.length()-1)==')'){
			int parenthes = 0;
			for(int i = 0;i<result.length()-1;i++){
//				if(re)
			}
		}
		return null;
	}
}
