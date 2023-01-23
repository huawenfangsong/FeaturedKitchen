package net.minecraft.world.entity.ai.behavior;

import java.util.function.Predicate;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class CopyMemoryWithExpiry {
   public static <E extends LivingEntity, T> BehaviorControl<E> create(Predicate<E> pCanCopyMemory, MemoryModuleType<? extends T> pSourceMemory, MemoryModuleType<T> pTargetMemory, UniformInt pDurationOfCopy) {
      return BehaviorBuilder.create((p_260141_) -> {
         return p_260141_.group(p_260141_.present(pSourceMemory), p_260141_.absent(pTargetMemory)).apply(p_260141_, (p_259306_, p_259907_) -> {
            return (p_259404_, p_259935_, p_260222_) -> {
               if (!pCanCopyMemory.test(p_259935_)) {
                  return false;
               } else {
                  p_259907_.setWithExpiry(p_260141_.get(p_259306_), (long)pDurationOfCopy.sample(p_259404_.random));
                  return true;
               }
            };
         });
      });
   }
}