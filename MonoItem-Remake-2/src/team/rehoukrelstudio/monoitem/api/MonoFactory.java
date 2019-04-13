package team.rehoukrelstudio.monoitem.api;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import team.rehoukrelstudio.monoitem.MonoItem;
import team.rehoukrelstudio.monoitem.api.ability.Ability;
import team.rehoukrelstudio.monoitem.api.fixed.OptionEnum;
import team.rehoukrelstudio.monoitem.api.fixed.StatsEnum;
import team.rehoukrelstudio.monoitem.nms.nbt.NBTManager;
import utils.DataConverter;
import utils.language.Placeholder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonoFactory {

    private MonoItem plugin = MonoItem.getPlugin(MonoItem.class);
    private File formatFile = new File(plugin.getDataFolder(), "format.yml");
    private FileConfiguration formatConfig;

    private ItemStack item;
    private ItemMeta meta;
    private NBTManager nbtManager;
    private Placeholder plc = new Placeholder(plugin);

    public MonoFactory(ItemStack item){
        if (!formatFile.exists()){
            plugin.saveResource("format.yml", false);
        }
        this.formatConfig = YamlConfiguration.loadConfiguration(this.formatFile);
        this.item = item;
        this.meta = item.getItemMeta();
        this.nbtManager = plugin.getNmsManager().getNBTManager(this.item);
    }

    public void setStats(StatsEnum stats, double min, double max, boolean unidentified){

        int lineLore = getNbtManager().hasNbt(stats.getLore()) ? getNbtManager().getInteger(stats.getLore()) : ( getMeta().hasLore() ? getMeta().getLore().size() : 0);
        double result = 0;
        if (stats.equals(StatsEnum.ATTACK_SPEED)){
            result = new Random().nextInt((int) max);
            result = result < min ? min : result;
        }else{
            result = DataConverter.randomDouble(min, max);
        }

        if (!getNbtManager().hasNbt(stats.getLore())) {
            getNbtManager().customNbtData(stats.getLore(), lineLore);
        }

        getNbtManager().customNbtData(stats.getMin(), min);
        getNbtManager().customNbtData(stats.getMax(), max);
        getNbtManager().customNbtData(stats.getResult(), result);
        getNbtManager().customNbtData(OptionEnum.UNIDENTIFIED.getState(), unidentified);

        getPlaceholder().addReplacer(stats.getResult(), DataConverter.returnDecimalFormated(2, result));
        getPlaceholder().addReplacer(stats.getMin(), min + "");
        getPlaceholder().addReplacer(stats.getMax(), max + "");
        getPlaceholder().addReplacer(stats.getLore(), lineLore + "");

        setItem(getNbtManager().getItem());
        setMeta(getItem().getItemMeta());
        setLore(getFormatConfig().getString("stats." + stats.name().toLowerCase()), getPlaceholder(), lineLore);
    }

    public void setOptions(OptionEnum option, boolean status){
        switch(option) {
            case UNBREAKABLE:
                getMeta().setUnbreakable(status);
                getItem().setItemMeta(getMeta());
                break;
            default:
                getNbtManager().customNbtData(option.getState(), status);
                break;
        }

        getPlaceholder().addReplacer(option.name(), status + "");
        setItem(getNbtManager().getItem());
        setMeta(getItem().getItemMeta());
    }

    public void addAbility(Ability ability, boolean replace, Ability.TriggerType trigger){

    }

    public void removeLore(int line){
        List<String> list = getMeta().hasLore() ? getMeta().getLore() : new ArrayList<>();
        if (!list.isEmpty() && (list.get(line) != null || list.size() - 1 >= line)){
            list.remove(line);
        }
        getMeta().setLore(list);
        getItem().setItemMeta(getMeta());
    }

    public void setLore(String text, Placeholder plc, int line){
        List<String> list = getMeta().hasLore() ? getMeta().getLore() : new ArrayList<>();
        text = ChatColor.translateAlternateColorCodes('&', text);
        text = plc.use(text);
        if (line > list.size() - 1){
            list.add(text);
        }else{
            list.set(line, text);
        }
        getMeta().setLore(list);
        getItem().setItemMeta(getMeta());
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void setMeta(ItemMeta meta) {
        this.meta = meta;
    }

    public void deleteStats(StatsEnum stats){
        getNbtManager().deleteCustomNbtData(stats.name());
    }

    public boolean hasStats(StatsEnum stats){
        return getNbtManager().hasNbt(stats.getResult());
    }

    public double getStatsResult(StatsEnum stats){
        return getNbtManager().getDouble(stats.getResult());
    }

    public ItemMeta getMeta() {
        return meta;
    }

    public MonoItem getPlugin() {
        return plugin;
    }

    public Placeholder getPlaceholder() {
        return plc;
    }

    public FileConfiguration getFormatConfig() {
        return formatConfig;
    }

    public File getFormatFile() {
        return formatFile;
    }

    public ItemStack getItem() {
        return item;
    }

    public NBTManager getNbtManager() {
        return nbtManager;
    }
}
