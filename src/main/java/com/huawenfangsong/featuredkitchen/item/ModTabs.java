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
                .icon(()->new ItemStack(Items.CARROT_OF_UNDYING_ITEM.get()))
                .title(Component.translatable("itemGroup."+ FeaturedKitchen.MODID))
                .displayItems((featureFlags,output,hasOp)->{
                    output.accept(Items.CARROT_OF_UNDYING_ITEM.get());
                    output.accept(Items.GIGAPPLE.get());
                    output.accept(Items.NETHER_WART_SOUP.get());
                    output.accept(Items.SEA_STEW.get());
                }));
    }
}
