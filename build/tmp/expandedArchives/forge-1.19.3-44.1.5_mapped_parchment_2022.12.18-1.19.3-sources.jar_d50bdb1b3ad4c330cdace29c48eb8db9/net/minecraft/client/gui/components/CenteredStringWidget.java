package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CenteredStringWidget extends AbstractWidget {
   private int color = 16777215;
   private final Font font;

   public CenteredStringWidget(Component pMessage, Font pFont) {
      this(0, 0, pFont.width(pMessage.getVisualOrderText()), 9, pMessage, pFont);
   }

   public CenteredStringWidget(int pWidth, int pHeight, Component pMessage, Font pFont) {
      this(0, 0, pWidth, pHeight, pMessage, pFont);
   }

   public CenteredStringWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, Font pFont) {
      super(pX, pY, pWidth, pHeight, pMessage);
      this.font = pFont;
      this.active = false;
   }

   public CenteredStringWidget color(int pColor) {
      this.color = pColor;
      return this;
   }

   public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
   }

   public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
      drawCenteredString(pPoseStack, this.font, this.getMessage(), this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 9) / 2, this.color);
   }
}