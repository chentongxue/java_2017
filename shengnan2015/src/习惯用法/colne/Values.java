package œ∞πﬂ”√∑®.colne;

import java.util.Date;

public class Values implements Cloneable {
	private String abc;
	private  double foo;
	private int[] bars;
	private Date hired;
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		Values result = (Values)super.clone();
		result.bars = result.bars.clone();
		result.hired = (Date) result.hired.clone();
		return result;
	}
	
}
