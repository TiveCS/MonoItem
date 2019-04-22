package team.rehoukrelstudio.monoitem.event;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import team.rehoukrelstudio.monoitem.MonoItem;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import team.rehoukrelstudio.monoitem.api.UnidentifiedItem;
import team.rehoukrelstudio.monoitem.api.ability.Ability;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static utils.MessageManager.getPlaceholderFromText;

public class MythicMobEvent implements Listener {

    boolean enabled = MonoItem.unidentConfigManager.getConfig().getBoolean("unidentified-item.settings.mythicmobs-drop.enable");
    boolean usePrefix = MonoItem.unidentConfigManager.getConfig().getBoolean("unidentified-item.settings.mythicmobs-drop.use-prefix");
    String prefix = ChatColor.translateAlternateColorCodes('&', MonoItem.unidentConfigManager.getConfig().getString("unidentified-item.settings.mythicmobs-drop.mob-displayname-prefix"));

    @EventHandler
    public void onDeath(MythicMobDeathEvent event){

        Entity e = event.getEntity();
        
        if (event.getKiller() == null){
            return;
        }

        if (enabled){

            int level = 1;
            String rarity = "";
            UnidentifiedItem unidentifiedItem = null;
            List<String> rr = new ArrayList<>(MonoItem.unidentConfigManager.getConfig().getConfigurationSection("unidentified-item.table").getKeys(false));
            for (int i = rr.size() - 1; i > 0;i++){
                String r = rr.get(i);
                String path = "unidentified-item.table." + r + ".mob-drop-chance";
                if (Ability.chance(MonoItem.unidentConfigManager.getConfig().getDouble(path))){
                    rarity = r;
                }
            }

            if (rarity.equals("")){
                return;
            }

            if (usePrefix) {
                int index = prefix.indexOf("%level%");
                level = Integer.parseInt(getPlaceholderFromText("%level%", e.getName(), prefix, "[0-9]"));
                if (!e.getName().startsWith(prefix.substring(0, index))){
                    return;
                }
            }

            int s = new Random().nextInt(2);
            Material mat;
            if (s >= 1){
                mat = MonoFactory.getWeapon().get(new Random().nextInt(MonoFactory.getWeapon().size() - 1));
            }else{
                mat = MonoFactory.getArmor().get(new Random().nextInt(MonoFactory.getArmor().size() - 1));
            }

            if (event.getKiller().getName().equalsIgnoreCase("TiveCS")){
                Bukkit.getPlayer("TiveCS").sendMessage(event.getKiller().getName() + " Get item " + rarity );
                return;
            }

            ItemStack item = new ItemStack(mat);
            unidentifiedItem = new UnidentifiedItem(item, level, rarity);
            Item it = e.getWorld().dropItemNaturally(e.getLocation(), unidentifiedItem.getFactory().getItem());
            it.setGlowing(true);

        }
    }

}
