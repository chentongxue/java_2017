package sacred.alliance.magic.app.arena.action;

import com.game.draco.GameContext;
import com.game.draco.message.item.Arean1V1RewardItem;
import com.game.draco.message.response.C3859_Arena1V1ingDetailRespMessage;
import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.arena.ApplyInfo;
import sacred.alliance.magic.app.arena.ArenaType;
import sacred.alliance.magic.app.arena.config.ArenaConfig;
import sacred.alliance.magic.app.arena.config.Reward1V1Finish;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import java.util.ArrayList;
import java.util.List;

public abstract class Arena1V1AbstractAction<M extends Message> extends BaseAction<M> {


    protected C3859_Arena1V1ingDetailRespMessage buildIngDetailRespMessage(
            RoleInstance role, boolean autoApply) {
        C3859_Arena1V1ingDetailRespMessage respMsg = new C3859_Arena1V1ingDetailRespMessage();

        int activeId = 0;
        short specialTimes = 0;
        ArenaConfig config = GameContext.getArenaApp().getArenaConfig(ArenaType._1V1);
        if (null != config) {
            activeId = config.getActiveId();
            specialTimes = config.getSpecialTimes();
        }
        boolean isOpenNow = this.isOpenNow((short) activeId);
        if (isOpenNow) {
            int rank = GameContext.getArena1V1App().getRank(role.getRoleId());
            //设置玩家的当前排名
            respMsg.setCurrentRank((short) rank);
            //设置用户的排名页码
            respMsg.setPage((short) this.getPage(rank));
            respMsg.setOpenNow((byte) 1);
        } else {
            respMsg.setCurrentRank((short) 0);
            respMsg.setPage((byte) 0);
            respMsg.setOpenNow((byte) 0);
        }
        respMsg.setActiveId(activeId);

        ApplyInfo applyInfo = GameContext.getArenaApp().getApplyInfo(role.getRoleId());
        if (null != applyInfo && applyInfo.getActiveId() == activeId) {
            //已经报名
            respMsg.setApplySeconds((short) ((System.currentTimeMillis() - applyInfo.getCreateDate()) / 1000));
            respMsg.setApplyStatus((byte) 1);
        }
        respMsg.setAutoApply(autoApply ? (byte) 1 : (byte) 0);
        respMsg.setCurrentScore(role.getRoleArena().getScore(ArenaType._1V1));
        int winTimes = role.getRoleArena().getCycleWinTime(ArenaType._1V1);
        int failTimes = role.getRoleArena().getCycleFailTime(ArenaType._1V1);
        respMsg.setFailTimes((short) failTimes);
        respMsg.setWinTimes((short) winTimes);
        respMsg.setTodayTimes((short) (winTimes + failTimes));
        respMsg.setSpecialTimes(specialTimes);
        //奖励列表
        respMsg.setRewardList(this.buildReward(GameContext.getArena1V1App().getRoleLevelReward1V1(role.getLevel())));
        return respMsg;
    }

    private boolean isOpenNow(short activeId) {
        Active active = GameContext.getActiveApp().getActive(activeId);
        if (null == active) {
            return false;
        }
        return active.isTimeOpen();
    }

    protected List<Arean1V1RewardItem> buildReward(List<Reward1V1Finish> configList) {
        if (Util.isEmpty(configList)) {
            return null;
        }
        List<Arean1V1RewardItem> rewardList = new ArrayList<Arean1V1RewardItem>();
        for (Reward1V1Finish config : configList) {
            Arean1V1RewardItem item = new Arean1V1RewardItem();
            item.setStartRank((short) config.getStartRank());
            item.setEndRank((short) config.getEndRank());
            item.setImageId(config.getImageId());
            item.setAttriList(config.getAttriList());
            item.setGoodsList(config.getGoodsList());
            rewardList.add(item);
        }
        return rewardList;
    }


    protected int getPage(int rank) {
        if (rank <= 0) {
            return 1;
        }
        int pageSize = GameContext.getArena1V1App().getPageSize();
        int p = rank / pageSize;
        if (0 == rank % pageSize) {
            return p;
        }
        return p + 1;
    }


}
