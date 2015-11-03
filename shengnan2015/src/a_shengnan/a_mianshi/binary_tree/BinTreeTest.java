package a_shengnan.a_mianshi.binary_tree;

import java.util.LinkedList;
import java.util.List;

public class BinTreeTest {


	private static int arr[] = {1, 2, 3, 4, 5, 6, 7, 8, 9};
	private static List<Node> list = null;
	/**
	 * 内部类，节点
	 *
	 */
	private static class Node{
		Node leftChild;
		Node rightChild;
		int data;
		Node(int data){
			leftChild = null;
			rightChild = null;
			this.data = data;
		}
	}
	private void createBinTree(){
		list = new LinkedList<Node>();
		for(int i = 0; i< arr.length; i++){
			list.add(new Node(arr[i]));
		}
		for(int i = 0; i< arr.length /2 -1; i++){
			list.get(i).leftChild = list.get(i * 2 + 1);
			list.get(i).rightChild = list.get(i * 2 + 2);
		}
		int last = arr.length /2 -1;
		list.get(last).leftChild = list.get(last *2 +1);
		if(arr.length %2 == 1){
			list.get(last).rightChild = list.get(last * 2 + 2); 
		}
	}
	public static void inOrderTraverse(Node node){
		if(node == null){
			return;
		}
		System.out.println(node.data+"");
		inOrderTraverse(node.leftChild);
		inOrderTraverse(node.rightChild);
		
	}
	public static void main(String[] args) {
		BinTreeTest bt = new BinTreeTest();
		bt.createBinTree();
		Node root = list.get(0);
		inOrderTraverse(root);
	}
}
