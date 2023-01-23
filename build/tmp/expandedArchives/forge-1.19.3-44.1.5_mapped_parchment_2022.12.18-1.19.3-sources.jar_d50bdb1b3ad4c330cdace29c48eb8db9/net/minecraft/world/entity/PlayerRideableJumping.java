package net.minecraft.world.entity;

import net.minecraft.world.entity.player.Player;

public interface PlayerRideableJumping extends PlayerRideable {
   void onPlayerJump(int pJumpPower);

   boolean canJump(Player pPlayer);

   void handleStartJump(int pJumpPower);

   void handleStopJump();

   default int getJumpCooldown() {
      return 0;
   }
}