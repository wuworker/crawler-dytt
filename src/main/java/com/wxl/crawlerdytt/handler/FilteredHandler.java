package com.wxl.crawlerdytt.handler;

import com.wxl.crawlerdytt.core.DyttUrl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

/**
 * Create by wuxingle on 2020/5/2
 * 包含过滤器的handler
 */
public abstract class FilteredHandler<T> implements HtmlHandler {

    private List<DyttFilter<T>> filters;


    @Override
    public final Object handle(DyttUrl url, String html) {
        Document doc = Jsoup.parse(html);

        T result = handle(url, doc);

        for (DyttFilter<T> filter : filters) {
            result = filter.filter(result);
        }

        return result;
    }


    protected abstract T handle(DyttUrl url, Document doc);

}
