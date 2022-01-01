var registerApplications = null;
var applications = null;
$(document).ready(function () {
    reloadRegisterApplications();
    reloadApplications();
});

/**
 * 获取注册的arthas客户端
 */
function reloadRegisterApplications() {
    var result = reqSync("/api/arthas/clients", "get");
    registerApplications = result;
    initSelect("#selectServer", registerApplications, "");
}

function reloadAgent() {
    reloadRegisterApplications();
    reloadApplications();
}

/**
 * 获取注册的应用
 */
function reloadApplications() {
    applications = reqSync("/api/applications", "get");
    console.log(applications)
}

/**
 * 初始化下拉选择框
 */
function initSelect(uiSelect, list, key) {
    $(uiSelect).html('');
    var server;
    for (var i = 0; i < list.length; i++) {
        //server = list[i].toLowerCase().split("@");
        //if ("phantom-admin" === server[0]) continue;
        //$(uiSelect).append("<option value=" + list[i].toLowerCase() + ">" + server[0] + "</option>");
        server = list[i].toLowerCase();
        $(uiSelect).append("<option value=" + server + ">" + server + "</option>");
    }
}

/**
 * 重置配置文件
 */
function release() {
    var currentServer = $("#selectServer").text();
    for (var i = 0; i < applications.length; i++) {
        serverId = applications[i].id;
        serverName = applications[i].name.toLowerCase();
        console.log(serverId + "/" + serverName);
        if (currentServer === serverName) {
            var result = reqSync("/api/applications/" + serverId + "/env/reset", "post");
            alert("env reset success");
        }
    }
}

function reqSync(url, method) {
    var result = null;
    $.ajax({
        url: url,
        type: method,
        async: false, //使用同步的方式,true为异步方式
        headers: {
            'Content-Type': 'application/json;charset=utf8;',
        },
        success: function (data) {
            // console.log(data);
            result = data;
        },
        error: function (data) {
            console.log("error");
        }
    });
    return result;
}