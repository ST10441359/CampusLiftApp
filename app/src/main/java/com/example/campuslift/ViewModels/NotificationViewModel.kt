package com.example.campuslift.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuslift.Data.dto.NotificationDto
import com.example.campuslift.Data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repo: NotificationRepository = NotificationRepository()
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationDto>>(emptyList())
    val notifications: StateFlow<List<NotificationDto>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun load(unreadOnly: Boolean? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repo.list(unreadOnly)
                .onSuccess { _notifications.value = it.items }
                .onFailure { _error.value = it.message ?: "Failed to load notifications" }
            _isLoading.value = false
        }
    }

    fun refreshUnreadCount() {
        viewModelScope.launch {
            repo.unreadCount()
                .onSuccess { _unreadCount.value = it.unread }
                .onFailure { /* silently fail — badge count */ }
        }
    }

    fun markRead(id: String) {
        viewModelScope.launch {
            repo.markRead(id, true)
                .onSuccess {
                    load()                // refresh list
                    refreshUnreadCount()  // refresh badge
                }
                .onFailure { _error.value = it.message }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            repo.markAllRead()
                .onSuccess {
                    load()
                    refreshUnreadCount()
                }
                .onFailure { _error.value = it.message }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            repo.delete(id)
                .onSuccess {
                    load()
                    refreshUnreadCount()
                }
                .onFailure { _error.value = it.message }
        }
    }

    fun clearError() {
        _error.value = null
    }
}