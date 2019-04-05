package team.rehoukrelstudio.monoitem.api;

import org.bukkit.Material;

public enum OptionEnum {

    UNBREAKABLE(Material.BEDROCK), RANGED_ONLY(Material.BOW), DISABLE_ON_LAND(Material.DIRT), DISABLE_ON_WATER(Material.WATER_BUCKET),
    UNIDENTIFIED(Material.KNOWLEDGE_BOOK);

    private Material icon;

    OptionEnum(Material material){
        icon = material;
    }

    public Material getIcon() {
        return icon;
    }

    public String getState(){
        return name() + ".STATE";
    }

}
