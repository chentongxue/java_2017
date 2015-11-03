package a_shengnan.albb_0;

//What will happen when you attempt to compile and run the following code?    

public class Base
{
	int i = 99;
	public void amethod(){
		System.out.println("Base.amethod()");
	}
	base(){
		amethod();
	}
	public static void main(String args[]){
		new Base();
	}
}
/*Choices:  
A.Derived.amethod() -1 Derived.amethod() 
B.Derived.amethod() 99 Derived.amethod()
C.Compile time error 
D.Derived.amethod() */