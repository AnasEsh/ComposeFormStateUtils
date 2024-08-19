package com.mobily.composeformstateutils

import androidx.annotation.StringRes
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.text.isDigitsOnly

open class FormFieldState<ValueType : Any,
        //this generic will be used to generate errors of any type (Designed that way to make it flexible)
        ErrorType:Any>(
    private val _state: MutableState<ValueType?>,
    var label: String="",
    @StringRes
    var localizedResLabelId: Int?=null,
    var requiredError: ErrorType? = null,
    var customKeyboardType: KeyboardOptions? = null,
    var autoValidate: Boolean = false,
    var maxLength:Int? = null,
    enabled: Boolean = true,
    var customValidator: ((ValueType) -> ErrorType?)? = null
) {
    val required:Boolean
        get() = requiredError!=null;

    var dirty:Boolean = false
        private set

    private val _enabled by lazy {
        mutableStateOf(enabled)
    }
    val disabled: Boolean
        get() = !_enabled.value

    val enabled: Boolean
        get() = !disabled

    val _errorState: MutableState<ErrorType?> = mutableStateOf(null)
    val errorState: State<ErrorType?> = _errorState;
    val state: State<ValueType?> = _state;
    val value
        get() = state.value

    var keyboardOptions = if (_state.value is Number) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions();

    fun set(value: ValueType): Unit {
        dirty=true;

        val invalidInput=
            value is String&&
            ((maxLength!=null&&value.length>maxLength!!)
                    ||
            (keyboardOptions.keyboardType== KeyboardType.Number&&!value.trim().isDigitsOnly())
                    )

        if(invalidInput) {
            // exceeded max length or entered non digit instead of digits
            return;
        }

        _state.value = value
        if (autoValidate)
            validate(value);
    }

    fun hasError(): Boolean {
        validate();
        return errorState.value != null;
    }

    fun enable() {
        _enabled.value = true;
    }

    fun disable() {
        _enabled.value = false;
    }

    fun toggleEnabled() {
        _enabled.value = !_enabled.value;
    }

    fun validate(value: ValueType? = null): Unit {
        val valueToExamine = value ?: state.value;

        _errorState.value =
            if (required && (state.value==null || valueToExamine is String && valueToExamine.isBlank()))
                requiredError
            else valueToExamine?.let{
                customValidator?.invoke(valueToExamine)
            }
    }

}