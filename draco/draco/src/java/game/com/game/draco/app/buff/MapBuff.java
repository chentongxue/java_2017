package com.game.draco.app.buff;


public class MapBuff extends BuffAdaptor {

	public MapBuff(short buffId) {
		super(buffId);
	}

	@Override
	public void attacked(BuffContext context){
		
	}
	
	@Override
	protected void store(BuffContext context){
		
	}

	@Override
	protected void resume(BuffContext context){
		
	}
	
	
	@Override
	protected void execute(BuffContext context, BuffFuncPoint fp) {
		try {
			this.execSkills(context);
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}

	@Override
	protected boolean hasProcess(BuffContext context) {
		return true;
	}

}
