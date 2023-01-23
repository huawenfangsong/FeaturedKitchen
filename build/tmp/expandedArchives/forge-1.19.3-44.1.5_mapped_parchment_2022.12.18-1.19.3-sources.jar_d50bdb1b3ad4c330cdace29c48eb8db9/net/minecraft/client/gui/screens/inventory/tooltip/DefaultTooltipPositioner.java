package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector2i;
import org.joml.Vector2ic;

@OnlyIn(Dist.CLIENT)
public class DefaultTooltipPositioner implements ClientTooltipPositioner {
   public static final ClientTooltipPositioner INSTANCE = new DefaultTooltipPositioner();

   private DefaultTooltipPositioner() {
   }

   public Vector2ic positionTooltip(Screen pScreen, int pMouseX, int pMouseY, int pWidth, int pHeight) {
      Vector2i vector2i = new Vector2i(pMouseX, pMouseY);
      this.positionTooltip(pScreen, vector2i, pWidth, pHeight);
      return vector2i;
   }

   private void positionTooltip(Screen pScreen, Vector2i pPosition, int pWidth, int pHeight) {
      if (pPosition.x + pWidth > pScreen.width) {
         pPosition.x = Math.max(pPosition.x - 24 - pWidth, 4);
      }

      int i = pHeight + 3;
      if (pPosition.y + i > pScreen.height) {
         pPosition.y = pScreen.height - i;
      }

   }
}