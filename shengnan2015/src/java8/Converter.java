package java8;

/**
 * ����������������ע�����ע��Ľӿ��ж���һ�����󷽷���ʱ��ᱨ��
 */
@FunctionalInterface
interface Converter<F, T> {
    T convert(F from);
}
