package br.com.mobicare.cielo.component.requiredDataField.presentation.builder

import android.view.View

interface RequiredDataFieldBaseViewBuilder {
    fun build(): View
    fun setTitle(title: String)
    fun setFieldDataList(list: List<RequiredDataFieldViewBuilder.FieldData>)
}