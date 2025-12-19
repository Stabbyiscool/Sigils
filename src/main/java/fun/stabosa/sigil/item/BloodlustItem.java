package fun.stabosa.sigil.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class BloodlustItem extends SwordItem {

    public BloodlustItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(attacker instanceof PlayerEntity player)) return super.postHit(stack, target, attacker);
        World world = player.getWorld();
        if (player.getItemCooldownManager().isCoolingDown(this)) return super.postHit(stack, target, attacker);
        if (target instanceof EnderDragonEntity) return super.postHit(stack, target, attacker);

        boolean hasArmor =
                !target.getEquippedStack(EquipmentSlot.HEAD).isEmpty() ||
                !target.getEquippedStack(EquipmentSlot.CHEST).isEmpty() ||
                !target.getEquippedStack(EquipmentSlot.LEGS).isEmpty() ||
                !target.getEquippedStack(EquipmentSlot.FEET).isEmpty();

        if (hasArmor) return super.postHit(stack, target, attacker);

        float currentHealth = target.getHealth();
        float maxHealth = target.getMaxHealth();
        boolean killed = false;

        if (currentHealth > 0 && currentHealth <= maxHealth * 0.25f) {
            if (!world.isClient) {
                target.damage(world.getDamageSources().playerAttack(player), Float.MAX_VALUE);
                if (!target.isAlive()) {
                    player.getHungerManager().add(6, 0.4f);
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    killed = true;
                }
            } else {
                killed = true;
            }
        }

        if (killed) {
            int SwallowCooldown = (int) (100 + (target.getWidth() + target.getHeight()) * 100);
            player.getItemCooldownManager().set(this, SwallowCooldown);
            stack.getOrCreateNbt().putBoolean("cooling", true);
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof PlayerEntity player)) return;
        boolean wasCooling = stack.getOrCreateNbt().getBoolean("cooling");
        boolean isCooling = player.getItemCooldownManager().isCoolingDown(this);
        if (wasCooling && !isCooling) {
            stack.getOrCreateNbt().putBoolean("cooling", false);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }
}
