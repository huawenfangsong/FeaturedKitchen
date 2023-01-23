package net.minecraft.client.telemetry;

import java.time.Duration;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.telemetry.events.PerformanceMetricsEvent;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.client.telemetry.events.WorldLoadTimesEvent;
import net.minecraft.client.telemetry.events.WorldUnloadEvent;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldSessionTelemetryManager {
   private final UUID worldSessionId = UUID.randomUUID();
   private final TelemetryEventSender eventSender;
   private final WorldLoadEvent worldLoadEvent;
   private final WorldUnloadEvent worldUnloadEvent = new WorldUnloadEvent();
   private final PerformanceMetricsEvent performanceMetricsEvent;
   private final WorldLoadTimesEvent worldLoadTimesEvent;

   public WorldSessionTelemetryManager(TelemetryEventSender pEventSender, boolean pWorldLoadDuration, @Nullable Duration pNewWorld) {
      this.worldLoadEvent = new WorldLoadEvent();
      this.performanceMetricsEvent = new PerformanceMetricsEvent();
      this.worldLoadTimesEvent = new WorldLoadTimesEvent(pWorldLoadDuration, pNewWorld);
      this.eventSender = pEventSender.decorate((p_261981_) -> {
         this.worldLoadEvent.addProperties(p_261981_);
         p_261981_.put(TelemetryProperty.WORLD_SESSION_ID, this.worldSessionId);
      });
   }

   public void tick() {
      this.performanceMetricsEvent.tick(this.eventSender);
   }

   public void onPlayerInfoReceived(GameType pGameType, boolean pIsHardcore) {
      this.worldLoadEvent.setGameMode(pGameType, pIsHardcore);
      this.worldUnloadEvent.onPlayerInfoReceived();
      this.worldSessionStart();
   }

   public void onServerBrandReceived(String pServerBrand) {
      this.worldLoadEvent.setServerBrand(pServerBrand);
      this.worldSessionStart();
   }

   public void setTime(long pTime) {
      this.worldUnloadEvent.setTime(pTime);
   }

   public void worldSessionStart() {
      if (this.worldLoadEvent.send(this.eventSender)) {
         this.worldLoadTimesEvent.send(this.eventSender);
         this.performanceMetricsEvent.start();
      }

   }

   public void onDisconnect() {
      this.worldLoadEvent.send(this.eventSender);
      this.performanceMetricsEvent.stop();
      this.worldUnloadEvent.send(this.eventSender);
   }
}