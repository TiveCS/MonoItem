package team.rehoukrelstudio.monoitem.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import team.rehoukrelstudio.monoitem.api.ability.Ability;

import java.util.HashMap;

public class AbilityMenu extends MenuManager {
    public AbilityMenu() {
        super(6, "Factory > Ability");

        HashMap<Integer, ItemStack> icon = new HashMap<>();
        ItemStack back = new ItemStack(Material.RED_STAINED_GLASS_PANE), next = new ItemStack(Material.LIME_STAINED_GLASS_PANE), previous = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta meta = back.getItemMeta(), nmeta = next.getItemMeta(), pmeta = previous.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Back to Factory Menu");
        nmeta.setDisplayName(ChatColor.GREEN + "Next Page");
        pmeta.setDisplayName(ChatColor.GREEN + "Previous Page");
        back.setItemMeta(meta);
        next.setItemMeta(nmeta);
        previous.setItemMeta(pmeta);
        icon.put(getRows()*9 - 5, back);
        icon.put(getRows()*9 - 3, next);
        icon.put(getRows()*9 - 7, previous);
        putIconData(-1, new HashMap<>(icon));
        icon.clear();

        boolean moreThanOnePage = false;
        int slot = 0;
        for (String id : Ability.abilities.keySet()){
            Ability ability = Ability.abilities.get(id);
            icon.put(slot, ability.getIcon());
            if (icon.size() >= (getRows() - 1)*9){

            }
        }
    }

    @Override
    public void clickAction(InventoryClickEvent event) {
        int slot = event.getSlot();
        Player p = (Player) event.getWhoClicked();
        FactoryMenu fm = (FactoryMenu) getConnectedMenu().get("FACTORY");
        MonoFactory factory = (MonoFactory) fm.getMapCustomObject().get("factory");
        if (slot == getRows()*9 - 5){
            fm.open(p);
        }
        else if (slot == getRows()*9 - 3){
            nextPage(p);
        }
        else if (getPage() > 1) {
            if (slot == getRows() * 9 - 7) {
                previousPage(p);
            }
        }else{

        }
    }

    @Override
    public void chatAction(AsyncPlayerChatEvent event) {

    }

    @Override
    public void loadCustomDataItem(Object... object) {

    }

}
