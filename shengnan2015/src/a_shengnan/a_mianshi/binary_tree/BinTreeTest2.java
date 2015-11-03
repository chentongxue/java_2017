package a_shengnan.a_mianshi.binary_tree;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * [正]二叉树
 *
 */
public class BinTreeTest2 {


	private static int arr[] = {1,2,4,-1,-1,-1,3,5,7,-1,-1,8,-1,-1,6};
	private static List<Node> list = null;
	private static AtomicInteger index = new AtomicInteger(0);//计数器
	private int getNextValue(){
		return arr[index.getAndIncrement()];
	}
	private boolean isEnd(){
		return index.get() == arr.length;
	}

	private Node createBinTree(){
		if(isEnd()){//获取结束
			return null;
		}
		int data = getNextValue();
		if(data == -1){
			return null;
		}
		Node node = new Node(data);
		node.lChild = createBinTree();
		node.rChild = createBinTree();
		return node;
	}
	//中序
	public static void inOrderTraverse(Node node){
		if(node == null){
			return;
		}
		inOrderTraverse(node.lChild);
		System.out.println(node.data+"");
		inOrderTraverse(node.rChild);
		
	}
	//先序
	public static void prOrderTraverse(Node node){
		if(node == null){
			return;
		}
		System.out.println(node.data+"");
		prOrderTraverse(node.lChild);
		prOrderTraverse(node.rChild);
		
	}
	//后序
	public static void poOrderTraverse(Node node){
		if(node == null){
			return;
		}
		poOrderTraverse(node.lChild);
		poOrderTraverse(node.rChild);
		System.out.println(node.data+"");
		
	}
	
	//求深度
	public int height(Node node){
		int h, h1, h2;
		if(node == null){
			return 0;
		}
		h1 = height(node.lChild);
		h2 = height(node.rChild);
		h = Math.max(h1, h2) + 1;
		return h;
		
	}
	//数节点
	public int count(Node node){
		int m, n;
		if(node == null){
			return 0;
		}
		if(node.lChild == null && node.rChild == null){
			return 1;
		}
		m = height(node.lChild);
		n = height(node.rChild);
		return m + n;
		
	}
	public static void main(String[] args) {
		BinTreeTest2 tree = new BinTreeTest2();
		Node root = null;
		root = tree.createBinTree();
		poOrderTraverse(root);
		int h = tree.height(root);
		int sum = tree.count(root);
		System.out.println("height = " + h);
		System.out.println("sum = " + sum);
	}
}
