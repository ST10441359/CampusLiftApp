package com.example.campuslift.Data.dto

import com.google.gson.annotations.SerializedName

data class NotificationDto(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("type") val type: String,          // "booking" | "message" | "rating" | "system"
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: String?,
    @SerializedName("isRead") val isRead: Boolean
)

data class NotificationListResponse(
    @SerializedName("items") val items: List<NotificationDto>,
    @SerializedName("limit") val limit: Int,
    @SerializedName("offset") val offset: Int,
    @SerializedName("count") val count: Int
)

data class UnreadCountResponse(
    @SerializedName("unread") val unread: Int
)

data class MarkAllReadResponse(
    @SerializedName("markedRead") val markedRead: Int
)

data class UpdateNotificationRequest(
    @SerializedName("isRead") val isRead: Boolean
)