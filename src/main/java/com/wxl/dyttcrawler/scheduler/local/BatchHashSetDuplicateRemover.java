package com.wxl.dyttcrawler.scheduler.local;

import com.wxl.dyttcrawler.scheduler.BatchDuplicateRemover;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

/**
 * Create by wuxingle on 2020/7/20
 * hashSet保存访问过的request
 */
public class BatchHashSetDuplicateRemover extends HashSetDuplicateRemover
        implements BatchDuplicateRemover {

}
