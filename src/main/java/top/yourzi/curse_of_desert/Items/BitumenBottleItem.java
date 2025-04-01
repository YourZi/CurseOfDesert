package top.yourzi.curse_of_desert.Items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;

public class BitumenBottleItem extends Item {

    public BitumenBottleItem(Properties properties) {
        super(new Properties()
           .craftRemainder(Items.GLASS_BOTTLE)
        );
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }
}