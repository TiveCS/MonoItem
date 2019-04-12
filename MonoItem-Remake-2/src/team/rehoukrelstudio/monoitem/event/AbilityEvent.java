package team.rehoukrelstudio.monoitem.event;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import team.rehoukrelstudio.monoitem.api.MonoFactory;
import team.rehoukrelstudio.monoitem.api.ability.Ability;
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
        List<ItemStack> ai = attacker != null ? getCompleteEquipment(attacker) : new ArrayList<>(),
                vi = victim != null ? getCompleteEquipment(victim) : new ArrayList<>();

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
