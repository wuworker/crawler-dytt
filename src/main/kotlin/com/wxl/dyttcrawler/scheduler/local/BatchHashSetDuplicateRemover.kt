package com.wxl.dyttcrawler.scheduler.local

import com.wxl.dyttcrawler.scheduler.BatchDuplicateRemover
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover

/**
 * Create by wuxingle on 2021/10/11
 * hashSet保存访问过的request
 */
class BatchHashSetDuplicateRemover : HashSetDuplicateRemover(), BatchDuplicateRemover {


}

