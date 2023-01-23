package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.BelowOrAboveWidgetTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractWidget extends GuiComponent implements Renderable, GuiEventListener, NarratableEntry {
   public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   protected int width;
   protected int height;
   private int x;
   private int y;
   private Component message;
   protected boolean isHovered;
   public boolean active = true;
   public boolean visible = true;
   protected float alpha = 1.0F;
   private boolean focused;
   @Nullable
   private Tooltip tooltip;
   private int tooltipMsDelay;
   private long hoverOrFocusedStartTime;
   private boolean wasHoveredOrFocused;

   public AbstractWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
      this.x = pX;
      this.y = pY;
      this.width = pWidth;
      this.height = pHeight;
      this.message = pMessage;
   }

   public int getHeight() {
      return this.height;
   }

   protected int getYImage(boolean pIsHovered) {
      int i = 1;
      if (!this.active) {
         i = 0;
      } else if (pIsHovered) {
         i = 2;
      }

      return i;
   }

   public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
      if (this.visible) {
         this.isHovered = pMouseX >= this.getX() && pMouseY >= this.getY() && pMouseX < this.getX() + this.width && pMouseY < this.getY() + this.height;
         this.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
         this.updateTooltip();
      }
   }

   private void updateTooltip() {
      if (this.tooltip != null) {
         boolean flag = this.isHoveredOrFocused();
         if (flag != this.wasHoveredOrFocused) {
            if (flag) {
               this.hoverOrFocusedStartTime = Util.getMillis();
            }

            this.wasHoveredOrFocused = flag;
         }

         if (flag && Util.getMillis() - this.hoverOrFocusedStartTime > (long)this.tooltipMsDelay) {
            Screen screen = Minecraft.getInstance().screen;
            if (screen != null) {
               screen.setTooltipForNextRenderPass(this.tooltip, this.createTooltipPositioner(), this.isFocused());
            }
         }

      }
   }

   protected ClientTooltipPositioner createTooltipPositioner() {
      return (ClientTooltipPositioner)(this.isFocused() ? new BelowOrAboveWidgetTooltipPositioner(this) : DefaultTooltipPositioner.INSTANCE);
   }

   public void setTooltip(@Nullable Tooltip pTooltip) {
      this.tooltip = pTooltip;
   }

   public void setTooltipDelay(int pTooltipMsDelay) {
      this.tooltipMsDelay = pTooltipMsDelay;
   }

   protected MutableComponent createNarrationMessage() {
      return wrapDefaultNarrationMessage(this.getMessage());
   }

   public static MutableComponent wrapDefaultNarrationMessage(Component pMessage) {
      return Component.translatable("gui.narrate.button", pMessage);
   }

   public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
      Minecraft minecraft = Minecraft.getInstance();
      Font font = minecraft.font;
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
      int i = this.getYImage(this.isHoveredOrFocused());
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.enableDepthTest();
      this.blit(pPoseStack, this.getX(), this.getY(), 0, 46 + i * 20, this.width / 2, this.height);
      this.blit(pPoseStack, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
      this.renderBg(pPoseStack, minecraft, pMouseX, pMouseY);
      int j = getFGColor();
      drawCenteredString(pPoseStack, font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
   }

   protected void renderBg(PoseStack pPoseStack, Minecraft pMinecraft, int pMouseX, int pMouseY) {
   }

   public void onClick(double pMouseX, double pMouseY) {
   }

   public void onRelease(double pMouseX, double pMouseY) {
   }

   protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      if (this.active && this.visible) {
         if (this.isValidClickButton(pButton)) {
            boolean flag = this.clicked(pMouseX, pMouseY);
            if (flag) {
               this.playDownSound(Minecraft.getInstance().getSoundManager());
               this.onClick(pMouseX, pMouseY);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
      if (this.isValidClickButton(pButton)) {
         this.onRelease(pMouseX, pMouseY);
         return true;
      } else {
         return false;
      }
   }

   protected boolean isValidClickButton(int pButton) {
      return pButton == 0;
   }

   public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
      if (this.isValidClickButton(pButton)) {
         this.onDrag(pMouseX, pMouseY, pDragX, pDragY);
         return true;
      } else {
         return false;
      }
   }

   protected boolean clicked(double pMouseX, double pMouseY) {
      return this.active && this.visible && pMouseX >= (double)this.getX() && pMouseY >= (double)this.getY() && pMouseX < (double)(this.getX() + this.width) && pMouseY < (double)(this.getY() + this.height);
   }

   public boolean isHoveredOrFocused() {
      return this.isHovered || this.focused;
   }

   public boolean changeFocus(boolean pFocus) {
      if (this.active && this.visible) {
         this.focused = !this.focused;
         this.onFocusedChanged(this.focused);
         return this.focused;
      } else {
         return false;
      }
   }

   protected void onFocusedChanged(boolean pFocused) {
   }

   public boolean isMouseOver(double pMouseX, double pMouseY) {
      return this.active && this.visible && pMouseX >= (double)this.getX() && pMouseY >= (double)this.getY() && pMouseX < (double)(this.getX() + this.width) && pMouseY < (double)(this.getY() + this.height);
   }

   public void playDownSound(SoundManager pHandler) {
      pHandler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int pWidth) {
      this.width = pWidth;
   }

   public void setHeight(int value) {
      this.height = value;
   }

   public void setAlpha(float pAlpha) {
      this.alpha = pAlpha;
   }

   public void setMessage(Component pMessage) {
      this.message = pMessage;
   }

   public Component getMessage() {
      return this.message;
   }

   public boolean isFocused() {
      return this.focused;
   }

   public boolean isActive() {
      return this.visible && this.active;
   }

   protected void setFocused(boolean pFocused) {
      this.focused = pFocused;
   }

   public static final int UNSET_FG_COLOR = -1;
   protected int packedFGColor = UNSET_FG_COLOR;
   public int getFGColor() {
      if (packedFGColor != UNSET_FG_COLOR) return packedFGColor;
      return this.active ? 16777215 : 10526880; // White : Light Grey
   }
   public void setFGColor(int color) {
      this.packedFGColor = color;
   }
   public void clearFGColor() {
      this.packedFGColor = UNSET_FG_COLOR;
   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      if (this.focused) {
         return NarratableEntry.NarrationPriority.FOCUSED;
      } else {
         return this.isHovered ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
      }
   }

   public final void updateNarration(NarrationElementOutput pNarrationElementOutput) {
      this.updateWidgetNarration(pNarrationElementOutput);
      if (this.tooltip != null) {
         this.tooltip.updateNarration(pNarrationElementOutput);
      }

   }

   protected abstract void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput);

   protected void defaultButtonNarrationText(NarrationElementOutput pNarrationElementOutput) {
      pNarrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
      if (this.active) {
         if (this.isFocused()) {
            pNarrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.focused"));
         } else {
            pNarrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
         }
      }

   }

   public int getX() {
      return this.x;
   }

   public void setX(int pX) {
      this.x = pX;
   }

   public void setPosition(int pX, int pY) {
      this.setX(pX);
      this.setY(pY);
   }

   public int getY() {
      return this.y;
   }

   public void setY(int pY) {
      this.y = pY;
   }
}
