package net.minecraft.client.resources.model;

import com.google.common.annotations.VisibleForTesting;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelResourceLocation extends ResourceLocation {
   @VisibleForTesting
   static final char VARIANT_SEPARATOR = '#';
   private final String variant;

   private ModelResourceLocation(String p_251021_, String p_249350_, String p_251656_, @Nullable ResourceLocation.Dummy p_248802_) {
      super(p_251021_, p_249350_, p_248802_);
      this.variant = p_251656_;
   }

   public ModelResourceLocation(String pNamespace, String pLocation, String pPath) {
      super(pNamespace, pLocation);
      this.variant = lowercaseVariant(pPath);
   }

   public ModelResourceLocation(ResourceLocation pNamespace, String pPath) {
      this(pNamespace.getNamespace(), pNamespace.getPath(), lowercaseVariant(pPath), (ResourceLocation.Dummy)null);
   }

   public static ModelResourceLocation vanilla(String p_251132_, String p_248987_) {
      return new ModelResourceLocation("minecraft", p_251132_, p_248987_);
   }

   private static String lowercaseVariant(String p_248567_) {
      return p_248567_.toLowerCase(Locale.ROOT);
   }

   public String getVariant() {
      return this.variant;
   }

   public boolean equals(Object pOther) {
      if (this == pOther) {
         return true;
      } else if (pOther instanceof ModelResourceLocation && super.equals(pOther)) {
         ModelResourceLocation modelresourcelocation = (ModelResourceLocation)pOther;
         return this.variant.equals(modelresourcelocation.variant);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return 31 * super.hashCode() + this.variant.hashCode();
   }

   public String toString() {
      return super.toString() + "#" + this.variant;
   }
}