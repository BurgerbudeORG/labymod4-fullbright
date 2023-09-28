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

package org.burgerbude.labymod.addons.fullbright.core;

import net.labymod.api.addon.LabyAddon;
import net.labymod.api.event.Subscribe;
import net.labymod.api.models.addon.annotation.AddonMain;
import org.burgerbude.labymod.addons.fullbright.core.configuration.FullBrightConfiguration;
import org.burgerbude.labymod.addons.fullbright.core.event.UpdateLightmapTextureEvent;

import javax.inject.Singleton;

@Singleton
@AddonMain
public class FullBrightAddon extends LabyAddon<FullBrightConfiguration> {


  @Override
  protected void enable() {
    this.registerSettingCategory();
  }

  @Override
  protected Class<FullBrightConfiguration> configurationClass() {
    return FullBrightConfiguration.class;
  }

  @Subscribe
  public void onUpdateLightmapTexture(UpdateLightmapTextureEvent event) {
    if (this.configuration().enabled().get()) {
      event.setCancelled(true);
    }
  }

}
