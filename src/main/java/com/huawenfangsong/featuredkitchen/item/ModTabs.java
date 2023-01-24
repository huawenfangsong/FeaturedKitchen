package com.huawenfangsong.featuredkitchen.item;

import com.huawenfangsong.featuredkitchen.FeaturedKitchen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModTabs {
    public static CreativeModeTab tab;
    @SubscribeEvent
    public static void register(CreativeModeTabEvent.Register event){
        tab = event.registerCreativeModeTab(new ResourceLocation(FeaturedKitchen.MODID, FeaturedKitchen.MODID), builder -> builder
                .icon(()->new ItemStack(Items.PLANT_MAN.get()))
                .title(Component.translatable("itemGroup."+ FeaturedKitchen.MODID))
                .displayItems((featureFlags,output,hasOp)->{
                    output.accept(Items.PLANT_MAN.get());
                    output.accept(Items.DRIPLEAF_ROLLS.get());
                    output.accept(Items.GIGAPPLE.get());
                    output.accept(Items.NETHER_WART_SOUP.get());
                    output.accept(Items.SEA_STEW.get());
                    output.accept(Items.MOSS_STEW.get());
                    output.accept(Items.FUNGUS_STEW.get());
                }));
    }
}
