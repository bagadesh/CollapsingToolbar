@file:OptIn(ExperimentalPagerApi::class)

package com.bagadesh.collapsingtoolbar

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.bagadesh.collapsingtoolbar.custom.customPagerTabIndicatorOffset
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.max
import androidx.compose.ui.graphics.lerp
import kotlinx.coroutines.delay


@Composable
fun CollapsingToolbar() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp)
    )
    {
        val pagerState = rememberPagerState()
        val scope = rememberCoroutineScope()
        val tabResults = remember { getTabsList() }
        var searchText by remember { mutableStateOf("") }
        SearchBarUI(
            value = searchText,
            onValueChange = {
                searchText = it
            },
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .wrapContentHeight()
        )
        Text(
            text = "My Content Title",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(10.dp)
        )
        ShowTabsUI(
            pagerState = pagerState,
            tabResults = tabResults,
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
        ) {
            scope.launch {
                pagerState.animateScrollToPage(
                    page = it,
                    pageOffset = 0f,
                )
            }
        }
        HorizontalPager(state = pagerState, count = tabResults.size) {
            ShowListOfImages()
        }
    }
}

@Composable
fun SearchBarUI(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = colors.primary
        ),
        placeholder = {
            Text(text = "Search")
        }
    )
}

@Composable
fun ShowTabsUI(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    tabResults: List<TabData>,
    onTabClick: (index: Int) -> Unit
) {

    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        modifier = modifier,
        indicator = {
            TabRowDefaults.Indicator(
                modifier = Modifier.customPagerTabIndicatorOffset(
                    tabPositions = it,
                    pagerState = pagerState
                ),
                color = Color.White,
            )
        }
    ) {
        tabResults.forEachIndexed { index, tabData ->
            TabBarItem(
                pagerState = pagerState,
                modifier = Modifier
                    .height(50.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .noRippleClickable {
                        onTabClick(index)
                    }
                    .zIndex(1f),
                index = index,
                data = tabData
            )
        }
    }
}

@Composable
fun TabBarItem(
    modifier: Modifier = Modifier,
    index: Int,
    data: TabData,
    pagerState: PagerState
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val targetColor by pagerState.provideColorChange(index = index, surfaceColor = colors.surface, onSurfaceColor = colors.onSurface)
        // val color by animateColorAsState(targetValue = targetColor, animationSpec = tween(durationMillis = 500))
//
//        val targetColor = when(index == pagerState.currentPage) {
//            false -> colors.surface
//            true -> colors.onSurface
//        }

        Text(
            text = data.title,
            color = targetColor,
            modifier = Modifier,
        )
    }
}


fun PagerState.provideColorChange(
    index: Int,
    surfaceColor: Color,
    onSurfaceColor: Color
): State<Color> {
    return derivedStateOf {

        /**
         * Steps:
         * 1. Offset from 0 to 0.5 while index == targetIndex
         *      -> update target to surfaceColor to onSurfaceColor while value from 0 -> 0.5
         *      -> update current to onSurfaceColor to surfaceColor while value from 0 -> 0.5
         */

        when (index) {
            currentPage -> onSurfaceColor
            targetPage -> lerp(surfaceColor, onSurfaceColor, currentPageOffset.absoluteValue * 2)
            else -> surfaceColor
        }
    }
}

@Composable
fun ShowListOfImages() {
    val result = remember { getSampleListOfData() }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = result
        ) {
            ItemContentUI(title = it.title, id = it.id)
        }
    }
}

@Composable
fun ItemContentUI(title: String, id: Int) {
    Box(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
            .size(100.dp, 135.dp)
            .background(Color.Blue),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = "https://picsum.photos/200/300?random=${id}",
            contentDescription = "Random Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}