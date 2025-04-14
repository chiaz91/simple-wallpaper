package com.cy.practice.wallpaper.ui.screen.gallery.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cy.practice.wallpaper.shared.Constants
import com.cy.practice.wallpaper.shared.conditional


@Composable
fun PhotoSearchBar(
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    PhotoSearchBar(
        query,
        { query = it },
        onSearch,
        expanded,
        { expanded = it },
        modifier = modifier,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    trailingIconOnlyWhenExpanded: Boolean = true,
) {
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = {
                    onExpandedChange(false)
                    onSearch(query)
                },
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                placeholder = { Text("Search for topic") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (!trailingIconOnlyWhenExpanded || expanded) {
                        IconButton(onClick = {
                            if (query.isNotBlank()) {
                                onQueryChange("")
                            } else {
                                onExpandedChange(false)
                            }
                        }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
            )
        },
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        windowInsets = WindowInsets.systemBars,
        modifier = Modifier
            .conditional(!expanded) {
                // seems padding will also apply to expended state, breaking immerse experience
                // hence only apply modifier conditional
                modifier
            }
    ) {
        // suggestion or search results
        SuggestTopcis(
            onTopicClick = {
                onQueryChange(it)
                onExpandedChange(false)
                onSearch(it)
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuggestTopcis(
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Constants.TOPICS.forEach { topic ->
            Text(
                text = topic,
                softWrap = false,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(10.dp)
                    .clickable {
                        onTopicClick(topic)
                    },
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoSearchBarPreview(modifier: Modifier = Modifier) {
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            PhotoSearchBar(onSearch = {})
        }
    }
}