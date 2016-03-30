package sacred.alliance.magic.util;

public class SortedValueNumMap extends SortedValueMap {

	private static double COMPARISON_THRESHOLD = 0.001953125;

	/**
	 * Values should not be greater than this value.
	 */
	private double threshold = Double.MAX_VALUE;

	/**
	 * Use threshold?
	 */
	private boolean flagUseThreshold = false;

	/**
	 * The sum of the values should also not be greater than this value.
	 */
	private double sumThreshold = Double.MAX_VALUE;

	/**
	 * Use sum threshold?
	 */
	private boolean flagUseSumThreshold = false;

	/**
	 * Ascending or descending?
	 */
	private static boolean default_flagAscending = true;

	/**
	 * The default value for thresholds.
	 */
	private static double default_threshold = Double.MAX_VALUE;

	/**
	 * By default, should the SortedValueNumMap use the threshold value?
	 */
	private static boolean default_flagUseThreshold = false;

	/**
	 * The default value for sum thresholds.
	 */
	private static double default_sumThreshold = Double.MAX_VALUE;

	/**
	 * By default, should the SortedValueNumMap use the sum threshold value?
	 */
	private static boolean default_flagUseSumThreshold = false;

	/**
	 * Use this BEFORE you stick in items.
	 *
	 *  @param flag is true if ascending order, false if descending.
	 */
	public void setAscending(boolean flag) {
		super.setAscending(flag);
	}

	/**
	 * Set the threshold value for this SortedValueNumMap. Values added to this
	 * SortedValueNumMap cannot exceed this value. 
	 *
	 * @param d is the threshold value to set to.
	 */
	public void setThreshold(double d) {
		flagUseThreshold = true;
		threshold = d;
	}

	/**
	 * Use the current threshold value, or just ignore checking values?
	 *
	 * @param flag is true if we should check values against the threshold,
	 *        otherwise false.
	 */
	public void setUseThreshold(boolean flag) {
		flagUseThreshold = flag;
	}

	/**
	 * Set the threshold value for this SortedValueNumMap. The sum of values 
	 * added to this SortedValueNumMap cannot exceed this value. 
	 *
	 * @param d is the threshold sum value to set to.
	 */
	public void setSumThreshold(double d) {
		flagUseSumThreshold = true;
		sumThreshold = d;
	}

	/**
	 * Use the current sum threshold value, or just ignore checking the sum?
	 *
	 * @param flag is true if we should check sums against the sum threshold,
	 *        otherwise false.
	 */
	public void setUseSumThreshold(boolean flag) {
		flagUseSumThreshold = flag;
	}

	/**
	 * Set the default value for ascending or descending order. This is the
	 * sorting regime that new SortedValueNumMaps will use.
	 *
	 * @param flag is true if new SortedValueNumMaps should sort in ascending
	 *        order, false if in descending order.
	 */
	public static void setDefaultAscending(boolean flag) {
		default_flagAscending = flag;
	}

	/**
	 * Set the default threshold value. This is the threshold value that new
	 * SortedValueNumMaps will use. 
	 *
	 * @see   #setThreshold(double)
	 * @param f is the default threshold value to set to.
	 */
	public static void setDefaultThreshold(double d) {
		default_threshold = d;
	}

	/**
	 * Set whether or not new SortedValueNumMaps should use the threshold 
	 * value or not by default. That is, will new SortedValueNumMaps use a
	 * threshold value? New SortedValueNumMaps will use the default threshold
	 * value automatically, as set by setDefaultThresholdValue().
	 *
	 * @see   #setThreshold(double)
	 * @see   #setDefaultThreshold(double)
	 * @param flag is true if new SortedValueNumMaps should use threshold
	 *        value.
	 */
	public static void setDefaultUseThreshold(boolean flag) {
		default_flagUseThreshold = flag;
	}

	/**
	 * Set the default sum threshold value. This is the sum threshold value 
	 * that new SortedValueNumMaps will use.
	 *
	 * @see   #setSumThreshold(double)
	 * @param d is the default threshold value to set to.
	 */
	public static void setDefaultSumThreshold(double d) {
		default_sumThreshold = d;
	}

	/**
	 * Set whether or not new SortedValueNumMaps should use the sum threshold 
	 * value or not by default. That is, will new SortedValueNumMaps use a
	 * sum threshold value? New SortedValueNumMaps will use the default 
	 * sum threshold value automatically, as set by 
	 * setSumDefaultThresholdValue().
	 *
	 * @see   #setSumThreshold(double)
	 * @see   #setDefaultSumThreshold(double)
	 * @param flag is true if new SortedValueNumMaps should use threshold
	 *        value.
	 */
	public static void setDefaultUseSumThreshold(boolean flag) {
		default_flagUseSumThreshold = flag;
	}

	double sumVals = 0.0; // the sum of the values so far

	public SortedValueNumMap() {
		super(new NumericalComparator());

		//// 1. Set the threshold values to the default values.
		threshold = default_threshold;
		flagUseThreshold = default_flagUseThreshold;
		sumThreshold = default_sumThreshold;
		flagUseSumThreshold = default_flagUseSumThreshold;
	}

	/**
	 * Convenience method, puts number in as a Double object.
	 */
	public void put(Object key, int num) {
		put(key, new Double(num));
	}

	/**
	 * Convenience method, puts number in as a Double object.
	 */
	public void put(Object key, float num) {
		put(key, new Double(num));
	}

	/**
	 * Convenience method, puts number in as a Double object.
	 */
	public void put(Object key, double num) {
		put(key, new Double(num));
	}

	/**
	 * Add a key and its value. Convenience method, puts number in as a 
	 * Double object.
	 *
	 * @param     key   is the name of the key. if the key already exists, this
	 *                  will overwrite the previous value.
	 * @param     value is the value. Ensures that the value
	 *                  and the sum of the values does not exceed threshold.
	 * @exception NumberFormatException if the value or the sum exceeds
	 *            sumThreshold.
	 * @exception IllegalArgumentException if the same name is added more 
	 *            than once.
	 */
	public void put(Object key, Number num) {
		//// 0.1. Probability bounds checking.
		double value = num.doubleValue();
		if (flagUseThreshold && value > threshold + COMPARISON_THRESHOLD) {
			throw new NumberFormatException("Value " + value
					+ " cannot exceed threshold value " + threshold);
		}
		if (flagUseSumThreshold
				&& value + sumVals > sumThreshold + COMPARISON_THRESHOLD) {

			double val = value + sumVals;
			throw new NumberFormatException("Sum of values " + val
					+ " cannot exceed sum threshold value " + sumThreshold);
		}

		//// 1. Update the running total.
		sumVals += value;

		//// 2. Okay, just add it into the data struct.
		super.put(key, num);
	}

	public static void main(String[] argv) {
		SortedValueNumMap c = new SortedValueNumMap();
		c.setAscending(false);

		c.put("kuang", 12);
		c.put("zhang", 120);
		c.put("kuang", 121);
//		c.remove("kuang");
//		System.out.println(c);
		if (true) {
			return;
		}
		c.put("lemur", new Float(0.10f));
//		System.out.println(c);
		c.put("wallaby", new Float(0.09f));
//		System.out.println(c);

		c.put("wallaby", new Float(0.08f)); // should be no effect
//		System.out.println(c);

		c.put("kiwi", new Float(0.07f));
		c.put("mandrill", new Float(0.06f));
//		System.out.println(c);

		c.put("emu", new Float(0.05f));
//		System.out.println(c);

		c.put("exceedval", new Float(1.05f));
//		System.out.println(c);

		c.put("exceedsum", new Float(0.05f)); // should be added
//		System.out.println(c);

		c.remove("kiwi");
//		System.out.println(c);

		/*
		      SortedValueNumMap c2 = new SortedValueNumMap();
		      c2.setAscending(false);

		      c2.put("d", new Float(4.0f));
		      c2.put("a", new Float(1.0f));
		      c2.put("c", new Float(3.0f));
		      c2.put("b", new Float(2.0f));
		      System.out.println(c2);
		 */
	}

}
