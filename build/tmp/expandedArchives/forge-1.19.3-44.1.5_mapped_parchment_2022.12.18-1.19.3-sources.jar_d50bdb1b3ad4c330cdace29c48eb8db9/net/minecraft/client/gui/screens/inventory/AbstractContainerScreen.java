package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractContainerScreen<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
   /** The location of the inventory background texture */
   public static final ResourceLocation INVENTORY_LOCATION = new ResourceLocation("textures/gui/container/inventory.png");
   private static final float SNAPBACK_SPEED = 100.0F;
   private static final int QUICKDROP_DELAY = 500;
   public static final int SLOT_ITEM_BLIT_OFFSET = 100;
   private static final int HOVER_ITEM_BLIT_OFFSET = 200;
   /** The X size of the inventory window in pixels. */
   protected int imageWidth = 176;
   /** The Y size of the inventory window in pixels. */
   protected int imageHeight = 166;
   protected int titleLabelX;
   protected int titleLabelY;
   protected int inventoryLabelX;
   protected int inventoryLabelY;
   /** A list of the players inventory slots */
   protected final T menu;
   protected final Component playerInventoryTitle;
   /** Holds the slot currently hovered */
   @Nullable
   protected Slot hoveredSlot;
   /** Used when touchscreen is enabled */
   @Nullable
   private Slot clickedSlot;
   @Nullable
   private Slot snapbackEnd;
   @Nullable
   private Slot quickdropSlot;
   @Nullable
   private Slot lastClickSlot;
   /** Starting X position for the Gui. Inconsistent use for Gui backgrounds. */
   protected int leftPos;
   /** Starting Y position for the Gui. Inconsistent use for Gui backgrounds. */
   protected int topPos;
   /** Used when touchscreen is enabled. */
   private boolean isSplittingStack;
   /** Used when touchscreen is enabled */
   private ItemStack draggingItem = ItemStack.EMPTY;
   private int snapbackStartX;
   private int snapbackStartY;
   private long snapbackTime;
   /** Used when touchscreen is enabled */
   private ItemStack snapbackItem = ItemStack.EMPTY;
   private long quickdropTime;
   protected final Set<Slot> quickCraftSlots = Sets.newHashSet();
   protected boolean isQuickCrafting;
   private int quickCraftingType;
   private int quickCraftingButton;
   private boolean skipNextRelease;
   private int quickCraftingRemainder;
   private long lastClickTime;
   private int lastClickButton;
   private boolean doubleclick;
   private ItemStack lastQuickMoved = ItemStack.EMPTY;

   public AbstractContainerScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
      super(pTitle);
      this.menu = pMenu;
      this.playerInventoryTitle = pPlayerInventory.getDisplayName();
      this.skipNextRelease = true;
      this.titleLabelX = 8;
      this.titleLabelY = 6;
      this.inventoryLabelX = 8;
      this.inventoryLabelY = this.imageHeight - 94;
   }

   protected void init() {
      this.leftPos = (this.width - this.imageWidth) / 2;
      this.topPos = (this.height - this.imageHeight) / 2;
   }

   public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
      int i = this.leftPos;
      int j = this.topPos;
      this.renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ContainerScreenEvent.Render.Background(this, pPoseStack, pMouseX, pMouseY));
      RenderSystem.disableDepthTest();
      super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
      PoseStack posestack = RenderSystem.getModelViewStack();
      posestack.pushPose();
      posestack.translate((float)i, (float)j, 0.0F);
      RenderSystem.applyModelViewMatrix();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      this.hoveredSlot = null;
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

      for(int k = 0; k < this.menu.slots.size(); ++k) {
         Slot slot = this.menu.slots.get(k);
         if (slot.isActive()) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            this.renderSlot(pPoseStack, slot);
         }

         if (this.isHovering(slot, (double)pMouseX, (double)pMouseY) && slot.isActive()) {
            this.hoveredSlot = slot;
            int l = slot.x;
            int i1 = slot.y;
            renderSlotHighlight(pPoseStack, l, i1, this.getBlitOffset(), this.getSlotColor(k));
         }
      }

      this.renderLabels(pPoseStack, pMouseX, pMouseY);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ContainerScreenEvent.Render.Foreground(this, pPoseStack, pMouseX, pMouseY));
      ItemStack itemstack = this.draggingItem.isEmpty() ? this.menu.getCarried() : this.draggingItem;
      if (!itemstack.isEmpty()) {
         int l1 = 8;
         int i2 = this.draggingItem.isEmpty() ? 8 : 16;
         String s = null;
         if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
            itemstack = itemstack.copy();
            itemstack.setCount(Mth.ceil((float)itemstack.getCount() / 2.0F));
         } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
            itemstack = itemstack.copy();
            itemstack.setCount(this.quickCraftingRemainder);
            if (itemstack.isEmpty()) {
               s = ChatFormatting.YELLOW + "0";
            }
         }

         this.renderFloatingItem(itemstack, pMouseX - i - 8, pMouseY - j - i2, s);
      }

      if (!this.snapbackItem.isEmpty()) {
         float f = (float)(Util.getMillis() - this.snapbackTime) / 100.0F;
         if (f >= 1.0F) {
            f = 1.0F;
            this.snapbackItem = ItemStack.EMPTY;
         }

         int j2 = this.snapbackEnd.x - this.snapbackStartX;
         int k2 = this.snapbackEnd.y - this.snapbackStartY;
         int j1 = this.snapbackStartX + (int)((float)j2 * f);
         int k1 = this.snapbackStartY + (int)((float)k2 * f);
         this.renderFloatingItem(this.snapbackItem, j1, k1, (String)null);
      }

      posestack.popPose();
      RenderSystem.applyModelViewMatrix();
      RenderSystem.enableDepthTest();
   }

   public static void renderSlotHighlight(PoseStack pPoseStack, int pX, int pY, int pBlitOffset) {
       renderSlotHighlight(pPoseStack, pX, pY, pBlitOffset, -2130706433);
   }

   public static void renderSlotHighlight(PoseStack pPoseStack, int pX, int pY, int pBlitOffset, int slotColor) {
      RenderSystem.disableDepthTest();
      RenderSystem.colorMask(true, true, true, false);
      fillGradient(pPoseStack, pX, pY, pX + 16, pY + 16, slotColor, slotColor, pBlitOffset);
      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.enableDepthTest();
   }

   protected void renderTooltip(PoseStack pPoseStack, int pX, int pY) {
      if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
         this.renderTooltip(pPoseStack, this.hoveredSlot.getItem(), pX, pY);
      }

   }

   /**
    * Draws an ItemStack.
    * 
    * The z index is increased by 32 (and not decreased afterwards), and the item is then rendered at z=200.
    */
   private void renderFloatingItem(ItemStack pStack, int pX, int pY, String pAltText) {
      PoseStack posestack = RenderSystem.getModelViewStack();
      posestack.translate(0.0F, 0.0F, 32.0F);
      RenderSystem.applyModelViewMatrix();
      this.setBlitOffset(200);
      this.itemRenderer.blitOffset = 200.0F;
      var font = net.minecraftforge.client.extensions.common.IClientItemExtensions.of(pStack).getFont(pStack, net.minecraftforge.client.extensions.common.IClientItemExtensions.FontContext.ITEM_COUNT);
      if (font == null) font = this.font;
      this.itemRenderer.renderAndDecorateItem(pStack, pX, pY);
      this.itemRenderer.renderGuiItemDecorations(font, pStack, pX, pY - (this.draggingItem.isEmpty() ? 0 : 8), pAltText);
      this.setBlitOffset(0);
      this.itemRenderer.blitOffset = 0.0F;
   }

   protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
      this.font.draw(pPoseStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
      this.font.draw(pPoseStack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
   }

   protected abstract void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY);

   private void renderSlot(PoseStack pPoseStack, Slot pSlot) {
      int i = pSlot.x;
      int j = pSlot.y;
      ItemStack itemstack = pSlot.getItem();
      boolean flag = false;
      boolean flag1 = pSlot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
      ItemStack itemstack1 = this.menu.getCarried();
      String s = null;
      if (pSlot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemstack.isEmpty()) {
         itemstack = itemstack.copy();
         itemstack.setCount(itemstack.getCount() / 2);
      } else if (this.isQuickCrafting && this.quickCraftSlots.contains(pSlot) && !itemstack1.isEmpty()) {
         if (this.quickCraftSlots.size() == 1) {
            return;
         }

         if (AbstractContainerMenu.canItemQuickReplace(pSlot, itemstack1, true) && this.menu.canDragTo(pSlot)) {
            itemstack = itemstack1.copy();
            flag = true;
            AbstractContainerMenu.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, itemstack, pSlot.getItem().isEmpty() ? 0 : pSlot.getItem().getCount());
            int k = Math.min(itemstack.getMaxStackSize(), pSlot.getMaxStackSize(itemstack));
            if (itemstack.getCount() > k) {
               s = ChatFormatting.YELLOW.toString() + k;
               itemstack.setCount(k);
            }
         } else {
            this.quickCraftSlots.remove(pSlot);
            this.recalculateQuickCraftRemaining();
         }
      }

      this.setBlitOffset(100);
      this.itemRenderer.blitOffset = 100.0F;
      if (itemstack.isEmpty() && pSlot.isActive()) {
         Pair<ResourceLocation, ResourceLocation> pair = pSlot.getNoItemIcon();
         if (pair != null) {
            TextureAtlasSprite textureatlassprite = this.minecraft.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
            RenderSystem.setShaderTexture(0, textureatlassprite.atlasLocation());
            blit(pPoseStack, i, j, this.getBlitOffset(), 16, 16, textureatlassprite);
            flag1 = true;
         }
      }

      if (!flag1) {
         if (flag) {
            fill(pPoseStack, i, j, i + 16, j + 16, -2130706433);
         }

         RenderSystem.enableDepthTest();
         this.itemRenderer.renderAndDecorateItem(this.minecraft.player, itemstack, i, j, pSlot.x + pSlot.y * this.imageWidth);
         this.itemRenderer.renderGuiItemDecorations(this.font, itemstack, i, j, s);
      }

      this.itemRenderer.blitOffset = 0.0F;
      this.setBlitOffset(0);
   }

   private void recalculateQuickCraftRemaining() {
      ItemStack itemstack = this.menu.getCarried();
      if (!itemstack.isEmpty() && this.isQuickCrafting) {
         if (this.quickCraftingType == 2) {
            this.quickCraftingRemainder = itemstack.getMaxStackSize();
         } else {
            this.quickCraftingRemainder = itemstack.getCount();

            for(Slot slot : this.quickCraftSlots) {
               ItemStack itemstack1 = itemstack.copy();
               ItemStack itemstack2 = slot.getItem();
               int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
               AbstractContainerMenu.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, itemstack1, i);
               int j = Math.min(itemstack1.getMaxStackSize(), slot.getMaxStackSize(itemstack1));
               if (itemstack1.getCount() > j) {
                  itemstack1.setCount(j);
               }

               this.quickCraftingRemainder -= itemstack1.getCount() - i;
            }

         }
      }
   }

   @Nullable
   private Slot findSlot(double pMouseX, double pMouseY) {
      for(int i = 0; i < this.menu.slots.size(); ++i) {
         Slot slot = this.menu.slots.get(i);
         if (this.isHovering(slot, pMouseX, pMouseY) && slot.isActive()) {
            return slot;
         }
      }

      return null;
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      if (super.mouseClicked(pMouseX, pMouseY, pButton)) {
         return true;
      } else {
         InputConstants.Key mouseKey = InputConstants.Type.MOUSE.getOrCreate(pButton);
         boolean flag = this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey);
         Slot slot = this.findSlot(pMouseX, pMouseY);
         long i = Util.getMillis();
         this.doubleclick = this.lastClickSlot == slot && i - this.lastClickTime < 250L && this.lastClickButton == pButton;
         this.skipNextRelease = false;
         if (pButton != 0 && pButton != 1 && !flag) {
            this.checkHotbarMouseClicked(pButton);
         } else {
            int j = this.leftPos;
            int k = this.topPos;
            boolean flag1 = this.hasClickedOutside(pMouseX, pMouseY, j, k, pButton);
            if (slot != null) flag1 = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
            int l = -1;
            if (slot != null) {
               l = slot.index;
            }

            if (flag1) {
               l = -999;
            }

            if (this.minecraft.options.touchscreen().get() && flag1 && this.menu.getCarried().isEmpty()) {
               this.onClose();
               return true;
            }

            if (l != -1) {
               if (this.minecraft.options.touchscreen().get()) {
                  if (slot != null && slot.hasItem()) {
                     this.clickedSlot = slot;
                     this.draggingItem = ItemStack.EMPTY;
                     this.isSplittingStack = pButton == 1;
                  } else {
                     this.clickedSlot = null;
                  }
               } else if (!this.isQuickCrafting) {
                  if (this.menu.getCarried().isEmpty()) {
                     if (this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey)) {
                        this.slotClicked(slot, l, pButton, ClickType.CLONE);
                     } else {
                        boolean flag2 = l != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
                        ClickType clicktype = ClickType.PICKUP;
                        if (flag2) {
                           this.lastQuickMoved = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
                           clicktype = ClickType.QUICK_MOVE;
                        } else if (l == -999) {
                           clicktype = ClickType.THROW;
                        }

                        this.slotClicked(slot, l, pButton, clicktype);
                     }

                     this.skipNextRelease = true;
                  } else {
                     this.isQuickCrafting = true;
                     this.quickCraftingButton = pButton;
                     this.quickCraftSlots.clear();
                     if (pButton == 0) {
                        this.quickCraftingType = 0;
                     } else if (pButton == 1) {
                        this.quickCraftingType = 1;
                     } else if (this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey)) {
                        this.quickCraftingType = 2;
                     }
                  }
               }
            }
         }

         this.lastClickSlot = slot;
         this.lastClickTime = i;
         this.lastClickButton = pButton;
         return true;
      }
   }

   private void checkHotbarMouseClicked(int pKeyCode) {
      if (this.hoveredSlot != null && this.menu.getCarried().isEmpty()) {
         if (this.minecraft.options.keySwapOffhand.matchesMouse(pKeyCode)) {
            this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 40, ClickType.SWAP);
            return;
         }

         for(int i = 0; i < 9; ++i) {
            if (this.minecraft.options.keyHotbarSlots[i].matchesMouse(pKeyCode)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, i, ClickType.SWAP);
            }
         }
      }

   }

   protected boolean hasClickedOutside(double pMouseX, double pMouseY, int pGuiLeft, int pGuiTop, int pMouseButton) {
      return pMouseX < (double)pGuiLeft || pMouseY < (double)pGuiTop || pMouseX >= (double)(pGuiLeft + this.imageWidth) || pMouseY >= (double)(pGuiTop + this.imageHeight);
   }

   public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
      Slot slot = this.findSlot(pMouseX, pMouseY);
      ItemStack itemstack = this.menu.getCarried();
      if (this.clickedSlot != null && this.minecraft.options.touchscreen().get()) {
         if (pButton == 0 || pButton == 1) {
            if (this.draggingItem.isEmpty()) {
               if (slot != this.clickedSlot && !this.clickedSlot.getItem().isEmpty()) {
                  this.draggingItem = this.clickedSlot.getItem().copy();
               }
            } else if (this.draggingItem.getCount() > 1 && slot != null && AbstractContainerMenu.canItemQuickReplace(slot, this.draggingItem, false)) {
               long i = Util.getMillis();
               if (this.quickdropSlot == slot) {
                  if (i - this.quickdropTime > 500L) {
                     this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                     this.slotClicked(slot, slot.index, 1, ClickType.PICKUP);
                     this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                     this.quickdropTime = i + 750L;
                     this.draggingItem.shrink(1);
                  }
               } else {
                  this.quickdropSlot = slot;
                  this.quickdropTime = i;
               }
            }
         }
      } else if (this.isQuickCrafting && slot != null && !itemstack.isEmpty() && (itemstack.getCount() > this.quickCraftSlots.size() || this.quickCraftingType == 2) && AbstractContainerMenu.canItemQuickReplace(slot, itemstack, true) && slot.mayPlace(itemstack) && this.menu.canDragTo(slot)) {
         this.quickCraftSlots.add(slot);
         this.recalculateQuickCraftRemaining();
      }

      return true;
   }

   public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
      super.mouseReleased(pMouseX, pMouseY, pButton); //Forge, Call parent to release buttons
      Slot slot = this.findSlot(pMouseX, pMouseY);
      int i = this.leftPos;
      int j = this.topPos;
      boolean flag = this.hasClickedOutside(pMouseX, pMouseY, i, j, pButton);
      if (slot != null) flag = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
      InputConstants.Key mouseKey = InputConstants.Type.MOUSE.getOrCreate(pButton);
      int k = -1;
      if (slot != null) {
         k = slot.index;
      }

      if (flag) {
         k = -999;
      }

      if (this.doubleclick && slot != null && pButton == 0 && this.menu.canTakeItemForPickAll(ItemStack.EMPTY, slot)) {
         if (hasShiftDown()) {
            if (!this.lastQuickMoved.isEmpty()) {
               for(Slot slot2 : this.menu.slots) {
                  if (slot2 != null && slot2.mayPickup(this.minecraft.player) && slot2.hasItem() && slot2.isSameInventory(slot) && AbstractContainerMenu.canItemQuickReplace(slot2, this.lastQuickMoved, true)) {
                     this.slotClicked(slot2, slot2.index, pButton, ClickType.QUICK_MOVE);
                  }
               }
            }
         } else {
            this.slotClicked(slot, k, pButton, ClickType.PICKUP_ALL);
         }

         this.doubleclick = false;
         this.lastClickTime = 0L;
      } else {
         if (this.isQuickCrafting && this.quickCraftingButton != pButton) {
            this.isQuickCrafting = false;
            this.quickCraftSlots.clear();
            this.skipNextRelease = true;
            return true;
         }

         if (this.skipNextRelease) {
            this.skipNextRelease = false;
            return true;
         }

         if (this.clickedSlot != null && this.minecraft.options.touchscreen().get()) {
            if (pButton == 0 || pButton == 1) {
               if (this.draggingItem.isEmpty() && slot != this.clickedSlot) {
                  this.draggingItem = this.clickedSlot.getItem();
               }

               boolean flag2 = AbstractContainerMenu.canItemQuickReplace(slot, this.draggingItem, false);
               if (k != -1 && !this.draggingItem.isEmpty() && flag2) {
                  this.slotClicked(this.clickedSlot, this.clickedSlot.index, pButton, ClickType.PICKUP);
                  this.slotClicked(slot, k, 0, ClickType.PICKUP);
                  if (this.menu.getCarried().isEmpty()) {
                     this.snapbackItem = ItemStack.EMPTY;
                  } else {
                     this.slotClicked(this.clickedSlot, this.clickedSlot.index, pButton, ClickType.PICKUP);
                     this.snapbackStartX = Mth.floor(pMouseX - (double)i);
                     this.snapbackStartY = Mth.floor(pMouseY - (double)j);
                     this.snapbackEnd = this.clickedSlot;
                     this.snapbackItem = this.draggingItem;
                     this.snapbackTime = Util.getMillis();
                  }
               } else if (!this.draggingItem.isEmpty()) {
                  this.snapbackStartX = Mth.floor(pMouseX - (double)i);
                  this.snapbackStartY = Mth.floor(pMouseY - (double)j);
                  this.snapbackEnd = this.clickedSlot;
                  this.snapbackItem = this.draggingItem;
                  this.snapbackTime = Util.getMillis();
               }

               this.clearDraggingState();
            }
         } else if (this.isQuickCrafting && !this.quickCraftSlots.isEmpty()) {
            this.slotClicked((Slot)null, -999, AbstractContainerMenu.getQuickcraftMask(0, this.quickCraftingType), ClickType.QUICK_CRAFT);

            for(Slot slot1 : this.quickCraftSlots) {
               this.slotClicked(slot1, slot1.index, AbstractContainerMenu.getQuickcraftMask(1, this.quickCraftingType), ClickType.QUICK_CRAFT);
            }

            this.slotClicked((Slot)null, -999, AbstractContainerMenu.getQuickcraftMask(2, this.quickCraftingType), ClickType.QUICK_CRAFT);
         } else if (!this.menu.getCarried().isEmpty()) {
            if (this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey)) {
               this.slotClicked(slot, k, pButton, ClickType.CLONE);
            } else {
               boolean flag1 = k != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
               if (flag1) {
                  this.lastQuickMoved = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
               }

               this.slotClicked(slot, k, pButton, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
            }
         }
      }

      if (this.menu.getCarried().isEmpty()) {
         this.lastClickTime = 0L;
      }

      this.isQuickCrafting = false;
      return true;
   }

   public void clearDraggingState() {
      this.draggingItem = ItemStack.EMPTY;
      this.clickedSlot = null;
   }

   private boolean isHovering(Slot pSlot, double pMouseX, double pMouseY) {
      return this.isHovering(pSlot.x, pSlot.y, 16, 16, pMouseX, pMouseY);
   }

   protected boolean isHovering(int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY) {
      int i = this.leftPos;
      int j = this.topPos;
      pMouseX -= (double)i;
      pMouseY -= (double)j;
      return pMouseX >= (double)(pX - 1) && pMouseX < (double)(pX + pWidth + 1) && pMouseY >= (double)(pY - 1) && pMouseY < (double)(pY + pHeight + 1);
   }

   /**
    * Called when the mouse is clicked over a slot or outside the gui.
    */
   protected void slotClicked(Slot pSlot, int pSlotId, int pMouseButton, ClickType pType) {
      if (pSlot != null) {
         pSlotId = pSlot.index;
      }

      this.minecraft.gameMode.handleInventoryMouseClick(this.menu.containerId, pSlotId, pMouseButton, pType, this.minecraft.player);
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      InputConstants.Key mouseKey = InputConstants.getKey(pKeyCode, pScanCode);
      if (super.keyPressed(pKeyCode, pScanCode, pModifiers)) {
         return true;
      } else if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
         this.onClose();
         return true;
      } else {
         boolean handled = this.checkHotbarKeyPressed(pKeyCode, pScanCode);// Forge MC-146650: Needs to return true when the key is handled
         if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 0, ClickType.CLONE);
               handled = true;
            } else if (this.minecraft.options.keyDrop.isActiveAndMatches(mouseKey)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, hasControlDown() ? 1 : 0, ClickType.THROW);
               handled = true;
            }
         } else if (this.minecraft.options.keyDrop.isActiveAndMatches(mouseKey)) {
             handled = true; // Forge MC-146650: Emulate MC bug, so we don't drop from hotbar when pressing drop without hovering over a item.
         }

         return handled;
      }
   }

   protected boolean checkHotbarKeyPressed(int pKeyCode, int pScanCode) {
      if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null) {
         if (this.minecraft.options.keySwapOffhand.isActiveAndMatches(InputConstants.getKey(pKeyCode, pScanCode))) {
            this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 40, ClickType.SWAP);
            return true;
         }

         for(int i = 0; i < 9; ++i) {
            if (this.minecraft.options.keyHotbarSlots[i].isActiveAndMatches(InputConstants.getKey(pKeyCode, pScanCode))) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, i, ClickType.SWAP);
               return true;
            }
         }
      }

      return false;
   }

   public void removed() {
      if (this.minecraft.player != null) {
         this.menu.removed(this.minecraft.player);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   public final void tick() {
      super.tick();
      if (this.minecraft.player.isAlive() && !this.minecraft.player.isRemoved()) {
         this.containerTick();
      } else {
         this.minecraft.player.closeContainer();
      }

   }

   protected void containerTick() {
   }

   public T getMenu() {
      return this.menu;
   }

   @org.jetbrains.annotations.Nullable
   public Slot getSlotUnderMouse() { return this.hoveredSlot; }
   public int getGuiLeft() { return leftPos; }
   public int getGuiTop() { return topPos; }
   public int getXSize() { return imageWidth; }
   public int getYSize() { return imageHeight; }

   protected int slotColor = -2130706433;
   public int getSlotColor(int index) {
      return slotColor;
   }

   public void onClose() {
      this.minecraft.player.closeContainer();
      super.onClose();
   }
}
