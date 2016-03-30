package sacred.alliance.magic.util;


import lombok.Data;

public @Data
class ConsumeByLevel implements  Piecewise {

    private int minLevel ;
    private int maxLevel ;
    private int consumeValue ;

    @Override
    public int min() {
        return this.minLevel ;
    }

    @Override
    public int max() {
        return this.maxLevel ;
    }
}
