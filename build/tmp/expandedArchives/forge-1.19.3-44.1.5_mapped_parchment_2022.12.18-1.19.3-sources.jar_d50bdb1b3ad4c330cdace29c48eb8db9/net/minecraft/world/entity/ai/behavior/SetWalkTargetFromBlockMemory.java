package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;

public class SetWalkTargetFromBlockMemory {
   public static OneShot<Villager> create(MemoryModuleType<GlobalPos> pBlockTargetMemory, float pSpeedModifier, int pCloseEnoughDist, int pTooFarDistance, int pTooLongUnreachableDuration) {
      return BehaviorBuilder.create((p_258717_) -> {
         return p_258717_.group(p_258717_.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE), p_258717_.absent(MemoryModuleType.WALK_TARGET), p_258717_.present(pBlockTargetMemory)).apply(p_258717_, (p_258709_, p_258710_, p_258711_) -> {
            return (p_258727_, p_258728_, p_258729_) -> {
               GlobalPos globalpos = p_258717_.get(p_258711_);
               Optional<Long> optional = p_258717_.tryGet(p_258709_);
               if (globalpos.dimension() == p_258727_.dimension() && (!optional.isPresent() || p_258727_.getGameTime() - optional.get() <= (long)pTooLongUnreachableDuration)) {
                  if (globalpos.pos().distManhattan(p_258728_.blockPosition()) > pTooFarDistance) {
                     Vec3 vec3 = null;
                     int i = 0;
                     int j = 1000;

                     while(vec3 == null || (new BlockPos(vec3)).distManhattan(p_258728_.blockPosition()) > pTooFarDistance) {
                        vec3 = DefaultRandomPos.getPosTowards(p_258728_, 15, 7, Vec3.atBottomCenterOf(globalpos.pos()), (double)((float)Math.PI / 2F));
                        ++i;
                        if (i == 1000) {
                           p_258728_.releasePoi(pBlockTargetMemory);
                           p_258711_.erase();
                           p_258709_.set(p_258729_);
                           return true;
                        }
                     }

                     p_258710_.set(new WalkTarget(vec3, pSpeedModifier, pCloseEnoughDist));
                  } else if (globalpos.pos().distManhattan(p_258728_.blockPosition()) > pCloseEnoughDist) {
                     p_258710_.set(new WalkTarget(globalpos.pos(), pSpeedModifier, pCloseEnoughDist));
                  }
               } else {
                  p_258728_.releasePoi(pBlockTargetMemory);
                  p_258711_.erase();
                  p_258709_.set(p_258729_);
               }

               return true;
            };
         });
      });
   }
}