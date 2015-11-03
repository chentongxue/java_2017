package œ∞πﬂ”√∑®.compareto;

public class Person implements Comparable<Person>{
	private String firstName;
	private String lastName;
	private int birthdate;
	
	@Override
	public int compareTo(Person other){
		int comparison = firstName.compareTo(other.firstName);
		if(comparison == 0){
			comparison = lastName.compareTo(other.lastName);
		}
		if(comparison == 0){
			comparison = birthdate - other.birthdate;
		}
		return comparison;
	}
}
