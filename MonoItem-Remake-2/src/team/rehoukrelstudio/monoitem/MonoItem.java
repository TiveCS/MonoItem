package team.rehoukrelstudio.monoitem;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import team.rehoukrelstudio.monoitem.api.ability.*;
import team.rehoukrelstudio.monoitem.cmd.CmdIdentify;
import team.rehoukrelstudio.monoitem.cmd.CmdMonoItem;
import team.rehoukrelstudio.monoitem.event.AbilityEvent;
import team.rehoukrelstudio.monoitem.event.MenuEvent;
import team.rehoukrelstudio.monoitem.event.MythicMobEvent;
import team.rehoukrelstudio.monoitem.event.StatsEvent;
import team.rehoukrelstudio.monoitem.nms.NMSManager;
import utils.ConfigManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MonoItem extends JavaPlugin {

    private NMSManager nmsManager = new NMSManager();
    public File formatFile = new File(getDataFolder(), "format.yml");
    public static ConfigManager unidentConfigManager;
    public static List<String> softdependency = new ArrayList<>();
    File unident = new File(this.getDataFolder(), "unidentified-item.yml");

    public static Economy economy;

    @Override
    public void onEnable() {
        loadConfig();
        loadSoftDependency();
        loadCommands();
        loadAbilities();
        loadEvents();
    }

    private void loadSoftDependency() {
        softdependency.clear();
        for (String s : this.getDescription().getSoftDepend()){
            try {
                if (getServer().getPluginManager().getPlugin(s).isEnabled()) {
                    getConfig().set("plugin-hook." + s, true);
                    getLogger().info("Successfully hooked with " + s);
                    softdependency.add(s);
                }else{
                    getConfig().set("plugin-hook." + s, false);
                }
            }catch(Exception e){
                getConfig().set("plugin-hook." + s, false);
            }
        }
        saveConfig();
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        if (!unident.exists()) {
            saveResource("unidentified-item.yml", false);
        }
        unidentConfigManager = new ConfigManager(this, unident);

        File folder = new File(this.getDataFolder(), "Saved Item");
        if (!folder.exists()){
            folder.mkdir();
        }
        if (!formatFile.exists()){
            saveResource("format.yml", false);
        }

        for (CmdIdentify.IdentifyPrice p : CmdIdentify.IdentifyPrice.values()){
            CmdIdentify.price.put(p.name(), MonoItem.unidentConfigManager.getConfig().getDouble(p.getPath()));
        }

    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    private void loadEvents() {
        getServer().getPluginManager().registerEvents(new AbilityEvent(), this);
        getServer().getPluginManager().registerEvents(new StatsEvent(), this);
        getServer().getPluginManager().registerEvents(new MenuEvent(), this);
        getServer().getPluginManager().registerEvents(new CmdIdentify(), this);

        if (softdependency.contains("MythicMobs")) {
            getServer().getPluginManager().registerEvents(new MythicMobEvent(), this);
        }
    }

    private void loadCommands() {
        if (setupEconomy()) {
            getCommand("identify").setExecutor(new CmdIdentify());
        }
        getCommand("monoitem").setExecutor(new CmdMonoItem());
        getCommand("monoitem").setTabCompleter(new CmdMonoItem());
    }

    public void loadAbilities(){
        Ability.registerAbility(new Reflection(), new IronSkin(), new Leap(), new VorpalSlash(), new ArrowStorm()
        ,new TeleportDamage());
    }

    public NMSManager getNmsManager() {
        return nmsManager;
    }
}
