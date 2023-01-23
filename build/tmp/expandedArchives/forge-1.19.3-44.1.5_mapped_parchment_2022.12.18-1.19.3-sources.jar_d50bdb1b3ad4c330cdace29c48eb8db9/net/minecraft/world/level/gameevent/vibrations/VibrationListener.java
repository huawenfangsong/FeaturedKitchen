package net.minecraft.world.level.gameevent.vibrations;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class VibrationListener implements GameEventListener {
   @VisibleForTesting
   public static final Object2IntMap<GameEvent> VIBRATION_FREQUENCY_FOR_EVENT = Object2IntMaps.unmodifiable(Util.make(new Object2IntOpenHashMap<>(), (p_248462_) -> {
      p_248462_.put(GameEvent.STEP, 1);
      p_248462_.put(GameEvent.FLAP, 2);
      p_248462_.put(GameEvent.SWIM, 3);
      p_248462_.put(GameEvent.ELYTRA_GLIDE, 4);
      p_248462_.put(GameEvent.HIT_GROUND, 5);
      p_248462_.put(GameEvent.TELEPORT, 5);
      p_248462_.put(GameEvent.SPLASH, 6);
      p_248462_.put(GameEvent.ENTITY_SHAKE, 6);
      p_248462_.put(GameEvent.BLOCK_CHANGE, 6);
      p_248462_.put(GameEvent.NOTE_BLOCK_PLAY, 6);
      p_248462_.put(GameEvent.PROJECTILE_SHOOT, 7);
      p_248462_.put(GameEvent.DRINK, 7);
      p_248462_.put(GameEvent.PRIME_FUSE, 7);
      p_248462_.put(GameEvent.PROJECTILE_LAND, 8);
      p_248462_.put(GameEvent.EAT, 8);
      p_248462_.put(GameEvent.ENTITY_INTERACT, 8);
      p_248462_.put(GameEvent.ENTITY_DAMAGE, 8);
      p_248462_.put(GameEvent.EQUIP, 9);
      p_248462_.put(GameEvent.SHEAR, 9);
      p_248462_.put(GameEvent.ENTITY_ROAR, 9);
      p_248462_.put(GameEvent.BLOCK_CLOSE, 10);
      p_248462_.put(GameEvent.BLOCK_DEACTIVATE, 10);
      p_248462_.put(GameEvent.BLOCK_DETACH, 10);
      p_248462_.put(GameEvent.DISPENSE_FAIL, 10);
      p_248462_.put(GameEvent.BLOCK_OPEN, 11);
      p_248462_.put(GameEvent.BLOCK_ACTIVATE, 11);
      p_248462_.put(GameEvent.BLOCK_ATTACH, 11);
      p_248462_.put(GameEvent.ENTITY_PLACE, 12);
      p_248462_.put(GameEvent.BLOCK_PLACE, 12);
      p_248462_.put(GameEvent.FLUID_PLACE, 12);
      p_248462_.put(GameEvent.ENTITY_DIE, 13);
      p_248462_.put(GameEvent.BLOCK_DESTROY, 13);
      p_248462_.put(GameEvent.FLUID_PICKUP, 13);
      p_248462_.put(GameEvent.ITEM_INTERACT_FINISH, 14);
      p_248462_.put(GameEvent.CONTAINER_CLOSE, 14);
      p_248462_.put(GameEvent.PISTON_CONTRACT, 14);
      p_248462_.put(GameEvent.PISTON_EXTEND, 15);
      p_248462_.put(GameEvent.CONTAINER_OPEN, 15);
      p_248462_.put(GameEvent.EXPLODE, 15);
      p_248462_.put(GameEvent.LIGHTNING_STRIKE, 15);
      p_248462_.put(GameEvent.INSTRUMENT_PLAY, 15);
   }));
   protected final PositionSource listenerSource;
   protected final int listenerRange;
   protected final VibrationListener.VibrationListenerConfig config;
   @Nullable
   protected VibrationInfo currentVibration;
   protected int travelTimeInTicks;
   private final VibrationSelector selectionStrategy;

   public static Codec<VibrationListener> codec(VibrationListener.VibrationListenerConfig p_223782_) {
      return RecordCodecBuilder.create((p_248464_) -> {
         return p_248464_.group(PositionSource.CODEC.fieldOf("source").forGetter((p_223802_) -> {
            return p_223802_.listenerSource;
         }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("range").forGetter((p_223800_) -> {
            return p_223800_.listenerRange;
         }), VibrationInfo.CODEC.optionalFieldOf("event").forGetter((p_248473_) -> {
            return Optional.ofNullable(p_248473_.currentVibration);
         }), VibrationSelector.CODEC.fieldOf("selector").forGetter((p_248461_) -> {
            return p_248461_.selectionStrategy;
         }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("event_delay").orElse(0).forGetter((p_223794_) -> {
            return p_223794_.travelTimeInTicks;
         })).apply(p_248464_, (p_248468_, p_248469_, p_248470_, p_248471_, p_248472_) -> {
            return new VibrationListener(p_248468_, p_248469_, p_223782_, p_248470_.orElse((VibrationInfo)null), p_248471_, p_248472_);
         });
      });
   }

   private VibrationListener(PositionSource pListenerSource, int pListenerRange, VibrationListener.VibrationListenerConfig pConfig, @Nullable VibrationInfo pCurrentVibration, VibrationSelector pSelectionStrategy, int pTravelTimeInTicks) {
      this.listenerSource = pListenerSource;
      this.listenerRange = pListenerRange;
      this.config = pConfig;
      this.currentVibration = pCurrentVibration;
      this.travelTimeInTicks = pTravelTimeInTicks;
      this.selectionStrategy = pSelectionStrategy;
   }

   public VibrationListener(PositionSource pListenerSource, int pListenerRange, VibrationListener.VibrationListenerConfig pConfig) {
      this(pListenerSource, pListenerRange, pConfig, (VibrationInfo)null, new VibrationSelector(), 0);
   }

   public static int getGameEventFrequency(GameEvent pGameEvent) {
      return VIBRATION_FREQUENCY_FOR_EVENT.getOrDefault(pGameEvent, 0);
   }

   public void tick(Level pLevel) {
      if (pLevel instanceof ServerLevel serverlevel) {
         if (this.currentVibration == null) {
            this.selectionStrategy.chosenCandidate(serverlevel.getGameTime()).ifPresent((p_248466_) -> {
               this.currentVibration = p_248466_;
               Vec3 vec3 = this.currentVibration.pos();
               this.travelTimeInTicks = Mth.floor(this.currentVibration.distance());
               serverlevel.sendParticles(new VibrationParticleOption(this.listenerSource, this.travelTimeInTicks), vec3.x, vec3.y, vec3.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
               this.config.onSignalSchedule();
               this.selectionStrategy.startOver();
            });
         }

         if (this.currentVibration != null) {
            --this.travelTimeInTicks;
            if (this.travelTimeInTicks <= 0) {
               this.travelTimeInTicks = 0;
               this.config.onSignalReceive(serverlevel, this, new BlockPos(this.currentVibration.pos()), this.currentVibration.gameEvent(), this.currentVibration.getEntity(serverlevel).orElse((Entity)null), this.currentVibration.getProjectileOwner(serverlevel).orElse((Entity)null), this.currentVibration.distance());
               this.currentVibration = null;
            }
         }
      }

   }

   /**
    * Gets the position of the listener itself.
    */
   public PositionSource getListenerSource() {
      return this.listenerSource;
   }

   /**
    * Gets the listening radius of the listener. Events within this radius will notify the listener when broadcasted.
    */
   public int getListenerRadius() {
      return this.listenerRange;
   }

   public boolean handleGameEvent(ServerLevel pLevel, GameEvent pGameEvent, GameEvent.Context pContext, Vec3 pPos) {
      if (this.currentVibration != null) {
         return false;
      } else if (!this.config.isValidVibration(pGameEvent, pContext)) {
         return false;
      } else {
         Optional<Vec3> optional = this.listenerSource.getPosition(pLevel);
         if (optional.isEmpty()) {
            return false;
         } else {
            Vec3 vec3 = optional.get();
            if (!this.config.shouldListen(pLevel, this, new BlockPos(pPos), pGameEvent, pContext)) {
               return false;
            } else if (isOccluded(pLevel, pPos, vec3)) {
               return false;
            } else {
               this.scheduleVibration(pLevel, pGameEvent, pContext, pPos, vec3);
               return true;
            }
         }
      }
   }

   public void forceGameEvent(ServerLevel pLevel, GameEvent pGameEvent, GameEvent.Context pContext, Vec3 pPos) {
      this.listenerSource.getPosition(pLevel).ifPresent((p_248460_) -> {
         this.scheduleVibration(pLevel, pGameEvent, pContext, pPos, p_248460_);
      });
   }

   public void scheduleVibration(ServerLevel pLevel, GameEvent pGameEvent, GameEvent.Context pContext, Vec3 pStartPos, Vec3 pEndPos) {
      this.selectionStrategy.addCandidate(new VibrationInfo(pGameEvent, (float)pStartPos.distanceTo(pEndPos), pStartPos, pContext.sourceEntity()), pLevel.getGameTime());
   }

   private static boolean isOccluded(Level pLevel, Vec3 pFrom, Vec3 pTo) {
      Vec3 vec3 = new Vec3((double)Mth.floor(pFrom.x) + 0.5D, (double)Mth.floor(pFrom.y) + 0.5D, (double)Mth.floor(pFrom.z) + 0.5D);
      Vec3 vec31 = new Vec3((double)Mth.floor(pTo.x) + 0.5D, (double)Mth.floor(pTo.y) + 0.5D, (double)Mth.floor(pTo.z) + 0.5D);

      for(Direction direction : Direction.values()) {
         Vec3 vec32 = vec3.relative(direction, (double)1.0E-5F);
         if (pLevel.isBlockInLine(new ClipBlockStateContext(vec32, vec31, (p_223780_) -> {
            return p_223780_.is(BlockTags.OCCLUDES_VIBRATION_SIGNALS);
         })).getType() != HitResult.Type.BLOCK) {
            return false;
         }
      }

      return true;
   }

   public interface VibrationListenerConfig {
      default TagKey<GameEvent> getListenableEvents() {
         return GameEventTags.VIBRATIONS;
      }

      default boolean canTriggerAvoidVibration() {
         return false;
      }

      default boolean isValidVibration(GameEvent pGameEvent, GameEvent.Context pContext) {
         if (!pGameEvent.is(this.getListenableEvents())) {
            return false;
         } else {
            Entity entity = pContext.sourceEntity();
            if (entity != null) {
               if (entity.isSpectator()) {
                  return false;
               }

               if (entity.isSteppingCarefully() && pGameEvent.is(GameEventTags.IGNORE_VIBRATIONS_SNEAKING)) {
                  if (this.canTriggerAvoidVibration() && entity instanceof ServerPlayer) {
                     ServerPlayer serverplayer = (ServerPlayer)entity;
                     CriteriaTriggers.AVOID_VIBRATION.trigger(serverplayer);
                  }

                  return false;
               }

               if (entity.dampensVibrations()) {
                  return false;
               }
            }

            if (pContext.affectedState() != null) {
               return !pContext.affectedState().is(BlockTags.DAMPENS_VIBRATIONS);
            } else {
               return true;
            }
         }
      }

      boolean shouldListen(ServerLevel pLevel, GameEventListener pListener, BlockPos pPos, GameEvent pGameEvent, GameEvent.Context pContext);

      void onSignalReceive(ServerLevel pLevel, GameEventListener pListener, BlockPos pSourcePos, GameEvent pGameEvent, @Nullable Entity pSourceEntity, @Nullable Entity pProjectileOwner, float pDistance);

      default void onSignalSchedule() {
      }
   }
}