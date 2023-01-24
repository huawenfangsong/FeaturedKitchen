package com.huawenfangsong.featuredkitchen.datagen;

import com.huawenfangsong.featuredkitchen.FeaturedKitchen;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Lang {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        // Data Generator 启动时，该方法便会调用，新添加的 DataProvider 也开始工作
        var gen = event.getGenerator();
        gen.addProvider(true ,new EnglishLanguageProvider(gen));
        gen.addProvider(true, new ChineseLanguageProvider(gen));
    }


    public static class EnglishLanguageProvider extends LanguageProvider {
        public EnglishLanguageProvider(DataGenerator gen) {
            // 前三个参数分别是 Data Generator 本身，模组 ID，以及语言代码
            super(gen.getPackOutput(), FeaturedKitchen.MODID, "en_us");
        }

        @Override
        protected void addTranslations() {
            this.add("advancements.kitchen.root.title","Kitchen");
            this.add("advancements.kitchen.root.description","These foods don't taste good");
            this.add("advancements.kitchen.it_live.title","It is alive!");
            this.add("advancements.kitchen.it_live.description","\"Kill\" one plant man");
            this.add("advancements.kitchen.sea_stew.title","The taste of the sea！");
            this.add("advancements.kitchen.sea_stew.description","Drink a bowl of Sea Stew");
            this.add("itemGroup.featured_kitchen", "Featured Kitchen");
            this.add("item.featured_kitchen.plant_man","Plant Man");
            this.add("item.featured_kitchen.dripleaf_rolls","Dripleaf_Rolls");
            this.add("item.featured_kitchen.gigapple","Gigapple");
            this.add("item.featured_kitchen.nether_wart_soup","Nether Wart Soup");
            this.add("item.featured_kitchen.sea_stew","Sea Stew");
            this.add("item.featured_kitchen.moss_stew","Moss Stew");
            this.add("item.featured_kitchen.fungus_stew","Fungus Stew");
        }
    }


    public static class ChineseLanguageProvider extends LanguageProvider {
        public ChineseLanguageProvider(DataGenerator gen) {
            // 前三个参数分别是 Data Generator 本身，模组 ID，以及语言代码
            super(gen.getPackOutput(), FeaturedKitchen.MODID, "zh_cn");
        }

        @Override
        protected void addTranslations() {
            this.add("advancements.kitchen.root.title","厨房");
            this.add("advancements.kitchen.root.description","那些食物吃起来不太妙");
            this.add("advancements.kitchen.it_live.title","它是活的！");
            this.add("advancements.kitchen.it_live.description","“杀害”一个植物人");
            this.add("advancements.kitchen.sea_stew.title","海的味道！");
            this.add("advancements.kitchen.sea_stew.description","喝下一碗海洋煲");
            this.add("itemGroup.featured_kitchen", "特色厨房");
            this.add("item.featured_kitchen.plant_man","植物人");
            this.add("item.featured_kitchen.dripleaf_rolls","垂滴叶卷");
            this.add("item.featured_kitchen.gigapple","Gigapple");
            this.add("item.featured_kitchen.nether_wart_soup","地狱疙瘩汤");
            this.add("item.featured_kitchen.sea_stew","海洋煲");
            this.add("item.featured_kitchen.moss_stew","苔藓煲");
            this.add("item.featured_kitchen.fungus_stew","真菌煲");
        }
    }
}
