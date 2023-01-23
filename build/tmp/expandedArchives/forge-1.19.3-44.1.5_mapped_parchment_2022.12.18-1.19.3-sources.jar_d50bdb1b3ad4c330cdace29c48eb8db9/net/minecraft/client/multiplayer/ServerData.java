package net.minecraft.client.multiplayer;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerData {
   public String name;
   public String ip;
   public Component status;
   public Component motd;
   public long ping;
   public int protocol = SharedConstants.getCurrentVersion().getProtocolVersion();
   public Component version = Component.literal(SharedConstants.getCurrentVersion().getName());
   public boolean pinged;
   public List<Component> playerList = Collections.emptyList();
   private ServerData.ServerPackStatus packStatus = ServerData.ServerPackStatus.PROMPT;
   @Nullable
   private String iconB64;
   /** True if the server is a LAN server */
   private boolean lan;
   private boolean enforcesSecureChat;
   public net.minecraftforge.client.ExtendedServerListData forgeData = null;

   public ServerData(String pName, String pIp, boolean pLan) {
      this.name = pName;
      this.ip = pIp;
      this.lan = pLan;
   }

   /**
    * Returns an NBTTagCompound with the server's name, IP and maybe acceptTextures.
    */
   public CompoundTag write() {
      CompoundTag compoundtag = new CompoundTag();
      compoundtag.putString("name", this.name);
      compoundtag.putString("ip", this.ip);
      if (this.iconB64 != null) {
         compoundtag.putString("icon", this.iconB64);
      }

      if (this.packStatus == ServerData.ServerPackStatus.ENABLED) {
         compoundtag.putBoolean("acceptTextures", true);
      } else if (this.packStatus == ServerData.ServerPackStatus.DISABLED) {
         compoundtag.putBoolean("acceptTextures", false);
      }

      return compoundtag;
   }

   public ServerData.ServerPackStatus getResourcePackStatus() {
      return this.packStatus;
   }

   public void setResourcePackStatus(ServerData.ServerPackStatus pPackStatus) {
      this.packStatus = pPackStatus;
   }

   /**
    * Takes an NBTTagCompound with 'name' and 'ip' keys, returns a ServerData instance.
    */
   public static ServerData read(CompoundTag pNbtCompound) {
      ServerData serverdata = new ServerData(pNbtCompound.getString("name"), pNbtCompound.getString("ip"), false);
      if (pNbtCompound.contains("icon", 8)) {
         serverdata.setIconB64(pNbtCompound.getString("icon"));
      }

      if (pNbtCompound.contains("acceptTextures", 1)) {
         if (pNbtCompound.getBoolean("acceptTextures")) {
            serverdata.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);
         } else {
            serverdata.setResourcePackStatus(ServerData.ServerPackStatus.DISABLED);
         }
      } else {
         serverdata.setResourcePackStatus(ServerData.ServerPackStatus.PROMPT);
      }

      return serverdata;
   }

   /**
    * Returns the base-64 encoded representation of the server's icon, or null if not available
    */
   @Nullable
   public String getIconB64() {
      return this.iconB64;
   }

   public static String parseFavicon(String p_233809_) throws ParseException {
      if (p_233809_.startsWith("data:image/png;base64,")) {
         return p_233809_.substring("data:image/png;base64,".length());
      } else {
         throw new ParseException("Unknown format", 0);
      }
   }

   public void setIconB64(@Nullable String pIconB64) {
      this.iconB64 = pIconB64;
   }

   /**
    * Returns {@code true} if the server is a LAN server.
    */
   public boolean isLan() {
      return this.lan;
   }

   public void setEnforcesSecureChat(boolean pEnforcesSecureChat) {
      this.enforcesSecureChat = pEnforcesSecureChat;
   }

   public boolean enforcesSecureChat() {
      return this.enforcesSecureChat;
   }

   public void copyNameIconFrom(ServerData pOther) {
      this.ip = pOther.ip;
      this.name = pOther.name;
      this.iconB64 = pOther.iconB64;
   }

   public void copyFrom(ServerData pServerData) {
      this.copyNameIconFrom(pServerData);
      this.setResourcePackStatus(pServerData.getResourcePackStatus());
      this.lan = pServerData.lan;
      this.enforcesSecureChat = pServerData.enforcesSecureChat;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ServerPackStatus {
      ENABLED("enabled"),
      DISABLED("disabled"),
      PROMPT("prompt");

      private final Component name;

      private ServerPackStatus(String pName) {
         this.name = Component.translatable("addServer.resourcePack." + pName);
      }

      public Component getName() {
         return this.name;
      }
   }
}
