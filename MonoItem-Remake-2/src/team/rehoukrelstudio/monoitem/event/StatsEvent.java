package team.rehoukrelstudio.monoitem.event;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import team.rehoukrelstudio.monoitem.MonoItem;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import team.rehoukrelstudio.monoitem.api.OptionEnum;
import team.rehoukrelstudio.monoitem.api.StatsEnum;
import utils.DataConverter;

import java.util.ArrayList;
import java.util.List;

public class StatsEvent implements Listener {

    private MonoItem plugin = MonoItem.getPlugin(MonoItem.class);

    public List<ItemStack> getCompleteEquipment(LivingEntity entity){
        List<ItemStack> items = new ArrayList<ItemStack>();
        if (!(entity.getEquipment().getItemInMainHand().getType().equals(Material.AIR))){
            items.add(entity.getEquipment().getItemInMainHand());
        }
        if (!(entity.getEquipment().getItemInOffHand().getType().equals(Material.AIR))){
            items.add(entity.getEquipment().getItemInOffHand());
        }

        if (entity.getEquipment().getHelmet() != null){
            items.add(entity.getEquipment().getHelmet());
        }
        if (entity.getEquipment().getChestplate() != null){
            items.add(entity.getEquipment().getChestplate());
        }
        if (entity.getEquipment().getLeggings() != null){
            items.add(entity.getEquipment().getLeggings());
        }
        if (entity.getEquipment().getBoots() != null){
            items.add(entity.getEquipment().getBoots());
        }
        return items;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){

        LivingEntity attacker = null, victim = null;
        if (event.getDamager() instanceof LivingEntity){
            attacker = (LivingEntity) event.getDamager();
        }else if (event.getDamager() instanceof Projectile){
            attacker = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
        }
        if (event.getEntity() instanceof LivingEntity){
            victim = (LivingEntity) event.getEntity();
        }
        List<ItemStack> ai = attacker != null ? getCompleteEquipment(attacker) : new ArrayList<>()
                , vi = victim != null ? getCompleteEquipment(victim) : new ArrayList<>();

        double physicalDamage = 0, magicDamage = 0, physicalArmor = 0, magicArmor = 0;
        double critRate = plugin.getConfig().getInt("base-stats-modifier.critical_rate"), critDamage = plugin.getConfig().getInt("base-stats-modifier.critical_damage"),
                blockRate = plugin.getConfig().getInt("base-stats-modifier.block_rate"), blockAmount = plugin.getConfig().getInt("base-stats-modifier.block_amount");
        double totalDamage = 0, totalDefense = 0;
        int cooldown = plugin.getConfig().getInt("base-stats-modifier.attack_speed");
        boolean useMonoItem = false, useMonoItemDefense = false;

        Location loc = attacker.getLocation(), eye = attacker.getEyeLocation();
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
            for (ItemStack item : ai) {
                MonoFactory factory = new MonoFactory(item);

                if (factory.getNbtManager().hasNbt(OptionEnum.DISABLE_ON_LAND.getState())){
                    if (factory.getNbtManager().getBoolean(OptionEnum.DISABLE_ON_LAND.getState())){
                        if (!loc.getBlock().isLiquid() && !eye.getBlock().isLiquid()) {
                            continue;
                        }
                    }
                }
                if (factory.getNbtManager().hasNbt(OptionEnum.DISABLE_ON_WATER.getState())){
                    if (factory.getNbtManager().getBoolean(OptionEnum.DISABLE_ON_WATER.getState())){
                        if (loc.getBlock().isLiquid() && eye.getBlock().isLiquid()) {
                            continue;
                        }
                    }
                }

                if (useMonoItem == false) {
                    if (factory.hasStats(StatsEnum.PHYSICAL_DAMAGE) || factory.hasStats(StatsEnum.MAGICAL_DAMAGE)) {
                        useMonoItem = true;
                    }
                }

                physicalDamage += factory.getStatsResult(StatsEnum.PHYSICAL_DAMAGE);
                magicDamage += factory.getStatsResult(StatsEnum.MAGICAL_DAMAGE);
                critRate += factory.getStatsResult(StatsEnum.CRITICAL_RATE);
                critDamage += factory.getStatsResult(StatsEnum.CRITICAL_DAMAGE);
            }

            for (ItemStack item : vi) {
                MonoFactory factory = new MonoFactory(item);
                if (useMonoItemDefense == false) {
                    if (factory.hasStats(StatsEnum.PHYSICAL_DEFENSE) || factory.hasStats(StatsEnum.MAGICAL_DEFENSE)) {
                        useMonoItemDefense = true;
                    }
                }

                physicalArmor += factory.getStatsResult(StatsEnum.PHYSICAL_DEFENSE);
                magicArmor += factory.getStatsResult(StatsEnum.MAGICAL_DEFENSE);
                blockAmount += factory.getStatsResult(StatsEnum.BLOCK_AMOUNT);
                blockRate += factory.getStatsResult(StatsEnum.BLOCK_RATE);
            }

            if (useMonoItem) {
                totalDamage = physicalDamage + magicDamage;
                totalDamage = DataConverter.randomDouble((75 + DataConverter.randomDouble(5, 15)) / 100 * totalDamage, (125 - DataConverter.randomDouble(5, 15)) / 100 * totalDamage);
                event.setDamage(totalDamage);
                if (event.getDamage() < 0){event.setDamage(0);}
                if (attacker instanceof Player){
                    Player p = (Player) attacker;
                    if (!(p.getInventory().getItemInMainHand().getType().equals(Material.AIR))) {
                        if (p.hasCooldown(p.getInventory().getItemInMainHand().getType())){
                            event.setDamage(0);
                        }else {
                            p.setCooldown(p.getInventory().getItemInMainHand().getType(), cooldown);
                        }
                    }
                }
            }

            if (DataConverter.chance(critRate)) {
                event.setDamage(event.getDamage() + event.getDamage() * critDamage / 100);
                victim.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, victim.getLocation(), 1);
                victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            }

            if (DataConverter.chance(blockRate)) {
                event.setDamage(event.getDamage() - event.getDamage() * blockAmount / 100);
                victim.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, victim.getLocation(), 5, 0,0,0, 0.5);
                victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1, 1);
            }

            if (useMonoItemDefense) {
                totalDefense = magicArmor + physicalArmor;
                totalDefense = DataConverter.randomDouble((75 + DataConverter.randomDouble(5, 15)) / 100 * totalDefense, (115 - DataConverter.randomDouble(5, 15)) / 100 * totalDefense);
                if (event.getDamage() < totalDefense) {
                    event.setDamage(0);
                } else {
                    event.setDamage(event.getDamage() - totalDefense);
                }
            }

        }
    }

}
