package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractContainerWidget extends AbstractWidget implements ContainerEventHandler {
   @Nullable
   private GuiEventListener focused;
   private boolean dragging;

   public AbstractContainerWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
      super(pX, pY, pWidth, pHeight, pMessage);
   }

   public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
      for(AbstractWidget abstractwidget : this.getContainedChildren()) {
         abstractwidget.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
      }

   }

   public boolean isMouseOver(double pMouseX, double pMouseY) {
      for(AbstractWidget abstractwidget : this.getContainedChildren()) {
         if (abstractwidget.isMouseOver(pMouseX, pMouseY)) {
            return true;
         }
      }

      return false;
   }

   public void mouseMoved(double pMouseX, double pMouseY) {
      this.getContainedChildren().forEach((p_253796_) -> {
         p_253796_.mouseMoved(pMouseX, pMouseY);
      });
   }

   public List<? extends GuiEventListener> children() {
      return this.getContainedChildren();
   }

   protected abstract List<? extends AbstractWidget> getContainedChildren();

   public boolean isDragging() {
      return this.dragging;
   }

   public void setDragging(boolean pIsDragging) {
      this.dragging = pIsDragging;
   }

   public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
      boolean flag = false;

      for(AbstractWidget abstractwidget : this.getContainedChildren()) {
         if (abstractwidget.isMouseOver(pMouseX, pMouseY) && abstractwidget.mouseScrolled(pMouseX, pMouseY, pDelta)) {
            flag = true;
         }
      }

      return flag || super.mouseScrolled(pMouseX, pMouseY, pDelta);
   }

   public boolean changeFocus(boolean pFocus) {
      return ContainerEventHandler.super.changeFocus(pFocus);
   }

   @Nullable
   protected GuiEventListener getHovered() {
      for(AbstractWidget abstractwidget : this.getContainedChildren()) {
         if (abstractwidget.isHovered) {
            return abstractwidget;
         }
      }

      return null;
   }

   @Nullable
   public GuiEventListener getFocused() {
      return this.focused;
   }

   public void setFocused(@Nullable GuiEventListener pFocused) {
      this.focused = pFocused;
   }

   public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
      GuiEventListener guieventlistener = this.getHovered();
      if (guieventlistener != null) {
         if (guieventlistener instanceof NarrationSupplier) {
            NarrationSupplier narrationsupplier = (NarrationSupplier)guieventlistener;
            narrationsupplier.updateNarration(pNarrationElementOutput.nest());
         }
      } else {
         GuiEventListener guieventlistener1 = this.getFocused();
         if (guieventlistener1 != null && guieventlistener1 instanceof NarrationSupplier) {
            NarrationSupplier narrationsupplier1 = (NarrationSupplier)guieventlistener1;
            narrationsupplier1.updateNarration(pNarrationElementOutput.nest());
         }
      }

   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      if (!this.isHovered && this.getHovered() == null) {
         return this.focused != null ? NarratableEntry.NarrationPriority.FOCUSED : super.narrationPriority();
      } else {
         return NarratableEntry.NarrationPriority.HOVERED;
      }
   }

   public void setX(int pX) {
      for(AbstractWidget abstractwidget : this.getContainedChildren()) {
         int i = abstractwidget.getX() + (pX - this.getX());
         abstractwidget.setX(i);
      }

      super.setX(pX);
   }

   public void setY(int pY) {
      for(AbstractWidget abstractwidget : this.getContainedChildren()) {
         int i = abstractwidget.getY() + (pY - this.getY());
         abstractwidget.setY(i);
      }

      super.setY(pY);
   }

   /**
    * Returns the first event listener that intersects with the mouse coordinates.
    */
   public Optional<GuiEventListener> getChildAt(double pMouseX, double pMouseY) {
      return ContainerEventHandler.super.getChildAt(pMouseX, pMouseY);
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      return ContainerEventHandler.super.mouseClicked(pMouseX, pMouseY, pButton);
   }

   public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
      return ContainerEventHandler.super.mouseReleased(pMouseX, pMouseY, pButton);
   }

   public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
      return ContainerEventHandler.super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
   }

   @OnlyIn(Dist.CLIENT)
   protected abstract static class AbstractChildWrapper {
      public final AbstractWidget child;
      public final LayoutSettings.LayoutSettingsImpl layoutSettings;

      protected AbstractChildWrapper(AbstractWidget pChild, LayoutSettings pLayoutSettings) {
         this.child = pChild;
         this.layoutSettings = pLayoutSettings.getExposed();
      }

      public int getHeight() {
         return this.child.getHeight() + this.layoutSettings.paddingTop + this.layoutSettings.paddingBottom;
      }

      public int getWidth() {
         return this.child.getWidth() + this.layoutSettings.paddingLeft + this.layoutSettings.paddingRight;
      }

      public void setX(int p_253789_, int p_253842_) {
         float f = (float)this.layoutSettings.paddingLeft;
         float f1 = (float)(p_253842_ - this.child.getWidth() - this.layoutSettings.paddingRight);
         int i = (int)Mth.lerp(this.layoutSettings.xAlignment, f, f1);
         this.child.setX(i + p_253789_);
      }

      public void setY(int p_253610_, int p_254525_) {
         float f = (float)this.layoutSettings.paddingTop;
         float f1 = (float)(p_254525_ - this.child.getHeight() - this.layoutSettings.paddingBottom);
         int i = (int)Mth.lerp(this.layoutSettings.yAlignment, f, f1);
         this.child.setY(i + p_253610_);
      }
   }
}