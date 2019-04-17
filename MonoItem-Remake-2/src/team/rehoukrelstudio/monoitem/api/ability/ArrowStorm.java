package team.rehoukrelstudio.monoitem.api.ability;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import team.rehoukrelstudio.monoitem.MonoItem;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import utils.ParticleManager;

import java.util.HashMap;
import java.util.Random;

public class ArrowStorm extends Ability {

    MonoItem plugin = MonoItem.getPlugin(MonoItem.class);

    public ArrowStorm() {
        super("ARROW_STORM", "Arrow Storm", Material.ARROW);

        addAllowedTrigger(TriggerType.SNEAK, TriggerType.PROJECTILE_SHOOT, TriggerType.LEFT_CLICK, TriggerType.RIGHT_CLICK);
    }

    @Override
    public void loadDefaultData() {
        setTriggerType(TriggerType.SNEAK);
        setModifier(AbilityModifier.MINIMUM, 7);
        setModifier(AbilityModifier.MAXIMUM, 14);
        setModifier(AbilityModifier.DAMAGE, 120);
        setModifier(AbilityModifier.COOLDOWN, 100);
    }

    private void arrowStorm(LivingEntity shooter, MonoFactory factory){
        if (isCooldown(shooter)){return;}
        HashMap<AbilitySerialize, Object> map = getAbilitySerialize(factory);
        setModifier((HashMap<AbilityModifier, Double>) map.get(AbilitySerialize.DEFAULT_MODIFIER));

        long amount = (int) Math.round(Double.parseDouble(getModifier().get(AbilityModifier.RESULT).toString()));
        long cooldown = Math.round(getModifier().get(AbilityModifier.COOLDOWN));

        for (int i = 0; i < amount;i++){
            int finalI = i;
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    Vector spread = shooter.getEyeLocation().getDirection().multiply(1.5);
                    spread.setX(spread.getX() + new Random().nextDouble()/4);
                    spread.setZ(spread.getZ() + new Random().nextDouble()/4);
                    Arrow proj = shooter.launchProjectile(Arrow.class, spread);
                    proj.setPickupStatus(Arrow.PickupStatus.CREATIVE_ONLY);
                    proj.setMetadata("removeOnHit", new FixedMetadataValue(plugin, true));
                    proj.getWorld().playSound(proj.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
                    ParticleManager pm = new ParticleManager(plugin, proj);
                    pm.trails(Particle.CRIT_MAGIC, proj, 1, 20, 2);
                }

            }, 3*i);
        }
        startCooldown(shooter, cooldown);
    }

    @Override
    public void projectileHit(ProjectileHitEvent event, MonoFactory factory) {

    }

    @Override
    public void entityShoot(EntityShootBowEvent event, LivingEntity executor, MonoFactory factory) {
        arrowStorm(executor, factory);
    }

    @Override
    public void entityDamageByEntity(EntityDamageByEntityEvent event, Damageable attacker, Damageable victim, MonoFactory factory) {

    }

    @Override
    public void playerClick(PlayerInteractEvent event, MonoFactory factory) {
        arrowStorm(event.getPlayer(), factory);
    }

    @Override
    public void playerSneak(PlayerToggleSneakEvent event, MonoFactory factory) {
        arrowStorm(event.getPlayer(), factory);
    }
}
