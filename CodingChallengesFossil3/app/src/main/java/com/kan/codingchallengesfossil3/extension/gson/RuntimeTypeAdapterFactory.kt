package com.kan.codingchallengesfossil3.extension.gson

import com.google.gson.*
import com.google.gson.internal.Streams
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.util.*

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */
class RuntimeTypeAdapterFactory<T> private constructor(
    baseType: Class<*>?,
    typeFieldName: String?,
    maintainType: Boolean
) :
    TypeAdapterFactory {
    private val baseType: Class<*>
    private val typeFieldName: String
    private val labelToSubtype: MutableMap<String, Class<*>?> =
        LinkedHashMap()
    private val subtypeToLabel: MutableMap<Class<*>, String?> =
        LinkedHashMap()
    private val maintainType: Boolean
    /**
     * Registers `type` identified by `label`. Labels are case
     * sensitive.
     *
     * @throws IllegalArgumentException if either `type` or `label`
     * have already been registered on this type adapter.
     */
    /**
     * Registers `type` identified by its [simple][Class.getSimpleName]. Labels are case sensitive.
     *
     * @throws IllegalArgumentException if either `type` or its simple name
     * have already been registered on this type adapter.
     */
    @JvmOverloads
    fun registerSubtype(
        type: Class<out T>?,
        label: String? = type!!.simpleName
    ): RuntimeTypeAdapterFactory<T> {
        if (type == null || label == null) {
            throw NullPointerException()
        }
        require(!(subtypeToLabel.containsKey(type) || labelToSubtype.containsKey(label))) { "types and labels must be unique" }
        labelToSubtype[label] = type
        subtypeToLabel[type] = label
        return this
    }

    override fun <R : Any> create(
        gson: Gson,
        type: TypeToken<R>
    ): TypeAdapter<R?>? {
        if (type.rawType != baseType) {
            return null
        }
        val labelToDelegate: MutableMap<String, TypeAdapter<*>> =
            LinkedHashMap()
        val subtypeToDelegate: MutableMap<Class<*>?, TypeAdapter<*>> =
            LinkedHashMap()
        for ((key, value) in labelToSubtype) {
            val delegate =
                gson.getDelegateAdapter(this, TypeToken.get(value))
            labelToDelegate[key] = delegate
            subtypeToDelegate[value] = delegate
        }
        return object : TypeAdapter<R>() {
            @Throws(IOException::class)
            override fun read(`in`: JsonReader): R {
                val jsonElement = Streams.parse(`in`)
                val labelJsonElement: JsonElement?
                labelJsonElement = if (maintainType) {
                    jsonElement.asJsonObject[typeFieldName]
                } else {
                    jsonElement.asJsonObject.remove(typeFieldName)
                }
                if (labelJsonElement == null) {
                    throw JsonParseException(
                        "cannot deserialize " + baseType
                                + " because it does not define a field named " + typeFieldName
                    )
                }
                val label = labelJsonElement.asString
                val delegate = labelToDelegate[label] as TypeAdapter<R>?
                    ?: throw JsonParseException(
                        "cannot deserialize " + baseType + " subtype named "
                                + label + "; did you forget to register a subtype?"
                    )
                return delegate.fromJsonTree(jsonElement)
            }

            @Throws(IOException::class)
            override fun write(
                out: JsonWriter,
                value: R
            ) {
                val srcType: Class<*> = value.javaClass
                val label = subtypeToLabel[srcType]
                val delegate = subtypeToDelegate[srcType] as TypeAdapter<R>?
                    ?: throw JsonParseException(
                        "cannot serialize " + srcType.name
                                + "; did you forget to register a subtype?"
                    )
                val jsonObject = delegate.toJsonTree(value).asJsonObject
                if (maintainType) {
                    Streams.write(jsonObject, out)
                    return
                }
                val clone = JsonObject()
                if (jsonObject.has(typeFieldName)) {
                    throw JsonParseException(
                        "cannot serialize " + srcType.name
                                + " because it already defines a field named " + typeFieldName
                    )
                }
                clone.add(typeFieldName, JsonPrimitive(label))
                for ((key, value1) in jsonObject.entrySet()) {
                    clone.add(key, value1)
                }
                Streams.write(clone, out)
            }
        }.nullSafe()
    }

    companion object {
        /**
         * Creates a new runtime type adapter using for `baseType` using `typeFieldName` as the type field name. Type field names are case sensitive.
         * `maintainType` flag decide if the type will be stored in pojo or not.
         */
        fun <T> of(
            baseType: Class<T>?,
            typeFieldName: String?,
            maintainType: Boolean
        ): RuntimeTypeAdapterFactory<T> {
            return RuntimeTypeAdapterFactory(baseType, typeFieldName, maintainType)
        }

        /**
         * Creates a new runtime type adapter using for `baseType` using `typeFieldName` as the type field name. Type field names are case sensitive.
         */
        fun <T> of(
            baseType: Class<T>?,
            typeFieldName: String?
        ): RuntimeTypeAdapterFactory<T> {
            return RuntimeTypeAdapterFactory(baseType, typeFieldName, false)
        }

        /**
         * Creates a new runtime type adapter for `baseType` using `"type"` as
         * the type field name.
         */
        fun <T> of(baseType: Class<T>?): RuntimeTypeAdapterFactory<T> {
            return RuntimeTypeAdapterFactory(baseType, "type", false)
        }
    }

    init {
        if (typeFieldName == null || baseType == null) {
            throw NullPointerException()
        }
        this.baseType = baseType
        this.typeFieldName = typeFieldName
        this.maintainType = maintainType
    }
}
