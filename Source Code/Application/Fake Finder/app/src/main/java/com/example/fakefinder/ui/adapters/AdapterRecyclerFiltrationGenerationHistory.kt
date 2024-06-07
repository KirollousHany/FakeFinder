package com.example.fakefinder.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fakefinder.Models.ModelFilterGenerationUpload
import com.example.fakefinder.R
import com.example.fakefinder.databinding.ItemFilterGenerationHistoryBinding

class AdapterRecyclerFiltrationGenerationHistory :
    RecyclerView.Adapter<AdapterRecyclerFiltrationGenerationHistory.Holder>() {

    var voicesList: ArrayList<ModelFilterGenerationUpload>? = null
    var onClick: OnClick? = null
    var voicePosition = -1

    @SuppressLint("NotifyDataSetChanged")
    fun updatePosition(position: Int) {
        this.voicePosition = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemFilterGenerationHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return voicesList?.size ?: 0
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(voicesList!![position])
    }

    inner class Holder(private val binding: ItemFilterGenerationHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.playPauseButton.setOnClickListener {
                onClick?.onPlayPauseClick(
                    voicesList!![layoutPosition].voiceUrl.toString(),
                    layoutPosition
                )
            }
            binding.downloadButton.setOnClickListener {
                onClick?.onDownloadClick(
                    voicesList!![layoutPosition].voiceUrl.toString(),
                    voicesList!![layoutPosition].name.toString()
                )
            }
        }

        fun bind(modelFilterGenerationUpload: ModelFilterGenerationUpload) {
            if (layoutPosition == voicePosition) {
                binding.playPauseButton.setImageResource(R.drawable.pause_icon)
            } else {
                binding.playPauseButton.setImageResource(R.drawable.play_icon)
            }
            binding.textViewVoiceName.text = modelFilterGenerationUpload.name
        }

    }

    interface OnClick {
        fun onPlayPauseClick(url: String, position: Int)

        fun onDownloadClick(url: String, name: String)
    }
}