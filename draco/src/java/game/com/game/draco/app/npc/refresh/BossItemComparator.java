package com.game.draco.app.npc.refresh;

import java.util.Comparator;

import com.game.draco.message.item.BossListItem;
/**
 *  可以进入的排在前，不可进入的放在后
 */
public class BossItemComparator implements Comparator<BossListItem> {

    @Override
    public int compare(BossListItem o, BossListItem o1) {
        int comparison = o.getRoleLevelMin() - o1.getRoleLevelMin();
        if (comparison == 0) {
            comparison = o.getRemainSecond() - o1.getRemainSecond();
        }
        return comparison;
    }

}
