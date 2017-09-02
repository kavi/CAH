function GameCtrl($scope, $http) {

    
    $scope.init = function() {
	$scope.activeMenu.init();
    }

    $scope.setActiveMenu = function(menu) {
	$scope.activeMenu = menu;
	$scope.activeMenu.init();
    };

    $scope.logout = function() {
	$http.defaults.headers.common.Authorization = 'Basic ';
	$scope.setActiveMenu($scope.loginMenu);
    };


    $scope.isActiveMenu = function(menu) {
	if ($scope.activeMenu == menu) {
	    return "active";
	}
	return "";
    }

  $scope.login = function() {
	url = '../user';
	var auth = btoa($scope.currentuser.name + ':' + $scope.currentuser.password);
	$http.defaults.headers.common.Authorization = 'Basic ' + auth;
	$http.get(url).
            success(function(data) {
		$scope.authenticated = true;
		$scope.setActiveMenu($scope.menus[0]);
            }).
	    error(function(data) {
		$scope.authenticated = false;
		$scope.password = '';
		alert(data);
	    });
    };



    $scope.initOpenGames = function() {
	url = '../user/games/started';
	$http.get(url).
            success(function(data) {
		$scope.games = data;
            }).
	    error(function(data) {
		$scope.authenticated = false;
		$scope.password = '';
		alert(data);
	    });
    };

    $scope.initLogin = function() {
	$scope.authenticated = false;
	$scope.currentuser = { name: '', password: '' };	
    };

    $scope.loginMenu = { id: 'login', title: 'Login', init: $scope.initLogin, loginRequired: false };

    $scope.menus = [ { id: 'open-games', title: 'Play', init: $scope.initOpenGames, loginRequired: true },
		     { id: 'logout', title: 'Logout', init: $scope.logout, loginRequired: true }
		   ];

    $scope.menus.push($scope.loginMenu);

    $scope.activeMenu = $scope.loginMenu;


    $scope.init();
}
