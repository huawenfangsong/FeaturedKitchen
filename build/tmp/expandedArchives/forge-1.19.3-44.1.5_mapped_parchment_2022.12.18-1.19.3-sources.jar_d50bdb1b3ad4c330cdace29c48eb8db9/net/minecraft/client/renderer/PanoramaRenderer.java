package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PanoramaRenderer {
   private final Minecraft minecraft;
   private final CubeMap cubeMap;
   private float spin;
   private float bob;

   public PanoramaRenderer(CubeMap pCubeMap) {
      this.cubeMap = pCubeMap;
      this.minecraft = Minecraft.getInstance();
   }

   public void render(float pDeltaT, float pAlpha) {
      float f = (float)((double)pDeltaT * this.minecraft.options.panoramaSpeed().get());
      this.spin = wrap(this.spin + f * 0.1F, 360.0F);
      this.bob = wrap(this.bob + f * 0.001F, ((float)Math.PI * 2F));
      this.cubeMap.render(this.minecraft, Mth.sin(this.bob) * 5.0F + 25.0F, -this.spin, pAlpha);
   }

   private static float wrap(float p_249058_, float p_249548_) {
      return p_249058_ > p_249548_ ? p_249058_ - p_249548_ : p_249058_;
   }
}