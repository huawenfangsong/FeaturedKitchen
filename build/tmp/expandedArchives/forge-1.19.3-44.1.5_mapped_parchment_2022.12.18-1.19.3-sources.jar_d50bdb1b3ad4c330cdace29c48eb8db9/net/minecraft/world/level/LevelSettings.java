package net.minecraft.world.level;

import com.mojang.serialization.Dynamic;
import net.minecraft.world.Difficulty;

public final class LevelSettings {
   private final String levelName;
   private final GameType gameType;
   private final boolean hardcore;
   private final Difficulty difficulty;
   private final boolean allowCommands;
   private final GameRules gameRules;
   private final WorldDataConfiguration dataConfiguration;
   private final com.mojang.serialization.Lifecycle lifecycle;

   public LevelSettings(String pLevelName, GameType pGameType, boolean pHardcore, Difficulty pDifficulty, boolean pAllowCommands, GameRules pGameRules, WorldDataConfiguration pDataConfiguration) {
      this(pLevelName, pGameType, pHardcore, pDifficulty, pAllowCommands, pGameRules, pDataConfiguration, com.mojang.serialization.Lifecycle.stable());
   }
   public LevelSettings(String pLevelName, GameType pGameType, boolean pHardcore, Difficulty pDifficulty, boolean pAllowCommands, GameRules pGameRules, WorldDataConfiguration pDataConfiguration, com.mojang.serialization.Lifecycle lifecycle) {
      this.levelName = pLevelName;
      this.gameType = pGameType;
      this.hardcore = pHardcore;
      this.difficulty = pDifficulty;
      this.allowCommands = pAllowCommands;
      this.gameRules = pGameRules;
      this.dataConfiguration = pDataConfiguration;
      this.lifecycle = lifecycle;
   }

   public static LevelSettings parse(Dynamic<?> pDynamic, WorldDataConfiguration pDataConfiguration) {
      GameType gametype = GameType.byId(pDynamic.get("GameType").asInt(0));
      return new LevelSettings(pDynamic.get("LevelName").asString(""), gametype, pDynamic.get("hardcore").asBoolean(false), pDynamic.get("Difficulty").asNumber().map((p_46928_) -> {
         return Difficulty.byId(p_46928_.byteValue());
      }).result().orElse(Difficulty.NORMAL), pDynamic.get("allowCommands").asBoolean(gametype == GameType.CREATIVE), new GameRules(pDynamic.get("GameRules")), pDataConfiguration, net.minecraftforge.common.ForgeHooks.parseLifecycle(pDynamic.get("forgeLifecycle").asString("stable")));
   }

   public String levelName() {
      return this.levelName;
   }

   public GameType gameType() {
      return this.gameType;
   }

   public boolean hardcore() {
      return this.hardcore;
   }

   public Difficulty difficulty() {
      return this.difficulty;
   }

   public boolean allowCommands() {
      return this.allowCommands;
   }

   public GameRules gameRules() {
      return this.gameRules;
   }

   public WorldDataConfiguration getDataConfiguration() {
      return this.dataConfiguration;
   }

   public LevelSettings withGameType(GameType pGameType) {
      return new LevelSettings(this.levelName, pGameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, this.dataConfiguration, this.lifecycle);
   }

   public LevelSettings withDifficulty(Difficulty pDifficulty) {
      net.minecraftforge.common.ForgeHooks.onDifficultyChange(pDifficulty, this.difficulty);
      return new LevelSettings(this.levelName, this.gameType, this.hardcore, pDifficulty, this.allowCommands, this.gameRules, this.dataConfiguration, this.lifecycle);
   }

   public LevelSettings withDataConfiguration(WorldDataConfiguration pDataConfiguration) {
      return new LevelSettings(this.levelName, this.gameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, dataConfiguration, this.lifecycle);
   }

   public LevelSettings copy() {
      return new LevelSettings(this.levelName, this.gameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules.copy(), this.dataConfiguration, this.lifecycle);
   }
   public LevelSettings withLifecycle(com.mojang.serialization.Lifecycle lifecycle) {
      return new LevelSettings(this.levelName, this.gameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, this.dataConfiguration, lifecycle);
   }
   public com.mojang.serialization.Lifecycle getLifecycle() {
      return this.lifecycle;
   }
}
