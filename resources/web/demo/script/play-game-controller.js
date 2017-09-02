function PlayGameController($scope, cah, $routeParams) {

    $scope.game = { id: $routeParams.gameId }

    $scope.pickedCards = [];


    $scope.playGame = function(game, user) {
	cah.getGameDetails(game, $scope.setGame);
	$scope.gotoPage('/play');
    };

    $scope.setGame = function(game) {
	if (!game.started || !game.active) {
	    alert('Selected game is not in a state to play');
	    return;
	}
	$scope.game = game;
	cah.getPlayerDetails(game, $scope.setPlayerDetails);
	cah.getActiveBlackCard(game);
    };
    
    $scope.setPlayerDetails = function(player) {
	$scope.game.player = player;
    };

    $scope.pickCard = function(card) {
	$scope.pickedCards.push(card);
	card.picked = true;
	card.position = $scope.pickedCards.length;
    };

    $scope.togglePicked = function(card) {
	if ($scope.game.player.actionPerformed) {
	    // Do nothing if player has already performed an action
	    return;
	}
	if (card.picked) {
	    $scope.removePickedCard(card);
	} else {
	    if ($scope.pickedCards.length >= $scope.game.activeBlackCard.card.cardsToPick) {
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
	cah.vote($scope.game, playedCard, 1, $scope.setGame);
    };

    $scope.nextRound = function() {
	cah.nextRound($scope.game, $scope.setGame);
    };

    $scope.playCards = function(game) {
	cah.playCards(game, $scope.pickedCards, $scope.setGame);
    };

    cah.getGameDetails($scope.game.id, $scope.setGame);
}
