package com.example.campuslift.Data.repository

import com.example.campuslift.Data.dto.MarkAllReadResponse
import com.example.campuslift.Data.dto.NotificationDto
import com.example.campuslift.Data.dto.NotificationListResponse
import com.example.campuslift.Data.dto.UnreadCountResponse
import com.example.campuslift.Data.dto.UpdateNotificationRequest
import com.example.campuslift.Data.remote.RetrofitClient

class NotificationRepository {
    private val api = RetrofitClient.api

    suspend fun list(
        unreadOnly: Boolean? = null,
        limit: Int = 30,
        offset: Int = 0
    ): Result<NotificationListResponse> =
        runCatching { api.notifications(unreadOnly, limit, offset) }

    suspend fun unreadCount(): Result<UnreadCountResponse> =
        runCatching { api.unreadCount() }

    suspend fun markRead(id: String, isRead: Boolean = true): Result<NotificationDto> =
        runCatching { api.markNotificationRead(id, UpdateNotificationRequest(isRead)) }

    suspend fun markAllRead(): Result<MarkAllReadResponse> =
        runCatching { api.markAllNotificationsRead() }

    suspend fun delete(id: String): Result<Unit> =
        runCatching { api.deleteNotification(id) }
}