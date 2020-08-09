/**
 * 首页电影相关js
 */
SEARCH_BATCH_SIZE = 10;

/**
 * 搜索电影
 */
var search_movie = function () {

    var query = {
        title: $("#search_txt").val(),
        originPlace: [],
        category: [],
        from: 0,
        size: SEARCH_BATCH_SIZE
    };

    // 分页
    var pageNo = $('input[name="pageNo"]').val();
    query.from = (parseInt(pageNo) - 1) * SEARCH_BATCH_SIZE;

    // 年代
    var year_val = $('input[name="year"]:checked').val();
    var year_start, year_end;
    var date = new Date();
    if (year_val == 1) {
        year_start = date.getFullYear();
        year_end = date.getFullYear();
    } else if (year_val == 2) {
        year_start = date.getFullYear() - 2;
        year_end = date.getFullYear();
    } else if (year_val == 3) {
        year_start = date.getFullYear() - 4;
        year_end = date.getFullYear();
    } else if (year_val == 4) {
        var start = $('#year_start_txt').val();
        var end = $('#year_end_txt').val();
        if (!is_blank(start)) {
            year_start = parseInt(start);
        }
        if (!is_blank(end)) {
            year_end = parseInt(end);
        }
    }
    if (year_start) {
        query.yearStart = year_start;
    }
    if (year_end) {
        query.yearEnd = year_end;
    }

    // 产地
    $('input[name="originPlace"]:checked').each(function () {
        query.originPlace.push($(this).val());
    });
    if ($('#place_custom_box').get(0).checked) {
        var custom_place = $("#place_custom_txt").val().split(',');
        if (!is_blank(custom_place)) {
            query.originPlace = query.originPlace.concat(custom_place);
        }
    }

    // 分类
    $('input[name="category"]:checked').each(function () {
        query.category.push($(this).val());
    });
    if ($('#category_custom_box').get(0).checked) {
        var custom_category = $("#category_custom_txt").val().split(',');
        if (!is_blank(custom_category)) {
            query.category = query.category.concat(custom_category);
        }
    }

    console.log(query);

    do_search(query, function (data) {

        remove_movie_item();

        // 结果数
        $("#result_total_span").text(data.total);

        //设置页数
        page = Math.floor((data.total + SEARCH_BATCH_SIZE - 1) / SEARCH_BATCH_SIZE);
        $('input[name=page]').val(page);
        var now_page = parseInt($('input[name=pageNo]').val());
        $('#prePageBtn').prop("disabled", false);
        $('#afterPageBtn').prop("disabled", false);
        if (now_page >= page) {
            $('#afterPageBtn').prop("disabled", true);
        }
        if (now_page == 1) {
            $('#prePageBtn').prop("disabled", true);
        }

        // 详情渲染
        data.list.forEach(element => {

            create_movie_item(element);
            $("#movie_list_div img").one("error", function (e) {
                $(this).attr("src", DEFAULT_MOVIE_PIC_PATH);
            });
        });

    })
};

/**
 * 重新搜索
 */
var reset_search = function () {
    var input_ele = $('input[name="pageNo"]');
    input_ele.val(1);
    search_movie();
};

/**
 * 上一页
 */
var pre_search = function () {
    var input_ele = $('input[name="pageNo"]');
    input_ele.val(parseInt(input_ele.val()) - 1);
    search_movie();
};

/**
 * 下一页
 */
var after_search = function () {
    var input_ele = $('input[name="pageNo"]');
    input_ele.val(parseInt(input_ele.val()) + 1);
    search_movie();
};

/**
 * 创建电影详情
 * @param {电影详情数据}} data
 */
var create_movie_item = function (data) {
    var container = $("<div></div>", {
        id: data.id,
        onclick: "window.open('" + data.url + "')"
    });
    container.append($("<p>", {
        text: data.title
    }));
    container.append($("<img>", {
        src: is_blank(data.picUrl) ? DEFAULT_MOVIE_PIC_PATH : data.picUrl
    }));
    container.append($("<br>"));
    container.append($("<div>", {
        text: "评分: " + data.score
    }));
    container.append($("<div>", {
        text: "产地: " + data.originPlace.join(',')
    }));
    container.append($("<div>", {
        text: "年代: " + data.year
    }));
    container.append($("<div>", {
        text: "分类: " + data.category.join(',')
    }));
    $("#movie_list_div").append(container);
};

var remove_movie_item = function () {
    $("#movie_list_div").children().remove();
};