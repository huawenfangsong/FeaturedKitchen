package com.huawenfangsong.featuredkitchen.datagen.model;

import net.minecraftforge.data.event.GatherDataEvent;

public class Models {
    public static void onGatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var helper = event.getExistingFileHelper();
        // 添加模型文件的 DataProvider
        gen.addProvider(true, new ModelProvider(gen, helper));
//        gen.addProvider(new StateProvider(gen, helper));
    }
}
