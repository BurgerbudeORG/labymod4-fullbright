package org.burgerbude.labymod.addons.fullbright.core.util;

import net.labymod.api.Laby;
import org.burgerbude.labymod.addons.fullbright.core.event.UpdateLightmapTextureEvent;

public final class TextureModifier {

  private static final int WHITE = 0xFFffffff;

  private static boolean textureUpdated;

  private TextureModifier() {
  }

  public static boolean updateTexture(
          Runnable updateTextureTask,
          Runnable uploadTask,
          Runnable updateStateTask
  ) {
    UpdateLightmapTextureEvent event = Laby.fireEvent(new UpdateLightmapTextureEvent());
    if (event.isCancelled()) {
      if (!textureUpdated) {
        updateTextureTask.run();
      }

      textureUpdated = true;
      uploadTask.run();
      return true;
    }

    // Is need for the singleplayer, if the user is in a screen,
    // the tick method is not called and therefore the light level is not updated
    if (textureUpdated) {
      updateStateTask.run();
    }

    textureUpdated = false;

    return false;
  }

  public static void modifyModernTexture(int width, int height, Pixel pixel) {
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        pixel.set(x, y, WHITE);
      }
    }
  }

  public static void modifyLegacyTexture(int width, int height, PixelArray pixel) {
    int size = width * height;
    for (int i = 0; i < size; i++) {
      pixel.set(i, WHITE);
    }
  }

  public static interface Pixel {

    void set(int x, int y, int color);

  }

  public static interface PixelArray {

    void set(int index, int color);

  }

}
