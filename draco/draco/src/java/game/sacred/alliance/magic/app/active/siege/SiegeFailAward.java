package sacred.alliance.magic.app.active.siege;

public class SiegeFailAward extends SiegeWinAward {

	public int getBindGold(float bindAddRate) {
		return (int) (this.getBindGold() * (1 + bindAddRate));
	}

	public int getSilverMoney(float moneyAddRate) {
		return (int) (this.getSilverMoney() * (1 + moneyAddRate));
	}

	public int getExp(float expAddRate) {
		return (int) (this.getExp() * (1 + expAddRate));
	}
}
