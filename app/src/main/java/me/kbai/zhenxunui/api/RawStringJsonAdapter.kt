package me.kbai.zhenxunui.api

import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

/**
 * @author Sean on 2023/6/10
 */
class RawStringJsonAdapter : TypeAdapter<String>() {
    override fun write(out: JsonWriter, value: String?) {
        out.jsonValue(value)
    }

    override fun read(`in`: JsonReader): String {
        return JsonParser.parseReader(`in`).toString()
    }
}