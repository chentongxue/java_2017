package a_shengnan.a_mianshi.binary_tree;


/**
 * 内部类，节点
 *
 */
public class Node{
	Node lChild;
	Node rChild;
	int data;
	Node(int data){
		lChild = null;
		rChild = null;
		this.data = data;
	}
}