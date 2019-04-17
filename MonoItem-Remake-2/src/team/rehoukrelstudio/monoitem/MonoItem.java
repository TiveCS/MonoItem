package team.rehoukrelstudio.monoitem;

import org.bukkit.plugin.java.JavaPlugin;
import team.rehoukrelstudio.monoitem.api.ability.*;
import team.rehoukrelstudio.monoitem.cmd.CmdMonoItem;
import team.rehoukrelstudio.monoitem.event.AbilityEvent;
import team.rehoukrelstudio.monoitem.event.MenuEvent;
import team.rehoukrelstudio.monoitem.event.StatsEvent;
import team.rehoukrelstudio.monoitem.nms.NMSManager;
import utils.ConfigManager;

import java.io.File;

public class MonoItem extends JavaPlugin {

    private NMSManager nmsManager = new NMSManager();
    public static ConfigManager unidentConfigManager;
    File unident = new File(this.getDataFolder(), "unidentified-item.yml");

    @Override
    public void onEnable() {
        loadConfig();
        loadEvents();
        loadCommands();
        loadAbilities();
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        if (!unident.exists()) {
            saveResource("unidentified-item.yml", false);
        }
        unidentConfigManager = new ConfigManager(this, unident);
    }

    private void loadEvents() {
        getServer().getPluginManager().registerEvents(new AbilityEvent(), this);
        getServer().getPluginManager().registerEvents(new StatsEvent(), this);
        getServer().getPluginManager().registerEvents(new MenuEvent(), this);
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
