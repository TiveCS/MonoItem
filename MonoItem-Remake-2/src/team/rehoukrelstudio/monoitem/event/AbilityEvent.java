package team.rehoukrelstudio.monoitem.event;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import team.rehoukrelstudio.monoitem.api.ability.Ability;
import team.rehoukrelstudio.monoitem.api.ability.Leap;
import team.rehoukrelstudio.monoitem.nms.nbt.NBTManager;

import java.util.ArrayList;
import java.util.List;

public class AbilityEvent implements Listener {

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

    @EventHandler
    public void onDeath(EntityDeathEvent event){
        if (Ability.cooldownList.containsKey(event.getEntity())){
            Ability.cooldownList.remove(event.getEntity());
        }
    }

    @EventHandler
    public void projectileHit(ProjectileHitEvent event){
        Projectile projectile = event.getEntity();
        if (projectile.hasMetadata("removeOnHit")){
            projectile.remove();
        }
        if (projectile.getShooter() instanceof LivingEntity) {
            LivingEntity p = (LivingEntity) projectile.getShooter();
            if (p instanceof Player) {
                List<ItemStack> ai = p != null ? getCompleteEquipment(p) : new ArrayList<>();
                if (ai.isEmpty()) {
                    return;
                }
                for (ItemStack item : ai) {
                    MonoFactory factory = new MonoFactory(item);
                    NBTManager nbt = factory.getNbtManager();
                    for (Ability ability : Ability.abilities.values()) {
                        if (nbt.hasNbt(ability.getId() + ".TRIGGER_TYPE")) {
                            Ability.TriggerType type = Ability.TriggerType.valueOf(nbt.getString(ability.getId() + ".TRIGGER_TYPE"));
                            if (type.equals(Ability.TriggerType.PROJECTILE_HIT)) {
                                if (event.getHitEntity() != null) {
                                    ability.setTriggerType(type);
                                    ability.projectileHit(event, factory);
                                }
                            }else if (type.equals(Ability.TriggerType.PROJECTILE_HIT_BLOCK)) {
                                if (event.getHitBlock() != null) {
                                    ability.setTriggerType(type);
                                    ability.projectileHit(event, factory);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onAnyDamage(EntityDamageEvent event){
        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
            if (Leap.leap.contains(event.getEntity())){
                do{
                    Leap.leap.remove(event.getEntity());
                }while(Leap.leap.contains(event.getEntity()));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerSneak(PlayerToggleSneakEvent event){
        Player p = event.getPlayer();
        List<ItemStack> ai = p != null ? getCompleteEquipment(p) : new ArrayList<>();
        if (ai.isEmpty()){
            return;
        }
        for (ItemStack item : ai){
            MonoFactory factory = new MonoFactory(item);
            NBTManager nbt = factory.getNbtManager();
            for (Ability ability : Ability.abilities.values()){
                if (nbt.hasNbt(ability.getId() + ".TRIGGER_TYPE")){
                    Ability.TriggerType type = Ability.TriggerType.valueOf(nbt.getString(ability.getId() + ".TRIGGER_TYPE"));
                    if (type.equals(Ability.TriggerType.SNEAK)) {
                        ability.setTriggerType(type);
                        ability.playerSneak(event, factory);
                    }
                }
            }
        }
    }

    @EventHandler
    public void entityShoot(EntityShootBowEvent event){
        Projectile projectile = (Projectile) event.getProjectile();
        LivingEntity p = event.getEntity();
        if (p instanceof Player){
            List<ItemStack> ai = p != null ? getCompleteEquipment(p) : new ArrayList<>();
            if (ai.isEmpty()){
                return;
            }
            for (ItemStack item : ai){
                MonoFactory factory = new MonoFactory(item);
                NBTManager nbt = factory.getNbtManager();
                for (Ability ability : Ability.abilities.values()){
                    if (nbt.hasNbt(ability.getId() + ".TRIGGER_TYPE")){
                        Ability.TriggerType type = Ability.TriggerType.valueOf(nbt.getString(ability.getId() + ".TRIGGER_TYPE"));
                        if (type.equals(Ability.TriggerType.PROJECTILE_SHOOT)) {
                            ability.setTriggerType(type);
                            ability.entityShoot(event, p, factory);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void playerClick(PlayerInteractEvent event){
        Player p = event.getPlayer();
        List<ItemStack> ai = p != null ? getCompleteEquipment(p) : new ArrayList<>();
        if (ai.isEmpty()){
            return;
        }
        for (ItemStack item : ai){
            MonoFactory factory = new MonoFactory(item);
            NBTManager nbt = factory.getNbtManager();
            for (Ability ability : Ability.abilities.values()){
                if (nbt.hasNbt(ability.getId() + ".TRIGGER_TYPE")){
                    Ability.TriggerType type = Ability.TriggerType.valueOf(nbt.getString(ability.getId() + ".TRIGGER_TYPE"));
                    if (event.getAction().name().contains(type.name())) {
                        ability.setTriggerType(type);
                        ability.playerClick(event, factory);
                    }
                }
            }
        }
    }

    @EventHandler
    public void damage(EntityDamageByEntityEvent event){
        LivingEntity attacker = null, victim = null;
        if (event.getDamager() instanceof Projectile){
            attacker = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
        }
        if (event.getDamager() instanceof LivingEntity){
            attacker = (LivingEntity) event.getDamager();
        }
        if (event.getEntity() instanceof LivingEntity){
            victim = (LivingEntity) event.getEntity();
        }
        List<ItemStack> ai = attacker != null ? getHandEquipment(attacker) : new ArrayList<>(),
                vi = victim != null ? getArmor(victim) : new ArrayList<>();

        // Attacker
        for (ItemStack item : ai){
            MonoFactory factory = new MonoFactory(item);
            NBTManager nbt = factory.getNbtManager();
            for (Ability ability : Ability.abilities.values()){
                if (nbt.hasNbt(ability.getId() + ".TRIGGER_TYPE")){
                    Ability.TriggerType type = Ability.TriggerType.valueOf(nbt.getString(ability.getId() + ".TRIGGER_TYPE"));
                    if (type.equals(Ability.TriggerType.DAMAGE)) {
                        ability.setTriggerType(type);
                        ability.entityDamageByEntity(event, attacker, victim, factory);
                    }
                }
            }
        }

        // Victim
        for (ItemStack item : vi){
            MonoFactory factory = new MonoFactory(item);
            NBTManager nbt = factory.getNbtManager();
            for (Ability ability : Ability.abilities.values()){
                if (nbt.hasNbt(ability.getId() + ".TRIGGER_TYPE")){
                    Ability.TriggerType type = Ability.TriggerType.valueOf(nbt.getString(ability.getId() + ".TRIGGER_TYPE"));
                    if (type.equals(Ability.TriggerType.DAMAGE_TAKEN)) {
                        ability.setTriggerType(type);
                        ability.entityDamageByEntity(event, attacker, victim, factory);
                    }
                }
            }
        }
    }

}
