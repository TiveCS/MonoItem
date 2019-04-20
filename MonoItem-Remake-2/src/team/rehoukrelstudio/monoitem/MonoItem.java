package team.rehoukrelstudio.monoitem;

import org.bukkit.plugin.java.JavaPlugin;
import team.rehoukrelstudio.monoitem.api.ability.*;
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
    public static ConfigManager unidentConfigManager, savedItemDirectory;
    public static List<String> softdependency = new ArrayList<>();
    File unident = new File(this.getDataFolder(), "unidentified-item.yml");

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

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        if (!unident.exists()) {
            saveResource("unidentified-item.yml", false);
        }
        unidentConfigManager = new ConfigManager(this, unident);
        savedItemDirectory = new ConfigManager(this, new File(this.getDataFolder() + "/Saved Item"));

    }

    private void loadEvents() {
        getServer().getPluginManager().registerEvents(new AbilityEvent(), this);
        getServer().getPluginManager().registerEvents(new StatsEvent(), this);
        getServer().getPluginManager().registerEvents(new MenuEvent(), this);

        if (softdependency.contains("MythicMobs")) {
            getServer().getPluginManager().registerEvents(new MythicMobEvent(), this);
        }
    }

    private void loadCommands() {
        getCommand("monoitem").setExecutor(new CmdMonoItem());
        getCommand("monoitem").setTabCompleter(new CmdMonoItem());
    }

    private void loadAbilities(){
        Ability.registerAbility(new Reflection(), new IronSkin(), new Leap(), new VorpalSlash(), new ArrowStorm()
        ,new TeleportDamage());
    }

    public NMSManager getNmsManager() {
        return nmsManager;
    }
}
