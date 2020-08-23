/**
 * 爬虫管理js
 */

/**
 * 显示消费进度
 */
function render_process() {
    crawler_process(function (data) {
        $("#todo").text("待处理数: " + data.todoSize);
        $("#total").text("已处理数: " + data.totalSize);
        $("#fail").text("处理失败数: " + data.failSize);
    });
}

/**
 * 刷新当前状态
 */
function refresh_status() {
    crawler_status(function (data) {

        if (data === "INIT") {
            $("#status").text("初始化完成，待启动");
            $("#status").css("color", "gray");

            $("#start_btn").text("启动");
            $("#start_btn").attr("onclick", "click_start()");
            $("#start_btn").removeAttr("disabled");
        } else if (data === "RUNNING") {
            $("#status").text("运行中。。。");
            $("#status").css("color", "green");

            $("#start_btn").text("停止");
            $("#start_btn").attr("onclick", "click_stop()");
            $("#start_btn").removeAttr("disabled");
        } else if (data === "STOPPING") {
            $("#status").text("停止中。。。");
            $("#status").css("color", "yellow");

            $("#start_btn").text("停止中");
            $("#start_btn").attr("disabled", "disabled");
        } else {
            $("#status").text("已停止");
            $("#status").css("color", "red");

            $("#start_btn").text("启动");
            $("#start_btn").attr("onclick", "click_start()");
            $("#start_btn").removeAttr("disabled");
        }

        setTimeout("refresh_status()", 1000);
    });
}

/**
 * 爬虫启动
 */
function click_start() {
    crawler_start(function (data) {
        if (!data) {
            alert("启动失败");
        }
    });
}

/**
 * 爬虫停止
 */
function click_stop() {
    crawler_stop(function (data) {
        if (!data) {
            alert("停止失败");
        }
    });
}


/**
 * 重置消费进度
 */
function reset_process() {
    if (confirm("确定重置消费进度")) {
        crawler_reset(function (data) {
            if (!data) {
                alert("重置失败");
            } else {
                render_process();
            }
        });
    }
}

/**
 * 保存url
 */
function save_url() {
    var url = $("#url_txt").val();
    if (!is_blank(url) && url.startsWith("http")) {
        $("#save_url_btn").attr("disabled", "disabled");
        crawler_save_url(function (data) {
            if (!data) {
                alert("保存失败");
            }

            $("#save_url_btn").removeAttr("disabled");
        }, url);
    } else {
        alert("url格式错误");
    }
}