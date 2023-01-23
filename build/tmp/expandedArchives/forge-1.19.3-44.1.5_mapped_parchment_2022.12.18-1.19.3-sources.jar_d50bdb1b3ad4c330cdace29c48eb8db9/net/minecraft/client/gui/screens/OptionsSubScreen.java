package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionsSubScreen extends Screen {
   protected final Screen lastScreen;
   protected final Options options;

   public OptionsSubScreen(Screen pLastScreen, Options pOptions, Component pTitle) {
      super(pTitle);
      this.lastScreen = pLastScreen;
      this.options = pOptions;
   }

   public void removed() {
      this.minecraft.options.save();
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   protected void basicListRender(PoseStack p_249017_, OptionsList p_250182_, int p_249819_, int p_251082_, float p_250584_) {
      this.renderBackground(p_249017_);
      p_250182_.render(p_249017_, p_249819_, p_251082_, p_250584_);
      drawCenteredString(p_249017_, this.font, this.title, this.width / 2, 20, 16777215);
      super.render(p_249017_, p_249819_, p_251082_, p_250584_);
   }
}