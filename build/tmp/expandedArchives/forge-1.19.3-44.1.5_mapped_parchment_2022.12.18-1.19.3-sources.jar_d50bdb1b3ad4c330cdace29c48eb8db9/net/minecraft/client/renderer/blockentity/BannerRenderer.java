package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BannerRenderer implements BlockEntityRenderer<BannerBlockEntity> {
   private static final int BANNER_WIDTH = 20;
   private static final int BANNER_HEIGHT = 40;
   private static final int MAX_PATTERNS = 16;
   public static final String FLAG = "flag";
   private static final String POLE = "pole";
   private static final String BAR = "bar";
   private final ModelPart flag;
   private final ModelPart pole;
   private final ModelPart bar;

   public BannerRenderer(BlockEntityRendererProvider.Context pContext) {
      ModelPart modelpart = pContext.bakeLayer(ModelLayers.BANNER);
      this.flag = modelpart.getChild("flag");
      this.pole = modelpart.getChild("pole");
      this.bar = modelpart.getChild("bar");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("flag", CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, 0.0F, -2.0F, 20.0F, 40.0F, 1.0F), PartPose.ZERO);
      partdefinition.addOrReplaceChild("pole", CubeListBuilder.create().texOffs(44, 0).addBox(-1.0F, -30.0F, -1.0F, 2.0F, 42.0F, 2.0F), PartPose.ZERO);
      partdefinition.addOrReplaceChild("bar", CubeListBuilder.create().texOffs(0, 42).addBox(-10.0F, -32.0F, -1.0F, 20.0F, 2.0F, 2.0F), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void render(BannerBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
      List<Pair<Holder<BannerPattern>, DyeColor>> list = pBlockEntity.getPatterns();
      float f = 0.6666667F;
      boolean flag = pBlockEntity.getLevel() == null;
      pPoseStack.pushPose();
      long i;
      if (flag) {
         i = 0L;
         pPoseStack.translate(0.5F, 0.5F, 0.5F);
         this.pole.visible = true;
      } else {
         i = pBlockEntity.getLevel().getGameTime();
         BlockState blockstate = pBlockEntity.getBlockState();
         if (blockstate.getBlock() instanceof BannerBlock) {
            pPoseStack.translate(0.5F, 0.5F, 0.5F);
            float f1 = -RotationSegment.convertToDegrees(blockstate.getValue(BannerBlock.ROTATION));
            pPoseStack.mulPose(Axis.YP.rotationDegrees(f1));
            this.pole.visible = true;
         } else {
            pPoseStack.translate(0.5F, -0.16666667F, 0.5F);
            float f3 = -blockstate.getValue(WallBannerBlock.FACING).toYRot();
            pPoseStack.mulPose(Axis.YP.rotationDegrees(f3));
            pPoseStack.translate(0.0F, -0.3125F, -0.4375F);
            this.pole.visible = false;
         }
      }

      pPoseStack.pushPose();
      pPoseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
      VertexConsumer vertexconsumer = ModelBakery.BANNER_BASE.buffer(pBufferSource, RenderType::entitySolid);
      this.pole.render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
      this.bar.render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
      BlockPos blockpos = pBlockEntity.getBlockPos();
      float f2 = ((float)Math.floorMod((long)(blockpos.getX() * 7 + blockpos.getY() * 9 + blockpos.getZ() * 13) + i, 100L) + pPartialTick) / 100.0F;
      this.flag.xRot = (-0.0125F + 0.01F * Mth.cos(((float)Math.PI * 2F) * f2)) * (float)Math.PI;
      this.flag.y = -32.0F;
      renderPatterns(pPoseStack, pBufferSource, pPackedLight, pPackedOverlay, this.flag, ModelBakery.BANNER_BASE, true, list);
      pPoseStack.popPose();
      pPoseStack.popPose();
   }

   /**
    * 
    * @param pBanner if {@code true}, uses banner material; otherwise if {@code false} uses shield material
    */
   public static void renderPatterns(PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay, ModelPart pFlagPart, Material pFlagMaterial, boolean pBanner, List<Pair<Holder<BannerPattern>, DyeColor>> pPatterns) {
      renderPatterns(pPoseStack, pBufferSource, pPackedLight, pPackedOverlay, pFlagPart, pFlagMaterial, pBanner, pPatterns, false);
   }

   /**
    * 
    * @param pBanner if {@code true}, uses banner material; otherwise if {@code false} uses shield material
    */
   public static void renderPatterns(PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay, ModelPart pFlagPart, Material pFlagMaterial, boolean pBanner, List<Pair<Holder<BannerPattern>, DyeColor>> pPatterns, boolean pGlint) {
      pFlagPart.render(pPoseStack, pFlagMaterial.buffer(pBufferSource, RenderType::entitySolid, pGlint), pPackedLight, pPackedOverlay);

      for(int i = 0; i < 17 && i < pPatterns.size(); ++i) {
         Pair<Holder<BannerPattern>, DyeColor> pair = pPatterns.get(i);
         float[] afloat = pair.getSecond().getTextureDiffuseColors();
         pair.getFirst().unwrapKey().map((p_234428_) -> {
            return pBanner ? Sheets.getBannerMaterial(p_234428_) : Sheets.getShieldMaterial(p_234428_);
         }).ifPresent((p_234425_) -> {
            pFlagPart.render(pPoseStack, p_234425_.buffer(pBufferSource, RenderType::entityNoOutline), pPackedLight, pPackedOverlay, afloat[0], afloat[1], afloat[2], 1.0F);
         });
      }

   }
}