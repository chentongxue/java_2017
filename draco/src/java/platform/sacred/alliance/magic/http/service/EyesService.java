package sacred.alliance.magic.http.service;

import com.alibaba.fastjson.JSON;
import com.game.draco.GameContext;
import com.game.draco.app.npc.refresh.NpcRefreshTask;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.channel.http.HttpContext;
import sacred.alliance.magic.channel.http.service.SimpleHttpService;

import java.util.List;
import java.util.Map;


public class EyesService extends SimpleHttpService {

    private final static String KEY = "zmkm" ;

    @Override
    public String getStringBody(HttpContext context) {
        this.setDefaultContentType("text/plain; charset=utf-8");
        String key = context.getRequest().getParameter("_k");
        if(Util.isEmpty(key) || !key.equals(KEY)){
            return "forbid" ;
        }
        String which = context.getRequest().getParameter("w");
        if(Util.isEmpty(which)){
            return "which?";
        }
        if(which.equals("npcrefresh")){
            Map<String, List<NpcRefreshTask>> info = GameContext.getNpcRefreshApp().getNpcRefreshTaskMap() ;
            return JSON.toJSONString(info,true);
        }
        if(which.equals("printrank")){
            GameContext.getRankApp().printRankLogTimer();
            return "success" ;
        }
        return "success";
    }
}
