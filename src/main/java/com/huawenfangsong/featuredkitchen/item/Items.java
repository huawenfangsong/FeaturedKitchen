package com.huawenfangsong.featuredkitchen.item;

import com.huawenfangsong.featuredkitchen.ModSoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.huawenfangsong.featuredkitchen.FeaturedKitchen.MODID;

public class Items {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static RegistryObject<Item> PLANT_MAN;
    public static RegistryObject<Item> DRIPLEAF_ROLLS;
    public static RegistryObject<Item> GIGAPPLE;
    public static RegistryObject<Item> NETHER_WART_SOUP;

    public static RegistryObject<Item> SEA_STEW;
    public static RegistryObject<Item> MOSS_STEW;
    public static RegistryObject<Item> FUNGUS_STEW;

    public static void registry() {
        PLANT_MAN = ITEMS.register("plant_man",
                () -> new Item(new Item.Properties().food(Foods.GOLDEN_CARROT)));
        DRIPLEAF_ROLLS = ITEMS.register("dripleaf_rolls",
                () -> new Item(new Item.Properties().food(Foods.CARROT)));

        GIGAPPLE = ITEMS.register("gigapple",
                () -> new GigApple(new Item.Properties().rarity(Rarity.EPIC).food((new FoodProperties.Builder()).nutrition(6).saturationMod(1.2F)
                        .effect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1), 1.0F)
                        .effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0), 1.0F)
                        .effect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0), 1.0F)
                        .effect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 3), 1.0F)
                        .alwaysEat().build())));
        NETHER_WART_SOUP =ITEMS.register("nether_wart_soup",
                () -> new BowlsFood(new Item.Properties().food((new FoodProperties.Builder()).nutrition(6).saturationMod(1.2F)
                        .effect(new MobEffectInstance(MobEffects.CONFUSION, 160, 0), 1.0F)
                        .build()).stacksTo(16)));

        SEA_STEW =ITEMS.register("sea_stew",
                () -> new BowlsFood(new Item.Properties().food(Foods.GOLDEN_CARROT).stacksTo(16)));
        MOSS_STEW =ITEMS.register("moss_stew",
                () -> new BowlsFood(new Item.Properties().food(Foods.CARROT).stacksTo(16)));
        FUNGUS_STEW =ITEMS.register("fungus_stew",
                () -> new BowlsFood(new Item.Properties().food(Foods.MUSHROOM_STEW).stacksTo(16)));

        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());

    }

}
