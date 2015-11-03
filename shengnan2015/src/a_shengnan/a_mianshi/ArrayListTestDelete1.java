package a_shengnan.a_mianshi;

import java.util.ArrayList;
import java.util.Iterator;

//在循环中删除元素
public class ArrayListTestDelete1
{
    public static void main(String[] args)
    {
       ArrayList<Object> al = new ArrayList();
       al.add(new Person("List01", 30));
       al.add(new Person("List02", 31));
       al.add(new Person("List03", 32));
       al.add(new Person("List04", 33));
       
        System.out.println("remove"+al.remove(new Person("List01", 30)));
       
       Iterator it = al.iterator();
       while(it.hasNext())
       {
    	   System.out.println(al);
    	   it.next();//必须先it.next();才可以用it.remove();否则爆出IllegalStateException 
    	   it.remove();
    	   
       }
       System.out.println(al);
    }
}