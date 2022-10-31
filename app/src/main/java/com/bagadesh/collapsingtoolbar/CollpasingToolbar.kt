@file:OptIn(ExperimentalPagerApi::class)

package com.bagadesh.collapsingtoolbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.bagadesh.collapsingtoolbar.custom.customPagerTabIndicatorOffset
import com.bagadesh.collapsingtoolbar.custom.listOfBlendModes
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.max


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
                .height(50.dp)
        ) {
            scope.launch {
                pagerState.animateScrollToPage(it)
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
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
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
    key(data) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            val targetColor by derivedStateOf {
                val targetDistance by derivedStateOf {
                    (pagerState.targetPage - pagerState.currentPage).absoluteValue
                }
                val fraction by derivedStateOf {
                    (pagerState.currentPageOffset / max(targetDistance, 1)).absoluteValue
                }
                when {
                    index == pagerState.targetPage && fraction > 0.2f -> {
                        Color.Black
                    }
                    index == pagerState.currentPage && fraction > 0.5f -> {
                        Color.White
                    }
                    else -> {
                        when (index == pagerState.currentPage) {
                            false -> Color.White
                            true -> Color.Black
                        }
                    }
                }
            }
            val color by animateColorAsState(targetValue = targetColor, animationSpec = tween(durationMillis = 500))
//            var blendMode by remember {
//                mutableStateOf(listOfBlendModes.first())
//            }
//            LaunchedEffect(key1 = Unit) {
//                var index = 0
//                repeat(1000) {
//                    delay(1000)
//                    if (listOfBlendModes.size >= index - 1) {
//                        blendMode = listOfBlendModes[++index]
//                        println("datmug, $blendMode")
//                    }
//                }
//            }
            var onDraw: DrawScope.() -> Unit by remember { mutableStateOf({}) }
            Text(
                text = data.title,
                color = Color.Black,
                modifier = Modifier.drawBehind {
                    onDraw()
                },
                onTextLayout = {
                    onDraw = {
                        drawRect(
                            color = color,
                            size = size
                        )
                    }
                }
            )
//            Canvas(
//                modifier = Modifier
//                    .padding(10.dp)
//                    .size(10.dp, 10.dp)
//            ) {
//                this.drawContext.canvas
//            }
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