package team.rehoukrelstudio.monoitem.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import team.rehoukrelstudio.monoitem.api.OptionEnum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class OptionsMenu extends MenuManager {
    HashMap<ItemStack, OptionEnum> options = new HashMap<>();
    List<OptionEnum> optionEnumList = Arrays.asList(OptionEnum.values());
    public OptionsMenu() {
        super(4, "Factory > Options");

        HashMap<Integer, ItemStack> item = new HashMap<>();
        ItemStack back = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Back to Factory Menu");
        back.setItemMeta(meta);
        item.put(getRows()*9 - 5, back);
        putIconData(-1, new HashMap<>(item));

        item.clear();
        int count = 0;
        for (int i = 0; i < optionEnumList.size(); i++){
            OptionEnum e = optionEnumList.get(i);
            Material m = e.getIcon();

            ItemStack it = new ItemStack(m);
            ItemMeta mt = it.getItemMeta();
            mt.setDisplayName(ChatColor.AQUA + e.name());
            it.setItemMeta(mt);
            item.put(count, it);
            options.put(it, e);
            count++;
        }
        putIconData(1, item);
    }

    @Override
    public void clickAction(InventoryClickEvent event) {
        FactoryMenu fm = (FactoryMenu) getConnectedMenu().get("FACTORY");
        MonoFactory factory = (MonoFactory) fm.getMapCustomObject().get("factory");
        HashMap<Integer, ItemStack> slotItem = getIconData().get(getPage());
        if (event.getSlot() == getRows()*9 - 5){
            openOtherMenu((Player) event.getWhoClicked(), fm);
            return;
        }else{
            if (slotItem.containsKey(event.getSlot())) {
                OptionEnum option = options.get(slotItem.get(event.getSlot()));
                boolean state = true;
                if (factory.getNbtManager().hasNbt(option.getState())){
                    state = !factory.getNbtManager().getBoolean(option.getState());
                }
                factory.setOptions(option, state);
                event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[ State of &f&n" + option.name() + " &7switched to &b&n" + state + " &7]"));

                fm.getMapCustomObject().put("factory", factory);
                fm.getMapCustomObject().put("item", factory.getItem());
                HashMap<Integer, ItemStack> icon = fm.getIconData().get(-1);
                icon.put(4, (ItemStack) fm.getMapCustomObject().get("item"));
                fm.putIconData(-1, icon);
            }
        }
    }

    @Override
    public void chatAction(AsyncPlayerChatEvent event) {

    }

    @Override
    public void loadCustomDataItem(Object... object) {

    }
}
