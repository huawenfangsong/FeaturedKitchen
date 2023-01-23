package net.minecraft.server.packs.metadata.pack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.GsonHelper;

public class PackMetadataSectionSerializer implements MetadataSectionType<PackMetadataSection> {
   public PackMetadataSection fromJson(JsonObject pJson) {
      Component component = Component.Serializer.fromJson(pJson.get("description"));
      if (component == null) {
         throw new JsonParseException("Invalid/missing description!");
      } else {
         int i = GsonHelper.getAsInt(pJson, "pack_format");
         return new PackMetadataSection(component, i, net.minecraftforge.common.ForgeHooks.readTypedPackFormats(pJson));
      }
   }

   public JsonObject toJson(PackMetadataSection p_250206_) {
      JsonObject jsonobject = new JsonObject();
      jsonobject.add("description", Component.Serializer.toJsonTree(p_250206_.getDescription()));
      jsonobject.addProperty("pack_format", p_250206_.getPackFormat());
      net.minecraftforge.common.ForgeHooks.writeTypedPackFormats(jsonobject, p_250206_);
      return jsonobject;
   }

   /**
    * The name of this section type as it appears in JSON.
    */
   public String getMetadataSectionName() {
      return "pack";
   }
}
