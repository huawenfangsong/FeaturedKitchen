package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class WeightedPressurePlateBlock extends BasePressurePlateBlock {
   public static final IntegerProperty POWER = BlockStateProperties.POWER;
   private final int maxWeight;
   private final SoundEvent soundOff;
   private final SoundEvent soundOn;

   public WeightedPressurePlateBlock(int pMaxWeight, BlockBehaviour.Properties pProperties, SoundEvent pSoundOff, SoundEvent pSoundOn) {
      super(pProperties);
      this.registerDefaultState(this.stateDefinition.any().setValue(POWER, Integer.valueOf(0)));
      this.maxWeight = pMaxWeight;
      this.soundOff = pSoundOff;
      this.soundOn = pSoundOn;
   }

   protected int getSignalStrength(Level pLevel, BlockPos pPos) {
      int i = Math.min(pLevel.getEntitiesOfClass(Entity.class, TOUCH_AABB.move(pPos)).size(), this.maxWeight);
      if (i > 0) {
         float f = (float)Math.min(this.maxWeight, i) / (float)this.maxWeight;
         return Mth.ceil(f * 15.0F);
      } else {
         return 0;
      }
   }

   protected void playOnSound(LevelAccessor pLevel, BlockPos pPos) {
      pLevel.playSound((Player)null, pPos, this.soundOn, SoundSource.BLOCKS);
   }

   protected void playOffSound(LevelAccessor pLevel, BlockPos pPos) {
      pLevel.playSound((Player)null, pPos, this.soundOff, SoundSource.BLOCKS);
   }

   protected int getSignalForState(BlockState pState) {
      return pState.getValue(POWER);
   }

   protected BlockState setSignalForState(BlockState pState, int pStrength) {
      return pState.setValue(POWER, Integer.valueOf(pStrength));
   }

   protected int getPressedTime() {
      return 10;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
      pBuilder.add(POWER);
   }
}