package a_effective.enum_;
/***
 * 137 ��ʵ�����������
 *  Abuse of ordinal to derive an associated value -DON'T DO THIS
 */
public enum Ensemble {
	SOLO,	DUET, TRIO,	QUARTET, QUINTET,
	SEXTEX, SEPTET, OCTET, MONET, DECETET;
	
	public int numberOfMusicians(){ return ordinal() + 1;}
}
