package meiju;
public enum TrafficLamp
{
	RED(100){
		public TrafficLamp nextLamp() {
			return GREEN;
		}
	},
	GREEN(300){
		public TrafficLamp nextLamp(){
			return YELLOW;
		}
	},
	YELLOW(500){
		public TrafficLamp nextLamp(){
			return RED;
		}
	};
	public abstract TrafficLamp nextLamp();
	
	private int time;
	private TrafficLamp(int time){this.time = time;};
	
    public static void main(String[] args) throws InterruptedException
	{
		TrafficLamp t = TrafficLamp.GREEN;
		
		int i = 100;
		while(i--!=0)
		{
			Thread.sleep(t.time);
			t = t.nextLamp();
			System.out.println(t.name()+"¡¡¡À"+t.time+"∫¡√Î");
		}
	}
}

