package team.rehoukrelstudio.monoitem.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import team.rehoukrelstudio.monoitem.api.ability.Ability;

import java.util.HashMap;

public class AbilityMenu extends MenuManager {
    public AbilityMenu() {
        super(4, "Factory > Ability");
        HashMap<Integer, ItemStack> def = new HashMap<>();
        ItemStack back = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemStack next = new ItemStack(Material.LIME_STAINED_GLASS_PANE), prev = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta b = back.getItemMeta(),n = next.getItemMeta(),p = prev.getItemMeta();

        b.setDisplayName(ChatColor.RED + "Back to Factory Menu");
        n.setDisplayName(ChatColor.BLUE + "Previous Page");
        p.setDisplayName(ChatColor.GREEN + "Next Page");

        back.setItemMeta(b);
        next.setItemMeta(n);
        prev.setItemMeta(p);

        def.put(getRows()*9 - 5, back);
        def.put(getRows()*9 - 7, prev);
        def.put(getRows()*9 - 3, next);

        putIconData(1, new HashMap<>(def));
        def.clear();

    }

    @Override
    public void clickAction(InventoryClickEvent event) {
        if (event.getSlot() == getRows()*9 - 5){
            getConnectedMenu().get("FACTORY").open((Player) event.getWhoClicked());
        }
    }

    @Override
    public void chatAction(AsyncPlayerChatEvent event) {

    }

    @Override
    public void loadCustomDataItem(Object... object) {

    }
}
