package com.ipn.practica5.ui.favorites

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ipn.practica5.databinding.FragmentFavoritesBinding
import com.ipn.practica5.ui.MediaAdapter
import com.ipn.practica5.viewmodel.FavoritesViewModel

/**
 * Fragment que muestra los favoritos del usuario guardados en Room.
 * Disponible en modo offline ya que los datos son locales.
 */
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MediaAdapter(onFavoriteClick = { item ->
            viewModel.toggleFavorite(item)
        })

        binding.recyclerFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFavorites.adapter = adapter

        viewModel.favorites.observe(viewLifecycleOwner) { favs ->
            adapter.submitList(favs)
            binding.emptyFavorites.visibility = if (favs.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerFavorites.visibility = if (favs.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
