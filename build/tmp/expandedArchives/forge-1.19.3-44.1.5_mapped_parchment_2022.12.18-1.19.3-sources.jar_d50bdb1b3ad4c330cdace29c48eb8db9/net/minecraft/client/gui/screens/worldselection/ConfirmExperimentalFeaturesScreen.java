package net.minecraft.client.gui.screens.worldselection;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.util.Mth;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfirmExperimentalFeaturesScreen extends Screen {
   private static final Component TITLE = Component.translatable("selectWorld.experimental.title");
   private static final Component MESSAGE = Component.translatable("selectWorld.experimental.message");
   private static final Component DETAILS_BUTTON = Component.translatable("selectWorld.experimental.details");
   private static final int MARGIN = 20;
   private final BooleanConsumer callback;
   final Collection<Pack> enabledPacks;
   private MultiLineLabel multilineMessage = MultiLineLabel.EMPTY;

   public ConfirmExperimentalFeaturesScreen(Collection<Pack> pEnabledPacks, BooleanConsumer pCallback) {
      super(TITLE);
      this.enabledPacks = pEnabledPacks;
      this.callback = pCallback;
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), MESSAGE);
   }

   private int messageHeight() {
      return this.multilineMessage.getLineCount() * 9;
   }

   private int titleTop() {
      int i = (this.height - this.messageHeight()) / 2;
      return Mth.clamp(i - 20 - 9, 10, 80);
   }

   protected void init() {
      super.init();
      this.multilineMessage = MultiLineLabel.create(this.font, MESSAGE, this.width - 50);
      int i = Mth.clamp(this.titleTop() + 20 + this.messageHeight() + 20, this.height / 6 + 96, this.height - 24);
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_PROCEED, (p_252248_) -> {
         this.callback.accept(true);
      }).bounds(this.width / 2 - 50 - 105, i, 100, 20).build());
      this.addRenderableWidget(Button.builder(DETAILS_BUTTON, (p_251857_) -> {
         this.minecraft.setScreen(new ConfirmExperimentalFeaturesScreen.DetailsScreen());
      }).bounds(this.width / 2 - 50, i, 100, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (p_250397_) -> {
         this.callback.accept(false);
      }).bounds(this.width / 2 - 50 + 105, i, 100, 20).build());
   }

   public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
      this.renderBackground(pPoseStack);
      drawCenteredString(pPoseStack, this.font, this.title, this.width / 2, this.titleTop(), 16777215);
      this.multilineMessage.renderCentered(pPoseStack, this.width / 2, this.titleTop() + 20);
      super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
   }

   public void onClose() {
      this.callback.accept(false);
   }

   @OnlyIn(Dist.CLIENT)
   class DetailsScreen extends Screen {
      private ConfirmExperimentalFeaturesScreen.DetailsScreen.PackList packList;

      DetailsScreen() {
         super(Component.translatable("selectWorld.experimental.details.title"));
      }

      public void onClose() {
         this.minecraft.setScreen(ConfirmExperimentalFeaturesScreen.this);
      }

      protected void init() {
         super.init();
         this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (p_251286_) -> {
            this.onClose();
         }).bounds(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20).build());
         this.packList = new ConfirmExperimentalFeaturesScreen.DetailsScreen.PackList(this.minecraft, ConfirmExperimentalFeaturesScreen.this.enabledPacks);
         this.addWidget(this.packList);
      }

      public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
         this.renderBackground(pPoseStack);
         this.packList.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
         drawCenteredString(pPoseStack, this.font, this.title, this.width / 2, 10, 16777215);
         super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
      }

      @OnlyIn(Dist.CLIENT)
      class PackList extends ObjectSelectionList<ConfirmExperimentalFeaturesScreen.DetailsScreen.PackListEntry> {
         public PackList(Minecraft pMinecraft, Collection<Pack> pEnabledPacks) {
            super(pMinecraft, DetailsScreen.this.width, DetailsScreen.this.height, 32, DetailsScreen.this.height - 64, (9 + 2) * 3);

            for(Pack pack : pEnabledPacks) {
               String s = FeatureFlags.printMissingFlags(FeatureFlags.VANILLA_SET, pack.getRequestedFeatures());
               if (!s.isEmpty()) {
                  Component component = ComponentUtils.mergeStyles(pack.getTitle().copy(), Style.EMPTY.withBold(true));
                  Component component1 = Component.translatable("selectWorld.experimental.details.entry", s);
                  this.addEntry(DetailsScreen.this.new PackListEntry(component, component1, MultiLineLabel.create(DetailsScreen.this.font, component1, this.getRowWidth())));
               }
            }

         }

         public int getRowWidth() {
            return this.width * 3 / 4;
         }

         public boolean isFocused() {
            return DetailsScreen.this.getFocused() == this;
         }
      }

      @OnlyIn(Dist.CLIENT)
      class PackListEntry extends ObjectSelectionList.Entry<ConfirmExperimentalFeaturesScreen.DetailsScreen.PackListEntry> {
         private final Component packId;
         private final Component message;
         private final MultiLineLabel splitMessage;

         PackListEntry(Component pPackId, Component pMessage, MultiLineLabel pSplitMessage) {
            this.packId = pPackId;
            this.message = pMessage;
            this.splitMessage = pSplitMessage;
         }

         public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            GuiComponent.drawString(pPoseStack, DetailsScreen.this.minecraft.font, this.packId, pLeft, pTop, 16777215);
            this.splitMessage.renderLeftAligned(pPoseStack, pLeft, pTop + 12, 9, 16777215);
         }

         public Component getNarration() {
            return Component.translatable("narrator.select", CommonComponents.joinForNarration(this.packId, this.message));
         }
      }
   }
}