package team.rehoukrelstudio.monoitem.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.rehoukrelstudio.monoitem.MonoItem;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import team.rehoukrelstudio.monoitem.api.fixed.OptionEnum;
import team.rehoukrelstudio.monoitem.api.fixed.Requirement;

import java.util.HashMap;

public class CmdIdentify implements CommandExecutor {

    public static HashMap<String, Double> price = new HashMap<>();

    public enum IdentifyPrice{
        MONEY, XP, XP_LEVEL;

        String path = "unidentified-item.settings.identify-price." + name().toLowerCase();

        public String getPath() {
            return path;
        }
    }

    public void identify(Player p){
        if (p.getInventory().getItemInMainHand() != null){
            MonoFactory factory = new MonoFactory(p.getInventory().getItemInMainHand());
            if (factory.hasOption(OptionEnum.UNIDENTIFIED)){
                int level = 1;
                if (MonoItem.unidentConfigManager.getConfig().getBoolean("identify-price.multiply-price-per-level")){
                    level = factory.hasRequirement(Requirement.LEVEL) ? (int) factory.getRequirementValue(Requirement.LEVEL) : 1;
                }
                double money = price.get("MONEY")*level;
                int xp = (int) (price.get("XP")*level), xplevel = (int) (price.get("XP_LEVEL")*level);

                if (p.getTotalExperience() < xp){
                    return;
                }
                if (p.getLevel() < xplevel){
                    return;
                }

                if (MonoItem.economy != null){
                    if (MonoItem.economy.getBalance(p) > money) {
                        MonoItem.economy.withdrawPlayer(p, money);
                    }
                }
                factory.generateUnidentified();
                p.getInventory().setItemInMainHand(factory.getItem());
                p.sendMessage(ChatColor.GREEN + "Successfully identified item on hand");
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("identify")){

            if (commandSender instanceof Player){
                Player p = (Player) commandSender;
                if (strings.length == 0){
                    identify(p);
                    return true;
                }
                if (strings.length == 1){
                    if (p.hasPermission("monoitem.identify.other")){
                        Player t = Bukkit.getPlayer(strings[1]);
                        identify(t);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
