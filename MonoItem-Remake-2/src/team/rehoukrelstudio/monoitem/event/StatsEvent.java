package team.rehoukrelstudio.monoitem.event;

import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import team.rehoukrelstudio.monoitem.MonoItem;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import team.rehoukrelstudio.monoitem.api.ability.Ability;
import team.rehoukrelstudio.monoitem.api.fixed.OptionEnum;
import team.rehoukrelstudio.monoitem.api.fixed.StatsEnum;
import utils.DataConverter;

import java.util.ArrayList;
import java.util.List;

public class StatsEvent implements Listener {

    private MonoItem plugin = MonoItem.getPlugin(MonoItem.class);

    public List<ItemStack> getArmor(LivingEntity entity){
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack item : entity.getEquipment().getArmorContents()){
            if (item != null){
                list.add(item);
            }
        }
        return list;
    }

    public List<ItemStack> getHandEquipment(LivingEntity entity){
        EntityEquipment equip = entity.getEquipment();
        List<ItemStack> list = new ArrayList<>();
        if (!(equip.getItemInMainHand().getType().equals(Material.AIR))){
            list.add(equip.getItemInMainHand());
        }

        if (!(equip.getItemInOffHand().getType().equals(Material.AIR))){
            list.add(equip.getItemInOffHand());
        }
        return list;
    }

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
    public void onSwing(PlayerAnimationEvent event){
        if (!event.isCancelled()){
            Player p = event.getPlayer();
            if (event.getAnimationType().equals(PlayerAnimationType.ARM_SWING)){
                MonoFactory f = new MonoFactory(p.getInventory().getItemInMainHand());
                if (f.getNbtManager().hasNbt("MONOITEM")) {
                    if (p.hasCooldown(p.getInventory().getItemInMainHand().getType())) {
                        event.setCancelled(true);
                        return;
                    } else {
                        int cooldown = plugin.getConfig().getInt("base-stats-modifier.attack_speed");
                        List<ItemStack> map = getCompleteEquipment(p);
                        Location eye = p.getEyeLocation();
                        for (ItemStack item : map) {
                            MonoFactory factory = new MonoFactory(item);
                            if (factory.hasOption(OptionEnum.UNIDENTIFIED)){
                                event.setCancelled(true);
                                return;
                            }
                            if (factory.hasOption(OptionEnum.DISABLE_ON_WATER)){
                                if (Boolean.parseBoolean(factory.getOption(OptionEnum.DISABLE_ON_WATER).toString())){
                                    if (eye.getBlock().isLiquid()) {
                                        event.setCancelled(true);
                                        return;
                                    }
                                }
                            }
                            if (factory.hasOption(OptionEnum.DISABLE_ON_LAND)){
                                if (Boolean.parseBoolean(factory.getOption(OptionEnum.DISABLE_ON_LAND).toString())){
                                    if (!(eye.getBlock().isLiquid())) {
                                        event.setCancelled(true);
                                        return;
                                    }
                                }
                            }
                            if (factory.hasStats(StatsEnum.ATTACK_SPEED)) {
                                if (item.equals(p.getInventory().getItemInOffHand())){
                                    cooldown += (-1 * Math.round(factory.getStatsResult(StatsEnum.ATTACK_SPEED) / 4));
                                }else {
                                    cooldown += (-1 * factory.getStatsResult(StatsEnum.ATTACK_SPEED));
                                }
                            }
                        }
                        p.setCooldown(p.getInventory().getItemInMainHand().getType(), cooldown);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event){
        if (!event.isCancelled()){
            if (event.getEntity() instanceof Player) {
                Player p = (Player) event.getEntity();
                if (p.hasCooldown(p.getInventory().getItemInMainHand().getType())) {
                    event.setCancelled(true);
                    return;
                } else {
                    Location eye = p.getEyeLocation();
                    MonoFactory f = new MonoFactory(event.getBow());
                    if (f.getNbtManager().hasNbt("MONOITEM")) {
                        if (f.hasOption(OptionEnum.UNIDENTIFIED)){
                            event.setCancelled(true);
                            return;
                        }
                        if (f.hasOption(OptionEnum.DISABLE_ON_WATER)){
                            if (Boolean.parseBoolean(f.getOption(OptionEnum.DISABLE_ON_WATER).toString())) {
                                if (eye.getBlock().isLiquid()) {
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                        }
                        if (f.hasOption(OptionEnum.DISABLE_ON_LAND)) {
                            if (Boolean.parseBoolean(f.getOption(OptionEnum.DISABLE_ON_LAND).toString())) {
                                if (!(eye.getBlock().isLiquid())) {
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                        }

                        int cooldown = plugin.getConfig().getInt("base-stats-modifier.attack_speed");
                        List<ItemStack> map = getCompleteEquipment(p);

                        for (ItemStack item : map) {
                            MonoFactory factory = new MonoFactory(item);
                            if (factory.hasStats(StatsEnum.ATTACK_SPEED)) {
                                if (item.equals(p.getInventory().getItemInOffHand())) {
                                    cooldown += (-1 * Math.round(factory.getStatsResult(StatsEnum.ATTACK_SPEED) / 4));
                                } else {
                                    cooldown += (-1 * factory.getStatsResult(StatsEnum.ATTACK_SPEED));
                                }
                            }
                        }
                        p.setCooldown(event.getBow().getType(), cooldown);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){

        if (event.isCancelled()){
            return;
        }

        LivingEntity attacker = null, victim = null;
        if (event.getDamager() instanceof LivingEntity){
            attacker = (LivingEntity) event.getDamager();
        }else if (event.getDamager() instanceof Projectile){
            attacker = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
        }
        if (event.getEntity() instanceof LivingEntity){
            victim = (LivingEntity) event.getEntity();
        }
        if (attacker instanceof Player){
            if (!event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                Player p = (Player) attacker;
                if (!(p.getInventory().getItemInMainHand().getType().equals(Material.AIR))) {
                    if (p.hasCooldown(p.getInventory().getItemInMainHand().getType())) {
                        event.setDamage(0);
                        return;
                    }
                }
            }
        }

        List<ItemStack> ai = attacker != null ? getCompleteEquipment(attacker) : new ArrayList<>()
                , vi = victim != null ? getCompleteEquipment(victim) : new ArrayList<>();

        double physicalDamage = 0, magicDamage = 0, physicalArmor = 0, magicArmor = 0;
        double critRate = plugin.getConfig().getInt("base-stats-modifier.critical_rate"), critDamage = plugin.getConfig().getInt("base-stats-modifier.critical_damage"),
                blockRate = plugin.getConfig().getInt("base-stats-modifier.block_rate"), blockAmount = plugin.getConfig().getInt("base-stats-modifier.block_amount");
        double totalDamage = 0, totalDefense = 0;
        boolean useMonoItem = false, useMonoItemDefense = false;
        double offhandRemedy = plugin.getConfig().getDouble("settings.offhand-remedy");
        double blockPenetration = 0, critResist = 0;
        double incDamage = 0, decDamage = 0;
        double dodge = 0;

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
            for (ItemStack item : ai) {
                MonoFactory factory = new MonoFactory(item);
                if (item.getType().equals(Material.AIR)){
                    continue;
                }
                if (factory.hasOption(OptionEnum.UNIDENTIFIED)){
                    event.setCancelled(true);
                    return;
                }

                if (factory.hasOption(OptionEnum.RANGED_ONLY)) {
                    if (factory.getOption(OptionEnum.RANGED_ONLY).toString().equalsIgnoreCase("true")) {
                        if (!event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                            continue;
                        }
                    }
                }
                if (factory.getNbtManager().hasNbt(OptionEnum.DISABLE_ON_LAND.getState())){
                    if (factory.getNbtManager().getBoolean(OptionEnum.DISABLE_ON_LAND.getState())){
                        Location loc = attacker.getLocation(), eye = attacker.getEyeLocation();
                        if (!loc.getBlock().isLiquid() && !eye.getBlock().isLiquid()) {
                            continue;
                        }
                    }
                }
                if (factory.getNbtManager().hasNbt(OptionEnum.DISABLE_ON_WATER.getState())){
                    if (factory.getNbtManager().getBoolean(OptionEnum.DISABLE_ON_WATER.getState())){
                        Location loc = attacker.getLocation(), eye = attacker.getEyeLocation();
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

                if (item.equals(attacker.getEquipment().getItemInOffHand())){
                    incDamage += factory.getStatsResult(StatsEnum.INCREASE_DAMAGE)*offhandRemedy;
                    physicalDamage += factory.getStatsResult(StatsEnum.PHYSICAL_DAMAGE)*offhandRemedy;
                    magicDamage += factory.getStatsResult(StatsEnum.MAGICAL_DAMAGE)*offhandRemedy;
                    critRate += factory.getStatsResult(StatsEnum.CRITICAL_RATE)*offhandRemedy;
                    critDamage += factory.getStatsResult(StatsEnum.CRITICAL_DAMAGE)*offhandRemedy;
                    blockPenetration += factory.getStatsResult(StatsEnum.BLOCK_PENETRATION)*offhandRemedy;
                }else {
                    blockPenetration += factory.getStatsResult(StatsEnum.BLOCK_PENETRATION);
                    incDamage += factory.getStatsResult(StatsEnum.INCREASE_DAMAGE);
                    physicalDamage += factory.getStatsResult(StatsEnum.PHYSICAL_DAMAGE);
                    magicDamage += factory.getStatsResult(StatsEnum.MAGICAL_DAMAGE);
                    critRate += factory.getStatsResult(StatsEnum.CRITICAL_RATE);
                    critDamage += factory.getStatsResult(StatsEnum.CRITICAL_DAMAGE);
                }
            }

            for (ItemStack item : vi) {
                if (item.getType().equals(Material.AIR)){
                    continue;
                }
                MonoFactory factory = new MonoFactory(item);
                if (factory.hasOption(OptionEnum.UNIDENTIFIED)){
                    continue;
                }
                if (useMonoItemDefense == false) {
                    if (factory.hasStats(StatsEnum.PHYSICAL_DEFENSE) || factory.hasStats(StatsEnum.MAGICAL_DEFENSE)) {
                        useMonoItemDefense = true;
                    }
                }
                if (item.equals(attacker.getEquipment().getItemInOffHand())){
                    decDamage += factory.getStatsResult(StatsEnum.DECREASE_DAMAGE)*offhandRemedy;
                    physicalArmor += factory.getStatsResult(StatsEnum.PHYSICAL_DEFENSE)*offhandRemedy;
                    magicArmor += factory.getStatsResult(StatsEnum.MAGICAL_DEFENSE)*offhandRemedy;
                    blockAmount += factory.getStatsResult(StatsEnum.BLOCK_AMOUNT)*offhandRemedy;
                    blockRate += factory.getStatsResult(StatsEnum.BLOCK_RATE)*offhandRemedy;
                    dodge += factory.getStatsResult(StatsEnum.DODGE)*offhandRemedy;
                    critResist += factory.getStatsResult(StatsEnum.CRITICAL_RESISTANCE)*offhandRemedy;
                }else {
                    critResist += factory.getStatsResult(StatsEnum.CRITICAL_RESISTANCE);
                    decDamage += factory.getStatsResult(StatsEnum.DECREASE_DAMAGE);
                    dodge += factory.getStatsResult(StatsEnum.DODGE);
                    physicalArmor += factory.getStatsResult(StatsEnum.PHYSICAL_DEFENSE);
                    magicArmor += factory.getStatsResult(StatsEnum.MAGICAL_DEFENSE);
                    blockAmount += factory.getStatsResult(StatsEnum.BLOCK_AMOUNT);
                    blockRate += factory.getStatsResult(StatsEnum.BLOCK_RATE);
                }
            }

            if (Ability.chance(dodge)){
                event.setDamage(0);
                event.setCancelled(true);
                return;
            }

            critRate -= critResist;
            blockRate -= blockPenetration;
            if (critRate < 0){
                critRate = 0;
            }
            if (blockRate < 0){
                blockRate = 0;
            }

            if (useMonoItem) {
                totalDamage = physicalDamage + magicDamage;
                totalDamage = DataConverter.randomDouble((75 + DataConverter.randomDouble(5, 15)) / 100 * totalDamage, (125 - DataConverter.randomDouble(5, 15)) / 100 * totalDamage);
                event.setDamage(totalDamage);
                event.setDamage(event.getDamage() + event.getDamage()*incDamage/100);
                if (event.getDamage() < 0){event.setDamage(0);}
            }

            if (DataConverter.chance(critRate)) {
                event.setDamage(event.getDamage() + event.getDamage() * critDamage / 100);
                victim.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, victim.getLocation(), 1);
                victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            }

            if (DataConverter.chance(blockRate)) {
                event.setDamage(event.getDamage() - event.getDamage() * blockAmount / 100);
                victim.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, victim.getLocation(), 2, 0,0,0, 0.2);
                victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1, 1);
            }

            if (useMonoItemDefense) {
                totalDefense = magicArmor + physicalArmor;
                totalDefense = DataConverter.randomDouble((75 + DataConverter.randomDouble(5, 15)) / 100 * totalDefense, (115 - DataConverter.randomDouble(5, 15)) / 100 * totalDefense);
                if (event.getDamage() < totalDefense) {
                    event.setDamage(0);
                } else {
                    event.setDamage(event.getDamage() - totalDefense);
                    event.setDamage(event.getDamage() - event.getDamage()*decDamage/100);
                }
            }

        }
    }

}
