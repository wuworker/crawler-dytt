package com.wxl.crawlerdytt.handler.impl;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.wxl.crawlerdytt.core.DyttDetail;
import com.wxl.crawlerdytt.core.DyttReleaseDate;
import com.wxl.crawlerdytt.core.DyttUrl;
import com.wxl.crawlerdytt.handler.FilteredHandler;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Create by wuxingle on 2020/5/2
 * 电影天堂详情页处理
 */
public class DyttDetailHandler extends FilteredHandler<DyttDetail> {

    private Pattern pathPattern = Pattern.compile("/html/gndy/\\w+/(\\d+)/(\\d+)\\.html");
    private Pattern scorePattern = Pattern.compile("([0-9.]+)/10 from ([0-9]+) users");
    private Pattern releaseDatePattern = Pattern.compile("([0-9]{4}-[0-9]{2}-[0-9]{2})\\((.+)\\)");
    private Pattern publishDatePattern = Pattern.compile("发布时间.+([0-9]{4}-[0-9]{2}-[0-9]{2})");
    private List<String> linkStarts = Lists.newArrayList("ftp", "magnet");

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

    /**
     * 页面解析
     *
     * @return 处理结果
     */
    @Override
    protected DyttDetail handle(DyttUrl url, Document doc) {
        DyttDetail dyttDetail = new DyttDetail();

        Matcher idMatcher = pathPattern.matcher(url.getUrl());
        if (idMatcher.find()) {
            String id = idMatcher.group(1) + idMatcher.group(2);
            dyttDetail.setId(id);
        } else {
            dyttDetail.setId(url.getUrl());
        }

        dyttDetail.setUrl(url.getUrl());

        Elements elements = doc.select("div.bd3 div.bd3l div.co_area2");
        Element root = elements.first();

        // 标题
        String title = title(root);
        dyttDetail.setTitle(title);

        // 发布时间
        String publishDateText = root.select("div.co_content8").text();
        Matcher matcher = publishDatePattern.matcher(publishDateText);
        if (matcher.find()) {
            dyttDetail.setPublishDate(matcher.group(1));
        }

        Element zoom = root.select("div#Zoom").first();
        Element p = zoom.select("span p").first();

        // 封面地址
        String picUrl = p.select("img[src]").attr("src");
        dyttDetail.setPicUrl(picUrl);

        List<TextNode> textNodes = p.textNodes();

        // 其他属性
        setTranslateName(dyttDetail, textNodes);
        setName(dyttDetail, textNodes);
        setYear(dyttDetail, textNodes);
        setOriginPlace(dyttDetail, textNodes);
        setCategory(dyttDetail, textNodes);
        setLanguage(dyttDetail, textNodes);
        setWords(dyttDetail, textNodes);
        setScore(dyttDetail, textNodes);
        setReleaseDate(dyttDetail, textNodes);
        setDirector(dyttDetail, textNodes);
        setScreenWriter(dyttDetail, textNodes);
        setAct(dyttDetail, textNodes);
        setAwards(dyttDetail, textNodes);
        setTags(dyttDetail, textNodes);
        setDesc(dyttDetail, textNodes);
        setFileSize(dyttDetail, textNodes);
        setFileFormat(dyttDetail, textNodes);
        setVideoSize(dyttDetail, textNodes);

        // 下载地址
        List<String> links = new ArrayList<>();
        Elements hrefs = root.select("div.co_content8").select("a[href]");
        for (Element href : hrefs) {
            String link = href.attr("href");
            if (isLink(link)) {
                links.add(link);
            }
        }
        dyttDetail.setDownLinks(links);

        return dyttDetail;
    }

    /**
     * 是否能够处理
     */
    @Override
    public boolean support(DyttUrl url, String html) {
        String path = url.getPath();
        return pathPattern.matcher(path).matches();
    }


    private String title(Element element) {
        Element titleElement = element.select("div.title_all h1").first();
        return titleElement.text();
    }

    private void setTranslateName(DyttDetail dyttDetail, List<TextNode> nodes) {
        List<String> translateName = Arrays.stream(findBrValue(nodes, P_TRANSLATE_NAME).split("/"))
                .map(String::trim)
                .filter(s -> !"".equals(s))
                .collect(Collectors.toList());
        dyttDetail.setTranslateNames(translateName);
    }

    private void setName(DyttDetail dyttDetail, List<TextNode> nodes) {
        String name = findBrValue(nodes, P_NAME);
        dyttDetail.setName(name);
    }

    private void setYear(DyttDetail dyttDetail, List<TextNode> nodes) {
        String year = findBrValue(nodes, P_YEAR);
        if (StringUtils.hasText(year)) {
            int y = Integer.parseInt(year);
            dyttDetail.setYear(y);
        }
    }

    private void setOriginPlace(DyttDetail dyttDetail, List<TextNode> nodes) {
        String place = findBrValue(nodes, P_ORIGIN_PLACE);
        dyttDetail.setOriginPlace(place);
    }

    private void setCategory(DyttDetail dyttDetail, List<TextNode> nodes) {
        String category = findBrValue(nodes, P_CATEGORY);
        List<String> collect = Arrays.stream(category.split("/"))
                .map(String::trim)
                .collect(Collectors.toList());
        dyttDetail.setCategory(collect);
    }

    private void setLanguage(DyttDetail dyttDetail, List<TextNode> nodes) {
        String language = findBrValue(nodes, P_LANGUAGE);
        List<String> collect = Arrays.stream(language.split("/"))
                .map(String::trim)
                .collect(Collectors.toList());
        dyttDetail.setLanguage(collect);
    }

    private void setWords(DyttDetail dyttDetail, List<TextNode> nodes) {
        String words = findBrValue(nodes, P_WORDS);
        dyttDetail.setWords(words);
    }

    private void setScore(DyttDetail dyttDetail, List<TextNode> nodes) {
        String scoreValue = findBrValue(nodes, P_SCORES);
        Matcher matcher = scorePattern.matcher(scoreValue);
        if (matcher.find()) {
            dyttDetail.setScore(Double.parseDouble(matcher.group(1)));
            dyttDetail.setScoreNums(Integer.parseInt(matcher.group(2)));
        }
    }

    private void setReleaseDate(DyttDetail dyttDetail, List<TextNode> nodes) {
        String releaseDate = findBrValue(nodes, P_RELEASE_DATE);
        List<DyttReleaseDate> collect = Arrays.stream(releaseDate.split("/"))
                .map(String::trim)
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

    private void setDirector(DyttDetail dyttDetail, List<TextNode> nodes) {
        String director = findBrValue(nodes, P_DIRECTOR);
        List<String> collect = Arrays.stream(director.split("\n"))
                .map(String::trim)
                .collect(Collectors.toList());
        dyttDetail.setDirector(collect);
    }

    private void setScreenWriter(DyttDetail dyttDetail, List<TextNode> nodes) {
        String screenwriter = findBrValue(nodes, P_SCREEN_WRITER);
        List<String> collect = Arrays.stream(screenwriter.split("\n"))
                .map(String::trim)
                .collect(Collectors.toList());
        dyttDetail.setScreenwriter(collect);
    }

    private void setAct(DyttDetail dyttDetail, List<TextNode> nodes) {
        String act = findBrValue(nodes, P_ACT);

        List<String> collect = Arrays.stream(act.split("\n"))
                .map(CharMatcher.whitespace()::trimFrom)
                .collect(Collectors.toList());
        dyttDetail.setAct(collect);
    }

    private void setTags(DyttDetail dyttDetail, List<TextNode> nodes) {
        String tags = findBrValue(nodes, P_TAGS);
        List<String> collect = Arrays.stream(tags.split("\\|"))
                .map(String::trim)
                .collect(Collectors.toList());
        dyttDetail.setTags(collect);
    }


    private void setAwards(DyttDetail dyttDetail, List<TextNode> nodes) {
        String awards = findBrValue(nodes, P_AWARDS);
        List<String> collect = Arrays.stream(awards.split("\n"))
                .map(CharMatcher.whitespace()::trimFrom)
                .filter(s -> !"".equals(s))
                .collect(Collectors.toList());
        dyttDetail.setAwards(collect);
    }

    private void setDesc(DyttDetail dyttDetail, List<TextNode> nodes) {
        String desc = findBrValue(nodes, P_DESC);
        dyttDetail.setDesc(desc);
    }

    private void setFileSize(DyttDetail dyttDetail, List<TextNode> nodes) {
        String size = findBrValue(nodes, P_FILE_SIZE);
        dyttDetail.setFileSize(size);
    }

    private void setFileFormat(DyttDetail dyttDetail, List<TextNode> nodes) {
        String format = findBrValue(nodes, P_FILE_FORMAT);
        dyttDetail.setFileFormat(format);
    }

    private void setVideoSize(DyttDetail dyttDetail, List<TextNode> nodes) {
        String size = findBrValue(nodes, P_VIDEO_SIZE);
        dyttDetail.setVideoSize(size);
    }

    private String findBrValue(List<TextNode> nodes, String key) {
        boolean hasNext = false;
        StringBuilder value = new StringBuilder();
        for (TextNode node : nodes) {
            String text = node.text().trim();

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

    private boolean isLink(String link) {
        for (String linkStart : linkStarts) {
            if (link.startsWith(linkStart)) {
                return true;
            }
        }
        return false;
    }

}

