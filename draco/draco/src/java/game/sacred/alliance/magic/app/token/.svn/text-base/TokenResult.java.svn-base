package sacred.alliance.magic.app.token;

import sacred.alliance.magic.base.Result;

public class TokenResult extends Result{
	
	private AccountToken accountToken;
	
	@Override
	public TokenResult setInfo(String info) {
		this.info = info;
		return this;
	}
	
	@Override
	public TokenResult success(){
		this.result = TokenResult.SUCCESS;
		return this;
	}
	
	@Override
	public TokenResult failure(){
		this.result = TokenResult.FAIL;
		return this;
	}

	public AccountToken getAccountToken() {
		return accountToken;
	}

	public void setAccountToken(AccountToken accountToken) {
		this.accountToken = accountToken;
	}
	
}
