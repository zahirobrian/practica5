package com.ipn.practica5.ui.recommendations

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.ipn.practica5.App
import com.ipn.practica5.databinding.FragmentRecommendationsBinding
import com.ipn.practica5.ui.MediaAdapter
import com.ipn.practica5.viewmodel.RecommendationsViewModel
import kotlinx.coroutines.launch

class RecommendationsFragment : Fragment() {

    private var _binding: FragmentRecommendationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecommendationsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val repo = (requireActivity().application as App).repository

        val adapter = MediaAdapter(
            onFavoriteClick = { item ->
                lifecycleScope.launch { repo.toggleFavorite(item) }
            }
        )
        binding.recyclerRec.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRec.adapter = adapter

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressRec.visibility = if (loading) View.VISIBLE else View.GONE
        }
        viewModel.recommendations.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.emptyRec.visibility   = if (items.isNullOrEmpty()) View.VISIBLE else View.GONE
            binding.recyclerRec.visibility = if (items.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.loadRecommendations()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
