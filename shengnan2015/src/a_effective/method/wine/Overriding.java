package a_effective.method.wine;
//166
public class Overriding {

	/**
	 * ���ڸ�����Ȼ��ÿ�ε����У������������Wine�������ñ����ǵķ���ʱ������ı������Ͳ���Ӱ�쵽�ĸ���������ִ�С�
	 * ��Ϊ����ģ�most specific���Ǹ����ǰ汾����ִ�С������ص����Σ����������ʱ����Ӱ���ĸ����ذ汾����ִ�У�ѡ�������ڱ���ʱ���еģ���ȫ���ڲ����ı���ʱ���͡�
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