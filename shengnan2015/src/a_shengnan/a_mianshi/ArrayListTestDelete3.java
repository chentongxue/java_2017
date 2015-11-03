package a_shengnan.a_mianshi;

import java.util.ArrayList;

//
public class ArrayListTestDelete3
{
    public static void main(String[] args)
    {
       ArrayList<Person> al = new ArrayList<Person>();
       al.add(new Person("List01", 30));
       al.add(new Person("List02", 31));
       al.add(new Person("List03", 32));
       al.add(new Person("List04", 33));
       
       System.out.println("remove"+al.remove(new Person("List01", 30)));
       
       for (Person p : al) {
    	  System.out.println(al);
    	  al.remove(p);
       }
       System.out.println(al);
    }
}