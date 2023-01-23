package net.minecraft.client.renderer.texture.atlas;

import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface SpriteSource {
   void run(ResourceManager pResourceManager, SpriteSource.Output pOutput);

   SpriteSourceType type();

   @OnlyIn(Dist.CLIENT)
   public interface Output {
      default void add(ResourceLocation p_261841_, Resource p_261651_) {
         this.add(p_261841_, () -> {
            return SpriteLoader.loadSprite(p_261841_, p_261651_);
         });
      }

      void add(ResourceLocation p_261821_, SpriteSource.SpriteSupplier p_261760_);

      void removeAll(Predicate<ResourceLocation> p_261532_);
   }

   @OnlyIn(Dist.CLIENT)
   public interface SpriteSupplier extends Supplier<SpriteContents> {
      default void discard() {
      }
   }
}