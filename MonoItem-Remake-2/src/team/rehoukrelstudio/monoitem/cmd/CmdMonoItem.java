package team.rehoukrelstudio.monoitem.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import team.rehoukrelstudio.monoitem.api.StatsEnum;
import team.rehoukrelstudio.monoitem.menu.FactoryMenu;
import team.rehoukrelstudio.monoitem.menu.StatsMenu;

import java.util.ArrayList;
import java.util.List;

public class CmdMonoItem implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("monoitem")){
            if (commandSender instanceof Player){
                Player p = (Player) commandSender;
                if (strings.length == 1){
                    if (strings[0].equalsIgnoreCase("edit")){
                        FactoryMenu menu = new FactoryMenu();
                        menu.open(p);
                        return true;
                    }
                }
                if (strings.length == 4){
                    if (strings[0].equalsIgnoreCase("stats")){
                        try {
                            MonoFactory factory = new MonoFactory(p.getInventory().getItemInMainHand());
                            StatsEnum stats = StatsEnum.valueOf(strings[1].toUpperCase());
                            double min = Double.parseDouble(strings[2]), max = Double.parseDouble(strings[3]);
                            factory.setStats(stats, min, max, false);
                            p.sendMessage("" + stats.name() + " " + min + " > " + max);
                            return true;
                        }catch(Exception e){return false;}
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        if (strings.length == 1){
            list.add("stats");
            list.add("edit");
            return list;
        }
        if (strings.length == 2){
            for (StatsEnum e : StatsEnum.values()){
                list.add(e.name());
            }
            return list;
        }
        return list;
    }
}
