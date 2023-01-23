package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.slf4j.Logger;

public class BiomeParametersDumpReport implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path topPath;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public BiomeParametersDumpReport(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
      this.topPath = pOutput.getOutputFolder(PackOutput.Target.REPORTS).resolve("biome_parameters");
      this.registries = pRegistries;
   }

   public CompletableFuture<?> run(CachedOutput pOutput) {
      return this.registries.thenCompose((p_258200_) -> {
         DynamicOps<JsonElement> dynamicops = RegistryOps.create(JsonOps.INSTANCE, p_258200_);
         HolderGetter<Biome> holdergetter = p_258200_.lookupOrThrow(Registries.BIOME);
         return CompletableFuture.allOf(MultiNoiseBiomeSource.Preset.getPresets().map((p_255488_) -> {
            MultiNoiseBiomeSource multinoisebiomesource = p_255488_.getSecond().biomeSource(holdergetter, false);
            return dumpValue(this.createPath(p_255488_.getFirst()), pOutput, dynamicops, MultiNoiseBiomeSource.CODEC, multinoisebiomesource);
         }).toArray((p_253398_) -> {
            return new CompletableFuture[p_253398_];
         }));
      });
   }

   private static <E> CompletableFuture<?> dumpValue(Path pPath, CachedOutput pOutput, DynamicOps<JsonElement> pOps, Encoder<E> pEncoder, E pValue) {
      Optional<JsonElement> optional = pEncoder.encodeStart(pOps, pValue).resultOrPartial((p_236195_) -> {
         LOGGER.error("Couldn't serialize element {}: {}", pPath, p_236195_);
      });
      return optional.isPresent() ? DataProvider.saveStable(pOutput, optional.get(), pPath) : CompletableFuture.completedFuture((Object)null);
   }

   private Path createPath(ResourceLocation pLocation) {
      return this.topPath.resolve(pLocation.getNamespace()).resolve(pLocation.getPath() + ".json");
   }

   /**
    * Gets a name for this provider, to use in logging.
    */
   public final String getName() {
      return "Biome Parameters";
   }
}