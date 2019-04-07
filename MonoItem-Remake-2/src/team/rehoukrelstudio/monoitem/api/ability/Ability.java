package team.rehoukrelstudio.monoitem.api.ability;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import team.rehoukrelstudio.monoitem.MonoItem;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import team.rehoukrelstudio.monoitem.nms.nbt.NBTManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public abstract class Ability {

    private MonoItem plugin = MonoItem.getPlugin(MonoItem.class);
    public static HashMap<String, Ability> abilities = new HashMap<>();
    private File format = new File(plugin.getDataFolder(), "format.yml");
    private FileConfiguration config;

    private ItemStack ICON;
    protected String FORMAT;
    private String id, displayName;
    private HashMap<AbilityModifier, Double> modifier = new HashMap<>();
    private HashMap<String, Object> customModifier = new HashMap<>();
    protected HashMap<String, Class> customModifierClazz = new HashMap<>();
    private TriggerType triggerType = TriggerType.DAMAGE;
    private boolean isCooldown = false;

    public enum TriggerType{
        DAMAGE, DAMAGE_TAKEN, LEFT_CLICK, RIGHT_CLICK, SNEAK
    }

    // value type data is double only
    public enum AbilityModifier{
        COOLDOWN, DAMAGE, STAMINA, MINIMUM, MAXIMUM, RESULT;
    }

    public enum AbilitySerialize{
        TRIGGER_TYPE, DEFAULT_MODIFIER, CUSTOM_MODIFIER, LORE;
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
    }

    // Abstract

    public abstract void entityShoot(EntityShootBowEvent event, LivingEntity executor, ItemStack item);
    public abstract void entityDamageByEntity(EntityDamageByEntityEvent event, LivingEntity executor, ItemStack item);
    public abstract void playerClick(PlayerInteractEvent event, Player executor, ItemStack item);
    public abstract void playerSneak(PlayerToggleSneakEvent event, Player executor, ItemStack item);

    // Action

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public void set(MonoFactory factory, TriggerType triggerType){
        try {
            NBTManager nbt = factory.getNbtManager();
            nbt.customNbtData(getId() + ".TRIGGER_TYPE", triggerType.name());

            for (AbilityModifier mod : getModifier().keySet()) {
                String path = getId() + "." + mod.name();
                nbt.customNbtData(path, getModifier().get(mod));
                factory.getPlaceholder().addReplacer(path, getModifier().get(mod).toString());
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
    }

    public void set(ItemStack item, TriggerType triggerType){
        MonoFactory factory = new MonoFactory(item);
        set(factory, triggerType);
    }

    public void startCooldown(LivingEntity entity, int cooldown){
        String cdPath = getId() + "." + AbilityModifier.COOLDOWN.name();
        this.isCooldown = true;
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                isCooldown = false;
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

    public void addCustomModifier(String modifier, Class instance){
        getCustomModifierClazz().put(modifier, instance);
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

    public boolean isCooldown() {
        return isCooldown;
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


}
