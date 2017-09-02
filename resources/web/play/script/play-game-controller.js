function PlayGameController($rootScope, $scope, cah, chat, $routeParams) {
    var pollGameDetailsInterval = 3000;

    $scope.gameId =  $routeParams.gameId;
    $rootScope.game = { id: $scope.gameId }
    $scope.pickedCards = [];

    $scope.setGame = function(game) {
	if ($rootScope.game.id != game.id) {
	    console.log('State game id: ' + game.id + ' new id: ' + $rootScope.game.id);
	    return;
	}
	if (!game.started || !game.active) {
	    alert('Selected game is not in a state to play');
	    return;
	}
	$rootScope.game = game;
    };
    

    $scope.pickCard = function(card) {
	$scope.pickedCards.push(card);
	card.picked = true;
	card.position = $scope.pickedCards.length;
    };

    $scope.togglePicked = function(card) {
	if ($rootScope.game.currentPlayer.actionPerformed) {
	    // Do nothing if player has already performed an action
	    return;
	}
	if (card.picked) {
	    $scope.removePickedCard(card);
	} else {
	    if ($scope.pickedCards.length >= $rootScope.game.activeBlackCard.card.cardsToPick) {
		$scope.removePickedCard($scope.pickedCards[$scope.pickedCards.length - 1]);
	    }
	    $scope.pickCard(card);
	}
    };

    $scope.removePickedCard = function(card) {
	if (card.picked) {
	    var idx = $scope.pickedCards.indexOf(card);
	    $scope.pickedCards.splice(idx, 1);
	    card.picked = false;
	    card.position = undefined;
	    for (var i = 0;i < $scope.pickedCards.length;i++) {
		$scope.pickedCards[i].position = i + 1;
	    }
	}
    };

    $scope.vote = function(playedCard) {
	cah.vote($rootScope.game, playedCard, 1, $scope.setGame);
    };

    $scope.nextRound = function() {
	cah.nextRound($rootScope.game, $scope.setGame);
    };

    $scope.playCards = function(game) {
	cah.playCards(game, $scope.pickedCards, $scope.setGame);
    };

    $scope.pollGameDetails = function() {
	if ($rootScope.globals.authenticated && $scope.gameId == $rootScope.game.id) {
	    var now = new Date();
	    console.log(now.getMinutes() + ':' + now.getSeconds() + ':' + now.getMilliseconds() + ' : polling');
	    if ($rootScope.game && $rootScope.game.currentPlayer && $rootScope.game.currentPlayer.actionPerformed) {
		cah.getGameDetails($rootScope.game.id, $scope.setGame);
	    }
	    setTimeout($scope.pollGameDetails, pollGameDetailsInterval);
	}
    }

    cah.getGameDetails($rootScope.game.id, $scope.setGame);

    $scope.pollGameDetails();

}
