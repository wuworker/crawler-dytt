package com.wxl.dyttcrawler.processor;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.wxl.dyttcrawler.domain.DyttMovie;
import com.wxl.dyttcrawler.domain.DyttReleaseDate;
import com.wxl.dyttcrawler.urlhandler.PriorityUrlCalculator;
import com.wxl.dyttcrawler.urlhandler.UrlFilter;
import com.wxl.dyttcrawler.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Selectable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.wxl.dyttcrawler.core.DyttConstants.GNDY_DETAIL_PATH_PATTERN;

/**
 * Create by wuxingle on 2020/5/10
 * 高光电影详情页处理
 */
@Slf4j
@Order(0)
@Component
public class DyttGndyMoviePageProcessor extends AbstractDyttProcessor {

    private static final String SPACE = String.valueOf((char) 32);

    private static final String P_CONTENT_START = "◎";
    private static final Pattern P_TRANSLATE_NAME = Pattern.compile("◎译.{0,2}名:*\\s*(.+)");
    private static final Pattern P_NAME = Pattern.compile("◎片.{0,2}名:*\\s*(.+)");
    private static final Pattern P_YEAR = Pattern.compile("◎年.{0,2}代:*\\s*(.+)");
    private static final Pattern P_ORIGIN_PLACE = Pattern.compile("◎产.{0,2}地:*\\s*(.+)");
    private static final Pattern P_CATEGORY = Pattern.compile("◎类.{0,2}别:*\\s*(.+)");
    private static final Pattern P_LANGUAGE = Pattern.compile("◎语.{0,2}言:*\\s*(.+)");
    private static final Pattern P_WORDS = Pattern.compile("◎字.{0,2}幕:*\\s*(.+)");
    private static final Pattern P_SCORES = Pattern.compile("◎豆瓣评分:*\\s*(.+)");
    private static final Pattern P_RELEASE_DATE = Pattern.compile("◎上映日期:*\\s*(.+)");
    private static final Pattern P_DIRECTOR = Pattern.compile("◎导.{0,2}演:*\\s*(.+)");
    private static final Pattern P_SCREEN_WRITER = Pattern.compile("◎编.{0,2}剧:*\\s*(.+)");
    private static final Pattern P_ACT = Pattern.compile("◎主.{0,2}演:*\\s*(.+)");
    private static final Pattern P_TAGS = Pattern.compile("◎标.{0,2}签:*\\s*(.+)");
    private static final Pattern P_AWARDS = Pattern.compile("◎获奖情况:*\\s*(.*)");
    private static final Pattern P_DESC = Pattern.compile("◎简.{0,2}介:*\\s*(.*)");
    private static final Pattern P_FILE_SIZE = Pattern.compile("◎文件大小:*\\s*(.+)");

    // 评分
    private Pattern scorePattern = Pattern.compile("([0-9.]+)/10\\s*from\\s*([0-9,]+)\\s*users");
    // 文件大小
    private Pattern sizePattern = Pattern.compile("([0-9.]+).+");
    // 发布日期
    private Pattern releaseDatePattern = Pattern.compile("([0-9]{4}-[0-9]{2}-?[0-9]{0,2})\\((.+)\\)");
    // 产地,匹配中文
    private Pattern placePattern = Pattern.compile("[\\u4e00-\\u9fa5]+");

    // 日期格式化
    private DateTimeFormatter dateDayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private List<String> linkStarts = Lists.newArrayList("ftp", "magnet");


    @Autowired
    public DyttGndyMoviePageProcessor(PriorityUrlCalculator priorityCalculator,
                                      UrlFilter urlFilter) {
        super(priorityCalculator, urlFilter);
    }

    @Override
    public void process(Page page) {
        DyttMovie movie = new DyttMovie();
        // 设置更新时间
        movie.setUpdateTime(new Date());

        Matcher matcher = GNDY_DETAIL_PATH_PATTERN.matcher(page.getUrl().get());
        if (!matcher.find()) {
            page.setSkip(true);
            return;
        }
        String id = matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4);

        movie.setId(id);
        movie.setUrl(page.getUrl().get());

        Selectable root = page.getHtml().xpath("//div[@class='bd3']/div[@class='bd3l']/div[@class='co_area2']");

        // 标题
        String title = root.xpath("//div[@class='title_all']/h1/font/text()").get();
        movie.setTitle(wordFilter(title));

        // 发布时间
        String publishDate = root.xpath("//div[@class=co_content8]/ul/text()")
                .regex("发布时间.+([0-9]{4}-[0-9]{2}-[0-9]{2})")
                .get();
        if (StringUtils.hasText(publishDate)) {
            movie.setPublishDate(DateUtils.parseDate(publishDate, dateDayFormatter));
        }

        // 链接
        List<String> links = root.css("a[href]", "href").all();
        List<String> downloads = new ArrayList<>();
        for (String link : links) {
            if (isDownloadLink(link)) {
                downloads.add(link);
            } else {
                addLink(page, link);
            }
        }
        movie.setDownLinks(downloads);

        // 内容处理
        List<String> nodes;
        List<String> contentDiv = root.css("div#Zoom span div").all();
        if (contentDiv.isEmpty()) {

            // 封面地址
            Selectable p = root.css("div#Zoom span p");

            String picUrl = p.css("img[src]", "src").get();
            movie.setPicUrl(picUrl);

            // 内容
            String[] all = p.regex("<br>(.+)<br>").get().split("<br>");
            nodes = wordFilter(Arrays.stream(all))
                    .filter(s -> !s.startsWith("<"))
                    .collect(Collectors.toList());
        } else {
            nodes = new ArrayList<>();
            boolean findPic = false;
            for (String div : contentDiv) {
                Document doc = Jsoup.parse(div);
                Element img = doc.select("img[src]").first();
                // 封面地址
                if (!findPic && img != null) {
                    String src = img.attr("src");
                    movie.setPicUrl(src);
                    findPic = true;
                } else if (img == null) {
                    nodes.add(wordFilter(doc.text()));
                } else {
                    break;
                }
            }
        }

        // 可能是列表页
        if (!nodes.isEmpty()) {
            setTranslateName(movie, nodes);
            setName(movie, nodes);
            setYear(movie, nodes);
            setOriginPlace(movie, nodes);
            setCategory(movie, nodes);
            setLanguage(movie, nodes);
            setWords(movie, nodes);
            setScore(movie, nodes);
            setReleaseDate(movie, nodes);
            setDirector(movie, nodes);
            setScreenWriter(movie, nodes);
            setAct(movie, nodes);
            setAwards(movie, nodes);
            setTags(movie, nodes);
            setDesc(movie, nodes);
            setFileSize(movie, nodes);

            putObject(page, movie);
        }
    }


    @Override
    public boolean match(Page page) {
        return page.getUrl().regex(GNDY_DETAIL_PATH_PATTERN.pattern()).match();
    }

    /**
     * 去掉特殊字符
     */
    protected String wordFilter(String text) {
        text = text.replaceAll("&nbsp;", SPACE);
        return CharMatcher.whitespace().replaceFrom(text, SPACE).trim();
    }

    protected Stream<String> wordFilter(Stream<String> stream) {
        return stream.map(this::wordFilter)
                .filter(StringUtils::hasText);
    }

    /**
     * 是否为下载地址
     */
    protected boolean isDownloadLink(String link) {
        for (String linkStart : linkStarts) {
            if (link.startsWith(linkStart)) {
                return true;
            }
        }
        return false;
    }

    private void setTranslateName(DyttMovie movie, List<String> nodes) {
        List<String> translateName = collectList(findBrValue(nodes, P_TRANSLATE_NAME).split("/"));
        movie.setTranslateNames(translateName);
    }

    private void setName(DyttMovie movie, List<String> nodes) {
        String name = findBrValue(nodes, P_NAME);
        movie.setName(name);
    }

    private void setYear(DyttMovie movie, List<String> nodes) {
        String year = findBrValue(nodes, P_YEAR);
        if (StringUtils.hasText(year)) {
            int y = Integer.parseInt(wordFilter(year));
            movie.setYear(y);
        }
    }

    private void setOriginPlace(DyttMovie movie, List<String> nodes) {
        String places = findBrValue(nodes, P_ORIGIN_PLACE);
        List<String> list = new ArrayList<>();
        if (StringUtils.hasText(places)) {
            Matcher matcher = placePattern.matcher(places);
            while (matcher.find()) {
                String place = matcher.group();
                list.add(place);
            }
        }
        movie.setOriginPlace(list);
    }

    private void setCategory(DyttMovie movie, List<String> nodes) {
        String category = findBrValue(nodes, P_CATEGORY);
        List<String> collect = collectList(category.split("[/]"));
        movie.setCategory(collect);
    }

    private void setLanguage(DyttMovie movie, List<String> nodes) {
        String language = findBrValue(nodes, P_LANGUAGE);
        List<String> collect = collectList(language.split("[/,]"));
        movie.setLanguage(collect);
    }

    private void setWords(DyttMovie movie, List<String> nodes) {
        String words = findBrValue(nodes, P_WORDS);
        movie.setWords(words);
    }

    private void setScore(DyttMovie movie, List<String> nodes) {
        String scoreValue = findBrValue(nodes, P_SCORES);
        Matcher matcher = scorePattern.matcher(scoreValue);
        if (matcher.find()) {
            movie.setScore(Double.parseDouble(matcher.group(1)));
            movie.setScoreNums(Integer.parseInt(matcher.group(2).replace(",", "")));
        }
    }

    private void setReleaseDate(DyttMovie movie, List<String> nodes) {
        String releaseDateStr = findBrValue(nodes, P_RELEASE_DATE);
        List<DyttReleaseDate> collect = collectList(releaseDateStr.split("/")).stream()
                .map(s -> {
                    Matcher matcher = releaseDatePattern.matcher(s);
                    if (matcher.find()) {
                        DyttReleaseDate releaseDate = new DyttReleaseDate();
                        try {
                            releaseDate.setDate(DateUtils.parseDate(matcher.group(1), dateDayFormatter));
                        } catch (DateTimeParseException e) {
                            try {
                                releaseDate.setDate(new SimpleDateFormat("yyyy-MM").parse(matcher.group(1)));
                            } catch (ParseException e1) {
                                return null;
                            }
                        }
                        releaseDate.setPlace(matcher.group(2));
                        return releaseDate;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        movie.setReleaseDates(collect);
    }

    private void setDirector(DyttMovie movie, List<String> nodes) {
        String director = findBrValue(nodes, P_DIRECTOR);
        List<String> collect = collectList(director.split("[\n/]"));
        movie.setDirector(collect);
    }

    private void setScreenWriter(DyttMovie movie, List<String> nodes) {
        String screenwriter = findBrValue(nodes, P_SCREEN_WRITER);
        List<String> collect = collectList(screenwriter.split("[\n/]"));
        movie.setScreenwriter(collect);
    }

    private void setAct(DyttMovie movie, List<String> nodes) {
        String act = findBrValue(nodes, P_ACT);

        List<String> collect = collectList(act.split("[\n/]"));
        movie.setAct(collect);
    }

    private void setTags(DyttMovie movie, List<String> nodes) {
        String tags = findBrValue(nodes, P_TAGS);
        List<String> collect = collectList(tags.split("\\|"));
        movie.setTags(collect);
    }

    private void setAwards(DyttMovie movie, List<String> nodes) {
        String awards = findBrValue(nodes, P_AWARDS);
        List<String> collect = collectList(awards.split("\n"));
        movie.setAwards(collect);
    }

    private void setDesc(DyttMovie movie, List<String> nodes) {
        String desc = findBrValue(nodes, P_DESC);
        movie.setDesc(desc);
    }

    private void setFileSize(DyttMovie movie, List<String> nodes) {
        String size = findBrValue(nodes, P_FILE_SIZE);
        Matcher matcher = sizePattern.matcher(size);
        if (matcher.find()) {
            String group = matcher.group(1);
            double s = Double.parseDouble(group);
            movie.setFileSize(s);
        }
    }

    private String findBrValue(List<String> nodes, Pattern pattern) {
        boolean hasNext = false;
        StringBuilder value = new StringBuilder();
        for (String node : nodes) {
            String text = node.trim();

            if (hasNext) {
                if (!text.startsWith(P_CONTENT_START)) {
                    value.append("\n").append(text);
                } else {
                    break;
                }
            } else {
                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    String group = matcher.group(1);
                    value.append(group.trim());
                    hasNext = true;
                }
            }
        }
        return value.toString().trim();
    }


    private List<String> collectList(String[] arrays) {
        return Arrays.stream(arrays)
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }


}
