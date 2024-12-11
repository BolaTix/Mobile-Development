package com.example.bolatix.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bolatix.databinding.FragmentStandingsBinding
import com.example.bolatix.ui.adapters.StandingsAdapter
import com.example.bolatix.ui.viewmodels.StandingsFragmentViewModel
import com.example.bolatix.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class StandingsFragment : Fragment() {

    private var _binding: FragmentStandingsBinding? = null
    private val binding get() = _binding!!
    private val standingsViewModel: StandingsFragmentViewModel by viewModel()
    private lateinit var standingsAdapter: StandingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStandingsBinding.inflate(inflater, container, false)
        standingsAdapter = StandingsAdapter()
        binding.rvStandings.layoutManager = LinearLayoutManager(context)
        binding.rvStandings.adapter = standingsAdapter
        binding.loading.root.visibility = View.VISIBLE
        standingsViewModel.getStandings()
        standingsViewModel.standings.observe(viewLifecycleOwner) { result ->
            result.onSuccess { standings ->
                standingsAdapter.submitList(standings)
                binding.content.visibility = View.VISIBLE
                binding.loading.root.visibility = View.GONE
            }
            result.onFailure {
                showToast(requireContext(), "Sepertinya terjadi kesalahan")
                binding.content.visibility = View.GONE
                binding.loading.root.visibility = View.GONE
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
