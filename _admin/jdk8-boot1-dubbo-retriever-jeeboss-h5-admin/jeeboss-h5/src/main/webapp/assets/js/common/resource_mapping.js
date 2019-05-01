var mapping = {
	resource_info: "resource_info.html",
	resource_list:"resource_list.html",
	org_list:"org_list.html",
	role_list:"role_list.html",
	user_list:"user_list.html",
	session_list:"session_list.html",
	monitor_meter:"../monitor/monitor_meter.html",
	monitor_histogram:"../monitor/monitor_histogram.html",
	job_list:"../job/job_list.html",
	dict_list:"dict_list.html"
}

var getResource = function(key) {
	return mapping[key];
}