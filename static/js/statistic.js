/**
 * 数据分析js
 */

 /**
  * 设置图表颜色
  */
function set_color(){
    Highcharts.setOptions({
		colors: ['#7cb5ec', '#f15c80', '#f7a35c', '#8085e9', 
        '#90ed7d', '#e4d354', '#8085e8', '#8d4653', '#91e8e1']
	});
}

/**
 * 渲染基础数据
 * @param {*} func 
 */
function reader_base() {
    stat_base(function (data) {
        $("#count").text("影片总数: " + data.count);
        $("#category").text("分类数: " + data.category);
        $("#place").text("来源地区: " + data.place);
        $("#language").text("覆盖语言: " + data.language);
    });
}

/**
 * 分类数据
 */
var category_config = {
    chart: {
        type: 'pie'
    },
    title: {
        text: '电影种类分布'
    },
    tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b><br>数量: <b>{point.y}</b>'
    },
    plotOptions: {
        pie: {
            allowPointSelect: true,
            cursor: 'pointer',
            dataLabels: {
                enabled: true,
                format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                style: {
                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                }
            },
            showInLegend: true
        }
    },
    series: [{
        name: "比例",
        colorByPoint: true,
        data: []
    }],
    credits: {
        text: "电影天堂",
        href: CREDIT_ORIGIN
    }
};

function reader_category() {
    stat_category(function (data) {
        var config = category_config;
        config.series[0].data.length = 0;
        for (var item of data.items) {
            config.series[0].data.push({
                name: item.key,
                y: item.value
            });
        }
        config.series[0].data.push({
            name: "其他",
            y: data.otherSize
        });
        config.series[0].data[0].sliced = true;
        config.series[0].data[0].selected = true;
        $("#category_chart").highcharts(config);
    });
}

/**
 * 地区数据
 */
var place_config = {
    title: {
        // floating:true,
        text: '电影产地分布'
    },
    tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b><br>数量: <b>{point.y}</b>'
    },
    plotOptions: {
        pie: {
            allowPointSelect: true,
            cursor: 'pointer',
            dataLabels: {
                enabled: true,
                format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                style: {
                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                }
            },
            showInLegend: true
        }
    },
    series: [{
        name: "比例",
        type: 'pie',
        innerSize: '60%',
        colorByPoint: true,
        data: []
    }],
    credits: {
        text: "电影天堂",
        href: CREDIT_ORIGIN
    }
};

function reader_place() {
    stat_place(function (data) {
        var config = place_config;
        config.series[0].data.length = 0;
        for (var item of data.items) {
            config.series[0].data.push({
                name: item.key,
                y: item.value
            });
        }
        config.series[0].data.push({
            name: "其他",
            y: data.otherSize
        });
        config.series[0].data[0].sliced = true;
        config.series[0].data[0].selected = true;
        $("#place_chart").highcharts(config);
    });
}

/**
 * 语言数据
 */
var language_config = {
    // chart: {
    //     type: 'pie'
    // },
    title: {
        text: '语言分布'
    },
    tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b><br>数量: <b>{point.y}</b>'
    },
    plotOptions: {
        pie: {
            allowPointSelect: true,
            cursor: 'pointer',
            dataLabels: {
                enabled: true,
                format: '<b>{point.name}</b>',
                style: {
                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                }
            },
            startAngle: -90, // 圆环的开始角度
            endAngle: 90, // 圆环的结束角度
            center: ['50%', '75%'],
            showInLegend: true
        }
    },
    series: [{
        type: 'pie',
        name: "比例",
        colorByPoint: true,
        innerSize: '50%',
        data: []
    }],
    credits: {
        text: "电影天堂",
        href: CREDIT_ORIGIN
    }
};

function reader_language() {
    stat_language(function (data) {
        var config = language_config;
        config.series[0].data.length = 0;
        for (var item of data.items) {
            config.series[0].data.push({
                name: item.key,
                y: item.value
            });
        }
        config.series[0].data.push({
            name: "其他",
            y: data.otherSize
        });
        config.series[0].data[0].sliced = true;
        config.series[0].data[0].selected = true;
        $("#lang_chart").highcharts(config);
    });
}

/**
 * 年份数据
 */
var year_config = {
    // chart: {
    //     type: 'pie'
    // },
    title: {
        text: '年代分布'
    },
    tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b><br>数量: <b>{point.y}</b>'
    },
    plotOptions: {
        pie: {
            allowPointSelect: true,
            cursor: 'pointer',
            dataLabels: {
                enabled: true,
                format: '<b>{point.name}</b>',
                style: {
                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                }
            },
            startAngle: -135, // 圆环的开始角度
            endAngle: 135, // 圆环的结束角度
            center: ['50%', '50%'],
            showInLegend: true
        }
    },
    series: [{
        type: 'pie',
        name: "比例",
        colorByPoint: true,
        innerSize: '50%',
        data: []
    }],
    credits: {
        text: "电影天堂",
        href: CREDIT_ORIGIN
    }
};

function reader_year() {
    stat_year(function (data) {
        var config = year_config;
        config.series[0].data.length = 0;
        for (var item of data.items) {
            config.series[0].data.push({
                name: item.key,
                y: item.value
            });
        }
        config.series[0].data.push({
            name: "其他",
            y: data.otherSize
        });
        config.series[0].data[0].sliced = true;
        config.series[0].data[0].selected = true;
        $("#year_chart").highcharts(config);
    });
}

/**
 * 每月电影数量
 */
var month_config = {
    title: {
        text: '电影数量分布图'
    },
    yAxis: {
        title: {
            text: '电影数量'
        }
    },
    xAxis: {
        categories: ['一月', '二月', '三月', '四月', '五月', '六月',
            '七月', '八月', '九月', '十月', '十一月', '十二月'
        ]
    },
    // tooltip: {
    //     crosshairs: true,
    //     shared: true
    // },
    plotOptions: {
        series: {
            label: {
                connectorAllowed: false
            }
        }
    },
    series: [],
    credits: {
        text: "电影天堂",
        href: CREDIT_ORIGIN
    }
};

function reader_month_count() {
    var now = new Date().getFullYear();
    var years = []
    var i = 0;
    for (var i = 0; i < 5; i++) {
        years.push(now - i);
    }
    stat_month_count(years.join(','), function (data) {
        var config = month_config;
        config.series.length = 0;
        for (var i of data) {
            var item = {
                name: i.key + "年",
                data: []
            };
            for (var j of i.value) {
                item.data.push(j.value);
            }
            config.series.push(item);
        }
        $("#month_year_chart").highcharts(config);
    });
}


/**
 * 每年地区电影数
 */
var place_year_config = {
    chart: {
        type: 'bar'
    },
    title: {
        text: '各地区近2年产出电影数'
    },
    xAxis: {
        categories: [],
        title: {
            text: null
        }
    },
    yAxis: {
        min: 0,
        title: {
            text: '电影数',
            align: 'high'
        },
        labels: {
            overflow: 'justify'
        }
    },
    tooltip: {
    },
    plotOptions: {
        bar: {
            dataLabels: {
                enabled: true,
                allowOverlap: true // 允许数据标签重叠
            },
            colorByPoint: true
        }
    },
    legend: {
        layout: 'vertical',
        align: 'right',
        verticalAlign: 'top',
        x: -40,
        y: 100,
        floating: true,
        borderWidth: 1,
        backgroundColor: ((Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'),
        shadow: true
    },
    series: [],
    credits: {
        text: "电影天堂",
        href: CREDIT_ORIGIN
    }
};

function reader_place_count() {
    var now = new Date().getFullYear();
    var years = []
    var i = 0;
    for (var i = 0; i < 2; i++) {
        years.push(now - i);
    }
    stat_place_count(years.join(","), function (data) {
        var config = place_year_config;
        config.series.length = 0;

        var x = new Set();
        for(var i of data){
            for(var j of i.value){
                x.add(j.key);
            }
        }
        config.xAxis.categories.length = 0;
        for(var j of x){
            config.xAxis.categories.push(j);
        }

        for(var i of data){
            var item = {
                name: i.key + "年",
                data: []
            };
            var kv = {};
            for(j of i.value){
                kv[j.key] = j.value;
            }
            console.log(kv);
            for(var j of x){
                if(j in kv){
                    item.data.push(kv[j]);
                } else {
                    item.data.push(0);
                }
            }
            config.series.push(item);
        }
        $("#place_year_chart").highcharts(config);
    });
}