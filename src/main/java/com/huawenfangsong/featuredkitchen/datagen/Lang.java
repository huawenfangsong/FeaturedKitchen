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
            this.add("advancements.kitchen.it_live.title","It Is Alive!");
            this.add("advancements.kitchen.it_live.description","\"Kill\" one plant man");
            this.add("advancements.kitchen.sea_stew.title","The Taste of The Sea！");
            this.add("advancements.kitchen.sea_stew.description","Drink a bowl of Sea Stew");
            this.add("advancements.kitchen.fungus_stew.title","Food From Nether");
            this.add("advancements.kitchen.fungus_stew.description","Drink a bowl of Fungus Stew");
            this.add("advancements.kitchen.moss_stew.title","Delicious Moss");
            this.add("advancements.kitchen.moss_stew.description","Drink a bowl of Moss Stew");
            this.add("advancements.kitchen.nether_wart_soup.title","Don't want to try again");
            this.add("advancements.kitchen.nether_wart_soup.description","Drink a bowl of Wart Soup");
            this.add("advancements.kitchen.dripleaf_rolls.title","Is this Sushi?");
            this.add("advancements.kitchen.dripleaf_rolls.description","Eat a Dripleaf Rolls");
            this.add("advancements.kitchen.salt.title","The Veins are also Weeping");
            this.add("advancements.kitchen.salt.description","Extract salt from Weeping Veins");
            this.add("itemGroup.featured_kitchen", "Featured Kitchen");
            this.add("item.featured_kitchen.plant_man","Plant Man");
            this.add("item.featured_kitchen.dripleaf_rolls","Dripleaf_Rolls");
            this.add("item.featured_kitchen.gigapple","Gigapple");
            this.add("item.featured_kitchen.nether_wart_soup","Nether Wart Soup");
            this.add("item.featured_kitchen.sea_stew","Sea Stew");
            this.add("item.featured_kitchen.moss_stew","Moss Stew");
            this.add("item.featured_kitchen.fungus_stew","Fungus Stew");
            this.add("item.featured_kitchen.salt","Salt");
            this.add("item.featured_kitchen.hawrolls","Hawrolls");
            this.add("item.featured_kitchen.slime_jelly","Slime Jelly");
            this.add("item.featured_kitchen.sugar_cane_soup","Super Cane Soup");
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
            this.add("advancements.kitchen.fungus_stew.title","来自下界的食物");
            this.add("advancements.kitchen.fungus_stew.description","喝下一碗真菌煲");
            this.add("advancements.kitchen.moss_stew.title","美味的苔藓");
            this.add("advancements.kitchen.moss_stew.description","喝下一碗苔藓煲");
            this.add("advancements.kitchen.nether_wart_soup.title","我不想吃第二次");
            this.add("advancements.kitchen.nether_wart_soup.description","喝下一碗”地狱“疙瘩汤");
            this.add("advancements.kitchen.dripleaf_rolls.title","这是寿司吗？");
            this.add("advancements.kitchen.dripleaf_rolls.description","吃下一个垂滴叶卷");
            this.add("advancements.kitchen.salt.title","藤蔓也会哭泣");
            this.add("advancements.kitchen.salt.description","从垂泪藤中提取盐");
            this.add("itemGroup.featured_kitchen", "特色厨房");
            this.add("item.featured_kitchen.plant_man","植物人");
            this.add("item.featured_kitchen.dripleaf_rolls","垂滴叶卷");
            this.add("item.featured_kitchen.gigapple","Gigapple");
            this.add("item.featured_kitchen.nether_wart_soup","“地狱”疙瘩汤");
            this.add("item.featured_kitchen.sea_stew","海洋煲");
            this.add("item.featured_kitchen.moss_stew","苔藓煲");
            this.add("item.featured_kitchen.fungus_stew","真菌煲");
            this.add("item.featured_kitchen.salt","盐");
            this.add("item.featured_kitchen.hawrolls","果丹皮");
            this.add("item.featured_kitchen.slime_jelly","史莱姆果冻");
            this.add("item.featured_kitchen.sugar_cane_soup","甘蔗汤");
        }
    }
}
