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
        return super.finishUsingItem(createFilledResult(pStack,(Player) pEntityLiving,new ItemStack(Items.BOWL),true), pLevel, pEntityLiving);
    }

    protected ItemStack fill(ItemStack pStack, Player pPlayer, ItemStack pFilledStack) {
        return createFilledResult(pStack, pPlayer, pFilledStack,true);
    }

    public static ItemStack createFilledResult(ItemStack pEmptyStack, Player pPlayer, ItemStack pFilledStack, boolean pPreventDuplicates) {
        boolean flag = pPlayer.getAbilities().instabuild;
        if (pPreventDuplicates && flag) {
            if (!pPlayer.getInventory().contains(pFilledStack)) {
                pPlayer.getInventory().add(pFilledStack);
            }

            return pEmptyStack;
        } else {
            if (pEmptyStack.isEmpty()) {
                return pFilledStack;
            } else {
                if (!pPlayer.getInventory().add(pFilledStack)) {
                    pPlayer.drop(pFilledStack, false);
                }

                return pEmptyStack;
            }
        }
    }
}
