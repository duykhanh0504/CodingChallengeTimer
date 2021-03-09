package com.kan.codingchallengesfossil3.feature.main.timer

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kan.codingchallengesfossil3.R
import com.kan.codingchallengesfossil3.data.model.TimerModel
import com.kan.codingchallengesfossil3.extension.inflate
import com.kan.codingchallengesfossil3.extension.setOnSafeClickListener
import com.kan.codingchallengesfossil3.model.TimerModelUI
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_timer_setup.view.*

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

class TimerAdapter : RecyclerView.Adapter<TimerAdapter.ItemViewHolder>() {

    private val asyncListDiffer = AsyncListDiffer(this, DiffItemCallback())

    var onItemClick: ((TimerModelUI) -> Unit)? = null
    var onItemDelete: ((TimerModelUI) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(parent.inflate(R.layout.item_timer_setup))
    }

    override fun getItemCount() = asyncListDiffer.currentList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    fun submitList(elements: List<TimerModelUI>) {
        asyncListDiffer.submitList(elements)
    }

    inner class ItemViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private lateinit var timerModel: TimerModelUI

        init {

        }

        fun bind(timerModel: TimerModelUI) {
            this.timerModel = timerModel
            containerView.timer.text = timerModel.timerSecond.toString()

            containerView.timer.setOnSafeClickListener {
                onItemClick?.invoke(timerModel)
            }

            containerView.deleteTimer.setOnSafeClickListener {
                onItemDelete?.invoke(timerModel)
            }
        }

    }

    inner class DiffItemCallback : DiffUtil.ItemCallback<TimerModelUI>() {
        override fun areItemsTheSame(oldItem: TimerModelUI, newItem: TimerModelUI): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TimerModelUI, newItem: TimerModelUI): Boolean {
            return (oldItem.id == newItem.id) && (oldItem.updateAt == newItem.updateAt)
        }
    }

}