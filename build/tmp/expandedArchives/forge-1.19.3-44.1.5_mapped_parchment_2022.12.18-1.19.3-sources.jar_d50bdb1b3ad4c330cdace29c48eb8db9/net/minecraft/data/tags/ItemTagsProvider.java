package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public abstract class ItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {
   /** A function that resolves block tag builders. */
   private final Function<TagKey<Block>, TagBuilder> blockTags;

   /**
    * @deprecated Forge: Use the {@link #ItemTagsProvider(PackOutput, CompletableFuture, TagsProvider, String, net.minecraftforge.common.data.ExistingFileHelper) mod id variant}
    */
   @Deprecated
   public ItemTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, TagsProvider<Block> p_256467_) {
      this(pOutput, pLookupProvider, p_256467_, "vanilla", null);
   }
   public ItemTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, TagsProvider<Block> p_256467_, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
      super(pOutput, Registries.ITEM, pLookupProvider, (p_255790_) -> {
         return p_255790_.builtInRegistryHolder().key();
      }, modId, existingFileHelper);
      this.blockTags = p_256467_::getOrCreateRawBuilder;
   }

   /**
    * Copies the entries from a block tag into an item tag.
    */
   protected void copy(TagKey<Block> pBlockTag, TagKey<Item> pItemTag) {
      TagBuilder tagbuilder = this.getOrCreateRawBuilder(pItemTag);
      TagBuilder tagbuilder1 = this.blockTags.apply(pBlockTag);
      tagbuilder1.build().forEach(tagbuilder::add);
   }
}
