package com.irateam.vkplayer.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.vk.sdk.api.model.VKApiAudio;

public class AlbumCoverUtils {

    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static TextDrawable createFromAudio(VKApiAudio audio) {
        return TextDrawable.builder()
                .buildRound(String.valueOf(audio.artist.trim().charAt(0)), ColorGenerator.MATERIAL.getColor(audio.artist.trim()));
    }

    public static Bitmap createBitmapFromAudio(VKApiAudio audio) {
        return drawableToBitmap(createFromAudio(audio));
    }
}
