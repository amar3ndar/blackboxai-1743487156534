package com.example.todoapp.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.model.EisenhowerQuadrant
import com.example.todoapp.model.QuadrantState
import com.example.todoapp.ui.components.TodoCard
import com.example.todoapp.viewmodel.TodoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EisenhowerMatrixScreen(
    viewModel: TodoViewModel = viewModel(factory = TodoViewModel.Factory())
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddTodoDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Todo Eisenhower") },
                actions = {
                    IconButton(onClick = { viewModel.clearCompletedTodos() }) {
                        Icon(Icons.Default.Clear, "Clear completed")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddTodoDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Todo")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                // Matrix Grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // First Column (Urgent)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    ) {
                        QuadrantSection(
                            quadrantState = uiState.quadrants.find { it.quadrant == EisenhowerQuadrant.URGENT_IMPORTANT },
                            onToggleCompletion = viewModel::toggleTodoCompletion,
                            onDelete = viewModel::deleteTodo,
                            onMoveToQuadrant = viewModel::moveTodoToQuadrant,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        QuadrantSection(
                            quadrantState = uiState.quadrants.find { it.quadrant == EisenhowerQuadrant.URGENT_NOT_IMPORTANT },
                            onToggleCompletion = viewModel::toggleTodoCompletion,
                            onDelete = viewModel::deleteTodo,
                            onMoveToQuadrant = viewModel::moveTodoToQuadrant,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Second Column (Not Urgent)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    ) {
                        QuadrantSection(
                            quadrantState = uiState.quadrants.find { it.quadrant == EisenhowerQuadrant.NOT_URGENT_IMPORTANT },
                            onToggleCompletion = viewModel::toggleTodoCompletion,
                            onDelete = viewModel::deleteTodo,
                            onMoveToQuadrant = viewModel::moveTodoToQuadrant,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        QuadrantSection(
                            quadrantState = uiState.quadrants.find { it.quadrant == EisenhowerQuadrant.NOT_URGENT_NOT_IMPORTANT },
                            onToggleCompletion = viewModel::toggleTodoCompletion,
                            onDelete = viewModel::deleteTodo,
                            onMoveToQuadrant = viewModel::moveTodoToQuadrant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Add Todo Dialog
            if (showAddTodoDialog) {
                AddTodoDialog(
                    onDismiss = { showAddTodoDialog = false },
                    onAddTodo = { title, description, quadrant ->
                        viewModel.addTodo(title, description, quadrant)
                        showAddTodoDialog = false
                    }
                )
            }

            // Error Handling
            LaunchedEffect(uiState.error) {
                uiState.error?.let { error ->
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = error,
                            duration = SnackbarDuration.Short
                        )
                        viewModel.clearError()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuadrantSection(
    quadrantState: QuadrantState?,
    onToggleCompletion: (String) -> Unit,
    onDelete: (String) -> Unit,
    onMoveToQuadrant: (String, EisenhowerQuadrant) -> Unit,
    modifier: Modifier = Modifier
) {
    if (quadrantState == null) return

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = quadrantState.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            if (quadrantState.todos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = quadrantState.todos,
                        key = { it.id }
                    ) { todo ->
                        TodoCard(
                            todo = todo,
                            onToggleCompletion = onToggleCompletion,
                            onDelete = onDelete,
                            onMoveToQuadrant = onMoveToQuadrant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddTodoDialog(
    onDismiss: () -> Unit,
    onAddTodo: (String, String, EisenhowerQuadrant) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedQuadrant by remember { mutableStateOf(EisenhowerQuadrant.URGENT_IMPORTANT) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Todo") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Quadrant:", style = MaterialTheme.typography.labelLarge)
                EisenhowerQuadrant.values().forEach { quadrant ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedQuadrant == quadrant,
                            onClick = { selectedQuadrant = quadrant }
                        )
                        Text(
                            text = when (quadrant) {
                                EisenhowerQuadrant.URGENT_IMPORTANT -> "Do First"
                                EisenhowerQuadrant.NOT_URGENT_IMPORTANT -> "Schedule"
                                EisenhowerQuadrant.URGENT_NOT_IMPORTANT -> "Delegate"
                                EisenhowerQuadrant.NOT_URGENT_NOT_IMPORTANT -> "Eliminate"
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onAddTodo(title, description, selectedQuadrant)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}