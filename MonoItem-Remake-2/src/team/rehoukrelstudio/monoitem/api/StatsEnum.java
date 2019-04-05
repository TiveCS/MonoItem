package team.rehoukrelstudio.monoitem.api;

import org.bukkit.Material;

public enum StatsEnum {
    INCREASE_DAMAGE(Material.DIAMOND_SWORD), DECREASE_DAMAGE(Material.DIAMOND_CHESTPLATE),
    PHYSICAL_DAMAGE(Material.IRON_SWORD), PHYSICAL_DEFENSE(Material.IRON_CHESTPLATE),
    MAGICAL_DAMAGE(Material.BLAZE_ROD), MAGICAL_DEFENSE(Material.LEATHER_CHESTPLATE),
    CRITICAL_RATE(Material.IRON_AXE), CRITICAL_DAMAGE(Material.GOLDEN_AXE), CRITICAL_RESISTANCE(Material.DIAMOND_AXE),
    BLOCK_RATE(Material.GOLDEN_HORSE_ARMOR), BLOCK_AMOUNT(Material.IRON_HORSE_ARMOR), BLOCK_PENETRATION(Material.DIAMOND_HORSE_ARMOR),
    DURABILITY(Material.IRON_INGOT), DODGE(Material.LEATHER_BOOTS), ATTACK_SPEED(Material.STONE_SWORD);

    private Material icon;

    StatsEnum(Material material){
        icon = material;
    }

    public Material getIcon(){
        return this.icon;
    }

    public String getMin(){
        return name() + ".MINIMUM";
    }

    public String getMax(){
        return name() + ".MAXIMUM";
    }

    public String getResult(){
        return name() + ".RESULT";
    }

    public String getLore(){
        return name() + ".LORE";
    }
}
