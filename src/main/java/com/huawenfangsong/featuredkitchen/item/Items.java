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
    public static RegistryObject<Item> CARROT_OF_UNDYING_ITEM;
    public static RegistryObject<Item> GIGAPPLE;

    public static RegistryObject<Item> SEA_STEW;
    public static void registry() {
        CARROT_OF_UNDYING_ITEM = ITEMS.register("carrot_of_undying",
                () -> new Item(new Item.Properties().food(Foods.GOLDEN_CARROT)));
        GIGAPPLE = ITEMS.register("gigapple",
                () -> new GigApple(new Item.Properties().rarity(Rarity.RARE).food((new FoodProperties.Builder()).nutrition(6).saturationMod(1.2F)
                        .effect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1), 1.0F)
                        .effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0), 1.0F)
                        .effect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0), 1.0F)
                        .effect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 3), 1.0F)
                        .alwaysEat().build())));
        SEA_STEW =ITEMS.register("sea_stew",
                () -> new BowlsFood(new Item.Properties().food(Foods.GOLDEN_CARROT).stacksTo(16)));
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());

    }

}
