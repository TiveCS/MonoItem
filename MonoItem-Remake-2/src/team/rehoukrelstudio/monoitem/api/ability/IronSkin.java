package team.rehoukrelstudio.monoitem.api.ability;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import utils.ParticleManager;

import java.util.HashMap;

public class IronSkin extends Ability {
    public IronSkin() {
        super("IRON_SKIN", "Iron Skin", Material.IRON_CHESTPLATE);

        addAllowedTrigger(TriggerType.DAMAGE_TAKEN);
    }

    @Override
    public void loadDefaultData() {
        setTriggerType(TriggerType.DAMAGE_TAKEN);
        setModifier(AbilityModifier.COOLDOWN, 100);
        setModifier(AbilityModifier.CHANCE, 25);
        setModifier(AbilityModifier.MINIMUM, 20.5);
        setModifier(AbilityModifier.MAXIMUM, 32.25);
        setCustomModifier("knockbackpower", Double.class, 1);
        initializeResult();
    }

    @Override
    public void projectileHit(ProjectileHitEvent event, MonoFactory factory) {

    }

    @Override
    public void entityShoot(EntityShootBowEvent event, LivingEntity executor, MonoFactory factory) {

    }

    @Override
    public void entityDamageByEntity(EntityDamageByEntityEvent event, Damageable attacker, Damageable victim, MonoFactory factory) {
        if (getTriggerType().equals(TriggerType.DAMAGE_TAKEN) && !isCooldown((LivingEntity) victim)){
            HashMap<AbilitySerialize, Object> map = getAbilitySerialize(factory);
            setCustomModifier((HashMap<String, Object>) map.get(AbilitySerialize.CUSTOM_MODIFIER));
            setModifier((HashMap<AbilityModifier, Double>) map.get(AbilitySerialize.DEFAULT_MODIFIER));

            long cooldown = Math.round(getModifier().get(AbilityModifier.COOLDOWN));
            if (event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)){
                event.setCancelled(true);
            }else {
                double knockback = Double.parseDouble(getCustomModifier().get("knockbackpower").toString()),
                        decrease = Double.parseDouble(getModifier().get(AbilityModifier.RESULT).toString());
                event.setDamage(event.getDamage() - event.getDamage() * decrease / 100);
                attacker.setVelocity(attacker.getLocation().getDirection().multiply(-1 * (knockback)));
            }
            startCooldown((LivingEntity) victim, cooldown);
            victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_FLOP, 1, 1);
        }
    }

    @Override
    public void playerClick(PlayerInteractEvent event, MonoFactory factory) {

    }

    @Override
    public void playerSneak(PlayerToggleSneakEvent event, MonoFactory factory) {

    }
}
