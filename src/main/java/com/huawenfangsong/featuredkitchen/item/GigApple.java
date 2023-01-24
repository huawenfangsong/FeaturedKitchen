package com.huawenfangsong.featuredkitchen.item;

import com.huawenfangsong.featuredkitchen.ModSoundEvents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GigApple extends Item {
    public GigApple(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack p_41409_, Level p_41410_, LivingEntity p_41411_){
        p_41410_.playSound(p_41411_,p_41411_.getOnPos(), ModSoundEvents.PLAYER_EAT_GIGAPPLE.get(), SoundSource.MUSIC,1f,1f);
        if(p_41411_ instanceof Player){
            ((Player) p_41411_).getCooldowns().addCooldown(p_41409_.getItem(),140);
        }
        return super.finishUsingItem(p_41409_,p_41410_,p_41411_);
    };
}
