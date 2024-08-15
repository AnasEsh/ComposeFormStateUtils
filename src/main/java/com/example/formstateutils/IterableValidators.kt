package com.example.formstateutils


fun Iterable<FormFieldState<*,*>>.validate(): Boolean {
    forEach(FormFieldState<*,*>::validate)
    return all { it.errorState.value == null };
}