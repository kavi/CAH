function MainController($rootScope, $scope, $location, cah, chat, auth) {
    var pollGamesInterval = 1500;
//
    var pollChatInterval = 1000;

    $scope.menu = "Home";

    $rootScope.games = [];

    $scope.openModal = function(name) {
	$rootScope.Ui.turnOn(name);
    };

    $scope.closeModal = function(name) {
	$rootScope.Ui.turnOff(name);
    };

    // User agent displayed in home page
    $scope.userAgent = navigator.userAgent;
    

    $rootScope.$on('$locationChangeStart', function (event, next, current) {
	// redirect to login page if not logged in
	if ($location.path() !== '/login' && !$rootScope.globals.authenticated) {	    
	    $rootScope.$evalAsync(function() {
		$location.path('/login');
	    });
	}
    });
    
    $rootScope.$on('$routeChangeStart', function(){
	$rootScope.loading = true;
    });
    
    $rootScope.$on('$routeChangeSuccess', function(){
	$rootScope.loading = false;
    });
    
    $scope.login = function() {
	auth.login($scope.user, $scope.setUser)
	if ($rootScope.globals.authenticated) {
	    $location.path('/');
	} else {
	    // Authentication failed..
	}
    };
    
    $scope.gotoPage = function(path) {
	$location.path(path);
    };
    
    $scope.setUser = function(user) {
	$scope.user = user;
	if (user && $rootScope.globals.authenticated) {
	    cah.loadGames($rootScope.setGames);
	    if (!$rootScope.globals.polling) {
		setTimeout($scope.pollGames(), pollGamesInterval);
	    }
	}
    }
    
    $rootScope.logout = function() {
	auth.logout();
	$location.path('/login');
    };

    $rootScope.setGames = function(games) {
	$rootScope.games = games;
    }

    $scope.startGame = function(game, user) {
	cah.startGame(game, user, $rootScope.setGames);
	$scope.gotoPage('#/play/' + game.id);
    };

    $scope.canStartGame = function(game, user) {
	if (game.started) {
	    return false;
	}
	if (game.players.length < 3) {
	    return false;
	}
	return game.owner.id == user.id;
    };

    $scope.isUsersTurn = function(game, user) {
	return cah.isUsersTurn(game, user);
    }

    $scope.isUserInGame = function(game, user) {
	return cah.isUserInGame(game, user);
    }
    
    $scope.isStarted = function(game, user) {
	return cah.isStarted(game, user);
    }

    $scope.isOwner = function(game, user) {
	return game.owner.id == user.id;
    }

    $scope.filterGames = function(filter, inverse) {
	return cah.filterGames($rootScope.games, $scope.user, filter, inverse);
    }

    $scope.pollGames = function() {
	if ($rootScope.globals.authenticated) {
	    cah.loadGames($rootScope.setGames);
	    $rootScope.globals.polling = true;
	    setTimeout($scope.pollGames, pollGamesInterval);
	} else {
	    $rootScope.globals.polling = false;
	}
    }

    if ($rootScope.globals) {
	$rootScope.globals.polling = false;
    } else {
	$rootScope.globals = { polling: false };
    }

    // Init
    $scope.setUser(auth.checkLogin());

    $scope.startGame = function(game) {
	cah.startGame(game, $scope.user, $rootScope.setGames);
    };

    $scope.setChatRoom = function(room) {
	console.log('chatRoom set to: ' + room);
	$scope.chatRoom = room;
    }

    $scope.sendMessage = function(message) {
	if (message) { 
	    chat.sendMessage($rootScope.game.id, { message: message });
	    chat.loadChatRoom($rootScope.game.id, $scope.setChatRoom, false);
	}
    }

    $scope.pollChatRoom = function() {
	var now = new Date();
	console.log(now.getMinutes() + ':' + now.getSeconds() + ':' + now.getMilliseconds() + ' : callChatRoom called');
	if ($rootScope.globals.authenticated && $rootScope.game && $rootScope.game.id) {
	    console.log(now.getMinutes() + ':' + now.getSeconds() + ':' + now.getMilliseconds() + ' : polling chatRoom');
	    if ($rootScope.game) {
		chat.loadChatRoom($rootScope.game.id, $scope.setChatRoom, false);
	    }
	    setTimeout($scope.pollChatRoom, pollChatInterval);
	} else {
	    setTimeout($scope.pollChatRoom, 50);
	}
    }

    $scope.pollChatRoom();
}
