package com.wxl.dyttcrawler.processor

import com.google.common.base.CharMatcher
import com.wxl.dyttcrawler.core.DyttPattern.GNDY_DETAIL_PATH_PATTERN
import com.wxl.dyttcrawler.domain.DyttMovie
import com.wxl.dyttcrawler.domain.DyttReleaseDate
import com.wxl.dyttcrawler.urlhandler.PriorityUrlCalculator
import com.wxl.dyttcrawler.urlhandler.UrlFilter
import com.wxl.dyttcrawler.utils.DateUtils
import org.jsoup.Jsoup
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import us.codecraft.webmagic.Page
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern

/**
 * Create by wuxingle on 2021/10/12
 * 高光电影详情页处理
 */

private val SPACE = 32.toChar().toString()

//p标签正则
private val P_CONTENT_START = "◎"
private val P_TRANSLATE_NAME = Pattern.compile("◎译.{0,2}名:*\\s*(.+)")
private val P_NAME = Pattern.compile("◎片.{0,2}名:*\\s*(.+)")
private val P_YEAR = Pattern.compile("◎年.{0,2}代:*\\s*(.+)")
private val P_ORIGIN_PLACE = Pattern.compile("◎产.{0,2}地:*\\s*(.+)")
private val P_CATEGORY = Pattern.compile("◎类.{0,2}别:*\\s*(.+)")
private val P_LANGUAGE = Pattern.compile("◎语.{0,2}言:*\\s*(.+)")
private val P_WORDS = Pattern.compile("◎字.{0,2}幕:*\\s*(.+)")
private val P_SCORES = Pattern.compile("◎豆瓣评分:*\\s*(.+)")
private val P_RELEASE_DATE = Pattern.compile("◎上映日期:*\\s*(.+)")
private val P_DIRECTOR = Pattern.compile("◎导.{0,2}演:*\\s*(.+)")
private val P_SCREEN_WRITER = Pattern.compile("◎编.{0,2}剧:*\\s*(.+)")
private val P_ACT = Pattern.compile("◎主.{0,2}演:*\\s*(.+)")
private val P_TAGS = Pattern.compile("◎标.{0,2}签:*\\s*(.+)")
private val P_AWARDS = Pattern.compile("◎获奖情况:*\\s*(.*)")
private val P_DESC = Pattern.compile("◎简.{0,2}介:*\\s*(.*)")
private val P_FILE_SIZE = Pattern.compile("◎文件大小:*\\s*(.+)")

// 评分
private val SCORE_PATTERN = Pattern.compile("([0-9.]+)/10\\s*from\\s*([0-9,]+)\\s*users")

// 文件大小
private val SIZE_PATTERN = Pattern.compile("([0-9.]+).+")

// 发布日期
private val RELEASE_DATE_PATTERN = Pattern.compile("([0-9]{4}-[0-9]{2}-?[0-9]{0,2})\\((.+)\\)")

// 产地,匹配中文
private val PLACE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]+")

// 日期格式化
private val DATE_DAY_FORMATTER1 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val DATE_DAY_FORMATTER2 = DateTimeFormatter.ofPattern("yyyy-MM")

@Order(0)
@Component
class DyttGndyMoviePageProcessor(
    priorityUrlCalculator: PriorityUrlCalculator,
    urlFilter: UrlFilter
) : AbstractDyttProcessor(priorityUrlCalculator, urlFilter) {

    private val linkStarts = listOf("ftp", "magnet")

    override fun process(page: Page) {
        val matcher = GNDY_DETAIL_PATH_PATTERN.matcher(page.url.get())
        if (!matcher.find()) {
            page.setSkip(true)
            return
        }

        val movie = DyttMovie()
        // 设置更新时间
        movie.updateTime = Date()

        val id = matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4)
        movie.id = id
        movie.url = page.url.get()

        val root = page.html.xpath("//div[@class='bd3']/div[@class='bd3l']/div[@class='co_area2']")

        // 标题
        val title = root.xpath("//div[@class='title_all']/h1/font/text()").get()
        movie.title = wordFilter(title)

        // 发布时间
        val publishDate = root.xpath("//div[@class=co_content8]/ul/text()")
            .regex("发布时间.+([0-9]{4}-[0-9]{2}-[0-9]{2})")
            .get()
        if (!publishDate.isNullOrBlank()) {
            movie.publishDate = DateUtils.parseDate(publishDate, DATE_DAY_FORMATTER1)
        }

        // 链接
        val links = root.css("a[href]", "href").all()
        val downloads = mutableListOf<String>()
        for (link in links) {
            if (isDownloadLink(link)) {
                downloads.add(link)
            } else {
                addLink(page, link)
            }
        }
        movie.downLinks = downloads

        // 内容处理
        val nodes: List<String>
        val contentDiv = root.css("div#Zoom span div").all()
        if (contentDiv.isEmpty()) {
            // 封面地址
            val p = root.css("div#Zoom span p")

            val picUrl = p.css("img[src]", "src").get()
            movie.picUrl = picUrl

            // 内容
            val all = p.regex("<br>(.+)<br>").get().split("<br>")
            nodes = all.map { wordFilter(it) }
                .filter { it.isNotBlank() }
                .filterNot { it.startsWith("<") }
        } else {
            nodes = mutableListOf()
            var findPic = false
            for (div in contentDiv) {
                val doc = Jsoup.parse(div)
                val img = doc.select("img[src]").first()
                if (!findPic && img != null) {
                    val src = img.attr("src")
                    movie.picUrl = src
                    findPic = true
                } else if (img == null) {
                    nodes.add(wordFilter(doc.text()))
                } else {
                    break
                }
            }
        }

        // 可能是列表页
        if (nodes.isNotEmpty()) {
            setTranslateName(movie, nodes)
            setName(movie, nodes)
            setYear(movie, nodes)
            setOriginPlace(movie, nodes)
            setCategory(movie, nodes)
            setLanguage(movie, nodes)
            setWords(movie, nodes)
            setScore(movie, nodes)
            setReleaseDate(movie, nodes)
            setDirector(movie, nodes)
            setScreenWriter(movie, nodes)
            setAct(movie, nodes)
            setAwards(movie, nodes)
            setTags(movie, nodes)
            setDesc(movie, nodes)
            setFileSize(movie, nodes)

            putObject(page, movie)
        }
    }

    override fun match(page: Page): Boolean =
        page.url.regex(GNDY_DETAIL_PATH_PATTERN.pattern()).match()

    /**
     * 去掉特殊字符
     */
    private fun wordFilter(text: String): String {
        val s = text.replace("&nbsp;", SPACE)
        return CharMatcher.whitespace().replaceFrom(s, SPACE).trim()
    }

    /**
     * 是否为下载地址
     */
    private fun isDownloadLink(link: String): Boolean {
        for (linkStart in linkStarts) {
            if (link.startsWith(linkStart)) {
                return true
            }
        }
        return false
    }

    private fun setTranslateName(movie: DyttMovie, nodes: List<String>) {
        val translateName =
            collectList(findBrValue(nodes, P_TRANSLATE_NAME).split("/"))
        movie.translateNames = translateName
    }

    private fun setName(movie: DyttMovie, nodes: List<String>) {
        val name = findBrValue(nodes, P_NAME)
        movie.name = name
    }

    private fun setYear(movie: DyttMovie, nodes: List<String>) {
        val year = findBrValue(nodes, P_YEAR)
        if (year.isNotBlank()) {
            val y = wordFilter(year).toInt()
            movie.year = y
        }
    }

    private fun setOriginPlace(movie: DyttMovie, nodes: List<String>) {
        val places = findBrValue(nodes, P_ORIGIN_PLACE)

        val list = mutableListOf<String>()
        if (places.isNotBlank()) {
            val matcher = PLACE_PATTERN.matcher(places)
            while (matcher.find()) {
                val place = matcher.group()
                list.add(place)
            }
        }
        movie.originPlace = list
    }

    private fun setCategory(movie: DyttMovie, nodes: List<String>) {
        val category = findBrValue(nodes, P_CATEGORY)
        val collect = collectList(category.split("/"))
        movie.category = collect
    }

    private fun setLanguage(movie: DyttMovie, nodes: List<String>) {
        val language = findBrValue(nodes, P_LANGUAGE)
        val collect = collectList(language.split("/", ","))
        movie.language = collect
    }

    private fun setWords(movie: DyttMovie, nodes: List<String>) {
        val words = findBrValue(nodes, P_WORDS)
        movie.words = words
    }

    private fun setScore(movie: DyttMovie, nodes: List<String>) {
        val scoreValue = findBrValue(nodes, P_SCORES)
        val matcher = SCORE_PATTERN.matcher(scoreValue)
        if (matcher.find()) {
            movie.score = matcher.group(1).toDouble()
            movie.scoreNums = matcher.group(2).replace(",", "").toInt()
        }
    }

    private fun setReleaseDate(movie: DyttMovie, nodes: List<String>) {
        val releaseDateStr = findBrValue(nodes, P_RELEASE_DATE)
        val collect = collectList(releaseDateStr.split("/"))
            .mapNotNull {
                val matcher = RELEASE_DATE_PATTERN.matcher(it)
                if (matcher.find()) {
                    val dateStr = matcher.group(1)
                    val releaseDate = DyttReleaseDate()
                    releaseDate.date = DateUtils.parseDate(dateStr, DATE_DAY_FORMATTER1, DATE_DAY_FORMATTER2)
                    releaseDate.place = matcher.group(2)
                    return@mapNotNull releaseDate
                }
                return@mapNotNull null
            }
        movie.releaseDates = collect
    }

    private fun setDirector(movie: DyttMovie, nodes: List<String>) {
        val director = findBrValue(nodes, P_DIRECTOR)
        val collect = collectList(director.split("\n", "/"))
        movie.director = collect
    }

    private fun setScreenWriter(movie: DyttMovie, nodes: List<String>) {
        val screenwriter = findBrValue(nodes, P_SCREEN_WRITER)
        val collect = collectList(screenwriter.split("\n", "/"))
        movie.screenwriter = collect
    }

    private fun setAct(movie: DyttMovie, nodes: List<String>) {
        val act = findBrValue(nodes, P_ACT)
        val collect = collectList(act.split("\n", "/"))
        movie.act = collect
    }

    private fun setTags(movie: DyttMovie, nodes: List<String>) {
        val tags = findBrValue(nodes, P_TAGS)
        val collect = collectList(tags.split("|", "\\"))
        movie.tags = collect
    }

    private fun setAwards(movie: DyttMovie, nodes: List<String>) {
        val awards = findBrValue(nodes, P_AWARDS)
        val collect = collectList(awards.split("\n"))
        movie.awards = collect
    }

    private fun setDesc(movie: DyttMovie, nodes: List<String>) {
        val desc = findBrValue(nodes, P_DESC)
        movie.desc = desc
    }

    private fun setFileSize(movie: DyttMovie, nodes: List<String>) {
        val size = findBrValue(nodes, P_FILE_SIZE)
        val matcher = SIZE_PATTERN.matcher(size)
        if (matcher.find()) {
            val group = matcher.group(1)
            movie.fileSize = group.toDouble()
        }
    }

    private fun findBrValue(nodes: List<String>, pattern: Pattern): String {
        var hasNext = false
        val value = StringBuilder()
        for (node in nodes) {
            val text = node.trim()

            if (hasNext) {
                if (!text.startsWith(P_CONTENT_START)) {
                    value.append("\n").append(text)
                } else {
                    break
                }
            } else {
                val matcher = pattern.matcher(text)
                if (matcher.find()) {
                    val group = matcher.group(1)
                    value.append(group.trim())
                    hasNext = true
                }
            }
        }
        return value.toString().trim()
    }

    private fun collectList(arrays: List<String>): List<String> {
        return arrays.map { it.trim() }
            .filter { it.isNotBlank() }
    }

}
