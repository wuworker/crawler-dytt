package com.wxl.dyttcrawler.processor;

import com.wxl.dyttcrawler.core.DyttConstants;
import com.wxl.dyttcrawler.urlhandler.PriorityUrlCalculator;
import com.wxl.dyttcrawler.urlhandler.UrlFilter;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.utils.UrlUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        all.stream().map(url -> canonicalizeUrl(page, url))
                .filter(url -> url != null && urlFilter.filter(url))
                .forEach(url -> addExtraLink(page, url));
    }

    /**
     * 添加link
     */
    public void addLink(Page page, String url) {
        URL url1 = canonicalizeUrl(page, url);
        if (url1 != null && urlFilter.filter(url1)) {
            addExtraLink(page, url1);
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

    /**
     * url格式化
     *
     * @return 绝对地址
     */
    private URL canonicalizeUrl(Page page, String url) {
        if ("#".equals(url) || !StringUtils.hasText(url)) {
            return null;
        }
        url = UrlUtils.canonicalizeUrl(url, page.getUrl().toString());
        if (StringUtils.hasText(url)) {
            try {
                return new URL(url);
            } catch (MalformedURLException e) {
                return null;
            }
        }
        return null;
    }

    private void addExtraLink(Page page, URL url) {
        Request request = new Request();
        request.setUrl(url.toString());

        int priority = priorityCalculator.calculate(page, url);
        request.setPriority(priority);

        Integer depth = (Integer) page.getRequest().getExtra(DyttConstants.RequestAttr.DEPTH);

        Map<String, Object> extra = new HashMap<>();
        extra.put(DyttConstants.RequestAttr.DEPTH, depth == null ? 0 : depth + 1);

        request.setExtras(extra);

        page.addTargetRequest(request);
    }
}
