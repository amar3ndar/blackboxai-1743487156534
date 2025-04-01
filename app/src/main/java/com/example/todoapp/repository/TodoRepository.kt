package com.example.todoapp.repository

import com.example.todoapp.model.EisenhowerQuadrant
import com.example.todoapp.model.Todo
import com.example.todoapp.model.TodoError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Repository class that handles all Todo-related data operations
 */
class TodoRepository {
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: Flow<List<Todo>> = _todos

    /**
     * Get todos filtered by quadrant
     */
    fun getTodosByQuadrant(quadrant: EisenhowerQuadrant): Flow<List<Todo>> {
        return todos.map { todoList ->
            todoList.filter { it.quadrant == quadrant }
                .sortedByDescending { it.createdAt }
        }
    }

    /**
     * Add a new todo
     */
    suspend fun addTodo(title: String, description: String, quadrant: EisenhowerQuadrant): Result<Todo> {
        return try {
            val newTodo = Todo.createTodo(
                title = title,
                description = description,
                quadrant = quadrant
            )
            _todos.update { currentList -> currentList + newTodo }
            Result.success(newTodo)
        } catch (e: IllegalArgumentException) {
            Result.failure(TodoError.EmptyTitle())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update an existing todo
     */
    suspend fun updateTodo(todo: Todo): Result<Todo> {
        return try {
            val currentList = _todos.value
            val index = currentList.indexOfFirst { it.id == todo.id }
            
            if (index == -1) {
                Result.failure(TodoError.TodoNotFound())
            } else {
                _todos.update { list ->
                    list.toMutableList().apply {
                        set(index, todo)
                    }
                }
                Result.success(todo)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Toggle todo completion status
     */
    suspend fun toggleTodoCompletion(todoId: String): Result<Todo> {
        return try {
            val todo = _todos.value.find { it.id == todoId }
                ?: return Result.failure(TodoError.TodoNotFound())
            
            val updatedTodo = todo.copy(isCompleted = !todo.isCompleted)
            updateTodo(updatedTodo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Move todo to different quadrant
     */
    suspend fun moveTodoToQuadrant(todoId: String, newQuadrant: EisenhowerQuadrant): Result<Todo> {
        return try {
            val todo = _todos.value.find { it.id == todoId }
                ?: return Result.failure(TodoError.TodoNotFound())
            
            val updatedTodo = todo.copy(quadrant = newQuadrant)
            updateTodo(updatedTodo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete a todo
     */
    suspend fun deleteTodo(todoId: String): Result<Unit> {
        return try {
            _todos.update { currentList ->
                currentList.filterNot { it.id == todoId }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Clear all completed todos
     */
    suspend fun clearCompletedTodos(): Result<Unit> {
        return try {
            _todos.update { currentList ->
                currentList.filterNot { it.isCompleted }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: TodoRepository? = null

        fun getInstance(): TodoRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TodoRepository().also { INSTANCE = it }
            }
        }
    }
}