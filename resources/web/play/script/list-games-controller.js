function ListGamesController($rootScope, $scope, cah) {

    $scope.languages = [];
    $scope.selectedLanguage = { id: 1 };
    $scope.decks = [];
    $scope.selectedDecks = [];
    $rootScope.game = { };

    $scope.newgame = { name: '' };

    $scope.createGame = function(game) {
	cah.createGame(game, $scope.selectedDecks, $rootScope.setGames);
    };

    $scope.joinGame = function(game, user) {
	cah.joinGame(game, user, $rootScope.setGames);
    };

    $scope.startGame = function(game, user) {
	cah.startGame(game, user, $rootScope.setGames);
    };

    $scope.filterGames = function(games, user, filter, inverse) {
	return cah.filterGames(games, user, filter, inverse);
    };

    $scope.isUserInGame = function(game, user) {
	return cah.isUserInGame(game, user);
    };

    $scope.isUsersTurn = function(game, user) {
	return cah.isUsersTurn(game, user);
    };

    $scope.isStarted = function(game, user) {
	return cah.isStarted(game, user);
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

    $scope.currentGame = {};

/*    $scope.playGame = function(game, user) {
	$scope.gotoPage('/play/' + game.id + "/");
    };*/

    $scope.setCurrentGame = function(game) {
	$scope.currentGame = game;
	cah.getPlayerDetails(game, $scope.setPlayerDetails);
    };
    
    $scope.setPlayerDetails = function(player) {
	$scope.currentGame.player = player;
    };

    $scope.selectLanguage = function(lang) {
	$scope.selectedLanguage = lang;
	cah.loadDecks($scope.setDecks, $scope.selectedLanguage.id, true);
    };

    $scope.setLanguages = function(languages) {
	$scope.languages = languages;
	$scope.selectedLanguage = $scope.languages[0];
	cah.loadDecks($scope.setDecks, $scope.selectedLanguage.id, true);
    };

    $scope.setDecks = function(decks) {
	$scope.decks = decks;
	$scope.selectedDecks = [];
	if ($scope.decks.length > 0) {
	    $scope.selectedDecks.push($scope.decks[0]);
	}
    }

    $scope.isSelected = function(deck) {
	return $scope.selectedDecks.indexOf(deck) >= 0;
    }

    $scope.toggleSelected = function(deck) {
	var idx = $scope.selectedDecks.indexOf(deck);
	if (idx < 0) {
	    $scope.selectedDecks.push(deck);
	} else {
	    $scope.selectedDecks.splice(idx, 1);
	}
    };

    cah.loadLanguages($scope.setLanguages);
}
