package pers.defoliation.minigame.map;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.registry.WorldData;
import org.bukkit.Location;

public class WE12Region extends SimpleRegion {

    private BlockArrayClipboard clipboard;

    protected WE12Region(Location locationA, Location locationB) {
        super(locationA, locationB);
        CuboidRegion cuboidRegion = new CuboidRegion(BukkitUtil.toVector(locationA), BukkitUtil.toVector(getMaxLocation()));
        clipboard = new BlockArrayClipboard(cuboidRegion);
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(cuboidRegion.getWorld(), -1);
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                editSession, cuboidRegion, clipboard, cuboidRegion.getMinimumPoint()
        );
        try {
            Operations.complete(forwardExtentCopy);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {
        LocalWorld localWorld = BukkitUtil.getLocalWorld(getMaxLocation().getWorld());
        WorldData worldData = localWorld.getWorldData();
        ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard,worldData);
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(localWorld, -1);
        try {
            Operations.completeLegacy(clipboardHolder.createPaste(editSession,worldData)
                    .to(BukkitUtil.toVector(getMinLocation()))
                    .ignoreAirBlocks(false)
                    .build());
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }
}
