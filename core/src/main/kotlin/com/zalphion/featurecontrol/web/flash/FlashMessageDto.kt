package com.zalphion.featurecontrol.web.flash

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.addAdapter
import com.zalphion.featurecontrol.web.flash.FlashMessageDto.Type
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.ListAdapter
import org.http4k.format.obj
import org.http4k.format.string

data class FlashMessageDto(
    val type: Type,
    val message: String
) {
    enum class Type { Info, Error, Warning, Success }

    companion object {
        @OptIn(ExperimentalStdlibApi::class)
        internal val adapter = Moshi.Builder()
            .add(ListAdapter)
            .addAdapter(FlashMessageJsonAdapter)
            .let(::ConfigurableMoshi)
            .asBiDiMapping<Array<FlashMessageDto>>()
    }
}

private object FlashMessageJsonAdapter: JsonAdapter<FlashMessageDto>() {

    override fun fromJson(reader: JsonReader): FlashMessageDto {
        var type: Type? = null
        var message: String? = null

        reader.beginObject()
        while(reader.hasNext()) {
            when (reader.nextName()) {
                "type" -> type = Type.valueOf(reader.nextString())
                "message" -> message = reader.nextString()
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        if (type == null || message == null) error("Invalid flash message")
        return FlashMessageDto(type, message)
    }

    override fun toJson(writer: JsonWriter, dto: FlashMessageDto?) {
        writer.obj(dto) {
            writer.string("type", type.toString())
            writer.string("message", message)
        }
    }
}