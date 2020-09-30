package pers.defoliation.minigame.worldobject;

import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ObjectField {
    String value();

    String[] desc() default {"人懒无注释"};

    Material material() default Material.STONE;

    short materialData() default 0;

}
