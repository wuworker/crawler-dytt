package com.wxl.crawlerdytt.processor;

import com.wxl.crawlerdytt.urlhandler.PriorityUrlCalculator;
import com.wxl.crawlerdytt.urlhandler.UrlFilter;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.utils.UrlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wxl.crawlerdytt.core.DyttConstants.REQUEST_ATTR_DEPTH;

/**
 * Create by wuxingle on 2020/5/16
 * 包含Link相关处理
 */
public abstract class AbstractDyttProcessor implements DyttProcessor {

    /**
     * url优先级计算
     */
    private PriorityUrlCalculator priorityCalculator;

    /**
     * url过滤器
     */
    private UrlFilter urlFilter;

    public AbstractDyttProcessor(PriorityUrlCalculator priorityCalculator,
                                 UrlFilter urlFilter) {
        this.priorityCalculator = priorityCalculator;
        this.urlFilter = urlFilter;
    }


    /**
     * 添加link
     */
    public void addLinks(Page page) {
        List<String> all = page.getHtml().links().all();
        if (urlFilter != null) {
            all = urlFilter.filter(all);
        }
        all.forEach(url -> addExtraLink(page, url));
    }

    /**
     * 添加link
     */
    public void addLink(Page page, String url) {
        if (urlFilter == null || urlFilter.filter(url)) {
            addExtraLink(page, url);
        }
    }

    /**
     * 存结果对象
     */
    public void putObject(Page page, Object obj) {
        if (obj != null) {
            page.putField(obj.getClass().getName(), obj);
        }
    }


    private void addExtraLink(Page page, String url) {
        if (StringUtils.isBlank(url) || url.equals("#") || url.startsWith("javascript:")) {
            return;
        }
        Request request = new Request();
        url = UrlUtils.canonicalizeUrl(url, page.getUrl().toString());
        request.setUrl(url);

        int priority = priorityCalculator.calculate(page, url);
        request.setPriority(priority);

        Integer depth = (Integer) page.getRequest().getExtra(REQUEST_ATTR_DEPTH);

        Map<String, Object> extra = new HashMap<>();
        extra.put(REQUEST_ATTR_DEPTH, depth == null ? 0 : depth + 1);

        request.setExtras(extra);

        page.addTargetRequest(request);
    }
}
