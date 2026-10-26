package com.bizpulse.app.ui.screens.analytics
import com.bizpulse.app.data.repository.AppStore
import com.bizpulse.app.ui.screens.ScreenViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel class AnalyticsViewModel @Inject constructor(store: AppStore): ScreenViewModel(store)
