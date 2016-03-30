package sacred.alliance.magic.app.rank;

import java.util.List;

import com.game.draco.message.item.RankDetailItem;

import lombok.Data;

public @Data class RankLogData {
	private short totalPage;
	private short curPage;
	private List<RankDetailItem> rdItemList = null;
}
