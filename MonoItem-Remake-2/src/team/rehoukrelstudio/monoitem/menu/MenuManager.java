package team.rehoukrelstudio.monoitem.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class MenuManager {

    private int rows = 1;
    private String title = "My Title";
    private List<Object> customObject;
    private HashMap<String, Object> mapCustomObject = new HashMap<>();

    public static HashMap<Player, Object> stage = new HashMap<>();
    public static HashMap<Player, MenuManager> currentMenu = new HashMap<>();

    // icon format => [Page, (Slot, Item)]
    private HashMap<Object, MenuManager> connectedMenu = new HashMap<Object, MenuManager>();
    private HashMap<Integer, HashMap<Integer, ItemStack>> iconData = new HashMap<>();
    private Inventory menu;
    private int page = 1;

    public MenuManager(int rows, String title, Object... customObject){
        this.rows = rows;
        this.title = title;
        this.customObject = customObject != null ?  Arrays.asList(customObject) : null;

        this.menu = Bukkit.createInventory(null, rows*9, ChatColor.translateAlternateColorCodes('&', title));
    }

    public abstract void clickAction(InventoryClickEvent event);
    public abstract void chatAction(AsyncPlayerChatEvent event);
    public abstract void loadCustomDataItem(Object... object);

    public void addMapCustomObject(String s, Object obj){
        mapCustomObject.put(s, obj);
    }

    public void open(Player p){
        loadCustomDataItem();
        loadItem();
        p.openInventory(getMenu());
        currentMenu.put(p, this);
    }

    public void openOtherMenu(Player p, MenuManager mm){
        loadCustomDataItem();
        mm.open(p);
        currentMenu.put(p, mm);
    }

    public void nextPage(Player p){
        setPage(getPage() + 1);
        open(p);
    }

    public void previousPage(Player p){
        setPage(getPage() - 1);
        open(p);
    }

    public void loadItem(){
        if (!getIconData().isEmpty()) {
            HashMap<Integer, ItemStack> allPageIcon = getIconData().get(-1);
            HashMap<Integer, ItemStack> currentIconPage = getIconData().get(getPage());
            try {
                currentIconPage.putAll(allPageIcon);
            }catch(Exception e){e.printStackTrace();}
            if (!currentIconPage.isEmpty()) {
                for (int slot : currentIconPage.keySet()) {
                    getMenu().setItem(slot, currentIconPage.get(slot));
                }
            }
        }
    }

    public HashMap<String, Object> getMapCustomObject() {
        return mapCustomObject;
    }

    public void putIconData(int page, HashMap<Integer, ItemStack> iconData) {
        this.iconData.put(page, iconData);
    }

    public void addConnectedMenu(String key, MenuManager mm){
        this.connectedMenu.put(key, mm);
    }

    public void addTwoWayConnectedMenu(String targetKey, String instanceKey, MenuManager mm){
        mm.addConnectedMenu(instanceKey, this);
        addConnectedMenu(targetKey, mm);
    }

    public void setMenu(Inventory menu) {
        this.menu = menu;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public HashMap<Object, MenuManager> getConnectedMenu() {
        return connectedMenu;
    }

    public List<Object> getCustomObject() {
        return customObject;
    }

    public int getRows() {
        return rows;
    }

    public int getPage() {
        return page;
    }

    public Inventory getMenu() {
        return menu;
    }

    public HashMap<Integer, HashMap<Integer, ItemStack>> getIconData() {
        return iconData;
    }
}
