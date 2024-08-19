package com.mobily.composeformstateutils

import androidx.annotation.StringRes
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.MutableState


public class StringFieldState<ErrorType : Any>(
    _state: MutableState<String?>,
    label: String = "",
    @StringRes
    localizedResLabel: Int? = null,
    requiredError: ErrorType? = null,
    val maxLines: Int = 2,
    val minLines: Int = 1,
    customKeyboardType: KeyboardOptions? = null,
    autoValidate: Boolean = false, maxLength: Int? = null,
    enabled: Boolean = true,
    customValidator: ((String) -> ErrorType?)? = null
) : FormFieldState<String, ErrorType>(
    _state,
    label,
    localizedResLabel,
    requiredError,
    customKeyboardType,
    autoValidate, maxLength,
    enabled,
    customValidator
)
