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

package org.burgerbude.labymod.addons.fullbright.v1_20_5.mixins;

import com.mojang.blaze3d.platform.NativeImage;
import net.labymod.api.Laby;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.burgerbude.labymod.addons.fullbright.core.event.UpdateLightmapTextureEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightTexture.class)
public class MixinLightTexture {

  @Shadow
  @Final
  private NativeImage lightPixels;

  @Shadow
  @Final
  private DynamicTexture lightTexture;
  @Shadow
  private boolean updateLightTexture;
  private boolean fullbright$updated = false;

  @Inject(method = "updateLightTexture", at = @At("HEAD"), cancellable = true)
  private void fullbright$updateLightTexture(float v, CallbackInfo ci) {
    final var event = Laby.fireEvent(new UpdateLightmapTextureEvent());
    if (event.isCancelled()) {
      if (!this.fullbright$updated) {
        this.fullbright$writeWhiteTexture();
      }
      this.fullbright$updated = true;
      this.lightTexture.upload();
      ci.cancel();
      return;
    }

    // Is need for the singleplayer, if the user is in a screen,
    // the tick method is not called and therefore the light level is not updated
    if (this.fullbright$updated) {
      this.updateLightTexture = true;
    }

    this.fullbright$updated = false;
  }

  private void fullbright$writeWhiteTexture() {
    for (int x = 0; x < 16; x++) {
      for (int y = 0; y < 16; y++) {
        this.lightPixels.setPixelRGBA(x, y, 0xFFFFFFFF);
      }
    }
  }

}
