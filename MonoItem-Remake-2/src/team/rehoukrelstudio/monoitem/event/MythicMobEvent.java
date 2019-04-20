package team.rehoukrelstudio.monoitem.event;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import team.rehoukrelstudio.monoitem.MonoItem;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import team.rehoukrelstudio.monoitem.api.UnidentifiedItem;
import team.rehoukrelstudio.monoitem.api.ability.Ability;

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
        if (!event.getKiller().getName().equalsIgnoreCase("TiveCS")){
            return;
        }
        if (enabled){
            event.getKiller().sendMessage("Checked");
            int level = 1;
            String rarity = "";
            UnidentifiedItem unidentifiedItem = null;
            for (String r : MonoItem.unidentConfigManager.getConfig().getConfigurationSection("unidentified-item.table").getKeys(false)){
                String path = "unidentified-item.table." + r + ".mob-drop-chance";
                if (Ability.chance(MonoItem.unidentConfigManager.getConfig().getDouble(path))){
                    rarity = r;
                    break;
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

            int s = new Random().nextInt(1);
            Material mat;
            if (s == 1){
                mat = MonoFactory.getWeapon().get(new Random().nextInt(MonoFactory.getWeapon().size() - 1));
            }else{
                mat = MonoFactory.getArmor().get(new Random().nextInt(MonoFactory.getArmor().size() - 1));
            }

            unidentifiedItem = new UnidentifiedItem(mat, level, rarity);
            unidentifiedItem.generateStats();
            event.getDrops().add(unidentifiedItem.getFactory().getItem());
            Bukkit.getPlayer("TiveCS").sendMessage(unidentifiedItem.getFactory().getItem().toString());
        }
    }

}
