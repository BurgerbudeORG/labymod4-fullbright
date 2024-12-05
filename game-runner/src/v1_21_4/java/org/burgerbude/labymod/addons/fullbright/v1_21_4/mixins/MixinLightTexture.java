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

package org.burgerbude.labymod.addons.fullbright.v1_21_4.mixins;

import com.mojang.blaze3d.pipeline.TextureTarget;
import net.labymod.api.Laby;
import net.minecraft.client.renderer.LightTexture;
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
  private TextureTarget target;

  @Inject(method = "updateLightTexture", at = @At("HEAD"), cancellable = true)
  private void fullbright$updateLightTexture(float v, CallbackInfo ci) {
    final var event = Laby.fireEvent(new UpdateLightmapTextureEvent());
    if (event.isCancelled()) {
      this.target.clear();
      ci.cancel();
    }
  }
}
