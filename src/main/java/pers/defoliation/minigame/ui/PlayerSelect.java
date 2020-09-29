package pers.defoliation.minigame.ui;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class PlayerSelect {

    public final Block block;
    public final Entity entity;

    public PlayerSelect(Block block, Entity entity) {
        this.block = block;
        this.entity = entity;
    }

    public boolean isBlock() {
        return block != null;
    }

    public boolean isEntity() {
        return entity != null;
    }

}
