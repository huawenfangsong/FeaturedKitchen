package com.huawenfangsong.featuredkitchen.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BowlsFood extends Item {
    public BowlsFood(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving) {
        if (!((Player)pEntityLiving).getAbilities().instabuild
            &&!((Player)pEntityLiving).getInventory().add(new ItemStack(Items.BOWL))) {
            ((Player)pEntityLiving).drop(new ItemStack(Items.BOWL), false);
        }
        return super.finishUsingItem(pStack, pLevel, pEntityLiving);
    }
}
