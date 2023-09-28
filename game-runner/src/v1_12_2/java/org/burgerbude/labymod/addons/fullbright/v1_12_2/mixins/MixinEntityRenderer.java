/*
 * Copyright (C) 2022 BurgerbudeORG & Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.burgerbude.labymod.addons.fullbright.v1_12_2.mixins;


import net.labymod.api.Laby;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.burgerbude.labymod.addons.fullbright.core.event.UpdateLightmapTextureEvent;
import org.burgerbude.labymod.addons.fullbright.core.util.TextureModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

  @Shadow
  @Final
  private DynamicTexture lightmapTexture;

  @Shadow
  private boolean lightmapUpdateNeeded;
  private boolean fullbright$updated = false;

  @Inject(
          method = "updateLightmap",
          at = @At("HEAD"),
          cancellable = true
  )
  private void fullbright$updateLightTexture(float v, CallbackInfo ci) {
    UpdateLightmapTextureEvent event = Laby.fireEvent(new UpdateLightmapTextureEvent());
    if (event.isCancelled()) {
      if (!this.fullbright$updated) {
        this.fullbright$writeWhiteTexture();
      }
      this.fullbright$updated = true;
      this.lightmapTexture.updateDynamicTexture();
      ci.cancel();
      return;
    }

    if (this.fullbright$updated) {
      this.lightmapUpdateNeeded = true;
    }

    this.fullbright$updated = false;

  }

  private void fullbright$writeWhiteTexture() {
    TextureModifier.modifyLegacyTexture(16, 16, (index, color) -> this.lightmapTexture.getTextureData()[index] = color);
  }

}
