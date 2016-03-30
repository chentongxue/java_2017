package com.game.draco.component.ssdb.pool;

public class PoolException extends RuntimeException {
    private static final long serialVersionUID = -2946266495682282677L;

    public PoolException(String message) {
	super(message);
    }

    public PoolException(Throwable e) {
	super(e);
    }

    public PoolException(String message, Throwable cause) {
	super(message, cause);
    }
}
