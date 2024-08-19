package com.mobily.composeformstateutils


fun Iterable<FormFieldState<*,*>>.validate(): Boolean {
    forEach(FormFieldState<*,*>::validate)
    return all { it.errorState.value == null };
}