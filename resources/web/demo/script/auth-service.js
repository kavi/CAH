function AuthService($rootScope, $http, $cookieStore, $window) {
    var baseUrl = "../..";
    var userUrl = baseUrl + "/user";


    var service = {};

    service.login = function(user, setUser) {
	var username = user.name;
	var password = user.password;

  	var auth = btoa(username + ':' + password);
	$http.defaults.headers.common.Authorization = 'Basic ' + auth;

	$http.get(userUrl).
            success(function(data) {
		var user = data;
		user.password = password; // Password is obscured in JSON so recover it here

		$rootScope.globals = { 'user': user, authenticated: true };
		$cookieStore.put('globals', $rootScope.globals);

		setUser(data);
		$window.location.href = '#/';
            }).
	    error(function(data) {
		var user = { 'id': undefined, 'name': '', 'password': '' };
		$rootScope.globals = { user: user, authenticated: false };
		alert(data);
	    }); 

    };
    
    service.checkLogin = function() {
	$rootScope.globals = $cookieStore.get('globals') || { authenticated: false};
	if ($rootScope.globals.authenticated && $rootScope.globals.user) {
	    var user = $rootScope.globals.user;

  	    var auth = btoa(user.name + ':' + user.password);
	    $http.defaults.headers.common.Authorization = 'Basic ' + auth;	    

	    return user;
	} else {
	    return { name: '', password: '' };
	}
    };

    service.logout = function() {
	$cookieStore.remove('globals');
	$rootScope.globals = { user: { name: '', password: '' }, authenticated: false };
	$http.defaults.headers.common.Authorization = '';
    };

    service.updateUser = function(user, setUser) {
	$http.put(userUrl, user).
            success(function(data) {
		$rootScope.globals = { user: user, authenticated: true };
		$cookieStore.put('globals', $rootScope.globals);
  		var auth = btoa(user.name + ':' + user.password);
		$http.defaults.headers.common.Authorization = 'Basic ' + auth;
		setUser(user);
            }).
	    error(function(data) {
		alert(data);
	    });
    };

    service.createUser = function(user, setUser) {
	$http.post(userUrl, user).
            success(function(data) {
		$rootScope.globals = { user: user, authenticated: true };
		$cookieStore.put('globals', $rootScope.globals);
  		var auth = btoa(user.name + ':' + user.password);
		$http.defaults.headers.common.Authorization = 'Basic ' + auth;
		setUser(user);
            }).
	    error(function(data) {
		if (data.length > 0 && data[0].message) {
		    alert(data[0].message);
		}
	    });
    };

    return service;
}
