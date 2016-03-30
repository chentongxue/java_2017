package com.game.draco.base;

public interface ExecutorBean {

	/**
	 * 默认执行器
	 */
	public static final String pooledExecutor = "pooledExecutor";
	/**
	 * 登录执行器
	 */
	public static final String loginExecutor = "loginExecutor";
	/**
	 * 单用户单线程执行器
	 */
	public static final String userOrderedExecutor = "userOrderedExecutor";
	/**
	 * 公会执行器
	 */
	public static final String factionExecutor = "factionExecutor";
	/**
	 * 战斗执行器
	 */
	public static final String battleExecutor = "battleExecutor";
	/**
	 * 行走执行器
	 */
	public static final String posExecutor = "posExecutor";
	/**
	 * 停止执行器
	 */
	public static final String posStopExecutor = "posStopExecutor"; 
	/**
	 * 擂台赛执行器
	 */
	public static final String arenaExecutor = "arenaExecutor";
	/**
	 * 队伍执行器
	 */
	public static final String teamExecutor = "teamExecutor";
	/**
	 * 交易执行器
	 */
	public static final String tradingExecutor = "tradingExecutor";
	/**
	 * 阵营战执行器
	 */
	public static final String campWarExecutor = "campWarExecutor";

}
