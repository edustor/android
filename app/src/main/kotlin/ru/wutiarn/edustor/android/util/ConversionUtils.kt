package ru.wutiarn.edustor.android.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer
import org.threeten.bp.LocalDate

object ConversionUtils {
    class LocalDateJsonDeserializer : StdScalarDeserializer<LocalDate>(LocalDate::class.java) {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): LocalDate {
            return LocalDate.ofEpochDay(p.longValue)
        }
    }

    class LocalDateJsonSerializer : StdScalarSerializer<LocalDate>(LocalDate::class.java) {
        override fun serialize(value: LocalDate, gen: JsonGenerator, provider: SerializerProvider) {
            gen.writeNumber(value.toEpochDay())
        }
    }
}