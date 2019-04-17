package team.rehoukrelstudio.monoitem.api.ability;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;
import team.rehoukrelstudio.monoitem.MonoItem;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import team.rehoukrelstudio.monoitem.api.fixed.StatsEnum;
import utils.ParticleManager;

import java.util.HashMap;

public class TeleportDamage extends Ability {

    private MonoItem plugin = MonoItem.getPlugin(MonoItem.class);

    public TeleportDamage() {
        super("TELEPORT", "Teleport", Material.ENDER_PEARL);

        addAllowedTrigger(TriggerType.SNEAK, TriggerType.RIGHT_CLICK, TriggerType.LEFT_CLICK);
    }

    @Override
    public void loadDefaultData() {
        setTriggerType(TriggerType.RIGHT_CLICK);
        setModifier(AbilityModifier.COOLDOWN, 100);

        // Range of teleport
        setModifier(AbilityModifier.MINIMUM, 5);
        setModifier(AbilityModifier.MAXIMUM, 12);
        setCustomModifier("damage", Boolean.class, true);
    }

    private void teleport(LivingEntity entity, MonoFactory factory){
        if (isCooldown(entity)){return;}

        HashMap<AbilitySerialize, Object> map = getAbilitySerialize(factory);
        setCustomModifier((HashMap<String, Object>) map.get(AbilitySerialize.CUSTOM_MODIFIER));
        setModifier((HashMap<AbilityModifier, Double>) map.get(AbilitySerialize.DEFAULT_MODIFIER));
        setStatsModifier((HashMap<StatsEnum, HashMap<String, Double>>) map.get(AbilitySerialize.STATS_MODIFIER));

        double range = Double.parseDouble(getModifier().get(AbilityModifier.RESULT).toString());
        double magicDamage = factory.getStatsResult(StatsEnum.MAGICAL_DAMAGE);

        Location eye = entity.getEyeLocation();
        Vector dir = eye.getDirection();
        ParticleManager pm = new ParticleManager(plugin, eye);

        for (double i = 0; i < range; i+=0.5){
            dir.multiply(1);
            eye.add(dir);

            if (!(eye.getBlock().isLiquid() || eye.getBlock().isPassable())){
                break;
            }

            for (Entity e : eye.getWorld().getNearbyEntities(eye, 0.5, 0.5, 0.5)){
                if (e instanceof Damageable){
                    Damageable dm = (Damageable) e;
                    dm.damage(magicDamage, entity);
                }
            }
            pm.dotParticle(Particle.VILLAGER_HAPPY, 1);
        }
        entity.teleport(eye);
        startCooldown(entity, 0);
    }

    @Override
    public void projectileHit(ProjectileHitEvent event, MonoFactory factory) {

    }

    @Override
    public void entityShoot(EntityShootBowEvent event, LivingEntity executor, MonoFactory factory) {

    }

    @Override
    public void entityDamageByEntity(EntityDamageByEntityEvent event, Damageable attacker, Damageable victim, MonoFactory factory) {

    }

    @Override
    public void playerClick(PlayerInteractEvent event, MonoFactory factory) {

    }

    @Override
    public void playerSneak(PlayerToggleSneakEvent event, MonoFactory factory) {

    }
}
