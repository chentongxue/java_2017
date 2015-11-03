package a_shengnan.a_mianshi;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/*将自定义对象作为元素，存到ArrayList集合中，并去除重复元素*/
public class ArrayListTest2
{
    public static void main(String[] args)
    {
       ArrayList<Object> al = new ArrayList();
       al.add(new Person("List01", 30));
       al.add(new Person("List02", 31));
       al.add(new Person("List03", 32));
       al.add(new Person("List04", 33));
       
       al.add(new Person("List04", 33));
       al.add(new Person("List04", 33));
       
       al = singleElement(al);
        System.out.println("remove"+al.remove(new Person("List01", 30)));
       
       Iterator it = al.iterator();
       while(it.hasNext())
       {
           Person p = (Person) it.next();
           System.out.println(p.getName()+"::"+p.getAge());
       }
    }
    public static ArrayList singleElement(ArrayList al)
    {
       //定义一个临时容器
       ArrayList newAL = new ArrayList();
       Iterator it = al.iterator();
       while(it.hasNext())
       {
           Object obj = it.next();
           if(!newAL.contains(obj) )
               newAL.add(obj);
       }
       return newAL;
    }
}
class Person
{
    private String name;
    private int age;
    Person(String name,int age)
    {
       this.name = name;
       this.age = age;
    }
    public String getName()
    {
       return name;
    }
    public int getAge()
    {
       return age;
    }
    public boolean equals(Object obj)
    {
       if(!(obj instanceof Person))
           return false;
       Person p = (Person)obj;
       return this.name.equals(p.name)&&this.age == p.age;
    }
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
    
}