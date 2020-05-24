package com.example.util.simpletimetracker.feature_records.adapter

import com.example.util.simpletimetracker.core.adapter.BaseRecyclerAdapter
import com.example.util.simpletimetracker.core.adapter.ViewHolderType
import com.example.util.simpletimetracker.feature_records.viewData.RecordViewData

class RecordAdapter(
    onItemLongClick: ((RecordViewData) -> Unit)
) : BaseRecyclerAdapter() {

    init {
        delegates[ViewHolderType.VIEW] = RecordAdapterDelegate(onItemLongClick)
        delegates[ViewHolderType.FOOTER] = RecordEmptyAdapterDelegate()
    }
}