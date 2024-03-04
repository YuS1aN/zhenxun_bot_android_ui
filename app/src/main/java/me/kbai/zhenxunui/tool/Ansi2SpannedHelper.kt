package me.kbai.zhenxunui.tool

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.SpannedString
import android.text.style.BackgroundColorSpan
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.annotation.RequiresApi
import pk.ansi4j.core.DefaultFunctionFinder
import pk.ansi4j.core.DefaultParserFactory
import pk.ansi4j.core.DefaultTextHandler
import pk.ansi4j.core.api.Environment
import pk.ansi4j.core.api.FragmentType
import pk.ansi4j.core.api.FunctionFragment
import pk.ansi4j.core.api.ParserFactory
import pk.ansi4j.core.api.function.FunctionArgument
import pk.ansi4j.core.api.iso6429.ControlSequenceFunction
import pk.ansi4j.core.iso6429.C0ControlFunctionHandler
import pk.ansi4j.core.iso6429.C1ControlFunctionHandler
import pk.ansi4j.core.iso6429.ControlSequenceHandler
import pk.ansi4j.core.iso6429.ControlStringHandler
import pk.ansi4j.core.iso6429.IndependentControlFunctionHandler
import java.util.AbstractCollection

class Ansi2SpannedHelper {
    private val mFactory: ParserFactory? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        initFactory()
    } else {
        null
    }
    private val mStyles: ArrayList<CharacterStyle> = ArrayList()

    fun parse(text: String): Spanned {
        val builder = SpannableStringBuilder()
        val parser = (mFactory ?: return SpannedString(text)).createParser(text)

        while (true) {
            val fragment = parser.parse() ?: break
            if (fragment.type == FragmentType.TEXT) {
                val start = builder.length
                builder.append(fragment.text)
                mStyles.forEach {
                    builder.setSpan(it, start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } else if (fragment.type == FragmentType.FUNCTION) {
                val functionFragment = fragment as FunctionFragment
                if (functionFragment.function == ControlSequenceFunction.SGR_SELECT_GRAPHIC_RENDITION) {
                    functionFragment.arguments.forEach { parseArgument(it) }
                }
            }
        }
        mStyles.clear()
        return builder
    }

    /**
     * 只解析了颜色和粗体
     */
    private fun parseArgument(argument: FunctionArgument) {
        when (argument.value) {
            0 -> mStyles.clear()
            1 -> mStyles.add(StyleSpan(Typeface.BOLD))
            3 -> mStyles.add(StyleSpan(Typeface.ITALIC))
            4, 21 -> mStyles.add(UnderlineSpan())
            9 -> mStyles.add(StrikethroughSpan())
            22 -> mStyles.removeIfCompat { (it is ForegroundColorSpan || (it is StyleSpan && it.style == Typeface.BOLD)) }
            23 -> mStyles.removeIfCompat { it is StyleSpan && it.style == Typeface.ITALIC }
            24 -> mStyles.removeIfCompat { it is UnderlineSpan }
            29 -> mStyles.removeIfCompat { it is StrikethroughSpan }
            30 -> mStyles.add(ForegroundColorSpan(Color.BLACK))
            31 -> mStyles.add(ForegroundColorSpan(Color.RED))
            32 -> mStyles.add(ForegroundColorSpan(Color.GREEN))
            33 -> mStyles.add(ForegroundColorSpan(Color.YELLOW))
            34 -> mStyles.add(ForegroundColorSpan(Color.BLUE))
            35 -> mStyles.add(ForegroundColorSpan(Color.MAGENTA))
            36 -> mStyles.add(ForegroundColorSpan(Color.CYAN))
            37 -> mStyles.add(ForegroundColorSpan(Color.WHITE))
            39 -> mStyles.removeIfCompat { it is ForegroundColorSpan }
            40 -> mStyles.add(BackgroundColorSpan(Color.BLACK))
            41 -> mStyles.add(BackgroundColorSpan(Color.RED))
            42 -> mStyles.add(BackgroundColorSpan(Color.GREEN))
            43 -> mStyles.add(BackgroundColorSpan(Color.YELLOW))
            44 -> mStyles.add(BackgroundColorSpan(Color.BLUE))
            45 -> mStyles.add(BackgroundColorSpan(Color.MAGENTA))
            46 -> mStyles.add(BackgroundColorSpan(Color.CYAN))
            47 -> mStyles.add(BackgroundColorSpan(Color.WHITE))
            49 -> mStyles.removeIfCompat { it is BackgroundColorSpan }
        }
    }

    private fun <E> AbstractCollection<E>.removeIfCompat(filter: (E) -> Boolean) {
        val iterator = iterator()

        while (iterator.hasNext()) {
            val item = iterator.next()
            if (filter(item)) iterator.remove()
        }
    }

    @RequiresApi(24)
    private fun initFactory() = DefaultParserFactory.Builder()
        .environment(Environment._7_BIT)
        .textHandler(DefaultTextHandler())
        .functionFinder(DefaultFunctionFinder())
        .functionHandlers(
            C0ControlFunctionHandler(),
            C1ControlFunctionHandler(),
            ControlSequenceHandler(),
            IndependentControlFunctionHandler(),
            ControlStringHandler()
        )
        .build()
}