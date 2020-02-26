function addScript(){
	document.write("<script language='JavaScript' th:src='@{/layer/layer.js}'></script>")
};

addScript();

var X = {};

X.rest = function(serviceUrl, data, okHandler, errorHandler)
{
	jQuery.ajax({
		url: serviceUrl,
		method: 'POST',
		contentType: 'application/json',
		processData: false,
		data: JSON.stringify(data),
		dataType: 'json',
		success: function(ans){
			if(ans.error != 0)
			{
				if(errorHandler != null)
					errorHandler(ans.error, ans.reason);
				else
					X.restErrHandler(ans.error, ans.reason);
			}
			else
			{
				okHandler(ans.data);
			}
		},
		error: function(jqXHR, textStatus, errorThrown){
			X.httpErrHandler();
		}
	});
}

X.httpErrHandler = function()
{
	layer.msg('HTTP服务器错误', {time:2000, icon: 5});
}

X.restErrHandler = function(error, reason)
{
	layer.msg(reason, {time:2000, icon: 5});
}