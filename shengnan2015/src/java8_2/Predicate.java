package java8_2;
@FunctionalInterface
interface Predicate {
	<T> T test(T t);
}
