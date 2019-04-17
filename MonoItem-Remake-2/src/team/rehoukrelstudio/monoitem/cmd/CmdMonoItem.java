package team.rehoukrelstudio.monoitem.cmd;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import team.rehoukrelstudio.monoitem.MonoItem;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import team.rehoukrelstudio.monoitem.api.ability.Ability;
import team.rehoukrelstudio.monoitem.api.fixed.OptionEnum;
import team.rehoukrelstudio.monoitem.api.fixed.StatsEnum;
import team.rehoukrelstudio.monoitem.menu.AbilityMenu;
import team.rehoukrelstudio.monoitem.menu.FactoryMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CmdMonoItem implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("monoitem")){
            if (commandSender instanceof Player){
                Player p = (Player) commandSender;
                if (strings.length == 1){
                    if (strings[0].equalsIgnoreCase("identify")){
                        MonoFactory factory = new MonoFactory(p.getInventory().getItemInMainHand());
                        factory.generateUnidentified();
                        p.getInventory().setItemInMainHand(factory.getItem());
                        return true;
                    }
                    if (strings[0].equalsIgnoreCase("edit")){
                        FactoryMenu menu = new FactoryMenu();
                        if (!p.getInventory().getItemInMainHand().getType().equals(Material.AIR)){
                            menu.loadMainItem(p.getInventory().getItemInMainHand());
                        }
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
                            p.getInventory().setItemInMainHand(factory.getItem());
                            p.sendMessage("" + stats.name() + " " + min + " > " + max);
                            return true;
                        }catch(Exception e){return false;}
                    }
                }
                if (strings.length > 1){
                    if (strings[0].equalsIgnoreCase("options")){
                        try{
                            boolean stat = Boolean.parseBoolean(strings[2]);
                            OptionEnum opt = OptionEnum.valueOf(strings[1].toUpperCase());
                            MonoFactory factory = new MonoFactory(p.getInventory().getItemInMainHand());
                            if (opt.equals(OptionEnum.UNIDENTIFIED)){
                                factory.setUnidentified(stat, strings[3]);
                            }else {
                                factory.setOptions(opt, stat);
                            }
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[ State of &f&n" + opt.name() + "&r &7switched to &b&n" + stat + "&r &7]"));
                            p.getInventory().setItemInMainHand(factory.getItem());
                            return true;
                        }catch(Exception e){
                            e.printStackTrace();
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[ Option is not found &7]"));
                        }
                        return true;
                    }
                    if (strings[0].equalsIgnoreCase("ability")) {
                        MonoFactory factory = new MonoFactory(p.getInventory().getItemInMainHand());
                        Ability ability;
                        Ability.TriggerType type;
                        try{
                            ability = Ability.abilities.get(strings[1]);
                            if (strings.length > 2) {
                                type = Ability.TriggerType.valueOf(strings[2].toUpperCase());
                            }else{
                                type = ability.getTriggerType();
                            }
                        }catch (Exception e){return true;}
                        List<String> modifiers = new ArrayList<>();
                        for (Ability.AbilityModifier mod : Ability.AbilityModifier.values()){
                            modifiers.add(mod.name().toLowerCase());
                        }
                        for (int i = 3; i < strings.length; i++){
                            String[] args = strings[i].split(":");
                            String key = args[0].toLowerCase();
                            String value = args[1];

                            if (modifiers.contains(key)){
                                ability.setModifier(Ability.AbilityModifier.valueOf(key.toUpperCase()), Double.parseDouble(value));
                                continue;
                            }else if (ability.getCustomModifierClazz().keySet().contains(key)){
                                ability.setCustomModifier(key, ability.getCustomModifierClazz().get(key), value);
                                continue;
                            }
                        }

                        ability.set(factory, type);
                        p.getInventory().setItemInMainHand(factory.getItem());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        List<String> arr = Arrays.asList(strings);
        if (strings.length == 1){
            list.add("stats");
            list.add("identify");
            list.add("edit");
            list.add("ability");
            list.add("options");
            return list;
        }
        if (strings.length == 2){
            if (strings[0].equalsIgnoreCase("stats")) {
                for (StatsEnum e : StatsEnum.values()) {
                    list.add(e.name());
                }
                return list;
            }
        }
        if (strings[0].equalsIgnoreCase("options")){
            if (strings.length == 3){
                list.add("true");
                list.add("false");
                return list;
            }
            if (strings.length == 4){
                if (strings[1].equalsIgnoreCase("UNIDENTIFIED")){
                    for (String st : MonoItem.unidentConfigManager.getConfig().getConfigurationSection("unidentified-item.table").getKeys(false)){
                        list.add(st);
                    }
                    return list;
                }
            }
            if (strings.length == 2) {
                for (OptionEnum e : OptionEnum.values()) {
                    list.add(e.name());
                }
                return list;
            }
        }
        if (strings[0].equalsIgnoreCase("ability")){
            if (strings.length == 2) {
                list.addAll(Ability.abilities.keySet());
                return list;
            }
            if (strings.length == strings.length && strings.length > 2){
                Ability ability = null;
                try {
                    ability = Ability.abilities.get(strings[1]);
                }catch(Exception e){}
                if (ability != null) {
                    if (strings.length == 3){
                        for (Ability.TriggerType type : ability.getAllowedTrigger()){
                            list.add(type.name());
                        }
                        return list;
                    }else {
                        for (Ability.AbilityModifier modifier : ability.getModifier().keySet()) {
                            if (!arr.contains(modifier.name().toLowerCase() + ":")) {
                                list.add(modifier.name().toLowerCase() + ":");
                            }
                        }
                        for (String key : ability.getCustomModifierClazz().keySet()) {
                            if (!arr.contains(key + ":")) {
                                list.add(key + ":");
                            }
                        }
                    }
                }
                return list;
            }
        }
        return list;
    }
}
