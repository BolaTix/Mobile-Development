package com.example.bolatix.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bolatix.databinding.FragmentUpcomingMatchBinding
import com.example.bolatix.ui.adapters.UpcomingTicketAdapter
import com.example.bolatix.ui.viewmodels.UpcomingTicketFragmentViewModel
import com.example.bolatix.utils.filterUpcomingMatches
import com.example.bolatix.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class UpcomingTicketFragment : Fragment() {

    private var _binding: FragmentUpcomingMatchBinding? = null
    private val binding get() = _binding!!
    private val upTicketViewModel: UpcomingTicketFragmentViewModel by viewModel()
    private lateinit var upTicketAdapter: UpcomingTicketAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingMatchBinding.inflate(inflater, container, false)
        upTicketAdapter = UpcomingTicketAdapter()
        binding.rvUpcomingMatch.layoutManager = LinearLayoutManager(context)
        binding.rvUpcomingMatch.adapter = upTicketAdapter
        binding.pbLoading.visibility = View.VISIBLE
        upTicketViewModel.getAllMatch()
        upTicketViewModel.upTickets.observe(viewLifecycleOwner) { result ->
            result.onSuccess { v ->
                val filter = filterUpcomingMatches(v)
                upTicketAdapter.submitList(filter)
                binding.pbLoading.visibility = View.GONE
            }
            result.onFailure {
                showToast(requireContext(), "Sepertinya terjadi kesalahan")
                binding.pbLoading.visibility = View.GONE
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
