package com.huawenfangsong.featuredkitchen;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Locale;

public class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, FeaturedKitchen.MODID);
    public static RegistryObject<SoundEvent> PLAYER_EAT_GIGAPPLE = SOUNDS.register("entity.player.eat_gigapple", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(FeaturedKitchen.MODID,"entity.player.eat_gigapple")));
    public static void registry(){
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
