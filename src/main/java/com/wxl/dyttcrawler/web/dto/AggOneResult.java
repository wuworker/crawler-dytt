package com.wxl.dyttcrawler.web.dto;

import com.google.common.collect.ImmutableMap;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Create by wuxingle on 2020/7/13
 * 一次聚合结果
 */
public class AggOneResult<K, V> implements Serializable {

    private static final long serialVersionUID = 6167277419580951202L;

    /**
     * 聚合数据
     */
    protected Map<K, V> data = createDataMap();

    /**
     * 新增数据
     */
    public void add(K key, V val) {
        Assert.notNull(key, "key can not null");
        Assert.notNull(val, "val can not null");
        data.put(key, val);
    }

    /**
     * 获取数据
     */
    public Map<K,V> getData(){
        return ImmutableMap.copyOf(data);
    }

    @Override
    public String toString() {
        return data.toString();
    }


    /**
     * 默认LinkedHashMap
     */
    protected Map<K, V> createDataMap() {
        return new LinkedHashMap<>();
    }

}

