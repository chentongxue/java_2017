package a_effective.annotation.sample3;

import java.util.ArrayList;
import java.util.List;

public class Sample3 {
	public static void doublyBad(){
		List<String> list = new ArrayList<String>();
		
		//The spec permits this method to throw either
		//IndexOut ofBoundsException or nullPointerException
	}
}
