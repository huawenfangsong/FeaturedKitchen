package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FrameWidget extends AbstractContainerWidget {
   private final List<FrameWidget.ChildContainer> children = new ArrayList<>();
   private final List<AbstractWidget> containedChildrenView = Collections.unmodifiableList(Lists.transform(this.children, (p_254331_) -> {
      return p_254331_.child;
   }));
   private int minWidth;
   private int minHeight;
   private final LayoutSettings defaultChildLayoutSettings = LayoutSettings.defaults().align(0.5F, 0.5F);

   public static FrameWidget withMinDimensions(int pMinWidth, int pMinHeight) {
      return (new FrameWidget(0, 0, 0, 0)).setMinDimensions(pMinWidth, pMinHeight);
   }

   public FrameWidget() {
      this(0, 0, 0, 0);
   }

   public FrameWidget(int pX, int pY, int pWidth, int pHeight) {
      super(pX, pY, pWidth, pHeight, Component.empty());
   }

   public FrameWidget setMinDimensions(int pMinWidth, int pMinHeight) {
      return this.setMinWidth(pMinWidth).setMinHeight(pMinHeight);
   }

   public FrameWidget setMinHeight(int pMinHeight) {
      this.minHeight = pMinHeight;
      return this;
   }

   public FrameWidget setMinWidth(int pMinWidth) {
      this.minWidth = pMinWidth;
      return this;
   }

   public LayoutSettings newChildLayoutSettings() {
      return this.defaultChildLayoutSettings.copy();
   }

   public LayoutSettings defaultChildLayoutSetting() {
      return this.defaultChildLayoutSettings;
   }

   public void pack() {
      int i = this.minWidth;
      int j = this.minHeight;

      for(FrameWidget.ChildContainer framewidget$childcontainer : this.children) {
         i = Math.max(i, framewidget$childcontainer.getWidth());
         j = Math.max(j, framewidget$childcontainer.getHeight());
      }

      for(FrameWidget.ChildContainer framewidget$childcontainer1 : this.children) {
         framewidget$childcontainer1.setX(this.getX(), i);
         framewidget$childcontainer1.setY(this.getY(), j);
      }

      this.width = i;
      this.height = j;
   }

   public <T extends AbstractWidget> T addChild(T pChild) {
      return this.addChild(pChild, this.newChildLayoutSettings());
   }

   public <T extends AbstractWidget> T addChild(T pChild, LayoutSettings pLayoutSettings) {
      this.children.add(new FrameWidget.ChildContainer(pChild, pLayoutSettings));
      return pChild;
   }

   protected List<AbstractWidget> getContainedChildren() {
      return this.containedChildrenView;
   }

   public static void centerInRectangle(AbstractWidget p_254089_, int p_253673_, int p_253715_, int p_254062_, int p_253910_) {
      alignInRectangle(p_254089_, p_253673_, p_253715_, p_254062_, p_253910_, 0.5F, 0.5F);
   }

   public static void alignInRectangle(AbstractWidget p_254017_, int p_253970_, int p_254417_, int p_253830_, int p_254349_, float p_253759_, float p_254234_) {
      alignInDimension(p_253970_, p_253830_, p_254017_.getWidth(), p_254017_::setX, p_253759_);
      alignInDimension(p_254417_, p_254349_, p_254017_.getHeight(), p_254017_::setY, p_254234_);
   }

   public static void alignInDimension(int p_254540_, int p_254175_, int p_254347_, Consumer<Integer> p_254019_, float p_253883_) {
      int i = (int)Mth.lerp(p_253883_, 0.0F, (float)(p_254175_ - p_254347_));
      p_254019_.accept(p_254540_ + i);
   }

   @OnlyIn(Dist.CLIENT)
   static class ChildContainer extends AbstractContainerWidget.AbstractChildWrapper {
      protected ChildContainer(AbstractWidget p_254443_, LayoutSettings p_254403_) {
         super(p_254443_, p_254403_);
      }
   }
}