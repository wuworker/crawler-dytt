package com.wxl.crawlerdytt.processor;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.wxl.crawlerdytt.core.DyttDetail;
import com.wxl.crawlerdytt.core.DyttReleaseDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Create by wuxingle on 2020/5/10
 * 电影天堂详情页处理
 */
@Slf4j
@Order(0)
@Component
public class DyttDetailPageProcessor implements DyttProcessor {

    private static final String PATH_PATTERN = "https://www.dytt8.net/html/gndy/\\w+/(\\d+)/(\\d+)\\.html";

    private static final String P_CONTENT_START = "◎";
    private static final String P_TRANSLATE_NAME = "◎译　　名　";
    private static final String P_NAME = "◎片　　名　";
    private static final String P_YEAR = "◎年　　代　";
    private static final String P_ORIGIN_PLACE = "◎产　　地　";
    private static final String P_CATEGORY = "◎类　　别　";
    private static final String P_LANGUAGE = "◎语　　言　";
    private static final String P_WORDS = "◎字　　幕　";
    private static final String P_SCORES = "◎豆瓣评分　";
    private static final String P_RELEASE_DATE = "◎上映日期　";
    private static final String P_DIRECTOR = "◎导　　演　";
    private static final String P_SCREEN_WRITER = "◎编　　剧　";
    private static final String P_ACT = "◎主　　演　";
    private static final String P_TAGS = "◎标　　签　";
    private static final String P_AWARDS = "◎获奖情况";
    private static final String P_DESC = "◎简　　介";
    private static final String P_FILE_SIZE = "◎文件大小　";
    private static final String P_FILE_FORMAT = "◎文件格式　";
    private static final String P_VIDEO_SIZE = "◎视频尺寸　";

    // 评分
    private Pattern scorePattern = Pattern.compile("([0-9.]+)/10 from ([0-9]+) users");
    // 发布日期
    private Pattern releaseDatePattern = Pattern.compile("([0-9]{4}-[0-9]{2}-[0-9]{2})\\((.+)\\)");
    private List<String> linkStarts = Lists.newArrayList("ftp", "magnet");


    @Override
    public void process(Page page) {
        DyttDetail dyttDetail = new DyttDetail();

        String id1 = page.getUrl().regex(PATH_PATTERN, 1).get();
        String id2 = page.getUrl().regex(PATH_PATTERN, 2).get();
        if (id1 == null || id2 == null) {
            page.setSkip(true);
            return;
        }
        dyttDetail.setId(id1 + id2);
        dyttDetail.setUrl(page.getUrl().get());

        Selectable root = page.getHtml().xpath("//div[@class='bd3']/div[@class='bd3l']/div[@class='co_area2']");

        // 标题
        String title = root.xpath("//div[@class='title_all']/h1/font/text()").get();
        dyttDetail.setTitle(title);

        // 发布时间
        String publishDate = root.xpath("//div[@class=co_content8]/ul/text()")
                .regex("发布时间.+([0-9]{4}-[0-9]{2}-[0-9]{2})")
                .get();
        if (StringUtils.hasText(publishDate)) {
            dyttDetail.setPublishDate(publishDate);
        }

        // 封面地址
        Selectable p = root.css("div#Zoom span p");
        String picUrl = p.css("img[src]", "src").get();
        dyttDetail.setPicUrl(picUrl);

        // 内容
        String[] all = p.regex("<br>(.+)<br>").get().split("<br>");
        List<String> nodes = wordFilter(Arrays.stream(all))
                .filter(s -> !s.startsWith("<"))
                .collect(Collectors.toList());

        setTranslateName(dyttDetail, nodes);
        setName(dyttDetail, nodes);
        setYear(dyttDetail, nodes);
        setOriginPlace(dyttDetail, nodes);
        setCategory(dyttDetail, nodes);
        setLanguage(dyttDetail, nodes);
        setWords(dyttDetail, nodes);
        setScore(dyttDetail, nodes);
        setReleaseDate(dyttDetail, nodes);
        setDirector(dyttDetail, nodes);
        setScreenWriter(dyttDetail, nodes);
        setAct(dyttDetail, nodes);
        setAwards(dyttDetail, nodes);
        setTags(dyttDetail, nodes);
        setDesc(dyttDetail, nodes);
        setFileSize(dyttDetail, nodes);
        setFileFormat(dyttDetail, nodes);
        setVideoSize(dyttDetail, nodes);

        // 链接
        List<String> links = root.css("a[href]", "href").all();
        List<String> downloads = new ArrayList<>();
        for (String link : links) {
            if (isDownloadLink(link)) {
                downloads.add(link);
            } else {
                page.addTargetRequest(link);
            }
        }
        dyttDetail.setDownLinks(downloads);

        puthObject(page, dyttDetail);
    }


    @Override
    public boolean match(Page page) {
        return page.getUrl().regex(PATH_PATTERN).match();
    }

    /**
     * 去掉特殊字符
     */
    protected String wordFilter(String text) {
        return CharMatcher.whitespace().trimFrom(text);
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

    private void setTranslateName(DyttDetail dyttDetail, List<String> nodes) {
        List<String> translateName = collectList(findBrValue(nodes, P_TRANSLATE_NAME).split("/"));
        dyttDetail.setTranslateNames(translateName);
    }

    private void setName(DyttDetail dyttDetail, List<String> nodes) {
        String name = findBrValue(nodes, P_NAME);
        dyttDetail.setName(wordFilter(name));
    }

    private void setYear(DyttDetail dyttDetail, List<String> nodes) {
        String year = findBrValue(nodes, P_YEAR);
        if (StringUtils.hasText(year)) {
            int y = Integer.parseInt(wordFilter(year));
            dyttDetail.setYear(y);
        }
    }

    private void setOriginPlace(DyttDetail dyttDetail, List<String> nodes) {
        String place = findBrValue(nodes, P_ORIGIN_PLACE);
        dyttDetail.setOriginPlace(wordFilter(place));
    }

    private void setCategory(DyttDetail dyttDetail, List<String> nodes) {
        String category = findBrValue(nodes, P_CATEGORY);
        List<String> collect = collectList(category.split("/"));
        dyttDetail.setCategory(collect);
    }

    private void setLanguage(DyttDetail dyttDetail, List<String> nodes) {
        String language = findBrValue(nodes, P_LANGUAGE);
        List<String> collect = collectList(language.split("/"));
        dyttDetail.setLanguage(collect);
    }

    private void setWords(DyttDetail dyttDetail, List<String> nodes) {
        String words = findBrValue(nodes, P_WORDS);
        dyttDetail.setWords(wordFilter(words));
    }

    private void setScore(DyttDetail dyttDetail, List<String> nodes) {
        String scoreValue = findBrValue(nodes, P_SCORES);
        Matcher matcher = scorePattern.matcher(scoreValue);
        if (matcher.find()) {
            dyttDetail.setScore(Double.parseDouble(matcher.group(1)));
            dyttDetail.setScoreNums(Integer.parseInt(matcher.group(2)));
        }
    }

    private void setReleaseDate(DyttDetail dyttDetail, List<String> nodes) {
        String releaseDate = findBrValue(nodes, P_RELEASE_DATE);
        List<DyttReleaseDate> collect = wordFilter(Arrays.stream(releaseDate.split("/")))
                .map(s -> {
                    DyttReleaseDate date = new DyttReleaseDate();
                    Matcher matcher = releaseDatePattern.matcher(s);
                    if (matcher.find()) {
                        date.setDate(matcher.group(1));
                        date.setPlace(matcher.group(2));
                    }
                    return date;
                })
                .collect(Collectors.toList());
        dyttDetail.setReleaseDates(collect);
    }

    private void setDirector(DyttDetail dyttDetail, List<String> nodes) {
        String director = findBrValue(nodes, P_DIRECTOR);
        List<String> collect = collectList(director.split("\n"));
        dyttDetail.setDirector(collect);
    }

    private void setScreenWriter(DyttDetail dyttDetail, List<String> nodes) {
        String screenwriter = findBrValue(nodes, P_SCREEN_WRITER);
        List<String> collect = collectList(screenwriter.split("\n"));
        dyttDetail.setScreenwriter(collect);
    }

    private void setAct(DyttDetail dyttDetail, List<String> nodes) {
        String act = findBrValue(nodes, P_ACT);

        List<String> collect = collectList(act.split("\n"));
        dyttDetail.setAct(collect);
    }

    private void setTags(DyttDetail dyttDetail, List<String> nodes) {
        String tags = findBrValue(nodes, P_TAGS);
        List<String> collect = collectList(tags.split("\\|"));
        dyttDetail.setTags(collect);
    }

    private void setAwards(DyttDetail dyttDetail, List<String> nodes) {
        String awards = findBrValue(nodes, P_AWARDS);
        List<String> collect = collectList(awards.split("\n"));
        dyttDetail.setAwards(collect);
    }

    private void setDesc(DyttDetail dyttDetail, List<String> nodes) {
        String desc = findBrValue(nodes, P_DESC);
        dyttDetail.setDesc(wordFilter(desc));
    }

    private void setFileSize(DyttDetail dyttDetail, List<String> nodes) {
        String size = findBrValue(nodes, P_FILE_SIZE);
        dyttDetail.setFileSize(wordFilter(size));
    }

    private void setFileFormat(DyttDetail dyttDetail, List<String> nodes) {
        String format = findBrValue(nodes, P_FILE_FORMAT);
        dyttDetail.setFileFormat(wordFilter(format));
    }

    private void setVideoSize(DyttDetail dyttDetail, List<String> nodes) {
        String size = findBrValue(nodes, P_VIDEO_SIZE);
        dyttDetail.setVideoSize(wordFilter(size));
    }

    private String findBrValue(List<String> nodes, String key) {
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
            } else if (text.startsWith(key)) {
                value.append(text.substring(key.length()));
                hasNext = true;
            }
        }
        return value.toString().trim();
    }


    private List<String> collectList(String[] arrays) {
        return wordFilter(Arrays.stream(arrays)).collect(Collectors.toList());
    }


}
