package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class IndirectEntityDamageSource extends EntityDamageSource {
   @Nullable
   private final Entity cause;

   public IndirectEntityDamageSource(String pDamageTypeId, Entity pSource, @Nullable Entity pIndirectEntity) {
      super(pDamageTypeId, pSource);
      this.cause = pIndirectEntity;
   }

   /**
    * Retrieves the immediate causer of the damage, e.g. the arrow entity, not its shooter
    */
   @Nullable
   public Entity getDirectEntity() {
      return this.entity;
   }

   /**
    * Retrieves the true causer of the damage, e.g. the player who fired an arrow, the shulker who fired the bullet,
    * etc.
    */
   @Nullable
   public Entity getEntity() {
      return this.cause;
   }

   /**
    * Gets the death message that is displayed when the player dies
    */
   public Component getLocalizedDeathMessage(LivingEntity pLivingEntity) {
      Component component = this.cause == null ? this.entity.getDisplayName() : this.cause.getDisplayName();
      Entity $$5 = this.cause;
      ItemStack itemstack1;
      if ($$5 instanceof LivingEntity livingentity) {
         itemstack1 = livingentity.getMainHandItem();
      } else {
         itemstack1 = ItemStack.EMPTY;
      }

      ItemStack itemstack = itemstack1;
      String s = "death.attack." + this.msgId;
      if (!itemstack.isEmpty() && itemstack.hasCustomHoverName()) {
         String s1 = s + ".item";
         return Component.translatable(s1, pLivingEntity.getDisplayName(), component, itemstack.getDisplayName());
      } else {
         return Component.translatable(s, pLivingEntity.getDisplayName(), component);
      }
   }
}