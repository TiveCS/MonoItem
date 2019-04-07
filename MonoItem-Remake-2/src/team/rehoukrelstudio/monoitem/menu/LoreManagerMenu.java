package team.rehoukrelstudio.monoitem.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import utils.DataConverter;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoreManagerMenu extends MenuManager {
    FactoryMenu fm;
    MonoFactory factory;
    public LoreManagerMenu() {
        super(5, "Factory > Lore Manager");
    }

    private void loadLore(){
        List<String> lore = factory.getMeta().hasLore() ? factory.getMeta().getLore() : new ArrayList<>();
        HashMap<Integer, ItemStack> slot = new HashMap<>();
        ItemStack back = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta m = back.getItemMeta();
        m.setDisplayName(ChatColor.RED + "Back to Factory Menu");
        back.setItemMeta(m);
        slot.put(getRows()*9 - 5, back);
        putIconData(-1, new HashMap<>(slot));
        slot.clear();
        boolean checked = false;
        for (int i = 0; i <= lore.size(); i++){
            if (slot.size() -1 >= (getRows() - 1)*9){
                putIconData(getIconData().size() + 1, slot);
                checked = true;
            }else{
                if (i == lore.size()){
                    ItemStack icon = new ItemStack(Material.WRITABLE_BOOK);
                    ItemMeta meta = icon.getItemMeta();
                    meta.setDisplayName(ChatColor.GREEN + "Add new line");
                    icon.setItemMeta(meta);
                    slot.put(i, icon);
                    continue;
                }
                ItemStack icon = new ItemStack(Material.BOOK);
                ItemMeta meta = icon.getItemMeta();
                meta.setDisplayName(ChatColor.YELLOW + "Line #" + (i + 1));
                meta.setLore(Arrays.asList(factory.getMeta().getLore().get(i)));
                icon.setItemMeta(meta);
                slot.put(i, icon);
            }
        }
        if (checked == false){
            putIconData(1, slot);
        }
    }

    @Override
    public void clickAction(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        if (event.getClickedInventory().getItem(slot).equals(getIconData().get(-1).get(slot))){
            fm.open((Player) event.getWhoClicked());
        }else{
            if (getIconData().get(getPage()).containsKey(slot)){
                int line = slot + ((getPage() - 1)*((getRows() - 1)*9));
                if (event.getClick().equals(ClickType.DROP)){
                    factory.removeLore(line);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[ Line &a" + (line + 1) + "&7 lore has been removed ]"));
                    fm.getMapCustomObject().put("item", factory.getItem());
                    HashMap<Integer, ItemStack> a = fm.getIconData().get(-1);
                    a.put(4, (ItemStack) fm.getMapCustomObject().get("item"));
                    fm.putIconData(-1, new HashMap<>(a));
                    open(p);
                    stage.remove(p);
                }else {
                    event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[ Please input a text to edit lore on line &a" + (line + 1) + "&7 ]"));
                    stage.put((Player) event.getWhoClicked(), "LORE:" + line);
                    event.getWhoClicked().closeInventory();
                }
            }
        }
    }

    @Override
    public void chatAction(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        String msg = ChatColor.translateAlternateColorCodes('&', event.getMessage());
        String stg = stage.get(p).toString();
        if (stg.startsWith("LORE:")){
            int line = Integer.parseInt(stg.split(":")[1]);
            factory.setLore(msg, factory.getPlaceholder(), line);
        }

        fm.getMapCustomObject().put("item", factory.getItem());
        HashMap<Integer, ItemStack> a = fm.getIconData().get(-1);
        a.put(4, (ItemStack) fm.getMapCustomObject().get("item"));
        fm.putIconData(-1, new HashMap<>(a));
        open(p);
        stage.remove(p);
    }

    @Override
    public void loadCustomDataItem(Object... object) {
        fm = (FactoryMenu) getConnectedMenu().get("FACTORY");
        factory = (MonoFactory) fm.getMapCustomObject().get("factory");
        loadLore();
    }
}
