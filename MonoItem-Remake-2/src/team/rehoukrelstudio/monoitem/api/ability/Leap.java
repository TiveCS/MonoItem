package team.rehoukrelstudio.monoitem.api.ability;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;
import team.rehoukrelstudio.monoitem.MonoItem;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import utils.ParticleManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Leap extends Ability {

    private MonoItem plugin = MonoItem.getPlugin(MonoItem.class);
    public static List<LivingEntity> leap = new ArrayList<>();

    public Leap() {
        super("LEAP", "Leap", Material.LEATHER_BOOTS);
        addAllowedTrigger(TriggerType.SNEAK);
        addAllowedTrigger(TriggerType.DAMAGE);
        addAllowedTrigger(TriggerType.DAMAGE_TAKEN);
        addAllowedTrigger(TriggerType.LEFT_CLICK);
        addAllowedTrigger(TriggerType.RIGHT_CLICK);

    }

    @Override
    public void loadDefaultData() {
        setTriggerType(TriggerType.SNEAK);
        setModifier(AbilityModifier.COOLDOWN, 60);
        setModifier(AbilityModifier.RESULT, 2.2);
    }

    private void leap(LivingEntity entity, MonoFactory factory){
        if (!isCooldown(entity)) {
            HashMap<AbilitySerialize, Object> map = getAbilitySerialize(factory);
            HashMap<AbilityModifier, Double> def = (HashMap<AbilityModifier, Double>) map.get(AbilitySerialize.DEFAULT_MODIFIER);
            setModifier(def);

            long cooldown = Math.round(def.get(AbilityModifier.COOLDOWN));
            double result = def.get(AbilityModifier.RESULT);

            Location loc = entity.getLocation();
            Vector dir = loc.getDirection(), inc = new Vector(0, 1.2, 0);
            dir.setY(0);
            dir.multiply((result - result/4));
            dir.add(inc);
            entity.setVelocity(dir);

            ParticleManager pm = new ParticleManager(plugin, loc);
            loc.getWorld().playSound(loc, Sound.ENTITY_BAT_TAKEOFF, 1 ,1);
            pm.circle(Particle.CRIT, 1, 10, 20, 2);
            if (!leap.contains(entity)) {
                leap.add(entity);
            }
            startCooldown(entity, cooldown);
        }
    }

    @Override
    public void projectileHit(ProjectileHitEvent event, MonoFactory factory) {

    }

    @Override
    public void entityShoot(EntityShootBowEvent event, LivingEntity executor, MonoFactory factory) {

    }

    @Override
    public void entityDamageByEntity(EntityDamageByEntityEvent event, Damageable attacker, Damageable victim, MonoFactory factory) {
        if (getTriggerType().equals(TriggerType.DAMAGE)) {
            leap((LivingEntity) event.getDamager(), factory);
        }else if (getTriggerType().equals(TriggerType.DAMAGE_TAKEN)){
            leap((LivingEntity) event.getEntity(), factory);
        }
    }

    @Override
    public void playerClick(PlayerInteractEvent event, MonoFactory factory) {
        if (getTriggerType().equals(TriggerType.LEFT_CLICK) || getTriggerType().equals(TriggerType.RIGHT_CLICK)) {
            leap(event.getPlayer(), factory);
        }
    }

    @Override
    public void playerSneak(PlayerToggleSneakEvent event, MonoFactory factory) {
        if (getTriggerType().equals(TriggerType.SNEAK)) {
            leap(event.getPlayer(), factory);
        }
    }
}
