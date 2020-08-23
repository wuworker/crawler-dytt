// http成功响应
RES_SUCCESS_CODE = 200

// 搜索路径
SEARCH_PATH = "/dytt/search"

// 统计请求路径
STATISTIC_PATH = "/dytt/statistic"

// 爬虫管理路径
CRAWLER_PATH = "/dytt/crawler"

/**
 * 请求成功回调
 * @param {} func 
 */
function request_success(func) {
    return function (data) {
        if (data.code === RES_SUCCESS_CODE) {
            return func(data.data);
        }
        alert(data.code + ":" + data.message);
    }
}


/**
 * 请求失败回调
 * @param {*} xhr 
 * @param {*} textStatus 
 * @param {*} errorThrown 
 */
function request_error(xhr, textStatus, errorThrown) {
    alert("请求失败:" + xhr.url)
}

/**
 * get请求
 * @param {*} path 
 * @param {*} func 
 */
function do_get(path, func) {
    $.ajax({
        url: path,
        type: 'GET',
        dataType: 'json',
        success: request_success(func),
        error: request_error
    });
}

/**
 * post请求
 * @param {*} path 
 * @param {*} func 
 */
function do_post(path, func) {
    $.ajax({
        url: path,
        type: 'POST',
        dataType: 'json',
        success: request_success(func),
        error: request_error
    });
}

//---------------搜索相关-----------------------------
/**
 * 电影搜索
 * @param {*} query 
 * @param {*} func 
 */
function do_search(query, func) {
    $.ajax({
        url: SEARCH_PATH,
        type: 'POST',
        contentType: "application/json;charset=UTF-8",
        dataType: 'json',
        data: JSON.stringify(query),
        success: request_success(func),
        error: request_error
    })
}

//----------------------统计相关-----------------------
/**
 * 基础数据
 * @param {*} func 
 */
function stat_base(func) {
    do_get(STATISTIC_PATH + "/base", func);
}

/**
 * 种类数据
 * @param {*}} func 
 */
function stat_category(func) {
    do_get(STATISTIC_PATH + "/agg/category", func);
}

/**
 * 地区数据
 * @param {*}} func 
 */
function stat_place(func) {
    do_get(STATISTIC_PATH + "/agg/originPlace", func);
}

/**
 * 语言数据
 * @param {*}} func 
 */
function stat_language(func) {
    do_get(STATISTIC_PATH + "/agg/language", func);
}

/**
 * 年份数据
 * @param {*}} func 
 */
function stat_year(func) {
    do_get(STATISTIC_PATH + "/agg/year", func);
}

/**
 * 每月电影数量，按year分组
 * @param {*}} func 
 */
function stat_month_count(years, func) {
    if (is_blank(years)) {
        years = "";
    }
    do_get(STATISTIC_PATH + "/agg/month/year?years=" + years, func);
}

/**
 * 地区电影数量，按year分组
 * @param {*}} func 
 */
function stat_place_count(years, func) {
    if (is_blank(years)) {
        years = "";
    }
    do_get(STATISTIC_PATH + "/agg/place/year?years=" + years, func);
}


//----------------------爬虫相关-----------------------
/**
 * 消费进度
 * @param {*} func 
 */
function crawler_process(func) {
    do_get(CRAWLER_PATH + "/progress", func);
}

/**
 * 爬虫状态
 * @param {*} func 
 */
function crawler_status(func) {
    do_get(CRAWLER_PATH + "/status", func);
}

/**
 * 启动
 * @param {*} func 
 */
function crawler_start(func) {
    do_post(CRAWLER_PATH + "/start", func);
}


/**
 * 停止
 * @param {*} func 
 */
function crawler_stop(func) {
    do_post(CRAWLER_PATH + "/stop", func);
}

/**
 * 重置消费进度
 * @param {*} func 
 */
function crawler_reset(func) {
    do_post(CRAWLER_PATH + "/reset", func);
}

/**
 * 保存url
 * @param {*} func 
 * @param {*} url 
 */
function crawler_save_url(func, url) {
    $.ajax({
        url: CRAWLER_PATH + "/save",
        type: 'POST',
        contentType: "application/json;charset=UTF-8",
        dataType: 'json',
        data: JSON.stringify({
            url: url
        }),
        success: request_success(func),
        error: request_error
    })
}