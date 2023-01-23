package net.minecraft.world.level.redstone;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface NeighborUpdater {
   Direction[] UPDATE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH};

   void shapeUpdate(Direction p_230791_, BlockState p_230792_, BlockPos p_230793_, BlockPos p_230794_, int p_230795_, int p_230796_);

   void neighborChanged(BlockPos pPos, Block pBlock, BlockPos pFromPos);

   void neighborChanged(BlockState pState, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving);

   default void updateNeighborsAtExceptFromFacing(BlockPos pPos, Block pBlock, @Nullable Direction pFacing) {
      for(Direction direction : UPDATE_ORDER) {
         if (direction != pFacing) {
            this.neighborChanged(pPos.relative(direction), pBlock, pPos);
         }
      }

   }

   static void executeShapeUpdate(LevelAccessor pLevel, Direction pDirection, BlockState pQueried, BlockPos pPos, BlockPos pOffsetPos, int pFlags, int pRecursionLevel) {
      BlockState blockstate = pLevel.getBlockState(pPos);
      BlockState blockstate1 = blockstate.updateShape(pDirection, pQueried, pLevel, pPos, pOffsetPos);
      Block.updateOrDestroy(blockstate, blockstate1, pLevel, pPos, pFlags, pRecursionLevel);
   }

   static void executeUpdate(Level pLevel, BlockState pState, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
      try {
         pState.neighborChanged(pLevel, pPos, pBlock, pFromPos, pIsMoving);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception while updating neighbours");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Block being updated");
         crashreportcategory.setDetail("Source block type", () -> {
            try {
               return String.format(Locale.ROOT, "ID #%s (%s // %s)", BuiltInRegistries.BLOCK.getKey(pBlock), pBlock.getDescriptionId(), pBlock.getClass().getCanonicalName());
            } catch (Throwable throwable1) {
               return "ID #" + BuiltInRegistries.BLOCK.getKey(pBlock);
            }
         });
         CrashReportCategory.populateBlockDetails(crashreportcategory, pLevel, pPos, pState);
         throw new ReportedException(crashreport);
      }
   }
}