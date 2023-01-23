package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HangingSignItem extends StandingAndWallBlockItem {
   public HangingSignItem(Block pBlock, Block pWallBlock, Item.Properties pProperties) {
      super(pBlock, pWallBlock, pProperties, Direction.UP);
   }

   protected boolean canPlace(LevelReader pLevel, BlockState pState, BlockPos pPos) {
      Block block = pState.getBlock();
      if (block instanceof WallHangingSignBlock wallhangingsignblock) {
         if (!wallhangingsignblock.canPlace(pState, pLevel, pPos)) {
            return false;
         }
      }

      return super.canPlace(pLevel, pState, pPos);
   }

   protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
      boolean flag = super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);
      if (!pLevel.isClientSide && !flag && pPlayer != null) {
         BlockEntity blockentity = pLevel.getBlockEntity(pPos);
         if (blockentity instanceof SignBlockEntity) {
            SignBlockEntity signblockentity = (SignBlockEntity)blockentity;
            pPlayer.openTextEdit(signblockentity);
         }
      }

      return flag;
   }
}