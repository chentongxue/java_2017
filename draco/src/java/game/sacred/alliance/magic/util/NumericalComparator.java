package sacred.alliance.magic.util;

import java.util.Comparator;

public class NumericalComparator implements Comparator {
	private boolean flagUsePerturbations = false;

	/**
	 * Add perturbations to the values or not? Basically, if set to true, we
	 * will add two different small values to each of the numbers before
	 * comparison, to avoid equal values.
	 *
	 * @param flag is true if perturbations are to be used, false otherwise.
	 */
	public void setUsePerturbations(boolean flag) {
		flagUsePerturbations = flag;
	}

	public int compare(Object obj1, Object obj2) {
		if (obj1 instanceof Number && obj2 instanceof Number) {
			//// 1. Get numerical values.
			int returnVal = 0;
			double val1 = ((Number) obj1).doubleValue();
			double val2 = ((Number) obj2).doubleValue();

			//// 2. Do comparisons.
			if (val1 < val2) {
				returnVal = -1;
			} else {
				if (val1 > val2) {
					returnVal = 1;
				} else {
					returnVal = 0;
				}
			}

			//// 3. I actually thought about using perturbations, but this
			////    seems much simpler.
			if (flagUsePerturbations == true) {
				returnVal = 1;
			}

			return (returnVal);
		}
		throw new ClassCastException("Only know how to handle numbers.");
	} 

} 
