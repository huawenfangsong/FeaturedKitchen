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
        this.singleTexture("plant_man", new ResourceLocation("item/generated"), "layer0", new ResourceLocation(FeaturedKitchen.MODID, "item/" + "plant_man"));
        this.singleTexture("dripleaf_rolls", new ResourceLocation("item/generated"), "layer0", new ResourceLocation(FeaturedKitchen.MODID, "item/" + "dripleaf_rolls"));
        this.singleTexture("gigapple", new ResourceLocation("item/generated"), "layer0", new ResourceLocation(FeaturedKitchen.MODID, "item/" + "gigapple"));
        this.singleTexture("nether_wart_soup", new ResourceLocation("item/generated"), "layer0", new ResourceLocation(FeaturedKitchen.MODID, "item/" + "nether_wart_soup"));
        this.singleTexture("sea_stew", new ResourceLocation("item/generated"), "layer0", new ResourceLocation(FeaturedKitchen.MODID, "item/" + "sea_stew"));
        this.singleTexture("moss_stew", new ResourceLocation("item/generated"), "layer0", new ResourceLocation(FeaturedKitchen.MODID, "item/" + "moss_stew"));
        this.singleTexture("fungus_stew", new ResourceLocation("item/generated"), "layer0", new ResourceLocation(FeaturedKitchen.MODID, "item/" + "fungus_stew"));
    }
}
