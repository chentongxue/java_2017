package sacred.alliance.magic.app.arena;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.arena.config.ArenaConfig;
import sacred.alliance.magic.app.arena.config.Reward1V1Finish;
import sacred.alliance.magic.app.arena.config.Reward1v1Bout;
import sacred.alliance.magic.app.arena.domain.Arena1V1RealTime;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Arena1V1AppImpl implements Arena1V1App {

    private final static int Broadcast_Top_Num = 3;
    ;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 实时数据Map
     */
    private Map<String, Arena1V1RealTime> realTimeDataMap = new ConcurrentHashMap<String, Arena1V1RealTime>();
    /**
     * 实时数据排序(准实时)
     */
    private List<Arena1V1RealTime> realTimeSortList = new ArrayList<Arena1V1RealTime>();
    /**
     * key: roleId
     * value:排名
     */
    private Map<String, Integer> realTimeSortMap = new ConcurrentHashMap<String, Integer>();

    private Active active = null;

    private Map<Integer, Reward1v1Bout> reward1v1BoutMap = null;
    private int maxExpLevel = 0;
    /**
     * 1v1奖励配置map
     * key: roleLevel
     */
    private Map<Integer, List<Reward1V1Finish>> reward1V1FinishMap = new HashMap<Integer, List<Reward1V1Finish>>();

    private int maxArena1V1RewardRank = 0;


    private int pageSize = 10;

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }


    @Override
    public List<Arena1V1RealTime> getArena1V1RealTime() {
        return this.realTimeSortList;
    }

    @Override
    public int getRank(String roleId) {
        if (Util.isEmpty(roleId)) {
            return 0;
        }
        Integer rank = this.realTimeSortMap.get(roleId);
        return null == rank ? 0 : rank;
    }

    /**
     * 清除实时数据
     */
    private void cleanRealTimeData() {
        realTimeDataMap.clear();
        realTimeSortList.clear();
        realTimeSortMap.clear();
    }

    /**
     * 活动开始接口,系统任务调用
     * 1.清除实时数据
     */
    @Override
    public void activeStart() {
        this.cleanRealTimeData();
    }

    @Override
    public boolean isAutoApply(RoleInstance role) {
        if (null == role) {
            return false;
        }
        Arena1V1RealTime data = this.realTimeDataMap.get(role.getRoleId());
        if (null == data) {
            return false;
        }
        return data.isAutoApply();
    }

    @Override
    public void setAutoApply(RoleInstance role, boolean autoApply) {
        if (null == role) {
            return;
        }
        Arena1V1RealTime data = this.realTimeDataMap.get(role.getRoleId());
        if (null == data) {
            data = this.newArena1V1RealTime(role);
            this.realTimeDataMap.put(data.getRoleId(), data);
        }
        data.setAutoApply(autoApply);
    }


    @Override
    public void syncRealTimeData(RoleInstance role) {
        if (null == role) {
            return;
        }
        Arena1V1RealTime data = this.realTimeDataMap.get(role.getRoleId());
        if (null == data) {
            data = this.newArena1V1RealTime(role);
            this.realTimeDataMap.put(data.getRoleId(), data);
            return;
        }
        data.setScore(role.getRoleArena().getScore(ArenaType._1V1));
        data.setRoleLevel(role.getLevel());
    }

    private Arena1V1RealTime newArena1V1RealTime(RoleInstance role) {
        Arena1V1RealTime data = new Arena1V1RealTime();
        data.setRoleId(role.getRoleId());
        data.setRoleName(role.getRoleName());
        data.setScore(role.getRoleArena().getScore(ArenaType._1V1));
        data.setBattleScore(role.getBattleScore());
        data.setCampId(role.getCampId());
        data.setRoleLevel(role.getLevel());
        return data;
    }

    /**
     * 排序
     */
    private void sort() {
        if (Util.isEmpty(this.realTimeDataMap)) {
            return;
        }
        List<Arena1V1RealTime> orgiData = new ArrayList<Arena1V1RealTime>();
        orgiData.addAll(this.realTimeDataMap.values());
        Collections.sort(orgiData, new Comparator<Arena1V1RealTime>() {
            @Override
            public int compare(Arena1V1RealTime o1, Arena1V1RealTime o2) {
                if (o1.getScore() > o2.getScore()) {
                    return -1;
                }
                if (o1.getScore() < o2.getScore()) {
                    return 1;
                }
                if (o1.getBattleScore() > o2.getBattleScore()) {
                    return -1;
                }
                if (o1.getBattleScore() < o2.getBattleScore()) {
                    return 1;
                }
                return 0;
            }
        });
        this.realTimeSortList = orgiData;
        int rank = 0;
        Map<String, Integer> rankMap = new HashMap<String, Integer>();
        for (Arena1V1RealTime data : this.realTimeSortList) {
            rank++;
            rankMap.put(data.getRoleId(), rank);
            if (rank > 5) {
                continue;
            }
            int oldRank = this.getRank(data.getRoleId());
            if (oldRank > 0 && oldRank > rank) {
                // 世界广播
                this.broadcast(data.getRoleId(), rank);
            }
        }
        this.realTimeSortMap.clear();
        this.realTimeSortMap.putAll(rankMap);
        rankMap.clear();
        rankMap = null;
    }

    /**
     * 世界广播
     *
     * @param roleId
     * @param rank
     */
    private void broadcast(String roleId, int rank) {
        try {
            RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
            if (null == role) {
                return;
            }
            String roleName = Util.getColorRoleName(role, ChannelType.Publicize_Personal);
            String rankInfo = rank + Util.getColor(ChannelType.Publicize_Personal.getColor());
            String message = GameContext.getI18n().getText(TextId.BROAD_CAST_ARENA_PVP).replace(Wildcard.Role_Name, roleName).replace(Wildcard.Number, rankInfo);
            GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, message, null, null);
        } catch (Exception e) {
            logger.error("Arena1v1AppImpl.broadcast error!", e);
        }
    }

    @Override
    public void activeIng() {
        this.sort();
    }

    /**
     * 活动结束接口,系统任务调用 1.根据实时数据生成最新历史排名数据 2.清除实时数据
     */
    @Override
    public void activeStop() {
        //根据实时数据生成最新历史排名数据
        this.sort();
        this.broadcastTop3();
        this.reward();
        //保存RoleArena(为了大师赛入库前n名)
        this.saveRoleArean();
        this.cleanRealTimeData();
        //给大师赛入围者发送提示邮件
        GameContext.getArenaTopApp().racersMailAlert();
    }


    private void broadcastTop3() {
        try {
            int num = Math.min(realTimeSortList.size(), Broadcast_Top_Num);
            if (num <= 0) {
                return;
            }
            StringBuilder buffer = new StringBuilder();
            String cat = "";
            for (int i = 0; i < num; i++) {
                Arena1V1RealTime info = realTimeSortList.get(i);
                buffer.append(cat);
                buffer.append(info.getRoleName());
                cat = ",";
            }
            String text = GameContext.getI18n().messageFormat(
                    TextId.ARENA_1V1_BROADCAST_TOP, String.valueOf(num),
                    buffer.toString());
            GameContext.getChatApp().sendSysMessage(ChatSysName.System,
                    ChannelType.Publicize_System, text, null, null);
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    private void saveRoleArean() {
        if (Util.isEmpty(this.realTimeSortList)) {
            return;
        }
        for (Arena1V1RealTime data : this.realTimeSortList) {
            try {
                RoleInstance role = GameContext.getUserRoleApp()
                        .getRoleByRoleId(data.getRoleId());
                if (null == role) {
                    continue;
                }
                //RoleAreanSaveInternalMessage reqMsg = new RoleAreanSaveInternalMessage();
                //reqMsg.setRoleId(role.getRoleId());
                //role.getBehavior().addCumulateEvent(reqMsg);
                //不能放入单用户单线程,因为不确定什么时候入库完成，无法给入围者发送邮件提醒
                GameContext.getArenaApp().onLogout(role, null);
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
    }

    private void reward() {
        //将奖励发送到用户邮箱
        int rank = 0;
        int maxRank = this.getMaxArena1V1RewardRank();
        for (Arena1V1RealTime sort : realTimeSortList) {
            rank++;
            if (rank > maxRank) {
                break;
            }
            //发奖
            List<Reward1V1Finish> configList = this.getRoleLevelReward1V1(sort.getRoleLevel());
            Reward1V1Finish config = this.getMatchReward1V1Config(configList, rank);
            if (null == config) {
                continue;
            }
            this.sendRoleReward(sort.getRoleId(), rank, config);
        }
    }

    private Reward1V1Finish getMatchReward1V1Config(List<Reward1V1Finish> configList, int rank) {
        if (Util.isEmpty(configList) || rank <= 0) {
            return null;
        }
        for (Reward1V1Finish config : configList) {
            if (rank >= config.getStartRank() && rank <= config.getEndRank()) {
                return config;
            }
        }
        return null;
    }


    /**
     * 当前时间是否在活动期间(时间点)
     *
     * @return
     */
    @Override
    public boolean isAcitveTimes() {
        return this.active.isTimeOpen();
    }


    /**
     * 初始化实时数据
     */
    private void initRealTimeData() {
        List<Arena1V1RealTime> realTimeData = this.selectRealTimeData();
        if (Util.isEmpty(realTimeData)) {
            return;
        }
        this.realTimeSortList = realTimeData;
        int rank = 1;
        for (Arena1V1RealTime data : realTimeData) {
            this.realTimeDataMap.put(data.getRoleId(), data);
            //名次
            this.realTimeSortMap.put(data.getRoleId(), rank++);
        }
    }

    /**
     * 从数据库中获得当前排名的实时数据
     */
    private List<Arena1V1RealTime> selectRealTimeData() {
        return GameContext.getBaseDAO().selectAll(Arena1V1RealTime.class);
    }


    @Override
    public void setArgs(Object arg0) {

    }

    private void startInitData() {
        if (this.isAcitveTimes()) {
            //在活动开启时间内
            //2.加载实时数据
            this.initRealTimeData();
            Arena arena1v1 = GameContext.getArenaApp().getArena(this.active.getId());
            //服务器启动时,活动已经开始
            arena1v1.activeOpenWhenServerStart();
        }
    }

    @Override
    public void start() {
        this.loadAreanExp();
        this.loadReward1v1Finish();
        this.active = this.getActive();
        if (null == active) {
            Log4jManager.CHECK.error("not config active for ArenaType._1V1");
            Log4jManager.checkFail();
            return;
        }
        List<String> times = active.getTimes();
        if (null == times || times.size() != 1) {
            //封神榜只允许配置一个时间点
            Log4jManager.CHECK.error("the ArenaType._1V1 active must and only config one timeRange,acitveId=" + this.active.getId());
            Log4jManager.checkFail();
            return;
        }
        //启动初始化数据
        this.startInitData();
    }


    private Active getActive() {
        ArenaConfig config = GameContext.getArenaApp().getArenaConfig(ArenaType._1V1);
        if (null == config) {
            return null;
        }
        return GameContext.getActiveApp().getActive((short) config.getActiveId());
    }

    private void sendRoleReward(String roleId, int rank, Reward1V1Finish reward) {
        try {
            Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
            mail.setTitle(GameContext.getI18n().getText(TextId.ARENA_1V1_REWARD_MAIL_TITLE));
            mail.setSendRole(MailSendRoleType.Arena1V1.getName());
            mail.setRoleId(String.valueOf(roleId));
            mail.setSendSource(OutputConsumeType.arena1v1_award.getType());
            mail.setExp(reward.getExp());
            mail.setSilverMoney(reward.getGameMoney());
            List<GoodsLiteNamedItem> goodsList = reward.getGoodsList();
            if (!Util.isEmpty(goodsList)) {
                for (GoodsLiteNamedItem lite : goodsList) {
                    mail.addMailAccessory(lite.getGoodsId(), lite.getNum(), BindingType.get(lite.getBindType()));
                }
            }
            String content = GameContext.getI18n().getText(TextId.ARENA_1V1_REWARD_MAIL_TEXT);
            content = content.replace(Wildcard.Arena1V1_Rank, String.valueOf(rank));
            mail.setContent(content);
            GameContext.getMailApp().sendMail(mail);
        } catch (Exception ex) {
            logger.error("send arena1v1 reward error,roleId=" + roleId + " rank=" + rank, ex);
        }
    }

    @Override
    public void stop() {

    }

    private void loadReward1v1Finish() {
        String fileName = XlsSheetNameType.arena_reward_1v1_finish.getXlsName();
        String sheetName = XlsSheetNameType.arena_reward_1v1_finish.getSheetName();
        try {
            String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
            List<Reward1V1Finish> configList = XlsPojoUtil.sheetToList(sourceFile, sheetName, Reward1V1Finish.class);
            //排序
            Collections.sort(configList, new Comparator<Reward1V1Finish>() {
                @Override
                public int compare(Reward1V1Finish config1, Reward1V1Finish config2) {
                    if (config1.getMinLevel() > config2.getMinLevel()) {
                        return 1;
                    }
                    if (config1.getMinLevel() < config2.getMinLevel()) {
                        return -1;
                    }
                    if (config1.getMaxLevel() > config2.getMaxLevel()) {
                        return 1;
                    }
                    if (config1.getMaxLevel() < config2.getMaxLevel()) {
                        return -1;
                    }
                    if (config1.getStartRank() > config2.getStartRank()) {
                        return 1;
                    }
                    if (config1.getStartRank() < config2.getStartRank()) {
                        return -1;
                    }
                    if (config1.getEndRank() > config2.getEndRank()) {
                        return 1;
                    }
                    if (config1.getEndRank() < config2.getEndRank()) {
                        return -1;
                    }
                    return 0;
                }
            });
            this.reward1V1FinishMap.clear();

            for (Reward1V1Finish config : configList) {
                config.init();
                if (config.getEndRank() > this.maxArena1V1RewardRank) {
                    this.maxArena1V1RewardRank = config.getEndRank();
                }
                List<Reward1V1Finish> rewardList = this.reward1V1FinishMap.get(config.getMinLevel());
                if (null == rewardList) {
                    rewardList = new ArrayList<Reward1V1Finish>();
                    for (int i = config.getMinLevel(); i <= config.getMaxLevel(); i++) {
                        this.reward1V1FinishMap.put(i, rewardList);
                    }
                }
                rewardList.add(config);
            }
        } catch (Exception ex) {
            Log4jManager.checkFail();
            Log4jManager.CHECK.error("loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName, ex);
        }
    }

    private void loadAreanExp() {
        //加载擂台赛配置
        String fileName = XlsSheetNameType.arena_reward_1v1_bout.getXlsName();
        String sheetName = XlsSheetNameType.arena_reward_1v1_bout.getSheetName();
        try {
            String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
            reward1v1BoutMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, Reward1v1Bout.class);
        } catch (Exception ex) {
            Log4jManager.checkFail();
            Log4jManager.CHECK.error("loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName, ex);
        }
        if (Util.isEmpty(this.reward1v1BoutMap)) {
            Log4jManager.checkFail();
            Log4jManager.CHECK.error("not any config: sourceFile = " + fileName + " sheetName =" + sheetName);
        }
        //判断是否有等级没有配置经验
        for (int level : this.reward1v1BoutMap.keySet()) {
            if (level > this.maxExpLevel) {
                this.maxExpLevel = level;
            }
        }
        if (this.maxExpLevel != this.reward1v1BoutMap.size()) {
            Log4jManager.checkFail();
            Log4jManager.CHECK.error("arena exp config error,maxExpLevel=" + this.maxExpLevel
                    + " total recored size=" + this.reward1v1BoutMap.size());
        }
    }

    @Override
    public Reward1v1Bout getReward1v1Bout(int level) {
        if (level <= 0) {
            level = 1;
        } else if (level > this.maxExpLevel) {
            level = this.maxExpLevel;
        }
        return this.reward1v1BoutMap.get(level);
    }

    @Override
    public List<Reward1V1Finish> getDefaultReward1V1() {
        for (List<Reward1V1Finish> v : this.reward1V1FinishMap.values()) {
            return v;
        }
        return null;
    }

    @Override
    public List<Reward1V1Finish> getRoleLevelReward1V1(int roleLevel) {
        return this.reward1V1FinishMap.get(roleLevel);
    }

    public int getMaxArena1V1RewardRank() {
        return this.maxArena1V1RewardRank;
    }
}
