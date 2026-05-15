package com.ipn.practica5.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ipn.practica5.R
import com.ipn.practica5.data.local.entity.MediaItem
import com.ipn.practica5.databinding.ItemMediaBinding

/**
 * Adapter para mostrar libros y series en RecyclerView.
 * Soporta toggle de favorito y click en item.
 */
class MediaAdapter(
    private val onFavoriteClick: (MediaItem) -> Unit,
    private val onItemClick: (MediaItem) -> Unit = {}
) : ListAdapter<MediaItem, MediaAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemMediaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvTitle.text = item.title
            tvSubtitle.text = item.subtitle
            tvYear.text = item.year
            tvDescription.text = item.description

            // Portada con Glide
            if (item.coverUrl.isNotBlank()) {
                Glide.with(imgCover)
                    .load(item.coverUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(imgCover)
            } else {
                imgCover.setImageResource(
                    if (item.type == "book") R.drawable.ic_search else R.drawable.ic_lightbulb
                )
            }

            // Ícono de favorito
            btnFavorite.setImageResource(
                if (item.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline
            )
            btnFavorite.setOnClickListener { onFavoriteClick(item) }
            root.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<MediaItem>() {
            override fun areItemsTheSame(a: MediaItem, b: MediaItem) = a.id == b.id
            override fun areContentsTheSame(a: MediaItem, b: MediaItem) = a == b
        }
    }
}
