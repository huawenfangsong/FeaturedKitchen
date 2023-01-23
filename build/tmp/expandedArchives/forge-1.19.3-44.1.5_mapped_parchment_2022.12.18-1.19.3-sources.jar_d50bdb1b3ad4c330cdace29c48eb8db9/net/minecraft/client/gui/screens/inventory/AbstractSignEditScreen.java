package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.stream.IntStream;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSignEditScreen extends Screen {
   /** Reference to the sign object. */
   protected final SignBlockEntity sign;
   protected final String[] messages;
   protected final WoodType woodType;
   /** Counts the number of screen updates. */
   private int frame;
   /** The index of the line that is being edited. */
   private int line;
   private TextFieldHelper signField;

   public AbstractSignEditScreen(SignBlockEntity pSign, boolean pIsTextFilteringEnabled) {
      this(pSign, pIsTextFilteringEnabled, Component.translatable("sign.edit"));
   }

   public AbstractSignEditScreen(SignBlockEntity pSign, boolean pIsTextFilteringEnabled, Component pTitle) {
      super(pTitle);
      this.woodType = SignBlock.getWoodType(pSign.getBlockState().getBlock());
      this.messages = IntStream.range(0, 4).mapToObj((p_249021_) -> {
         return pSign.getMessage(p_249021_, pIsTextFilteringEnabled);
      }).map(Component::getString).toArray((p_249111_) -> {
         return new String[p_249111_];
      });
      this.sign = pSign;
   }

   protected void init() {
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_251194_) -> {
         this.onDone();
      }).bounds(this.width / 2 - 100, this.height / 4 + 120, 200, 20).build());
      this.sign.setEditable(false);
      this.signField = new TextFieldHelper(() -> {
         return this.messages[this.line];
      }, (p_248850_) -> {
         this.messages[this.line] = p_248850_;
         this.sign.setMessage(this.line, Component.literal(p_248850_));
      }, TextFieldHelper.createClipboardGetter(this.minecraft), TextFieldHelper.createClipboardSetter(this.minecraft), (p_250707_) -> {
         return this.minecraft.font.width(p_250707_) <= this.sign.getMaxTextLineWidth();
      });
   }

   public void removed() {
      ClientPacketListener clientpacketlistener = this.minecraft.getConnection();
      if (clientpacketlistener != null) {
         clientpacketlistener.send(new ServerboundSignUpdatePacket(this.sign.getBlockPos(), this.messages[0], this.messages[1], this.messages[2], this.messages[3]));
      }

      this.sign.setEditable(true);
   }

   public void tick() {
      ++this.frame;
      if (!this.sign.getType().isValid(this.sign.getBlockState())) {
         this.onDone();
      }

   }

   private void onDone() {
      this.sign.setChanged();
      this.minecraft.setScreen((Screen)null);
   }

   public boolean charTyped(char pCodePoint, int pModifiers) {
      this.signField.charTyped(pCodePoint);
      return true;
   }

   public void onClose() {
      this.onDone();
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      if (pKeyCode == 265) {
         this.line = this.line - 1 & 3;
         this.signField.setCursorToEnd();
         return true;
      } else if (pKeyCode != 264 && pKeyCode != 257 && pKeyCode != 335) {
         return this.signField.keyPressed(pKeyCode) ? true : super.keyPressed(pKeyCode, pScanCode, pModifiers);
      } else {
         this.line = this.line + 1 & 3;
         this.signField.setCursorToEnd();
         return true;
      }
   }

   public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
      Lighting.setupForFlatItems();
      this.renderBackground(pPoseStack);
      drawCenteredString(pPoseStack, this.font, this.title, this.width / 2, 40, 16777215);
      this.renderSign(pPoseStack);
      Lighting.setupFor3DItems();
      super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
   }

   protected abstract void renderSignBackground(PoseStack pPoseStack, MultiBufferSource.BufferSource pBufferSource, BlockState pState);

   protected abstract Vector3f getSignTextScale();

   protected void offsetSign(PoseStack pPoseStack, BlockState pState) {
      pPoseStack.translate((float)this.width / 2.0F, 90.0F, 50.0F);
   }

   private void renderSign(PoseStack pPoseStack) {
      MultiBufferSource.BufferSource multibuffersource$buffersource = this.minecraft.renderBuffers().bufferSource();
      BlockState blockstate = this.sign.getBlockState();
      pPoseStack.pushPose();
      this.offsetSign(pPoseStack, blockstate);
      pPoseStack.pushPose();
      this.renderSignBackground(pPoseStack, multibuffersource$buffersource, blockstate);
      pPoseStack.popPose();
      this.renderSignText(pPoseStack, multibuffersource$buffersource);
      pPoseStack.popPose();
   }

   private void renderSignText(PoseStack pPoseStack, MultiBufferSource.BufferSource pBufferSource) {
      pPoseStack.translate(0.0F, 0.0F, 4.0F);
      Vector3f vector3f = this.getSignTextScale();
      pPoseStack.scale(vector3f.x(), vector3f.y(), vector3f.z());
      int i = this.sign.getColor().getTextColor();
      boolean flag = this.frame / 6 % 2 == 0;
      int j = this.signField.getCursorPos();
      int k = this.signField.getSelectionPos();
      int l = 4 * this.sign.getTextLineHeight() / 2;
      int i1 = this.line * this.sign.getTextLineHeight() - l;
      Matrix4f matrix4f = pPoseStack.last().pose();

      for(int j1 = 0; j1 < this.messages.length; ++j1) {
         String s = this.messages[j1];
         if (s != null) {
            if (this.font.isBidirectional()) {
               s = this.font.bidirectionalShaping(s);
            }

            float f = (float)(-this.minecraft.font.width(s) / 2);
            this.minecraft.font.drawInBatch(s, f, (float)(j1 * this.sign.getTextLineHeight() - l), i, false, matrix4f, pBufferSource, false, 0, 15728880, false);
            if (j1 == this.line && j >= 0 && flag) {
               int k1 = this.minecraft.font.width(s.substring(0, Math.max(Math.min(j, s.length()), 0)));
               int l1 = k1 - this.minecraft.font.width(s) / 2;
               if (j >= s.length()) {
                  this.minecraft.font.drawInBatch("_", (float)l1, (float)i1, i, false, matrix4f, pBufferSource, false, 0, 15728880, false);
               }
            }
         }
      }

      pBufferSource.endBatch();

      for(int j3 = 0; j3 < this.messages.length; ++j3) {
         String s1 = this.messages[j3];
         if (s1 != null && j3 == this.line && j >= 0) {
            int k3 = this.minecraft.font.width(s1.substring(0, Math.max(Math.min(j, s1.length()), 0)));
            int l3 = k3 - this.minecraft.font.width(s1) / 2;
            if (flag && j < s1.length()) {
               fill(pPoseStack, l3, i1 - 1, l3 + 1, i1 + this.sign.getTextLineHeight(), -16777216 | i);
            }

            if (k != j) {
               int i4 = Math.min(j, k);
               int i2 = Math.max(j, k);
               int j2 = this.minecraft.font.width(s1.substring(0, i4)) - this.minecraft.font.width(s1) / 2;
               int k2 = this.minecraft.font.width(s1.substring(0, i2)) - this.minecraft.font.width(s1) / 2;
               int l2 = Math.min(j2, k2);
               int i3 = Math.max(j2, k2);
               Tesselator tesselator = Tesselator.getInstance();
               BufferBuilder bufferbuilder = tesselator.getBuilder();
               RenderSystem.setShader(GameRenderer::getPositionColorShader);
               RenderSystem.disableTexture();
               RenderSystem.enableColorLogicOp();
               RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
               bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
               bufferbuilder.vertex(matrix4f, (float)l2, (float)(i1 + this.sign.getTextLineHeight()), 0.0F).color(0, 0, 255, 255).endVertex();
               bufferbuilder.vertex(matrix4f, (float)i3, (float)(i1 + this.sign.getTextLineHeight()), 0.0F).color(0, 0, 255, 255).endVertex();
               bufferbuilder.vertex(matrix4f, (float)i3, (float)i1, 0.0F).color(0, 0, 255, 255).endVertex();
               bufferbuilder.vertex(matrix4f, (float)l2, (float)i1, 0.0F).color(0, 0, 255, 255).endVertex();
               BufferUploader.drawWithShader(bufferbuilder.end());
               RenderSystem.disableColorLogicOp();
               RenderSystem.enableTexture();
            }
         }
      }

   }
}