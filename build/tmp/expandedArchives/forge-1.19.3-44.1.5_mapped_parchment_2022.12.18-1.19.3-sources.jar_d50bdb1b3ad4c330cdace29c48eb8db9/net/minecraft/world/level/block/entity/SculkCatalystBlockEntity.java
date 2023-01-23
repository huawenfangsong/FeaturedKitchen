package net.minecraft.world.level.block.entity;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkCatalystBlock;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;

public class SculkCatalystBlockEntity extends BlockEntity implements GameEventListener {
   private final BlockPositionSource blockPosSource = new BlockPositionSource(this.worldPosition);
   private final SculkSpreader sculkSpreader = SculkSpreader.createLevelSpreader();

   public SculkCatalystBlockEntity(BlockPos pPos, BlockState pBlockState) {
      super(BlockEntityType.SCULK_CATALYST, pPos, pBlockState);
   }

   /**
    * Gets the position of the listener itself.
    */
   public PositionSource getListenerSource() {
      return this.blockPosSource;
   }

   /**
    * Gets the listening radius of the listener. Events within this radius will notify the listener when broadcasted.
    */
   public int getListenerRadius() {
      return 8;
   }

   public GameEventListener.DeliveryMode getDeliveryMode() {
      return GameEventListener.DeliveryMode.BY_DISTANCE;
   }

   public boolean handleGameEvent(ServerLevel pLevel, GameEvent pGameEvent, GameEvent.Context pContext, Vec3 pPos) {
      if (pGameEvent == GameEvent.ENTITY_DIE) {
         Entity $$5 = pContext.sourceEntity();
         if ($$5 instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)$$5;
            if (!livingentity.wasExperienceConsumed()) {
               int i = livingentity.getExperienceReward();
               if (livingentity.shouldDropExperience() && i > 0) {
                  this.sculkSpreader.addCursors(new BlockPos(pPos.relative(Direction.UP, 0.5D)), i);
                  this.tryAwardItSpreadsAdvancement(livingentity);
               }

               livingentity.skipDropExperience();
               SculkCatalystBlock.bloom(pLevel, this.worldPosition, this.getBlockState(), pLevel.getRandom());
            }

            return true;
         }
      }

      return false;
   }

   private void tryAwardItSpreadsAdvancement(LivingEntity p_249197_) {
      LivingEntity livingentity = p_249197_.getLastHurtByMob();
      if (livingentity instanceof ServerPlayer serverplayer) {
         DamageSource damagesource = p_249197_.getLastDamageSource() == null ? DamageSource.playerAttack(serverplayer) : p_249197_.getLastDamageSource();
         CriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST.trigger(serverplayer, p_249197_, damagesource);
      }

   }

   public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, SculkCatalystBlockEntity pSculkCatalyst) {
      pSculkCatalyst.sculkSpreader.updateCursors(pLevel, pPos, pLevel.getRandom(), true);
   }

   public void load(CompoundTag pTag) {
      super.load(pTag);
      this.sculkSpreader.load(pTag);
   }

   protected void saveAdditional(CompoundTag pTag) {
      this.sculkSpreader.save(pTag);
      super.saveAdditional(pTag);
   }

   @VisibleForTesting
   public SculkSpreader getSculkSpreader() {
      return this.sculkSpreader;
   }
}