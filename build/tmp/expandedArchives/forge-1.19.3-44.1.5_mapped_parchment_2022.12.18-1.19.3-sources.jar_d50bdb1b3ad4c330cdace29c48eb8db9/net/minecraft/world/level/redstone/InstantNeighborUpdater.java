package net.minecraft.world.level.redstone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class InstantNeighborUpdater implements NeighborUpdater {
   private final Level level;

   public InstantNeighborUpdater(Level pLevel) {
      this.level = pLevel;
   }

   public void shapeUpdate(Direction p_230755_, BlockState p_230756_, BlockPos p_230757_, BlockPos p_230758_, int p_230759_, int p_230760_) {
      NeighborUpdater.executeShapeUpdate(this.level, p_230755_, p_230756_, p_230757_, p_230758_, p_230759_, p_230760_ - 1);
   }

   public void neighborChanged(BlockPos pPos, Block pBlock, BlockPos pFromPos) {
      BlockState blockstate = this.level.getBlockState(pPos);
      this.neighborChanged(blockstate, pPos, pBlock, pFromPos, false);
   }

   public void neighborChanged(BlockState pState, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
      NeighborUpdater.executeUpdate(this.level, pState, pPos, pBlock, pFromPos, pIsMoving);
   }
}