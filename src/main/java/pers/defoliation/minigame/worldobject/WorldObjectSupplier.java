package pers.defoliation.minigame.worldobject;

import java.util.List;
import java.util.function.Supplier;

public class WorldObjectSupplier {

    public final Supplier<WorldObject> supplier;

    public final String objectName;
    public final List<String> desc;

    public WorldObjectSupplier(String objectName, List<String> desc, Supplier<WorldObject> supplier) {
        this.supplier = supplier;
        this.objectName = objectName;
        this.desc = desc;
    }

    public static WorldObjectSupplier get(String objectName, List<String> desc, Supplier<WorldObject> supplier) {
        return new WorldObjectSupplier(objectName, desc, supplier);
    }

}
