package a_effective.method.wine;
//166
public class Overriding {

	/**
	 * 对于覆盖虽然在每次迭代中，编译的类型是Wine，当调用被覆盖的方法时，对象的编译类型不会影响到哪个方法将被执行。
	 * 最为具体的（most specific）那个覆盖版本将被执行。而重载的情形，对象的运行时并不影响哪个重载版本将被执行，选择工作是在编译时进行的，完全基于参数的编译时类型。
	 */
	public static void main(String[] args) {
		Wine[] wines = {new Wine(), new SparklingWine(), new Champagne()};
		for(Wine w:wines){
			System.out.println(w.name());
		}
	}

}
/*
wine
Sparkling Wine
chanmpagne
*/