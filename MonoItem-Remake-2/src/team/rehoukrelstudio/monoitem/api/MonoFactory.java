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
import java.util.UUID;

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
        List<String> list = new ArrayList<>(MonoItem.unidentConfigManager.getConfig().getConfigurationSection("unidentified-item.table").getKeys(false));
        if (list.isEmpty()){
            return;
        }
        String rarity = list.get(0);
        setStats(stats, min, max, unidentified, rarity);
    }

    public void setStats(StatsEnum stats, double min, double max, boolean unidentified, String rarity){

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
        if (!getNbtManager().hasNbt("MONOITEM")) {
            getNbtManager().customNbtData("MONOITEM", UUID.randomUUID().toString());
        }

        getPlaceholder().addReplacer(stats.getResult(), DataConverter.returnDecimalFormated(2, result));
        getPlaceholder().addReplacer(stats.getMin(), min + "");
        getPlaceholder().addReplacer(stats.getMax(), max + "");
        getPlaceholder().addReplacer(stats.getLore(), lineLore + "");

        setItem(getNbtManager().getItem());
        setMeta(getItem().getItemMeta());
        if (unidentified == false) {
            setLore(getFormatConfig().getString("stats." + stats.name().toLowerCase()), getPlaceholder(), lineLore);
            setItem(getNbtManager().getItem());
            setMeta(getItem().getItemMeta());
            System.out.println(this.getItem());
        }else{
            setUnidentified(true, rarity);
        }
    }

    public void setOptions(OptionEnum option, boolean status){
        NBTManager nbt = getNbtManager();
        switch(option) {
            case UNBREAKABLE:
                getMeta().setUnbreakable(status);
                getItem().setItemMeta(getMeta());
                break;
            default:
                nbt.customNbtData(option.getState(), status);
                break;
        }

        if (!nbt.hasNbt("MONOITEM")) {
            nbt.customNbtData("MONOITEM", UUID.randomUUID().toString());
        }
        getPlaceholder().addReplacer(option.name(), status + "");
        setItem(getNbtManager().getItem());
        setMeta(getItem().getItemMeta());
    }

    public void addAbility(Ability ability, boolean replace, Ability.TriggerType trigger){
        ability.set(this, trigger);
    }

    public void setUnidentified(boolean status, String rarity){
        String path = "unidentified-item.table." + rarity;
        if (!MonoItem.unidentConfigManager.getConfig().contains(path)){
            return;
        }
        NBTManager nbt = getNbtManager();
        if (status) {
            for (StatsEnum e : StatsEnum.values()) {
                if (nbt.hasNbt(e.getLore())) {
                    removeLore(nbt.getInteger(e.getLore()));
                    nbt.deleteCustomNbtData(e.getLore());
                    if (nbt.hasNbt(e.getMin()) && nbt.hasNbt(e.getMax())) {
                        nbt.deleteCustomNbtData(e.getResult());
                    } else if (nbt.hasNbt(e.getResult())) {
                        nbt.deleteCustomNbtData(e.getMin());
                        nbt.deleteCustomNbtData(e.getMax());
                    } else {
                        nbt.deleteCustomNbtData(e.getMax());
                        nbt.deleteCustomNbtData(e.getMin());
                        nbt.deleteCustomNbtData(e.getResult());
                    }
                }
            }
            nbt.customNbtData(OptionEnum.UNIDENTIFIED.getState(), rarity);
            List<String> lore = DataConverter.colored(MonoItem.unidentConfigManager.getConfig().getStringList(path + ".item.lore"));
            String displayName = ChatColor.translateAlternateColorCodes('&', MonoItem.unidentConfigManager.getConfig().getString(path + ".item.display-name"));
            setItem(nbt.getItem());
            setMeta(nbt.getItem().getItemMeta());
            getMeta().setDisplayName(displayName);
            getMeta().setLore(lore);
            getItem().setItemMeta(getMeta());
        }else{
            nbt.deleteCustomNbtData(OptionEnum.UNIDENTIFIED.getState());
            setItem(getNbtManager().getItem());
            setMeta(getItem().getItemMeta());
        }
    }

    public void generateUnidentified(){
        if (!hasOption(OptionEnum.UNIDENTIFIED)){
            return;
        }
        String rarity = getOption(OptionEnum.UNIDENTIFIED);
        List<String> lore = MonoItem.unidentConfigManager.getConfig().getStringList("unidentified-item.table." + rarity + ".item.lore");
        String prefix = ChatColor.translateAlternateColorCodes('&', MonoItem.unidentConfigManager.getConfig().getString("unidentified-item.table." + rarity + ".prefix"));
        List<String> l = getMeta().getLore(),
            pf = plugin.getConfig().contains("unidentified-item-name." + rarity) ? plugin.getConfig().getStringList("unidentified-item-name.prefix." + rarity) : plugin.getConfig().getStringList("unidentified-item-name.prefix.default")
                , sf = plugin.getConfig().contains("unidentified-item-name." + rarity) ? plugin.getConfig().getStringList("unidentified-item-name.suffix." + rarity) : plugin.getConfig().getStringList("unidentified-item-name.suffix.default");
        l.removeAll(lore);
        getMeta().setLore(l);
        getMeta().setDisplayName(ChatColor.translateAlternateColorCodes('&', prefix + pf.get(new Random().nextInt(pf.size() - 1)) + sf.get(new Random().nextInt(sf.size() - 1))));
        getItem().setItemMeta(getMeta());

        for (StatsEnum st : StatsEnum.values()){
            double min = getStatsMin(st), max = getStatsMax(st);
            if (min != 0 && max != 0) {
                setStats(st, min, max, false, rarity);
            }
        }
    }

    public void removeLore(int line){
        List<String> list = getMeta().hasLore() ? getMeta().getLore() : new ArrayList<>();
        System.out.println(line + " " + list.size());
        if (!list.isEmpty() && (list.get(line) != null || list.size() - 1 >= line)){
            list.remove(line);
        }
        getMeta().setLore(list);
        getItem().setItemMeta(getMeta());
        getNbtManager().setItem(getItem());
    }

    public ItemStack setLore(ItemStack item, String text, Placeholder plc, int line){
        ItemMeta meta = item.getItemMeta();
        List<String> list = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        text = ChatColor.translateAlternateColorCodes('&', text);
        text = plc.use(text);
        if (line > list.size() - 1){
            list.add(text);
        }else{
            list.set(line, text);
        }
        meta.setLore(list);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack setLore(String text, Placeholder plc, int line){
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
        getNbtManager().setItem(getItem());
        return getItem();
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void setMeta(ItemMeta meta) {
        this.meta = meta;
    }

    public boolean hasOption(OptionEnum option){
        return getNbtManager().hasNbt(option.getState());
    }

    public String getOption(OptionEnum option){
        if (hasOption(option)){
            return getNbtManager().getString(option.getState());
        }
        return "";
    }

    public void deleteStats(StatsEnum stats){
        removeLore(getNbtManager().getInteger(stats.getLore()));
        getNbtManager().deleteCustomNbtData(stats.getLore());
        getNbtManager().deleteCustomNbtData(stats.getResult());
        getNbtManager().deleteCustomNbtData(stats.getMax());
        getNbtManager().deleteCustomNbtData(stats.getMin());
        setItem(getNbtManager().getItem());
        setMeta(getItem().getItemMeta());
    }

    public boolean hasStats(StatsEnum stats){
        return getNbtManager().hasNbt(stats.getResult());
    }

    public double getStatsResult(StatsEnum stats){
        return getNbtManager().getDouble(stats.getResult());
    }

    public double getStatsMin(StatsEnum stats){
        return getNbtManager().getDouble(stats.getMin());
    }

    public double getStatsMax(StatsEnum stats){
        return getNbtManager().getDouble(stats.getMax());
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
