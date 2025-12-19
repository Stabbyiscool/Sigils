package fun.stabosa.sigil.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EmptySyringeItem extends Item {

    public EmptySyringeItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient) {
            user.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.ITEM_HONEY_BOTTLE_DRINK, SoundCategory.PLAYERS, 1f, 1.2f);

            entity.damage(user.getDamageSources().generic(), 0f);

            // wip ItemStack blood = new ItemStack(ModItems.BLOOD_SYRINGE);

            if (!user.isCreative()) {
                stack.decrement(1);
            }

            //if (!user.getInventory().insertStack(blood)) {
            //    user.dropItem(blood, false);
            //}
        }

        return ActionResult.SUCCESS;
    }
}
