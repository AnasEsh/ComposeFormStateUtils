package com.example.formstateutils

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.KeyboardType
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

class FormState<KeyType : Any, ErrorType : Any>(val fields: MutableMap<KeyType, FormFieldState<Any, ErrorType>> = mutableMapOf()) {
    private val onSetListeners = mutableMapOf<KeyType, (Any) -> Unit>();
    val valid by lazy {
        derivedStateOf {
            // all of them has no errors
            fields.values.all {
                it.validate(it.state.value)
                !it.hasError()
            }
        }
    }

    fun addOnSetListener(key: KeyType, listener: (value: Any) -> Unit) {
        onSetListeners[key] = listener;
    }

    private fun safeFieldGetter(key: KeyType): FormFieldState<Any, ErrorType> {
        return fields[key]
            ?: throw Exception("the requested:${key} is not present in the form fields")
    }

    fun getField(key: KeyType): FormFieldState<Any, ErrorType> = safeFieldGetter(key);

    fun valueOf(key: KeyType) = getField(key).value

    fun updateValueOf(key: KeyType, value: Any) {
        getField(key).set(value)
        onSetListeners[key]?.invoke(value);
    }

    fun setValidators(validators: Map<KeyType, ((Any) -> ErrorType?)?>) {
        validators.forEach { (prop, validator) ->
            getField(prop).customValidator = validator;
        }
    }

    fun setLabels(labelResIds: Map<KeyType, Int>) {
        labelResIds.forEach { (prop, labelId) ->
            getField(prop).localizedResLabelId = labelId;
        }
    }

    fun setKeyboardType(propertiesMaxLength: Map<KeyType, KeyboardType>) {
        propertiesMaxLength.forEach { (prop, validator) ->
            getField(prop).keyboardOptions = KeyboardOptions(keyboardType = validator);
        }
    }

    fun setFieldsMaxLength(keyboardTypes: Map<KeyType, Int>) {
        keyboardTypes.forEach { (prop, maxLength) ->
            getField(prop).maxLength = maxLength;
        }
    }

    fun configureField(key: KeyType, configuration: FormFieldState<Any, ErrorType>.() -> Unit) {
        getField(key).apply(configuration)
    }

    companion object {
        inline fun <reified StateSourceType : Any, ErrorType : Any> from(
            state: StateSourceType,
            reflectChangesOnSrc: Boolean = false,
            execludedProperties: List<KProperty1<StateSourceType, *>>? = null,
            requiredError: ErrorType? = null
        ): FormState<KMutableProperty1<StateSourceType, Any>, ErrorType> {
            val mutableProperties =
                StateSourceType::class.memberProperties.filterIsInstance<KMutableProperty<*>>()
                    .filter {
                        it.visibility == KVisibility.PUBLIC && !(execludedProperties?.contains(
                            it as KProperty1<StateSourceType, *>
                        ) ?: false)
                    }
                    .filterIsInstance<KMutableProperty1<StateSourceType, Any>>();

            val fields = mutableProperties.associateWith {
                FormFieldState(
                    mutableStateOf(it.get(state)),
                    requiredError = requiredError
                )
            }
                .toMutableMap()

            val formState = FormState(fields);

            if (reflectChangesOnSrc)
                fields.keys.forEach { prop ->
                    formState.addOnSetListener(prop) {
                        prop.set(state, it)
                    };
                }

            return formState
        }
    }
}
