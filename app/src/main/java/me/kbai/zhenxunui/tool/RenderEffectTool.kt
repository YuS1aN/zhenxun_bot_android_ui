package me.kbai.zhenxunui.tool

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.HardwareRenderer
import android.graphics.PixelFormat
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.hardware.HardwareBuffer
import android.media.ImageReader
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * @author Sean on 2023/5/31
 */
object RenderEffectTool {

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.S)
    fun blur(toTransform: Bitmap, radius: Float): Bitmap? {
        val imageReader = ImageReader.newInstance(
            toTransform.width,
            toTransform.height,
            PixelFormat.RGBA_8888,
            1,
            HardwareBuffer.USAGE_GPU_SAMPLED_IMAGE or HardwareBuffer.USAGE_GPU_COLOR_OUTPUT
        )
        val renderNode = RenderNode("RenderEffect")
        val hardwareRenderer = HardwareRenderer().apply {
            setSurface(imageReader.surface)
            setContentRoot(renderNode)
        }
        renderNode.setPosition(0, 0, imageReader.width, imageReader.height)

        val blurEffect = RenderEffect.createBlurEffect(
            radius,
            radius,
            Shader.TileMode.MIRROR
        )
        renderNode.setRenderEffect(blurEffect)
        val renderCanvas = renderNode.beginRecording()
        renderCanvas.drawBitmap(toTransform, 0f, 0f, null)
        renderNode.endRecording()
        hardwareRenderer.createRenderRequest()
            .setWaitForPresent(true)
            .syncAndDraw()

        imageReader.acquireNextImage()?.use { image ->
            image.hardwareBuffer?.use { buffer ->
                return Bitmap.wrapHardwareBuffer(buffer, null)
            }
        }
        imageReader.close()
        renderNode.discardDisplayList()
        hardwareRenderer.destroy()
        return null
    }
}