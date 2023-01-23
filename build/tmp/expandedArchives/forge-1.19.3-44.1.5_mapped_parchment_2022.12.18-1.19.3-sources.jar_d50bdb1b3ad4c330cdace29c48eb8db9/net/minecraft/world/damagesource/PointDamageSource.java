package net.minecraft.world.damagesource;

import net.minecraft.world.phys.Vec3;

public class PointDamageSource extends DamageSource {
   private final Vec3 damageSourcePosition;

   public PointDamageSource(String pMsgId, Vec3 pDamageSourcePosition) {
      super(pMsgId);
      this.damageSourcePosition = pDamageSourcePosition;
   }

   /**
    * Gets the location from which the damage originates.
    */
   public Vec3 getSourcePosition() {
      return this.damageSourcePosition;
   }
}