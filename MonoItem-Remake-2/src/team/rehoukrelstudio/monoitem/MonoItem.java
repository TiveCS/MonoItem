package team.rehoukrelstudio.monoitem;

import org.bukkit.plugin.java.JavaPlugin;
import team.rehoukrelstudio.monoitem.cmd.CmdMonoItem;
import team.rehoukrelstudio.monoitem.event.MenuEvent;
import team.rehoukrelstudio.monoitem.event.StatsEvent;
import team.rehoukrelstudio.monoitem.nms.NMSManager;

public class MonoItem extends JavaPlugin {

    private NMSManager nmsManager = new NMSManager();

    @Override
    public void onEnable() {
        loadEvents();
        loadCommands();
    }

    private void loadEvents() {
        getServer().getPluginManager().registerEvents(new StatsEvent(), this);
        getServer().getPluginManager().registerEvents(new MenuEvent(), this);
    }

    private void loadCommands() {
        getCommand("monoitem").setExecutor(new CmdMonoItem());
        getCommand("monoitem").setTabCompleter(new CmdMonoItem());
    }

    public NMSManager getNmsManager() {
        return nmsManager;
    }
}
