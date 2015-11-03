package a_effective.enum_;
//135
/**
 * The strategy enum pattern
 *	枚举策略：虽然没有switch语句那么简洁，但是更加安全，也更灵活
 */
public enum PayrollDay {
	MONDAY(PayType.WEEKDAY),
	TUESDAY(PayType.WEEKDAY),
	WEDNESDAY(PayType.WEEKDAY),
	THURSDAY(PayType.WEEKDAY),
	FRIDAY(PayType.WEEKDAY),
	SATURDAY(PayType.WEEKEND),
	SUNDAY(PayType.WEEKEND);
	
	private final PayType payType;
	PayrollDay(PayType payType){
		this.payType = payType;
	}
	double pay(double hoursWorked, double payRate){
		return payType.pay(hoursWorked, payRate);
	}
	//The strategy enum type
	private enum PayType{
		WEEKDAY{
			double overtimePay(double hours,double payRate){
				return hours <= HOURS_PER_SHIFT ?0:(hours - HOURS_PER_SHIFT)*payRate/2;
			}
		},
		WEEKEND{
			double overtimePay(double hours,double payRate){
				return hours * payRate /2;
			}
		};
		private static final int HOURS_PER_SHIFT = 8;
		
		abstract double overtimePay(double hrs, double payRate);
		
		double pay(double hoursWorked, double payRate){
			double basePay = hoursWorked * payRate;
			return basePay + overtimePay(hoursWorked, payRate);
		}
	}
}
