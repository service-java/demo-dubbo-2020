$(function() {
	window.onresize = function() {
		setIframeSize();
	};
	loadUserResources();
	loadIndexData();
});

var showInfo = function(id) {
	layer.open({
		shade: 0.3,
		type: 2,
		area: ['520px', '80%'],
		fixed: false, //不固定
		maxmin: true,
		title: "个人信息",
		content: '/modules/sys/user_detail.html?id=' + id
	});
}

var logout = function(id) {
	deleteMethod(domain+"session/"+id,function(data){
		if(data.ret==0){
			window.location.reload();
		}else{
			layer.msg(data.msg);
		}
	})
}

var loadIndexData = function() {
	var payload = localStorage.getItem("payload");
	payload = JSON.parse(payload);
	if(payload) {
		get(domain + "sys/user/" + payload.uid, function(data) {
			$(".user-panel").html(template('user-panel-temp', data));
			$(".user-menu").html(template('user-info', data));
		});
	} else {
		window.location.href = "/modules/sys/login.html";
	}
}

var loadUserResources = function() {
	var payload = localStorage.getItem("payload");
	payload = JSON.parse(payload);
	if(payload) {
		get(domain + "sys/user/" + payload.uid + "/resources", function(data) {
			$("#menu-group").append(template('menu-temp', data));
			$(".sub-menu").click(function() {
				menuCilck(this);
			});
		});
	} else {
		//window.location.href = "/modules/sys/login.html";
	}

}

var menuCilck = function(menu) {
	var target = $(menu).attr("data-target");
	if(target) {
		$("li[data-targetId='" + target + "']").click();
		return;
	}
	var index = layer.load(1, {
		shade: [0.1, '#fff']
		// 0.1透明度的白色背景
	});
	var id = new Date().getTime();
	var resource = getResource($(menu).attr("data-href"));
	if(resource.indexOf("?")!=-1){
		resource+="&layerIndex="+index;
	}else{
		resource+="?layerIndex="+index;
	}
	var data = {
		name: $(menu).text(),
		id: id,
		href: resource
	}
	$(menu).attr("data-target", id);
	$("#content-main").append(template("content-temp", data));
	setIframeSize();
	var bar = $(template("status-bar-temp", data));
	$(".status-bar-container").append(bar);
	barCilck(bar[0]);
	bar.click(function() {
		barCilck(this);
	});
	bar.find(".status-bar-close").click(function() {
		barClose($(this).parent()[0])
	});

}

var barClose = function(bar) {
	if($(bar).hasClass("bar-active")) {
		if($(bar).prev()[0]) {
			$(bar).prev().click();
		} else if($(bar).next()[0]) {
			$(bar).next().click();
		}
	}
	var id = $(bar).attr("data-targetid");
	$("a[data-target='" + id + "']").removeAttr("data-target");
	$(bar).remove();
	$("iframe[data-iframeid='" + id + "']").remove();
}

var barCilck = function(bar) {
	console.log(bar);
	$(bar).parent().find(".bar-active").removeClass("bar-active");
	$(".iframe-active").removeClass(".iframe-active");
	$(".iframe-active").hide();
	$(bar).addClass("bar-active")
	var id = $(bar).attr("data-targetid");
	$("iframe[data-iframeid='" + id + "']").addClass("iframe-active");
	$("iframe[data-iframeid='" + id + "']").show();
}
var setIframeSize = function() {
	var nav = $("#nav-container").height() + 2;
	var wh = $(window).height();
	var mainHeaderHeight = $(".main-header").outerHeight(true);
	var bh = $(".content-header").outerHeight(true);
	var fh = $(".main-footer").outerHeight(true);
	$("iframe").css("height", wh - mainHeaderHeight - bh-fh-30 + 'px')
}
