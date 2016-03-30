package sacred.alliance.magic.app.invite;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.game.draco.GameContext;

import sacred.alliance.magic.base.OsType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

public class InviteAppImpl implements InviteApp ,Service{

	private List<ActivatedReward> activatedRewardList ;
	private InviteConfig inviteConfig ;
	private Map<String,InviteDownLoad> downLoadMap ;
		
	@Override
	public void setArgs(Object arg0) {
		
	}
	

	@Override
	public void start() {
		this.loadActivatedReward();
		this.loadInviteConfig() ;
		this.loadDownLoad() ;
	}

	@Override
	public void stop() {
		
	}
	
	private void checkGoodsId(int goodsId,String fileName,String sheetName){
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null != gb){
			return ;
		}
		Log4jManager.checkFail();
		Log4jManager.CHECK.error("goods not exist goodsId=" + goodsId + " sourceFile="+fileName +" sheetName="+sheetName);
	}
	
	private void loadDownLoad(){
		String fileName = XlsSheetNameType.invite_download.getXlsName();
		String sheetName = XlsSheetNameType.invite_download.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			downLoadMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, InviteDownLoad.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile=" + fileName + "sheetName=" + sheetName, ex);
		}
	}

	private void loadInviteConfig(){
		String fileName = XlsSheetNameType.invite_config.getXlsName();
		String sheetName = XlsSheetNameType.invite_config.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<InviteConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, InviteConfig.class);
			if(Util.isEmpty(list)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("not any config: sourceFile="+fileName +" sheetName="+sheetName);
			}
			this.inviteConfig = list.get(0);
			this.checkGoodsId(this.inviteConfig.getGoodsId(), fileName, sheetName);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile=" + fileName + "sheetName=" + sheetName, ex);
		}
	}
	
	private void loadActivatedReward(){
		String fileName = XlsSheetNameType.invite_activated_reward.getXlsName();
		String sheetName = XlsSheetNameType.invite_activated_reward.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			activatedRewardList = XlsPojoUtil.sheetToList(sourceFile, sheetName, ActivatedReward.class);
			if(Util.isEmpty(activatedRewardList)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("not any config: sourceFile="+fileName +" sheetName="+sheetName);
			}
			//按照次数排序
			Collections.sort(this.activatedRewardList, new Comparator<ActivatedReward>(){
				@Override
				public int compare(ActivatedReward o1, ActivatedReward o2) {
					return o1.getTimes() >= o2.getTimes()?1:-1 ;
				}
			});
			for(ActivatedReward reward : this.activatedRewardList){
				this.checkGoodsId(reward.getGoodsId(), fileName, sheetName);
			}
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile=" + fileName + "sheetName=" + sheetName, ex);
		}
	}


	@Override
	public List<ActivatedReward> getActivatedRewardList() {
		return this.activatedRewardList ;
	}

	@Override
	public InviteConfig getInviteConfig() {
		return this.inviteConfig ;
	}
	
	@Override
	public String getSharedInfo(RoleInstance role,String code) {
		String msg = this.inviteConfig.getInviteInfo();
		return MessageFormat.format(msg, code,this.getDownLoadUrl(role)) ;
	}
	
	private String getDownLoadUrl(RoleInstance role){
		int channelId = role.getChannelId() ;
		byte osType = OsType.getByResType(role.getResType()).getType();
		String url = GameContext.getInviteConfig().getDownLoadUrl(channelId, osType);
		if(null != url){
			return url ;
		}
		if(null == this.downLoadMap){
			return "" ;
		}
		String key = channelId + Cat.underline + osType ;
		InviteDownLoad object = this.downLoadMap.get(key);
		return (null == object)?"":object.getUrl() ;
	}
}
