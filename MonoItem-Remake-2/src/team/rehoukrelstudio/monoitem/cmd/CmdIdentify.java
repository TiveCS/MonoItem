package team.rehoukrelstudio.monoitem.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import team.rehoukrelstudio.monoitem.MonoItem;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import team.rehoukrelstudio.monoitem.api.fixed.OptionEnum;
import team.rehoukrelstudio.monoitem.api.fixed.Requirement;
import utils.ItemBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CmdIdentify implements CommandExecutor, Listener {

    public static HashMap<String, Double> price = new HashMap<>();

    public enum IdentifyPrice{
        MONEY, XP, XP_LEVEL;

        String path = "unidentified-item.settings.identify-price." + name().toLowerCase();

        public String getPath() {
            return path;
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event){
        if (event.getInventory().getTitle().equals("Identify this item?")){
            event.setCancelled(true);
            if (event.getSlot() == 2){
                identify((Player) event.getWhoClicked());
                event.getWhoClicked().closeInventory();
            }else if (event.getSlot() == 6){
                event.getWhoClicked().closeInventory();
            }
        }
    }

    public void identifyMenu(Player p){
        if (p.getInventory().getItemInMainHand().getType().equals(Material.AIR)){
            return;
        }
        int level = 1;
        double multiplier = 1;
        MonoFactory factory = new MonoFactory(p.getInventory().getItemInMainHand());
        if (factory.hasOption(OptionEnum.UNIDENTIFIED)) {
            String rarity = factory.getOption(OptionEnum.UNIDENTIFIED).toString();
            String path = "unidentified-item.table." + rarity;
            List<String> prc = new ArrayList<>();
            if (MonoItem.unidentConfigManager.getConfig().contains(path + ".identify-price-multiplier")){
                multiplier = MonoItem.unidentConfigManager.getConfig().getDouble(path + ".identify-price-multiplier");
            }

            if (MonoItem.unidentConfigManager.getConfig().getBoolean("unidentified-item.settings.identify-price.multiply-price-per-level")){
                level = factory.hasRequirement(Requirement.LEVEL) ? (int) factory.getRequirementValue(Requirement.LEVEL) : 1;
            }
            prc.add("&aItem Level &b" + level);
            prc.add("");
            prc.add("&e&lPRICE");


            Inventory inv = Bukkit.createInventory(null, 9, "Identify this item?");

            ItemBuilder acc = new ItemBuilder(new ItemStack(Material.DIAMOND), "&aIdentify").finish();
            for (String s : price.keySet()) {
                if (price.get(s) > 0) {
                    prc.add("&7- &f" + s + " &c" + (price.get(s) * multiplier*level));
                }
            }
            acc.setLore(prc);

            inv.setItem(2, acc.finish().getItem());
            inv.setItem(4, p.getInventory().getItemInMainHand());
            inv.setItem(6, new ItemBuilder(new ItemStack(Material.REDSTONE_TORCH), "&cCancel").finish().getItem());
            p.openInventory(inv);
        }
    }

    public void identify(Player p){
        double priceMultiplier = 1;
        if (p.getInventory().getItemInMainHand() != null){
            boolean allow = false, economy = false;
            MonoFactory factory = new MonoFactory(p.getInventory().getItemInMainHand());
            if (factory.hasOption(OptionEnum.UNIDENTIFIED)){
                int level = 1;
                String rarity = factory.getOption(OptionEnum.UNIDENTIFIED).toString();
                String path = "unidentified-item.table." + rarity;
                if (MonoItem.unidentConfigManager.getConfig().contains(path + ".identify-price-multiplier")){
                    priceMultiplier = MonoItem.unidentConfigManager.getConfig().getDouble(path + ".identify-price-multiplier");
                }

                if (MonoItem.unidentConfigManager.getConfig().getBoolean("unidentified-item.settings.identify-price.multiply-price-per-level")){
                    level = factory.hasRequirement(Requirement.LEVEL) ? (int) factory.getRequirementValue(Requirement.LEVEL) : 1;
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aItem level &b" + level));
                }
                double money = price.get("MONEY")*level*priceMultiplier;
                int xp = (int) (price.get("XP")*level*priceMultiplier), xplevel = (int) (price.get("XP_LEVEL")*level*priceMultiplier);

                economy = MonoItem.economy != null;
                allow = (economy ? MonoItem.economy.getBalance(p) >= money : true) && p.getLevel() >= xplevel && p.getTotalExperience() >= xp;

                if (allow) {
                    p.setLevel(p.getLevel() - xplevel);
                    p.setTotalExperience(p.getTotalExperience() - xp);
                    if (xplevel > 0){
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSpent &6" + xplevel + " &aexperience level"));
                    }
                    if (xp > 0){
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSpent &6" + xp + " &aexperience"));
                    }
                    if (MonoItem.economy != null){
                        MonoItem.economy.withdrawPlayer(p, money);
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSpent &6$" + money + " &abalance"));
                    }

                    factory.generateUnidentified();
                    p.getInventory().setItemInMainHand(factory.getItem());
                    p.sendMessage(ChatColor.GREEN + "Successfully identified item on hand");
                }else{
                    for (IdentifyPrice pr : IdentifyPrice.values()){
                        if (price.containsKey(pr.name())){
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eRequired &6" + pr.name() + " &eminimum by &c" + (price.get(pr.name())*level*priceMultiplier)));
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("identify")){

            if (commandSender instanceof Player){
                Player p = (Player) commandSender;
                if (strings.length == 0){
                    identifyMenu(p);
                    return true;
                }
                if (strings.length == 1) {
                    if (commandSender.hasPermission("monoitem.identify.other")) {
                        Player t = Bukkit.getPlayer(strings[0]);
                        identifyMenu(t);
                        return true;
                    }
                }
            }
            if (commandSender instanceof ConsoleCommandSender) {
                if (strings.length == 1) {
                    Player t = Bukkit.getPlayer(strings[0]);
                    identifyMenu(t);
                    return true;
                }
            }
        }
        return false;
    }
}
