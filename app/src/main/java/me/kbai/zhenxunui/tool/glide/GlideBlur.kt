package me.kbai.zhenxunui.tool.glide

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.HardwareRenderer
import android.graphics.ImageFormat
import android.graphics.PixelFormat
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.hardware.HardwareBuffer
import android.media.ImageReader
import android.os.Build
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.util.Util
import me.kbai.zhenxunui.ext.logI
import me.kbai.zhenxunui.tool.RenderEffectTool
import me.kbai.zhenxunui.tool.RenderToolkit
import java.lang.RuntimeException
import java.nio.ByteBuffer
import java.security.MessageDigest

/**
 * @author Sean on 2023/5/31
 */
class GlideBlur(val radius: Int = 5) : BitmapTransformation() {
    companion object {
        private const val VERSION = 1
        private const val ID = "me.kbai.zhenxunui.tool.glide.GlideBlur$VERSION"
        private val ID_BYTES = ID.toByteArray()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
        val radiusData = ByteBuffer.allocate(4).putInt(radius).array()
        messageDigest.update(radiusData)
    }

    @SuppressLint("WrongConstant")
    override fun transform(
        pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int
    ): Bitmap {
        var blurBitmap: Bitmap?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurBitmap = RenderEffectTool.blur(toTransform, radius.toFloat())
            if (blurBitmap != null) {
                try {
                    val result = pool[toTransform.width, toTransform.height, Bitmap.Config.HARDWARE]
                    Canvas(result).drawBitmap(blurBitmap, 0f, 0f, null)
                    return result
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
                return blurBitmap
            }
        }
        blurBitmap = RenderToolkit.blur(toTransform, radius)
        val result = pool[toTransform.width, toTransform.height, Bitmap.Config.ARGB_8888]
        Canvas(result).drawBitmap(blurBitmap, 0f, 0f, null)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other is GlideBlur) {
            return other.radius == radius
        }
        return false
    }

    override fun hashCode(): Int {
        return Util.hashCode(ID.hashCode(), Util.hashCode(radius))
    }
}