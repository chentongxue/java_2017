package com.game.draco.app.operate.simple;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;

import com.game.draco.GameContext;
import com.game.draco.app.operate.OperateActive;
import com.game.draco.app.operate.simple.config.SimpleActiveConfig;
import com.game.draco.app.operate.vo.OperateActiveType;
import com.google.common.collect.Lists;

public class SimpleAppImpl implements SimpleApp {
	private static final Logger logger = LoggerFactory.getLogger(SimpleAppImpl.class);

	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void start() {
		Result result = this.init();
		if (!result.isSuccess()) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error(result.getInfo());
		}
	}
	
	/**
	 * 加载配置
	 * @return
	 */
	private Result init() {
		Result result = new Result();
		try {
			String xlsPath = GameContext.getPathConfig().getXlsPath();
			String fileName = XlsSheetNameType.operate_simple_base.getXlsName();
			String sheetName = XlsSheetNameType.operate_simple_base.getSheetName();
			Map<Integer, SimpleActiveConfig> activeMap = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, SimpleActiveConfig.class);
			if (Util.isEmpty(activeMap)) {
				return result.success();
			}
			// 初始化配置
			for (SimpleActiveConfig config : activeMap.values()) {
				if (null == config) {
					continue;
				}
				Result initResult = config.init();
				if (!initResult.isSuccess()) {
					return initResult;
				}
			}
			Result regResult = GameContext.getOperateActiveApp().registerOperateActive(this.getOperateActiveList(activeMap.values()), OperateActiveType.simple);
			if (!regResult.isSuccess()) {
				return regResult;
			}
			result.success();
		} catch (Exception e) {
			logger.error("", e);
		}
		return result;
	}
	
	/**
	 * 封装类型活动
	 * @return
	 */
	private List<OperateActive> getOperateActiveList(Collection<SimpleActiveConfig> configList) {
		List<OperateActive> list = Lists.newArrayList();
		for (SimpleActiveConfig config : configList) {
			if (null == config) {
				continue;
			}
			list.add(new SimpleActive(config));
		}
		return list;
	}

	@Override
	public void stop() {
	}

	@Override
	public Result reload() {
		return this.init();
	}

}
