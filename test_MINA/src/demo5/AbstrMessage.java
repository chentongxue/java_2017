package demo5;
/**
 *  ���ȶ�����Ϣ�ĳ����࣬�����ȡ3��������Ϣ�ķ���
 */
import java.nio.charset.Charset;

public abstract class AbstrMessage {
	// Э����
	public abstract short getTag();

	// ����������
	public abstract int getLen(Charset charset);

	// ��ʵ����ƫ�Ƶ�ַ
	public abstract int getDataOffset();
}
