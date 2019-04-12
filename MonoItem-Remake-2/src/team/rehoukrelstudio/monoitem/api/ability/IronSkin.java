package team.rehoukrelstudio.monoitem.api.ability;

import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import team.rehoukrelstudio.monoitem.api.MonoFactory;

import java.util.HashMap;

public class IronSkin extends Ability {
    public IronSkin() {
        super("IRONSKIN", "Iron Skin", Material.IRON_CHESTPLATE);
    }

    @Override
    public void loadDefaultData() {
        setTriggerType(TriggerType.DAMAGE_TAKEN);
        setModifier(AbilityModifier.COOLDOWN, 100);
        setModifier(AbilityModifier.CHANCE, 25);
        setModifier(AbilityModifier.MINIMUM, 0.5);
        setModifier(AbilityModifier.MAXIMUM, 2.25);
        setCustomModifier("knockbackpower", Double.class, 1);
        setCustomModifier("damagedecrease", Double.class, 32.5);
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
        if (getTriggerType().equals(TriggerType.DAMAGE_TAKEN)){
            HashMap<AbilitySerialize, Object> map = getAbilitySerialize(factory);
            setCustomModifier((HashMap<String, Object>) map.get(AbilitySerialize.CUSTOM_MODIFIER));
            setModifier((HashMap<AbilityModifier, Double>) map.get(AbilitySerialize.DEFAULT_MODIFIER));
        }
    }

    @Override
    public void playerClick(PlayerInteractEvent event, MonoFactory factory) {

    }

    @Override
    public void playerSneak(PlayerToggleSneakEvent event, MonoFactory factory) {

    }
}
