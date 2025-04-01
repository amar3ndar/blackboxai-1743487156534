package com.example.todoapp.model

import java.util.UUID

/**
 * Represents the four quadrants of the Eisenhower Matrix
 */
enum class EisenhowerQuadrant {
    URGENT_IMPORTANT,      // Do First
    NOT_URGENT_IMPORTANT,  // Schedule
    URGENT_NOT_IMPORTANT,  // Delegate
    NOT_URGENT_NOT_IMPORTANT // Eliminate
}

/**
 * Represents a single Todo item in the application
 */
data class Todo(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val quadrant: EisenhowerQuadrant,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun createTodo(
            title: String,
            description: String = "",
            quadrant: EisenhowerQuadrant
        ): Todo {
            require(title.isNotBlank()) { "Title cannot be empty" }
            return Todo(
                title = title.trim(),
                description = description.trim(),
                quadrant = quadrant
            )
        }
    }
}

/**
 * Represents the UI state of a quadrant in the Eisenhower Matrix
 */
data class QuadrantState(
    val quadrant: EisenhowerQuadrant,
    val title: String,
    val todos: List<Todo> = emptyList()
)

/**
 * Represents possible error states in the Todo application
 */
sealed class TodoError : Exception() {
    data class EmptyTitle(override val message: String = "Title cannot be empty") : TodoError()
    data class TodoNotFound(override val message: String = "Todo not found") : TodoError()
    data class InvalidQuadrant(override val message: String = "Invalid quadrant") : TodoError()
}