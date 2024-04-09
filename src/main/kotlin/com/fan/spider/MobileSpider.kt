package com.fan.spider

//import com.fan.filter.TitleFilter.filterByMandatoryKeyWord
import com.fan.response.SearchResult

private const val ROWS = 200

object MobileSpider {
    fun useMobileApi(keyword: String) {
//        val total = ArrayList<Data>()
//        var index = 1
//        var data = SearchClient.search(keyword, index, ROWS)
//        total.add(data)
//        while (data.hasNext) {
//            sleep(1000)
//            index++
//            data = SearchClient.search(keyword, index, ROWS)
//            total.add(data)
//        }
//
//        val validResult = total.map { it.searchResultList }.flatten().asSequence().filter(filterByMandatoryKeyWords())
//        println("==========共有 %d 条有效数据==========".format(validResult.count()))
//        val infoCodes = validResult
//            .map { searchResult -> searchResult.attach }
//            .flatten()
//            .filter { it.infocode.isNotEmpty() }
//            .map { it.infocode }.toList()
//
//        infoCodes.forEach { infoCode ->
//            run {
//                sleep(1000)
//                val notice = fetchDetail(infoCode)
//                extractCompanyName(notice)
//            }
//        }
    }

    private fun filterByMandatoryKeyWords() = { searchResult: SearchResult ->
//        filterByMandatoryKeyWord(searchResult.title)
    }
}