package com.wxl.crawlerdytt.frontier;

import com.wxl.crawlerdytt.core.DyttUrl;

import java.util.List;

/**
 * Create by wuxingle on 2020/5/1
 * todo表
 */
public interface Frontier {

    List<DyttUrl> next(int num);

    void add(DyttUrl... url);

    default DyttUrl next() {
        List<DyttUrl> list = next(1);
        return list.isEmpty() ? null : list.get(0);
    }

}
