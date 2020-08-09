package com.wxl.dyttcrawler.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Create by wuxingle on 2020/8/8
 * k v包含对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item<K, V> implements Serializable {

    private static final long serialVersionUID = -1071250860208788710L;

    private K key;

    private V value;
}
