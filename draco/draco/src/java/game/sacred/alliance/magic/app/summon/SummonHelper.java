package sacred.alliance.magic.app.summon;

public class SummonHelper {
	public final static short SUMMON_CMD = 1180;
	public static final String SummonId_Key = "summonId" ;
	public static final String Summon_npcId = "npcId" ;
	
	/**
	 * 拼接字符串
	 * @param summonId
	 * @param npcId
	 * @return
	 */
	public static String formatSummonParam(int summonId, String npcId){
		return SummonId_Key + "=" +  summonId + "&" + Summon_npcId + "=" + npcId;
	}
}
