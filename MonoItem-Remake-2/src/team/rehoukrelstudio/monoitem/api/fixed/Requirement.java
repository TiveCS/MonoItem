package team.rehoukrelstudio.monoitem.api.fixed;

import org.bukkit.Material;

public enum Requirement {

    LEVEL, HUNGER, HEALTH, HEALTH_PERCENTAGE, HUNGER_PERCENTAGE;

    Material icon;

    public String getValue(){
        return name() + ".VALUE";
    }

    public Material getIcon() {
        return icon;
    }
}
