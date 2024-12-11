package com.example.bolatix.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bolatix.data.remote.response.DataALlMatch
import com.example.bolatix.databinding.FragmentHomeBinding
import com.example.bolatix.preference.UserPreferences
import com.example.bolatix.ui.adapters.MatchResultAdapter
import com.example.bolatix.ui.adapters.RecomendedAdapter
import com.example.bolatix.ui.viewmodels.RecomendedViewModel
import com.example.bolatix.ui.viewmodels.UpcomingTicketFragmentViewModel
import com.example.bolatix.utils.filterPastMatches
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var userPreferences: UserPreferences
    private val recomendedViewModel: RecomendedViewModel by viewModel()
    private val allMatchViewModel: UpcomingTicketFragmentViewModel by viewModel()

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private var currentPosition = 0
    private var matchResultList: List<DataALlMatch> = listOf()
    private var isUserScrolling = false

    private var isMatchResultReady = false
    private var isFavoriteReady = false
    private var isHistoryReady = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        userPreferences = UserPreferences(requireContext())
        handler = Handler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUserAndLoadData()
        loadMatchResult()
    }

    private fun observeUserAndLoadData() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            userPreferences.getUser().collect { user ->
                if (!user.id.isNullOrEmpty()) {
                    loadFavoriteAndHistory(user.id.toString())
                }
            }
        }
    }

    private fun loadFavoriteAndHistory(userId: String) {
        recomendedViewModel.getFavoriteByTeam(userId)
        recomendedViewModel.getFavoriteByHistory(userId)
        isLoading(true)

        recomendedViewModel.favoriteByTeam.observe(viewLifecycleOwner) { favoriteResult ->
            favoriteResult.onSuccess { favoriteList ->
                val rvFavorite = binding.rvFavoriteRecomendation
                val favoriteAdapter = RecomendedAdapter(favoriteList)
                rvFavorite.adapter = favoriteAdapter
                rvFavorite.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                rvFavorite.visibility = View.VISIBLE
                isFavoriteReady = true
                checkDataReadyAndHideLoading()
            }
            recomendedViewModel.favoriteByHistory.observe(viewLifecycleOwner) { historyResult ->
                historyResult.onSuccess { historyList ->
                    val rvHistory = binding.rvBuyRecomendation
                    if (historyList.isNotEmpty()) {
                        val historyAdapter = RecomendedAdapter(historyList)
                        rvHistory.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        rvHistory.adapter = historyAdapter
                        rvHistory.visibility = View.VISIBLE
                    } else {
                        rvHistory.visibility = View.GONE
                    }
                    isHistoryReady = true
                    checkDataReadyAndHideLoading()
                }
                historyResult.onFailure {
                    binding.layoutRecHistory.visibility = View.GONE
                    binding.rvFavoriteRecomendation.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    isHistoryReady = true
                    checkDataReadyAndHideLoading()
                }
            }
        }
    }

    private fun checkDataReadyAndHideLoading() {
        if (isMatchResultReady && isFavoriteReady && isHistoryReady) {
            isLoading(false)
        }
    }

    private fun loadMatchResult() {
        allMatchViewModel.getAllMatch()
        allMatchViewModel.upTickets.observe(viewLifecycleOwner) { result ->
            result.onSuccess { v ->
                matchResultList = filterPastMatches(v).reversed()
                currentPosition = 0
                val rvMatchResult: RecyclerView = binding.rvMatchResult
                rvMatchResult.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                val adapter = MatchResultAdapter(matchResultList)
                rvMatchResult.adapter = adapter
                setupAutoScroll(rvMatchResult)
                isMatchResultReady = true
                checkDataReadyAndHideLoading()
            }
        }
    }

    private fun setupAutoScroll(rvMatchResult: RecyclerView) {
        handler = Handler()
        runnable = Runnable {
            if (!isUserScrolling && matchResultList.isNotEmpty()) {
                currentPosition = (currentPosition + 1) % matchResultList.size
                rvMatchResult.smoothScrollToPosition(currentPosition)
            }
            handler.postDelayed(runnable, 2800)
        }
        handler.postDelayed(runnable, 2800)
        rvMatchResult.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isUserScrolling = true
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isUserScrolling = false
                }
            }
        })
    }

    private fun isLoading(isLoading: Boolean) {
        val loadLayout = if (isLoading) View.GONE else View.VISIBLE
        with(binding) {
            loading.root.visibility = if (isLoading) View.VISIBLE else View.GONE
            layoutMatchResult.visibility = loadLayout
            layoutRecFavorite.visibility = loadLayout
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}