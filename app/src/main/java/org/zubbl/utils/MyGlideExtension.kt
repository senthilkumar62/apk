package org.zubbl.utils

import android.content.Context
import android.support.annotation.NonNull
import android.util.DisplayMetrics
import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideOption
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

@GlideExtension
class MyGlideExtension private constructor() {
    companion object {
        @NonNull
        @GlideOption
        public fun roundedCorners(options: RequestOptions, @NonNull context: Context, cornerRadius: Int): RequestOptions {
            val px = Math.round(cornerRadius * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT))
            return options.transforms(RoundedCorners(px))
        }
    }

}