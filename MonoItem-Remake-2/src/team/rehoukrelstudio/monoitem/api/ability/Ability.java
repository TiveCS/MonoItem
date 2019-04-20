package team.rehoukrelstudio.monoitem.api.ability;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import team.rehoukrelstudio.monoitem.MonoItem;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import team.rehoukrelstudio.monoitem.api.fixed.StatsEnum;
import team.rehoukrelstudio.monoitem.nms.nbt.NBTManager;
import utils.DataConverter;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class Ability {

    private MonoItem plugin = MonoItem.getPlugin(MonoItem.class);
    public static HashMap<String, Ability> abilities = new HashMap<>();
    public static HashMap<LivingEntity, List<String>> cooldownList = new HashMap<>();
    private File format = new File(plugin.getDataFolder(), "format.yml");
    private FileConfiguration config;

    private ItemStack ICON;
    protected String FORMAT;
    private List<TriggerType> allowedTrigger = new ArrayList<>();
    private String id, displayName;
    private HashMap<AbilityModifier, Double> modifier = new HashMap<>();
    private HashMap<String, Object> customModifier = new HashMap<>();
    protected HashMap<String, Class> customModifierClazz = new HashMap<>();
    private HashMap<StatsEnum, HashMap<String, Double>> statsModifier = new HashMap<>();
    private TriggerType triggerType = TriggerType.DAMAGE;

    public enum TriggerType{
        DAMAGE, DAMAGE_TAKEN, LEFT_CLICK, RIGHT_CLICK, SNEAK, PROJECTILE_HIT, PROJECTILE_SHOOT, PROJECTILE_HIT_BLOCK,
        JUMP, SWIMMING, SPRINT, TARGETING
    }

    // value type data is double only
    public enum AbilityModifier{
        COOLDOWN, DAMAGE, STAMINA, MINIMUM, MAXIMUM, RESULT, CHANCE;
    }

    public enum AbilitySerialize{
        TRIGGER_TYPE, DEFAULT_MODIFIER, CUSTOM_MODIFIER, LORE, STATS_MODIFIER;
    }

    public Ability(String id, String display, Material mat){
        this.ICON = new ItemStack(mat);
        ItemMeta meta = this.ICON.getItemMeta();
        meta.setDisplayName(ChatColor.RED + display);
        this.ICON.setItemMeta(meta);
        this.id = id;
        this.displayName = display;
        this.config = YamlConfiguration.loadConfiguration(format);
        if (!getConfig().contains("ability." + id.toLowerCase())){
            getConfig().set("ability." + id.toLowerCase(), "&8[%" + getId() + ".TRIGGER_TYPE%] &6" + getDisplayName() +  " &e%" + getId() + ".RESULT%");
            try {
                getConfig().save(getFormatFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.FORMAT = getConfig().getString("ability." + getId().toLowerCase());
        loadDefaultData();
    }

    public Ability(String id, String display, ItemStack icon){
        this.ICON = icon;
        this.id = id;
        this.displayName = display;
        this.config = YamlConfiguration.loadConfiguration(format);
        if (!getConfig().contains("ability." + id.toLowerCase())){
            getConfig().set("ability." + id.toLowerCase(), "&8[%" + getId() + ".TRIGGER_TYPE%] &6" + getDisplayName() +  " &e%" + getId() + ".RESULT%");
            try {
                getConfig().save(getFormatFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.FORMAT = getConfig().getString("ability." + getId().toLowerCase());
        loadDefaultData();
    }

    public static boolean chance(double chance){
        return chance < 100 ? (new Random().nextDouble()*100 <= chance) : true;
    }

    public void initializeResult(){
        double min = getModifier().containsKey(AbilityModifier.MINIMUM) ? getModifier().get(AbilityModifier.MINIMUM) : 0,
                max = getModifier().containsKey(AbilityModifier.MAXIMUM) ? getModifier().get(AbilityModifier.MAXIMUM) : 0;
        setModifier(AbilityModifier.RESULT, DataConverter.randomDouble(min, max));
    }

    // Abstract
    public abstract void loadDefaultData();

    public abstract void projectileHit(ProjectileHitEvent event, MonoFactory factory);
    public abstract void entityShoot(EntityShootBowEvent event, LivingEntity executor, MonoFactory factory);
    public abstract void entityDamageByEntity(EntityDamageByEntityEvent event, Damageable attacker, Damageable victim, MonoFactory factory);
    public abstract void playerClick(PlayerInteractEvent event, MonoFactory factory);
    public abstract void playerSneak(PlayerToggleSneakEvent event, MonoFactory factory);

    // Action

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public void set(MonoFactory factory, TriggerType triggerType){
        try {
            NBTManager nbt = factory.getNbtManager();
            nbt.customNbtData(getId() + ".TRIGGER_TYPE", triggerType.name());
            if (!nbt.hasNbt("MONOITEM")) {
                nbt.customNbtData("MONOITEM", UUID.randomUUID().toString());
            }
            factory.getPlaceholder().addReplacer(getId() + ".TRIGGER_TYPE", triggerType.name());

            if (getModifier().containsKey(AbilityModifier.RESULT)){
                getModifier().remove(AbilityModifier.MINIMUM);
                getModifier().remove(AbilityModifier.MAXIMUM);
            }else{
                initializeResult();
            }
            for (AbilityModifier mod : getModifier().keySet()) {
                String path = getId() + "." + mod.name();
                nbt.customNbtData(path, getModifier().get(mod));
                factory.getPlaceholder().addReplacer(path, DataConverter.returnDecimalFormated(2, Double.parseDouble(getModifier().get(mod).toString())));
            }

            for (String key : getCustomModifier().keySet()) {
                String path = getId() + "." + key;
                nbt.customNbtData(path, getCustomModifier().get(key));
                factory.getPlaceholder().addReplacer(path, getCustomModifier().get(key).toString());
            }

            String lore = getId() + ".LORE";
            int lineLore = nbt.hasNbt(lore) ? nbt.getInteger(lore) : (factory.getMeta().hasLore() ? factory.getMeta().getLore().size() : 0);
            if (!nbt.hasNbt(lore)) {
                nbt.customNbtData(lore, lineLore);
            }

            factory.setItem(nbt.getItem());
            factory.setMeta(factory.getItem().getItemMeta());
            factory.setLore(getFormat(), factory.getPlaceholder(), lineLore);

            getModifier().clear();
            getCustomModifier().clear();
            setTriggerType(TriggerType.DAMAGE);
        }catch(Exception e){e.printStackTrace();}
        loadDefaultData();
    }

    public void set(ItemStack item, TriggerType triggerType){
        MonoFactory factory = new MonoFactory(item);
        set(factory, triggerType);
    }

    public void startCooldown(LivingEntity entity, long cooldown){
        String cdPath = getId() + "." + AbilityModifier.COOLDOWN.name();
        List<String> ab = cooldownList.containsKey(entity) ? cooldownList.get(entity) : new ArrayList<>();
        ab.add(cdPath);
        cooldownList.put(entity, ab);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                ab.remove(cdPath);
                cooldownList.put(entity, ab);
            }
        }, cooldown);
    }

    public void setModifier(AbilityModifier modifier, double value){
        getModifier().put(modifier, value);
    }

    public void setCustomModifier(String path, Class instance, Object value){
        if (value instanceof String){
            double a;
            int c;
            boolean d;
            boolean checked = false;
            try{
                a = Double.parseDouble(value.toString());
                checked = true;
                value = a;
            }catch(Exception e){}
            try{
                if (checked == false) {
                    c = Integer.parseInt(value.toString());
                    checked = true;
                    value = c;
                }
            }catch(Exception e){}
            try{
                if (checked == false) {
                    d = Boolean.parseBoolean(value.toString());
                    checked = true;
                    value = d;
                }
            }catch(Exception e){}

        }
        getCustomModifierClazz().put(path, instance);
        getCustomModifier().put(path, value);
    }

    public static void registerAbility(Ability... ability){
        for (Ability a : ability){
            abilities.put(a.getId(), a);
        }
    }

    public void setCustomModifier(HashMap<String, Object> customModifier) {
        this.customModifier = customModifier;
    }

    public void setModifier(HashMap<AbilityModifier, Double> modifier) {
        this.modifier = modifier;
    }

    public void setAllowedTrigger(List<TriggerType> allowedTrigger) {
        this.allowedTrigger = allowedTrigger;
    }

    public void setCustomModifierClazz(HashMap<String, Class> customModifierClazz) {
        this.customModifierClazz = customModifierClazz;
    }

    public void addCustomModifier(String modifier, Class instance){
        getCustomModifierClazz().put(modifier, instance);
    }

    public void addAllowedTrigger(TriggerType... type) { getAllowedTrigger().addAll(Arrays.asList(type));
    }

    public void setStatsModifier(HashMap<StatsEnum, HashMap<String, Double>> statsModifier) {
        this.statsModifier = statsModifier;
    }

    public void addAllowedTrigger(TriggerType type){
        getAllowedTrigger().add(type);
    }

    // Getter

    public HashMap<AbilitySerialize, Object> getAbilitySerialize(MonoFactory factory){
        HashMap<AbilitySerialize, Object> map = new HashMap<>();
        NBTManager nbt = factory.getNbtManager();

        // Trigger Type
        if (nbt.hasNbt(getId() + ".TRIGGER_TYPE")) {
            map.put(AbilitySerialize.TRIGGER_TYPE, nbt.getString(getId() + ".TRIGGER_TYPE"));
        }

        // Custom Modifier
        HashMap<String, Object> customModifier = new HashMap<>();
        for (String key : getCustomModifier().keySet()){
            if (nbt.hasNbt(getId() + "." + key)){
                customModifier.put(key, factory.getNbtManager().get(getId() + "." + key));
            }
        }
        if (!customModifier.isEmpty()){
            map.put(AbilitySerialize.CUSTOM_MODIFIER, customModifier);
        }

        // Default Modifier
        HashMap<AbilityModifier, Double> defaultModifier = new HashMap<>();
        for (AbilityModifier modifier : AbilityModifier.values()){
            if (nbt.hasNbt(getId() + "." + modifier.name())){
                defaultModifier.put(modifier, nbt.getDouble(getId() + "." + modifier.name()));
            }
        }
        if (!defaultModifier.isEmpty()){
            map.put(AbilitySerialize.DEFAULT_MODIFIER, defaultModifier);
        }
        map.put(AbilitySerialize.LORE, nbt.get(getId() + ".LORE"));

        HashMap<StatsEnum, HashMap<String, Double>> smap = new HashMap<>();
        for (StatsEnum stats : StatsEnum.values()){
            HashMap<String, Double> value = new HashMap<>();
            if (nbt.hasNbt(stats.getResult())){
                value.put(stats.getResult(), factory.getStatsResult(stats));
            }else if (nbt.hasNbt(stats.getMin()) && nbt.hasNbt(stats.getMax())){
                value.put(stats.getResult(), factory.getStatsMin(stats));
                value.put(stats.getResult(), factory.getStatsMax(stats));
            }
            smap.put(stats, value);
        }

        return map;
    }

    public String getFormat() {
        return FORMAT;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public File getFormatFile(){
        return format;
    }

    public ItemStack getIcon() {
        return ICON;
    }

    public boolean isCooldown(LivingEntity entity) {
        if (cooldownList.isEmpty()){
            return false;
        }
        List<String> list = cooldownList.containsKey(entity) ? cooldownList.get(entity) : new ArrayList<>();
        return list.contains(getId() + "." + AbilityModifier.COOLDOWN.name());
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public HashMap<AbilityModifier, Double> getModifier() {
        return modifier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }

    public HashMap<String, Object> getCustomModifier() {
        return customModifier;
    }

    public HashMap<String, Class> getCustomModifierClazz() {
        return customModifierClazz;
    }

    public List<TriggerType> getAllowedTrigger() {
        return allowedTrigger;
    }

    public HashMap<StatsEnum, HashMap<String, Double>> getStatsModifier() {
        return statsModifier;
    }
}
