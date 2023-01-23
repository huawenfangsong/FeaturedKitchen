package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class CustomRecipe implements CraftingRecipe {
   private final ResourceLocation id;
   private final CraftingBookCategory category;

   public CustomRecipe(ResourceLocation pId, CraftingBookCategory pCategory) {
      this.id = pId;
      this.category = pCategory;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   /**
    * If true, this recipe does not appear in the recipe book and does not respect recipe unlocking (and the
    * doLimitedCrafting gamerule)
    */
   public boolean isSpecial() {
      return true;
   }

   /**
    * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
    * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
    */
   public ItemStack getResultItem() {
      return ItemStack.EMPTY;
   }

   public CraftingBookCategory category() {
      return this.category;
   }
}