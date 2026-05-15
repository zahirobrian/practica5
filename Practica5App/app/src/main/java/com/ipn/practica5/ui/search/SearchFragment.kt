package com.ipn.practica5.ui.search

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.ipn.practica5.App
import com.ipn.practica5.R
import com.ipn.practica5.databinding.FragmentSearchBinding
import com.ipn.practica5.ui.MediaAdapter
import com.ipn.practica5.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

/**
 * Fragment de búsqueda con dos pestañas: Libros (Open Library) y Series (TVMaze).
 * Muestra banner de modo offline y cachea resultados en Room para acceso sin conexión.
 */
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: MediaAdapter
    private var searchType = "book"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        setupTabs()
        setupRecycler()
        setupSearch()
        observeViewModel()
    }

    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("📚 Libros"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("📺 Series"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                searchType = if (tab.position == 0) "book" else "show"
                binding.etSearch.hint = if (searchType == "book")
                    getString(R.string.search_hint_books)
                else getString(R.string.search_hint_shows)
                adapter.submitList(emptyList())
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupRecycler() {
        val repo = (requireActivity().application as App).repository
        adapter = MediaAdapter(
            onFavoriteClick = { item ->
                lifecycleScope.launch { repo.toggleFavorite(item) }
            }
        )
        binding.recyclerResults.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerResults.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(); true
            } else false
        }
    }

    private fun performSearch() {
        val query = binding.etSearch.text?.toString()?.trim() ?: return
        if (query.isBlank()) return
        if (searchType == "book") viewModel.searchBooks(query)
        else viewModel.searchShows(query)
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
        viewModel.isOffline.observe(viewLifecycleOwner) { offline ->
            binding.tvOffline.visibility = if (offline) View.VISIBLE else View.GONE
        }
        viewModel.results.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            val isEmpty = items.isNullOrEmpty()
            binding.recyclerResults.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
