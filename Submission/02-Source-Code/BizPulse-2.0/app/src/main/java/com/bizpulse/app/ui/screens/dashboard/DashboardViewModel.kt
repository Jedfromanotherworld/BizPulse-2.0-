package com.bizpulse.app.ui.screens.dashboard
import com.bizpulse.app.data.repository.AppStore
import com.bizpulse.app.ui.screens.ScreenViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel class DashboardViewModel @Inject constructor(store: AppStore): ScreenViewModel(store)
