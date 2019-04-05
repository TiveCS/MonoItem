package team.rehoukrelstudio.monoitem.event;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import team.rehoukrelstudio.monoitem.menu.MenuManager;

public class MenuEvent implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent event){
        if (MenuManager.currentMenu.containsKey(event.getWhoClicked())) {
            MenuManager mm = MenuManager.currentMenu.get(event.getWhoClicked());
            if (mm != null) {
                if (event.getClickedInventory() != null && event.getClickedInventory().equals(mm.getMenu())) {
                    event.setCancelled(true);
                    mm.clickAction(event);
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        if (MenuManager.stage.containsKey(event.getPlayer()) && MenuManager.currentMenu.containsKey(event.getPlayer())){
            event.setCancelled(true);
            MenuManager mm = MenuManager.currentMenu.get(event.getPlayer());
            mm.chatAction(event);
        }
    }

}
