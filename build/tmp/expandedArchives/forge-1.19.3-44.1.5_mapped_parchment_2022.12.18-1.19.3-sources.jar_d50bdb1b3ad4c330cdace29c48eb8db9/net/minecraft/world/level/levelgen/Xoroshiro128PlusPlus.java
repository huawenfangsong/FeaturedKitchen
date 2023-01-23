package net.minecraft.world.level.levelgen;

public class Xoroshiro128PlusPlus {
   private long seedLo;
   private long seedHi;

   public Xoroshiro128PlusPlus(RandomSupport.Seed128bit pSeed) {
      this(pSeed.seedLo(), pSeed.seedHi());
   }

   public Xoroshiro128PlusPlus(long pSeedLo, long pSeedHi) {
      this.seedLo = pSeedLo;
      this.seedHi = pSeedHi;
      if ((this.seedLo | this.seedHi) == 0L) {
         this.seedLo = -7046029254386353131L;
         this.seedHi = 7640891576956012809L;
      }

   }

   public long nextLong() {
      long i = this.seedLo;
      long j = this.seedHi;
      long k = Long.rotateLeft(i + j, 17) + i;
      j ^= i;
      this.seedLo = Long.rotateLeft(i, 49) ^ j ^ j << 21;
      this.seedHi = Long.rotateLeft(j, 28);
      return k;
   }
}