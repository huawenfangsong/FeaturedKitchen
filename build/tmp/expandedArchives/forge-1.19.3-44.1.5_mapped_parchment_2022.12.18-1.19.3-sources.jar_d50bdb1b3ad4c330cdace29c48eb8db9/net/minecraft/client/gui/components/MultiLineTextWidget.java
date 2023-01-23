package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MultiLineTextWidget extends AbstractWidget {
   private final MultiLineLabel multiLineLabel;
   private final int lineHeight;
   private final boolean centered;

   private MultiLineTextWidget(MultiLineLabel pMultiLineLabel, Font pFont, Component pMessage, boolean pCentered) {
      super(0, 0, pMultiLineLabel.getWidth(), pMultiLineLabel.getLineCount() * 9, pMessage);
      this.multiLineLabel = pMultiLineLabel;
      this.lineHeight = 9;
      this.centered = pCentered;
      this.active = false;
   }

   public static MultiLineTextWidget createCentered(int pMaxWidth, Font pFont, Component pMessage) {
      MultiLineLabel multilinelabel = MultiLineLabel.create(pFont, pMessage, pMaxWidth);
      return new MultiLineTextWidget(multilinelabel, pFont, pMessage, true);
   }

   public static MultiLineTextWidget create(int pMaxWidth, Font pFont, Component pMessage) {
      MultiLineLabel multilinelabel = MultiLineLabel.create(pFont, pMessage, pMaxWidth);
      return new MultiLineTextWidget(multilinelabel, pFont, pMessage, false);
   }

   protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
   }

   public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
      if (this.centered) {
         this.multiLineLabel.renderCentered(pPoseStack, this.getX() + this.getWidth() / 2, this.getY(), this.lineHeight, 16777215);
      } else {
         this.multiLineLabel.renderLeftAligned(pPoseStack, this.getX(), this.getY(), this.lineHeight, 16777215);
      }

   }
}