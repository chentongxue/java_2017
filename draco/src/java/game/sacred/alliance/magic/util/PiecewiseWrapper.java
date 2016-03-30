package sacred.alliance.magic.util;


import com.google.common.collect.Maps;

import java.util.Map;

public class PiecewiseWrapper<T extends Piecewise> {

    private Map<String, T> piecewiseMap = Maps.newHashMap();
    private T max = null;

    public PiecewiseWrapper(java.util.List<T> list) {
        if (null == list) {
            return;
        }
        for (T t : list) {
            for (int i = t.min(); i <= t.max(); i++) {
                this.piecewiseMap.put(String.valueOf(i), t);
                if (null == max) {
                    max = t;
                    continue;
                }
                if (max.max() > t.max()) {
                    continue;
                }
                max = t;
            }
        }
    }

    public T getOrMax(String k) {
        T t = this.get(k);
        return (null == t) ? max : t;
    }

    public T getOrMax(int k) {
        return this.getOrMax(String.valueOf(k));
    }

    public T getOrMax(short k) {
        return this.getOrMax(String.valueOf(k));
    }

    public T getOrMax(byte k) {
        return this.getOrMax(String.valueOf(k));
    }

    public T get(String k) {
        return this.piecewiseMap.get(k);
    }

    public T get(int k) {
        return this.get(String.valueOf(k));
    }

    public T get(short k) {
        return this.get(String.valueOf(k));
    }

    public T get(byte k) {
        return this.get(String.valueOf(k));
    }
}
