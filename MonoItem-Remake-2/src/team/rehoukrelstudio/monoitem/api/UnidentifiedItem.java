package team.rehoukrelstudio.monoitem.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import team.rehoukrelstudio.monoitem.MonoItem;
import team.rehoukrelstudio.monoitem.api.ability.Ability;
import team.rehoukrelstudio.monoitem.api.fixed.StatsEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class UnidentifiedItem {

    private MonoItem plugin = MonoItem.getPlugin(MonoItem.class);

    private MonoFactory factory;
    private int level = 1;
    private double multiplier = 1.5;
    private String rarity, rarityPath, type;

    private HashMap<StatsEnum, String> stats = new HashMap<>();
    private List<Ability> abilities = new ArrayList<>();
    private List<Ability.TriggerType> trigger = new ArrayList<>();

    public UnidentifiedItem(MonoFactory factory, int level, String rarity){
        this.factory = factory;
        this.level = level;
        this.rarity = rarity;
        this.rarityPath = "unidentified-item.table." + rarity;
        if (!MonoItem.unidentConfigManager.getConfig().contains(rarityPath)){
            return;
        }
        this.multiplier = MonoItem.unidentConfigManager.getConfig().getDouble(this.rarityPath + ".multiplier-result");
        this.type = "weapon";

        if (MonoFactory.getArmor().contains(getFactory().getItem().getType())) {
            this.type = "armor";
        }else if (MonoFactory.getWeapon().contains(getFactory().getItem().getType())){
            this.type = "weapon";
        }else{
            this.type = "default";
        }
        generateStats();
    }

    public UnidentifiedItem(Material mat, int level, String rarity){
        this(new MonoFactory(new ItemStack(mat)), level, rarity);
    }

    // Action

    public void generateStats(){
        int sMin , sMax, amount;
        sMin = MonoItem.unidentConfigManager.getConfig().getInt(getRarityPath() + ".amount.minimum");
        sMax = MonoItem.unidentConfigManager.getConfig().getInt(getRarityPath() + ".amount.maximum");
        amount = new Random().nextInt(sMax);
        if (amount < sMin){
            amount = sMin;
        }
        for (String s : MonoItem.unidentConfigManager.getConfig().getStringList("stats-base." + getType())) {
            if (getStats().size() < amount) {
                String[] sp = s.split(":");
                StatsEnum e = StatsEnum.valueOf(sp[0].toUpperCase());
                getStats().put(e, sp[1]);
            } else {
                break;
            }
        }

        for (StatsEnum e : getStats().keySet()){
            String[] sp = getStats().get(e).split("=");
            double min = Double.parseDouble(sp[0])*(getMultiplier()*getLevel()),
                    max = Double.parseDouble(sp[1])*(getMultiplier()*getLevel());
            getFactory().setStats(e, min, max, true);
        }
    }

    // Setter getter

    public void setFactory(MonoFactory factory) {
        this.factory = factory;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public void setAbilities(List<Ability> abilities) {
        this.abilities = abilities;
    }

    public void setStats(HashMap<StatsEnum, String> stats) {
        this.stats = stats;
    }

    public int getLevel() {
        return level;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public MonoFactory getFactory() {
        return factory;
    }

    public String getRarity() {
        return rarity;
    }

    public String getRarityPath() {
        return rarityPath;
    }

    public HashMap<StatsEnum, String> getStats() {
        return stats;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    public String getType() {
        return type;
    }
}
