package team.rehoukrelstudio.monoitem.api.ability;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import team.rehoukrelstudio.monoitem.MonoItem;
import team.rehoukrelstudio.monoitem.api.MonoFactory;

import java.util.HashMap;

public class Reflection extends Ability {

    private MonoItem plugin = MonoItem.getPlugin(MonoItem.class);

    public Reflection() {
        super("REFLECTION", "Reflection", new ItemStack(Material.GLASS));

        addAllowedTrigger(TriggerType.DAMAGE);
        addAllowedTrigger(TriggerType.DAMAGE_TAKEN);
        addAllowedTrigger(TriggerType.SNEAK);
        addAllowedTrigger(TriggerType.RIGHT_CLICK);
        addAllowedTrigger(TriggerType.LEFT_CLICK);
    }

    private void execute(Player p, MonoFactory factory){
        if (isCooldown(p)) return;

        double chance = factory.getNbtManager().getDouble(this.getId() + "." + AbilityModifier.CHANCE.name());
        int cooldown = factory.getNbtManager().getInteger(this.getId() + "." + AbilityModifier.COOLDOWN.name());
        if (getTriggerType().equals(TriggerType.DAMAGE) || getTriggerType().equals(TriggerType.DAMAGE_TAKEN)) {
            if (chance(chance)) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                    p.setCooldown(p.getInventory().getItemInMainHand().getType(), 0);
                    p.playSound(p.getLocation(), Sound.ENTITY_FISHING_BOBBER_THROW, 1, 1);
                    startCooldown(p, cooldown);
                }, 1L);
            }
        }else{
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                p.setCooldown(p.getInventory().getItemInMainHand().getType(), 0);
                p.playSound(p.getLocation(), Sound.ENTITY_FISHING_BOBBER_THROW, 1,1);
                startCooldown(p, cooldown);
            }, 1L);
        }
    }

    @Override
    public void loadDefaultData() {
        setModifier(AbilityModifier.COOLDOWN, 20);
        setModifier(AbilityModifier.CHANCE, 10);
    }

    @Override
    public void projectileHit(ProjectileHitEvent event, MonoFactory factory) {

    }

    @Override
    public void entityShoot(EntityShootBowEvent event, LivingEntity executor, MonoFactory factory) {

    }

    @Override
    public void entityDamageByEntity(EntityDamageByEntityEvent event, Damageable attacker, Damageable victim, MonoFactory factory) {
        if (getTriggerType().equals(TriggerType.DAMAGE)){
            if (attacker instanceof Player) {
                execute((Player) attacker, factory);
            }
        }else if (getTriggerType().equals(TriggerType.DAMAGE_TAKEN)){
            if (victim instanceof Player) {
                execute((Player) victim, factory);
            }
        }
    }

    @Override
    public void playerClick(PlayerInteractEvent event, MonoFactory factory) {
        execute(event.getPlayer(), factory);
    }

    @Override
    public void playerSneak(PlayerToggleSneakEvent event, MonoFactory factory) {
        execute(event.getPlayer(), factory);
    }


}
