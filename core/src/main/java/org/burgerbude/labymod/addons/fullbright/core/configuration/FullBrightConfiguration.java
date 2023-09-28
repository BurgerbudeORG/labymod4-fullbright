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

package org.burgerbude.labymod.addons.fullbright.core.configuration;

import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.ConfigAccessor;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;

@ConfigName("settings")
public class FullBrightConfiguration extends AddonConfig implements ConfigAccessor {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(false);

  @Override
  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }
}
