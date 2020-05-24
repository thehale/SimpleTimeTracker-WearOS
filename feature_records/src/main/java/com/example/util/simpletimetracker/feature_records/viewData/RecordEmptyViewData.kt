package com.example.util.simpletimetracker.feature_records.viewData

import com.example.util.simpletimetracker.core.adapter.ViewHolderType

data class RecordEmptyViewData(
    var message: String
) : ViewHolderType {

    override fun getViewType(): Int = ViewHolderType.FOOTER
}