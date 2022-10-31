package com.bagadesh.collapsingtoolbar


fun getSampleListOfData(): List<SampleData> {
    return mutableListOf<SampleData>().apply {
        repeat(100) {
            add(
                SampleData(
                    id = it + 1,
                    title = "Item $it"
                )
            )
        }
    }
}

fun getTabsList(): List<TabData> {
    return mutableListOf<TabData>().apply {
        repeat(10) {
            add(
                TabData(
                    id = it + 1,
                    title = "Tab $it"
                )
            )
        }
    }
}

data class SampleData(
    val id: Int,
    val title: String
)

data class TabData(
    val id: Int,
    val title: String
)