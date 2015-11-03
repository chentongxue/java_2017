package meiju;

public class TE {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int userType = 1;
		UserType type = UserType.valueOf(userType);
		switch (type) {
		case STUDENT: 
			System.out.println(type.getDescription());
			type = UserType.TEACHER;
		   break;
		case TEACHER:
			System.out.println(type.getDescription());
		   break;
		case PARENT:
			System.out.println(type.getDescription());
		   break;
		}
	}

}
