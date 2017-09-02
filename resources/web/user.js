function UserCtrl($scope, $http) {
    
    $scope.init = function() {
	$scope.authenticated = false;
	$scope.currentuser = { name: '', password: '', email: '' };
	$scope.newuser = { name: '', password: '', email: '' };
	$scope.activeMenu = 'login';

	$scope.newgame = { name: 'game1', password: '' };
    }

    $scope.setActiveMenu = function(menu) {
	$scope.activeMenu = menu;
    };

    $scope.logout = function() {
	$scope.init();
    };


    $scope.isActiveMenu = function(menu) {
	if ($scope.activeMenu == menu) {
	    return "active";
	}
	return "";
    }

    $scope.createUser = function() {
	url = '../user'
	$http.post(url, $scope.newuser).
            success(function(data) {
		alert('User created');
            }).
	    error(function(data) {
		alert(data);
	    });
	$scope.data.newCard.text = "";
    };

    $scope.createGame = function(game) {
	url = '../game'
	$http.post(url, game).
            success(function(data) {
		alert('Game created');
		$scope.loadGames();
            }).
	    error(function(data) {
		alert(data);
	    });
    };

    $scope.updateUser = function(user) {
	url = '../user'
	$http.put(url, user).
            success(function(data) {
		alert('User updated');
            }).
	    error(function(data) {
		alert(data);
	    });
    };


    $scope.validateUser = function(user) {
	if (user.email = '') {
	    return false;
	}
	if (user.password.length < 4) {
	    return false;
	}
	if (user.name.length < 3) {
	    return false;
	}
	if (user.name.indexOf(':') >= 0) {
	    return false;
	} 
	if (user.name.indexOf('>') >= 0) {
	    return false;
	} 
	if (user.name.indexOf('<') >= 0) {
	    return false;
	} 
	if (user.name.indexOf('"') >= 0) {
	    return false;
	} 
	if (user.name.indexOf("'") >= 0) {
	    return false;
	} 
	return true;
    };

  $scope.login = function() {
	url = '../user';
	var auth = btoa($scope.currentuser.name + ':' + $scope.currentuser.password);
	$http.defaults.headers.common.Authorization = 'Basic ' + auth;
	$http.get(url).
            success(function(data) {
		$scope.authenticated = true;
		$scope.setActiveMenu('create');
		$scope.currentuser.id = data.id;
		$scope.currentuser.name = data.name;
		$scope.currentuser.email = data.email;
		$scope.loadGames();
		
            }).
	    error(function(data) {
		$scope.authenticated = false;
		$scope.currentuser.password = '';
		alert(data);
	    });
    };

    $scope.currentUserInGame = function(game) {
	for (i = 0;i < game.players.length;i++) {
	    if (game.players[i].user.id == $scope.currentuser.id) {
		return true;
	    }
	}
	return false;
    };


    $scope.loadGames = function() {
	url = '../game'
	$http.get(url).
            success(function(data) {
		$scope.games = data;
            }).
	    error(function(data) {
		alert(data);
	    });
    };

    $scope.joinGame = function(game) {
	url = '../action/game/' + game.id + '/join'
	$http.post(url).
            success(function(data) {
		$scope.loadGames();
            }).
	    error(function(data) {
		alert('Unable to join\n' + data);
	    });
    };

    $scope.startGame = function(game) {
	url = '../action/game/' + game.id + '/start'
	$http.put(url).
            success(function(data) {
		alert('Game started');
            }).
	    error(function(data) {
		alert('Unable to start\n' + data);
	    });
    };

    $scope.init();
}
