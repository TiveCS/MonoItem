package team.rehoukrelstudio.monoitem.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import utils.ItemBuilder;

import java.util.HashMap;

public class FactoryMenu extends MenuManager {
    private StatsMenu sm;
    private LoreManagerMenu lm;
    private OptionsMenu om;
    private AbilityMenu ab;

    private final String INSTANCE_KEY = "FACTORY";

    public FactoryMenu() {
        super(4, "Factory");

        HashMap<Integer, ItemStack> item = new HashMap<>();
        addMapCustomObject("item", new ItemStack(Material.WOODEN_SWORD));
        addMapCustomObject("factory", new MonoFactory((ItemStack) getMapCustomObject().get("item")));
        item.put(4, (ItemStack) getMapCustomObject().get("item"));
        putIconData(-1, item);
        item.put(10, new ItemBuilder(new ItemStack(Material.STONE_SWORD), "&aStats Manager").finish().getItem());
        item.put(11, new ItemBuilder(new ItemStack(Material.NAME_TAG), "&aRename").finish().getItem());
        item.put(12, new ItemBuilder(new ItemStack(Material.SIGN), "&aSet Item Type").finish().getItem());
        item.put(13, new ItemBuilder(new ItemStack(Material.COMPASS), "&aOptions Manager").finish().getItem());
        item.put(14, new ItemBuilder(new ItemStack(Material.WRITABLE_BOOK), "&aLore Manager").finish().getItem());
        item.put(15, new ItemBuilder(new ItemStack(Material.BLAZE_ROD), "&aAbility Manager").finish().getItem());
        item.put(16, new ItemBuilder(new ItemStack(Material.ENCHANTING_TABLE), "&aEnchant Item").finish().getItem());
        putIconData(1, item);

        sm = new StatsMenu();
        lm = new LoreManagerMenu();
        om = new OptionsMenu();
        ab = new AbilityMenu();

        addTwoWayConnectedMenu("ABILITY", INSTANCE_KEY, ab);
        addTwoWayConnectedMenu("OPTIONS", INSTANCE_KEY, om);
        addTwoWayConnectedMenu("LORE_MANAGER", INSTANCE_KEY, lm);
        addTwoWayConnectedMenu("STATS", INSTANCE_KEY, sm);
    }

    public void loadMainItem(ItemStack main){
        HashMap<Integer, ItemStack> item = new HashMap<>();
        addMapCustomObject("item", main);
        addMapCustomObject("factory", new MonoFactory((ItemStack) getMapCustomObject().get("item")));
        item.put(4, (ItemStack) getMapCustomObject().get("item"));
        putIconData(-1, item);
    }

    @Override
    public void clickAction(InventoryClickEvent event) {
        if (event.getClickedInventory().getItem(event.getSlot()).equals(getIconData().get(-1).get(4))){
            event.getWhoClicked().getInventory().addItem(getIconData().get(-1).get(4));
        }
        else if (getPage() == 1){
            if (event.getSlot() == 15){
                ab.open((Player) event.getWhoClicked());
            }
            if (event.getSlot() == 14){
                lm.open((Player) event.getWhoClicked());
            }
            if (event.getSlot() == 13){
                om.open((Player) event.getWhoClicked());
            }
            if (event.getSlot() == 10){
                sm.open((Player) event.getWhoClicked());
            }
            if (event.getSlot() == 11){
                event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[ Please input a text to rename ]"));
                stage.put((Player) event.getWhoClicked(), "RENAME");
                event.getWhoClicked().closeInventory();
            }
            if (event.getSlot() == 12){
                event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[ Please input a Item Type name on chat ]"));
                event.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7For example: IRON_SWORD"));
                stage.put((Player) event.getWhoClicked(), "ITEM_TYPE");
                event.getWhoClicked().closeInventory();
            }
        }
    }

    @Override
    public void chatAction(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        String msg = ChatColor.translateAlternateColorCodes('&', event.getMessage());
        MonoFactory factory = (MonoFactory) getMapCustomObject().get("factory");
        String stg = stage.get(p).toString();

        if (stg.equalsIgnoreCase("RENAME")){
            factory.getMeta().setDisplayName(msg);
            factory.getItem().setItemMeta(factory.getMeta());
        }else if (stg.equalsIgnoreCase("ITEM_TYPE")){
            try {
                factory.getItem().setType(Material.valueOf(msg.toUpperCase()));
            }catch(Exception e){
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[ Wrong item type name... ]"));
            }
        }

        getMapCustomObject().put("item", factory.getItem());
        HashMap<Integer, ItemStack> a = getIconData().get(-1);
        a.put(4, (ItemStack) getMapCustomObject().get("item"));
        putIconData(-1, a);
        open(p);
        stage.remove(p);
    }

    @Override
    public void loadCustomDataItem(Object... object) {

    }
}
