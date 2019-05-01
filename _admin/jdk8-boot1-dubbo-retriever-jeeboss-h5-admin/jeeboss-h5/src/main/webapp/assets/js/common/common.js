var domain = "http://127.0.0.1:8088/";

$(function() {
	var index = getQueryByName("layerIndex");
	if(index) {
		parent.layer.close(index);
	}
});

var ready = function() {
	var index = layer.load(1, {
		shade: [0.1, '#fff']
		// 0.1透明度的白色背景
	});
	return index;
};

function getCookie(name) {
	var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
	if(arr = document.cookie.match(reg))
		return unescape(arr[2]);
	else
		return null;
}
var param2Json = function() {
	return $.parseJSON(param2String());
};

var getQueryByName = function(name) {
	var param = param2String();
	var params = param.split("&");
	for(var i = 0; i < params.length; i++) {
		var ky = params[i];
		var kys = ky.split("=");
		if(kys[0] == name) {
			return kys[1];
		}
	}
	return null;
};

function strToJson(str) {
	var json = (new Function("return " + str))();
	return json;
}
var param2String = function() {
	var param = decodeURI(window.location.href.slice(window.location.href
		.indexOf('?') + 1));
	return param;
};
var post = function(url, data, callBack, showLoading, async) {
	var index;
	if(showLoading == undefined || showLoading == true) {
		index = ready();
	}
	if(async == undefined) {
		async = true;
	}
	$.ajax({
		url: url,
		type: "post",
		dataType: "json",
		data: data,
		cache: false,
		async: async,
		headers: getHeaders(),
		success: function(data) {
			if(showLoading == undefined || showLoading == true) {
				layer.close(index);
			}
			if(data.ret == 401) {
				notLogin();
				return;
			}
			callBack(data);
		},
		error: function(data) {
			console.log(data);
		}
	});
};

var getHeaders = function() {
	var payload = localStorage.getItem("payload");
	payload = JSON.parse(payload);
	var headers = {};
	if(payload) {
		var stack = {
			reqId: guid(),
			list: [{
				method: window.location.pathname,
				duration: new Date().getTime()
			}]
		}
		headers = {
			payload: payload.token,
			trace: JSON.stringify(stack)
		};
	}
	return headers;
}

var guid = function() {
	function S4() {
		return(((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
	}
	return(S4() + S4() + S4() + S4() + S4() + S4() + S4() + S4());
}

var put = function(url, data, callBack, showLoading) {
	var index;
	if(showLoading == undefined || showLoading == true) {
		index = ready();
	}
	$.ajax({
		url: url,
		type: "put",
		dataType: "json",
		data: JSON.stringify(data),
		contentType: "application/json",
		cache: false,
		headers: getHeaders(),
		success: function(data) {
			if(showLoading == undefined || showLoading == true) {
				layer.close(index);
			}
			if(data.ret == 401) {
				notLogin();
				return;
			}
			callBack(data);
		},
		error: function(data) {
			console.log(data);
		}
	});
};

var notLogin = function() {
	window.location.href = "/modules/sys/login.html";
}

var get = function(url, callBack, showLoading, async) {
	if(showLoading == undefined || showLoading == true) {
		index = ready();
	}
	if(async == undefined) {
		async = true;
	}
	$.ajax({
		url: url,
		type: "get",
		dataType: "json",
		cache: false,
		async: async,
		headers: getHeaders(),
		success: function(data) {
			if(showLoading == undefined || showLoading == true) {
				layer.close(index);
			}
			if(data.ret == 401) {
				notLogin();
				return;
			}
			callBack(data);
		}
	});
};

var deleteMethod = function(url, callBack, showLoading, async) {
	if(showLoading == undefined || showLoading == true) {
		index = ready();
	}
	if(async == undefined) {
		async = true;
	}
	$.ajax({
		url: url,
		type: "delete",
		dataType: "json",
		cache: false,
		headers: getHeaders(),
		success: function(data) {
			if(showLoading == undefined || showLoading == true) {
				layer.close(index);
			}
			if(data.ret == 401) {
				notLogin();
				return;
			}
			callBack(data);
		}
	});
};

var pageTemp = '<nav aria-label="..."><ul class="pagination"><li class="disabled"><a href="#" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>{{each pages as page}}<li {{page.isCurrent?\'class=active\':\'onclick=jump(\'+page.num+\')\'}}><a href="#">{{page.num}} {{if page.isCurrent}}<span class="sr-only">(current)</span>{{/if}}</a></li>{{/each}}<li class="disabled"><a href="#" aria-label="Previous"><span aria-hidden="true">当前第{{info.pageNo}}页,每页{{info.pageSize}}条,共&nbsp;{{info.count}}条</span></a></li></ul></nav>';

var getPage = function(data) {
	var pages = new Array();
	for(var i = 1; i <= data.totalPage; i++) {
		var isCurrent = i == data.pageNo
		var page = {
			num: i,
			isCurrent: isCurrent
		}
		pages.push(page);
	}
	var render = template.compile(pageTemp);
	return render({
		pages: pages,
		info: data
	});
}

template.helper("toFixed", function(num, length) {
	var number = Number(num).toFixed(length);
	var nums = number.split(".");
	if(parseInt(nums[1]) == 0) {
		return nums[0]
	} else {
		var littleNum = parseInt(nums[1]);
		var m = 1;
		for(var i = 0; i < length - 1; i++) {
			m = m * 10;
		}
		if(littleNum % m == 0) {
			return nums[0] + "." + littleNum / m;
		}
	}
	return number;

});
template.helper('dateFormat', function(date, fmt) {
	var time = date;
	date = new Date();
	date.setTime(time);
	var o = {
		"M+": date.getMonth() + 1, // 月份
		"d+": date.getDate(), // 日
		"h+": date.getHours(), // 小时
		"m+": date.getMinutes(), // 分
		"s+": date.getSeconds(), // 秒
		"q+": Math.floor((date.getMonth() + 3) / 3), // 季度
		"S": date.getMilliseconds()
		// 毫秒
	};
	if(/(y+)/.test(fmt))
		fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "")
			.substr(4 - RegExp.$1.length));
	for(var k in o)
		if(new RegExp("(" + k + ")").test(fmt)) {

			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) :
				(("00" + o[k]).substr(("" + o[k]).length)));

		}

	return fmt;
});
template.helper("dateCompare", function(d1, d2) {
	if(d2 == undefined) {
		d2 = new Date();
	}
	var nDate = new Date();
	var hm = d1.split(":");
	nDate.setHours(hm[0]);
	nDate.setMinutes(hm[1]);
	return nDate.getTime() > d2.getTime();
});
template.helper("dateCompare2", function(d1, d2) {
	if(d2 == undefined) {
		d2 = new Date();
	}
	var nDate = new Date();
	var hm = d1.split(":");
	nDate.setHours(hm[0]);
	nDate.setMinutes(hm[1]);
	return nDate.getTime() < d2.getTime();
});

template.helper("getDicts", function(type) {
	var list;
	get(domain + "/sys/dicts/" + type, function(data) {
		if(data.ret == 0) {
			list =  data.data;
		}
	}, false, false);
	return list;
});

template.helper("getDictLabel", function(type, value, defaultValue) {
	var label;
	get(domain + "/sys/dict/" + type + "/" + value, function(data) {
		if(data.ret == 0 && data.data && data.data != "") {
			label = data.data.label;
		} else {
			label = defaultValue;
		}
	}, false, false);
	return label;
});