function GreeterService(win) {
	var service = {};
	service.msgs = [];
    service.notify = function(msg) {
    	service.msgs.push(msg);
    	if (service.msgs.length == 2) {
    		win.alert(service.msgs.join("\n"));
    		msgs = [];
    	}
    };

    service.hello = function(msg) {
    	win.alert('Hello ' + msg);
    };
    return service;
}