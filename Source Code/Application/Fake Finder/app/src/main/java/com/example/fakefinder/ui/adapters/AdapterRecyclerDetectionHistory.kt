package com.example.fakefinder.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fakefinder.Models.ModelDetectUpload
import com.example.fakefinder.R
import com.example.fakefinder.databinding.ItemDetectionHistoryBinding

class AdapterRecyclerDetectionHistory :
    RecyclerView.Adapter<AdapterRecyclerDetectionHistory.Holder>() {

    var voicesList: ArrayList<ModelDetectUpload>? = null
    var onClick: AdapterRecyclerFiltrationGenerationHistory.OnClick? = null
    var voicePosition = -1

    @SuppressLint("NotifyDataSetChanged")
    fun updatePosition(position: Int) {
        this.voicePosition = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemDetectionHistoryBinding.inflate(
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

    inner class Holder(private val binding: ItemDetectionHistoryBinding) :
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
                    voicesList!![layoutPosition].name.toString(),
                )
            }
        }

        fun bind(modelDetectUpload: ModelDetectUpload) {
            if (layoutPosition == voicePosition) {
                binding.playPauseButton.setImageResource(R.drawable.pause_icon)
            } else {
                binding.playPauseButton.setImageResource(R.drawable.play_icon)
            }
            binding.textViewVoiceName.text = modelDetectUpload.name
            binding.textViewReport.text = modelDetectUpload.report

        }

    }
}