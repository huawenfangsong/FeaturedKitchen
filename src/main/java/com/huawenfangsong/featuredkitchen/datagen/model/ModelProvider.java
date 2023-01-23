package com.huawenfangsong.featuredkitchen.datagen.model;

import com.huawenfangsong.featuredkitchen.FeaturedKitchen;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModelProvider extends ItemModelProvider {
    public ModelProvider(DataGenerator gen, ExistingFileHelper helper) {
        super(gen.getPackOutput(), FeaturedKitchen.MODID, helper);
    }
    @Override
    protected void registerModels() {
        this.singleTexture("carrot_of_undying", new ResourceLocation("item/generated"), "layer0", new ResourceLocation(FeaturedKitchen.MODID, "item/" + "carrot_of_undying"));
        this.singleTexture("gigapple", new ResourceLocation("item/generated"), "layer0", new ResourceLocation(FeaturedKitchen.MODID, "item/" + "gigapple"));
        this.singleTexture("sea_stew", new ResourceLocation("item/generated"), "layer0", new ResourceLocation(FeaturedKitchen.MODID, "item/" + "sea_stew"));

    }
}
