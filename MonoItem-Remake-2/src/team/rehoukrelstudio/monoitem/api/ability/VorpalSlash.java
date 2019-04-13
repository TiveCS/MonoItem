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

public class VorpalSlash extends Ability {

    public VorpalSlash() {
        super("VORPAL_SLASH", "Vorpal Slash", Material.SHEARS);
    }

    @Override
    public void loadDefaultData() {

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
