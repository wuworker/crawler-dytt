package com.wxl.crawlerdytt.frontier.local;

import com.google.common.hash.BloomFilter;
import com.wxl.crawlerdytt.core.DyttUrl;
import com.wxl.crawlerdytt.frontier.VisitedFrontier;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Create by wuxingle on 2020/5/1
 * bloom 实现visited表
 */
@Component
public class BloomVisitedFrontier implements VisitedFrontier {

    private BloomFilter<DyttUrl> filter;

    public BloomVisitedFrontier() {
        this.filter = BloomFilter.create((url, into) -> {
            String path = url.getPath();
            into.putString(path, StandardCharsets.UTF_8);

        }, 1000, 0.01);
    }

    @Override
    public void add(DyttUrl url) {
        filter.put(url);
    }

    @Override
    public boolean contains(DyttUrl url) {
        return filter.mightContain(url);
    }
}
