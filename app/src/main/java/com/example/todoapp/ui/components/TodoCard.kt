package com.example.todoapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todoapp.model.EisenhowerQuadrant
import com.example.todoapp.model.Todo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoCard(
    todo: Todo,
    onToggleCompletion: (String) -> Unit,
    onDelete: (String) -> Unit,
    onMoveToQuadrant: (String, EisenhowerQuadrant) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(if (todo.isCompleted) 0.6f else 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Checkbox(
                        checked = todo.isCompleted,
                        onCheckedChange = { onToggleCompletion(todo.id) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        EisenhowerQuadrant.values().forEach { quadrant ->
                            if (quadrant != todo.quadrant) {
                                DropdownMenuItem(
                                    text = { Text(getQuadrantTitle(quadrant)) },
                                    onClick = {
                                        onMoveToQuadrant(todo.id, quadrant)
                                        showMenu = false
                                    }
                                )
                            }
                        }
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                onDelete(todo.id)
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete todo"
                                )
                            }
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = todo.description.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = todo.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 44.dp, top = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getQuadrantTitle(quadrant: EisenhowerQuadrant): String {
    return when (quadrant) {
        EisenhowerQuadrant.URGENT_IMPORTANT -> "Move to Do First"
        EisenhowerQuadrant.NOT_URGENT_IMPORTANT -> "Move to Schedule"
        EisenhowerQuadrant.URGENT_NOT_IMPORTANT -> "Move to Delegate"
        EisenhowerQuadrant.NOT_URGENT_NOT_IMPORTANT -> "Move to Eliminate"
    }
}