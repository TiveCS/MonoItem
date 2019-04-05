package team.rehoukrelstudio.monoitem.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import team.rehoukrelstudio.monoitem.api.StatsEnum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class StatsMenu extends MenuManager {
    List<StatsEnum> statsEnumList = Arrays.asList(StatsEnum.values());
    public StatsMenu() {
        super(4, "Factory > Stats");

        HashMap<Integer, ItemStack> icon = new HashMap<>();
        int count = 0;
        for (int i = 0; i < statsEnumList.size(); i++){
            StatsEnum e = statsEnumList.get(i);
            Material m = e.getIcon();

            ItemStack item = new ItemStack(m);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.AQUA + e.name());
            item.setItemMeta(meta);
            icon.put(count, item);
            count++;
        }
        ItemStack back = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Back to Factory Menu");
        back.setItemMeta(meta);
        icon.put(getRows()*9 - 5, back);
        putIconData(1, icon);
    }

    @Override
    public void clickAction(InventoryClickEvent event) {
        if (event.getSlot() == getRows()*9 - 5){
            getConnectedMenu().get("FACTORY").open((Player) event.getWhoClicked());
        }else if (getIconData().get(1).containsKey(event.getSlot())){
            stage.put((Player) event.getWhoClicked(), "MIN:" + statsEnumList.get(event.getSlot()));
            event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[ Please input a number to put minimum value ]"));
            event.getWhoClicked().closeInventory();
        }
    }

    @Override
    public void chatAction(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        String msg = event.getMessage();
        MonoFactory factory = (MonoFactory) getConnectedMenu().get("FACTORY").getMapCustomObject().get("factory");
        StatsEnum stats = StatsEnum.valueOf(stage.get(p).toString().split(":")[1]);
        double value = 0;
        try{
            value = Double.parseDouble(msg);
        }catch(Exception e){e.printStackTrace();}

        if (stage.get(p).toString().startsWith("MIN")){
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[ Minimum = " + value +" ]"));
            getMapCustomObject().put("MIN", value);
            stage.put(p, "MAX:" + stats.name());
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[ Please input a number to put maximum value ]"));
            return;
        }else if (stage.get(p).toString().startsWith("MAX")){
            double min = (double) getMapCustomObject().get("MIN"), max = value;
            getMapCustomObject().clear();
            factory.setStats(stats, min, max, false);
            FactoryMenu fm = (FactoryMenu) getConnectedMenu().get("FACTORY");

            fm.getMapCustomObject().put("factory", factory);
            fm.getMapCustomObject().put("item", factory.getItem());
            HashMap<Integer, ItemStack> icon = fm.getIconData().get(-1);
            icon.put(4, (ItemStack) fm.getMapCustomObject().get("item"));
            fm.putIconData(-1, icon);

            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[ Minimum = " + min + " , Maximum = " + value + " > " + stats.name() + " ]"));
            stage.remove(p);
            open(p);
            return;
        }
    }

    @Override
    public void loadCustomDataItem(Object... object) {

    }
}
