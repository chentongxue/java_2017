package com.game.draco.app.forward;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.RTSI;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.forward.config.ForwardConfig;
import com.game.draco.app.forward.logic.ForwardLogic;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ForwardAppImpl implements ForwardApp {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Getter @Setter private Map<Short,ForwardConfig> forwardConfigMap = Maps.newHashMap() ;
	@Getter @Setter private Map<Byte,ForwardLogic> forwardLogicMap = Maps.newHashMap() ;
	
	
	private ForwardLogic getForwardLogic(byte type){
		return forwardLogicMap.get(type);
	}
	
	private ForwardConfig getForwardConfig(short id){
		return forwardConfigMap.get(id) ;
	}
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.registerLogic();
		this.loadForwardConfig();
	}
	
	

	@Override
	public void stop() {
		
	}

	@Override
	public void forward(RoleInstance role,short forwardId) {
		ForwardConfig config = this.getForwardConfig(forwardId) ;
		if(null == config){
			return ;
		}
		ForwardLogic logic = this.getForwardLogic(config.getType()) ;
		if(null == logic){
			return ;
		}
		logic.forward(role, config);
	}
	
	private void loadForwardConfig(){
		Map<Short,ForwardConfig> configMap = XlsPojoUtil.loadMap(XlsSheetNameType.forward_config, ForwardConfig.class, false) ;
		if(Util.isEmpty(configMap)){
			this.forwardConfigMap = configMap ;
			return ;
		}
		for(ForwardConfig config : configMap.values()){
			ForwardLogic logic = this.getForwardLogic(config.getType());
			if(null != logic){
				continue ;
			}
			Log4jManager.CHECK.error("ForwardConfig config error,type=" + config.getType()
					+ " not exist,id=" + config.getId());
			Log4jManager.checkFail();
		}
		this.forwardConfigMap = configMap ;
	}
	
	private void registerLogic() {
		try {
			List<String> pkgList = Lists.newArrayList();
			pkgList.add(ForwardLogic.class.getPackage().getName());

			Set<Class> logicList = RTSI.findClass(pkgList, ForwardLogic.class);
			for (Class clazz : logicList) {
				ForwardLogic logic = (ForwardLogic) clazz.newInstance();
				forwardLogicMap.put(logic.getForwardLogicType().getType(), logic);
				logger.info("registerLogic:" + clazz.getName());
			}
		} catch (Exception ex) {
			logger.error("registerLogic error",ex);
			Log4jManager.CHECK.error("register ForwardLogic error",ex);
			Log4jManager.checkFail();
		}
	}

	
}
