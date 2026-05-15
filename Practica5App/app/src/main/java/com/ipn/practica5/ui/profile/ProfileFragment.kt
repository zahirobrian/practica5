package com.ipn.practica5.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.ipn.practica5.R
import com.ipn.practica5.data.local.entity.SearchHistory
import com.ipn.practica5.databinding.FragmentProfileBinding
import com.ipn.practica5.databinding.ItemHistoryBinding
import com.ipn.practica5.ui.login.LoginActivity
import com.ipn.practica5.viewmodel.FavoritesViewModel
import com.ipn.practica5.viewmodel.SearchViewModel
import java.text.SimpleDateFormat
import java.util.*
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Fragment de perfil del usuario.
 * Muestra info de Google, contadores de favoritos y búsquedas,
 * historial de búsquedas y opción de cerrar sesión.
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val favViewModel: FavoritesViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        searchViewModel.currentUserId = user?.uid ?: ""

        // Datos del usuario
        binding.tvName.text = user?.displayName ?: "Usuario"
        binding.tvEmail.text = user?.email ?: ""

        // Foto de perfil con Glide
        user?.photoUrl?.let { photoUrl ->
            Glide.with(this).load(photoUrl)
                .transform(CircleCrop())
                .placeholder(R.drawable.bg_circle_primary)
                .into(binding.imgAvatar)
        }

        // Contador de favoritos
        favViewModel.favorites.observe(viewLifecycleOwner) { favs ->
            binding.tvFavCount.text = favs.size.toString()
        }

        // Historial
        val historyAdapter = HistoryAdapter()
        binding.recyclerHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHistory.adapter = historyAdapter

        searchViewModel.getHistory().observe(viewLifecycleOwner) { history ->
            historyAdapter.submitList(history)
            binding.tvSearchCount.text = history.size.toString()
        }

        // Cerrar sesión
        binding.btnSignOut.setOnClickListener { signOut() }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        GoogleSignIn.getClient(requireContext(), gso).signOut()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// ── History Adapter ───────────────────────────────────────────────────

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.VH>() {
    private var items = listOf<SearchHistory>()
    fun submitList(list: List<SearchHistory>) { items = list; notifyDataSetChanged() }

    inner class VH(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val h = items[position]
        holder.binding.tvQuery.text = h.query
        val typeLabel = if (h.type == "book") "Libro" else "Serie"
        val date = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(h.timestamp))
        holder.binding.tvType.text = "$typeLabel · $date"
    }
}
