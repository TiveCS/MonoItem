package team.rehoukrelstudio.monoitem.api.ability;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
import team.rehoukrelstudio.monoitem.api.fixed.StatsEnum;
import utils.DataConverter;

import java.util.HashMap;

public class VorpalSlash extends Ability {

    MonoItem plugin = MonoItem.getPlugin(MonoItem.class);

    public VorpalSlash() {
        super("VORPAL_SLASH", "Vorpal Slash", Material.SHEARS);
        addAllowedTrigger(TriggerType.DAMAGE, TriggerType.DAMAGE_TAKEN);
    }

    private void slash(LivingEntity executor, Damageable target, MonoFactory factory){
        if (!isCooldown(executor)) {
            HashMap<AbilitySerialize, Object> map = getAbilitySerialize(factory);
            setCustomModifier((HashMap<String, Object>) map.get(AbilitySerialize.CUSTOM_MODIFIER));
            setModifier((HashMap<AbilityModifier, Double>) map.get(AbilitySerialize.DEFAULT_MODIFIER));
            setStatsModifier((HashMap<StatsEnum, HashMap<String, Double>>) map.get(AbilitySerialize.STATS_MODIFIER));

            int amount = (int) Double.parseDouble(getCustomModifier().get("amount").toString());
            long cooldown = Math.round(getModifier().get(AbilityModifier.COOLDOWN));
            double damage = Double.parseDouble(getModifier().get(AbilityModifier.RESULT).toString());
            HashMap<String, Double> damageList = getStatsModifier().get(StatsEnum.PHYSICAL_DAMAGE);
            double statsDamage;
            if (damageList.containsKey(StatsEnum.PHYSICAL_DAMAGE.getResult())){
                statsDamage = damageList.get(StatsEnum.PHYSICAL_DAMAGE.getResult());
            }else{
                statsDamage = DataConverter.randomDouble(damageList.get(StatsEnum.PHYSICAL_DAMAGE.getMin()), damageList.get(StatsEnum.PHYSICAL_DAMAGE.getMax()));
            }

            for (int i = 0; i < amount; i++) {
                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Location eye = target.getLocation();
                        Vector dir = eye.getDirection();
                        dir.multiply(-1 * 1.2);
                        target.damage(statsDamage * damage / 100, executor);
                        target.getWorld().playSound(target.getLocation(), Sound.ENCHANT_THORNS_HIT, 1, 1);
                        target.setVelocity(dir);
                    }
                }, 15 * i);
            }
            startCooldown(executor, cooldown);
        }
    }

    @Override
    public void loadDefaultData() {
        setModifier(AbilityModifier.CHANCE, 25);
        setModifier(AbilityModifier.COOLDOWN, 200);
        setModifier(AbilityModifier.MINIMUM, 100);
        setModifier(AbilityModifier.MAXIMUM, 135);
        setCustomModifier("amount", Integer.class, 3);
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
            slash((LivingEntity) attacker, victim, factory);
        }else if (getTriggerType().equals(TriggerType.DAMAGE_TAKEN)){
            slash((LivingEntity) victim, attacker, factory);
        }
    }

    @Override
    public void playerClick(PlayerInteractEvent event, MonoFactory factory) {

    }

    @Override
    public void playerSneak(PlayerToggleSneakEvent event, MonoFactory factory) {

    }
}
