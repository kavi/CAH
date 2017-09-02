function ChatService($rootScope, $http) {
    var baseUrl = "../..";

    var chatUrl = baseUrl + "/chat"

    var service = {};

    var games = [];

    service.loadingGameDetails = false;

    service.loadChatRoom = function(gameId, setChatRoom, block) {
	console.log('loading chatRoom');
	var b = block == undefined ? true : block;
	$rootScope.loading = b;
	var url = chatUrl + "/" + gameId;
	$http.get(url).
            success(function(data) {
		$rootScope.loading = false;
		setChatRoom(data);
            }).
	    error(function(data) {
		$rootScope.loading = false;
		alert(data);
	    });
    };

    service.sendMessage = function(gameId, message) {
	if ($rootScope.loading) {
	    return;
	};
	var url = chatUrl + "/" + gameId;
	$rootScope.loading = true;
	$http.post(url, message).
            success(function(data) {
		$rootScope.loading = false;
            }).
	    error(function(data) {
		$rootScope.loading = false;
		alert(data);
	    });
    };

    return service;
}
