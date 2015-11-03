package a_shengnan.a_mianshi.binary_tree;

import java.util.LinkedList;
import java.util.List;
/**
 * 找到那个错误！
 */
public class BinTreeERR {


	private static int arr[] = {1, 2, 3, 4, 5, 6, 7, 8, 9};
	private static List<Node> list = null;
	/**
	 * 内部类，节点
	 *
	 */
	private static class Node<T>{
		Node<T> lChild;
		Node<T> rChild;
		T data;
		Node(T data){
			lChild = null;
			rChild = null;
			this.data = data;
		}
	}
	private void createBinTree(){
		list = new LinkedList<Node>();
		for(int i = 0; i< arr.length; i++){
			list.add(new Node(arr[i]));
		}
		for(int i = 0; i< arr.length /2 -1; i++){
			list.get(i).lChild = list.get(i * 2 + 1);
			list.get(i).rChild = list.get(i * 2 + 2);
		}
		int last = arr.length /2 -1;
		list.get(last).lChild = list.get(last *2 +1);
		if(arr.length %2 == 1){
			list.get(last).rChild = list.get(last * 2 + 2); 
		}
	}
	public static void inOrderTraverse(Node node){
		if(node == null){
			return;
		}
		System.out.println(node.data+"");
		inOrderTraverse(node.lChild);
		inOrderTraverse(node.rChild);
		
	}
	public static void main(String[] args) {
		BinTreeERR bt = new BinTreeERR();
		bt.createBinTree();
		Node root = list.get(0);
		inOrderTraverse(root);
	}
}
