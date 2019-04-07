package team.rehoukrelstudio.monoitem.api.ability;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

public class Reflection extends Ability {
    public Reflection() {
        super("REFLECTION", "Reflection", new ItemStack(Material.GLASS));

        setModifier(AbilityModifier.RESULT, 0.3);
        setModifier(AbilityModifier.COOLDOWN, 20);
        setModifier(AbilityModifier.MINIMUM, 0.1);
        setModifier(AbilityModifier.MAXIMUM, 0.5);
        addCustomModifier("chance", Double.class);
    }

    @Override
    public void entityShoot(EntityShootBowEvent event, LivingEntity executor, ItemStack item) {

    }

    @Override
    public void entityDamageByEntity(EntityDamageByEntityEvent event, LivingEntity executor, ItemStack item) {
        if (getTriggerType().equals(TriggerType.DAMAGE)){
            if (executor instanceof Player){
                Player p = (Player) executor;
                p.setCooldown(p.getInventory().getItemInMainHand().getType(), 0);
                startCooldown(executor, 0);
            }
        }
    }

    @Override
    public void playerClick(PlayerInteractEvent event, Player executor, ItemStack item) {

    }

    @Override
    public void playerSneak(PlayerToggleSneakEvent event, Player executor, ItemStack item) {

    }
}
