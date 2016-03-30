package com.game.draco.app.drama.config;


import com.game.draco.message.item.DramaBaseItem;
import com.game.draco.message.item.DramaBaseMusicItem;
import lombok.Data;

public @Data
class DramaItemMusic  extends DramaBase{

    private short musicId ;

    @Override
    public DramaBaseItem getDramaBaseInfo() {
        DramaBaseMusicItem item = new DramaBaseMusicItem();
        item.setMusicId(this.musicId);
        return item;
    }
}
