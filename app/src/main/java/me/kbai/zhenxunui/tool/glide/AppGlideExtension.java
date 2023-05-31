package me.kbai.zhenxunui.tool.glide;

import android.os.Build;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.load.resource.bitmap.Downsampler;
import com.bumptech.glide.request.BaseRequestOptions;

/**
 * @author Sean on 2023/5/31
 */
@GlideExtension
public class AppGlideExtension {

    private AppGlideExtension() {
    }

    @NonNull
    @GlideOption
    public static BaseRequestOptions<?> blur(BaseRequestOptions<?> options, @IntRange(from = 0, to = 25) int radius) {
        return options.transform(new GlideBlur(radius));
    }

    @NonNull
    @GlideOption
    public static BaseRequestOptions<?> allowHardwareBitmaps(BaseRequestOptions<?> options) {
        return options.set(Downsampler.ALLOW_HARDWARE_CONFIG, Build.VERSION.SDK_INT >= Build.VERSION_CODES.P);
    }
}
