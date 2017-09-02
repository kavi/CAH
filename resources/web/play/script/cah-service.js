function CahService($rootScope, $http) {
    var baseUrl = "../..";

    var languagesUrl = baseUrl + "/admin/languages"
    var decksUrl = baseUrl + "/admin/language/${languageId}/decks";
    var gameUrl = baseUrl + "/game";
    var userGamesUrl = baseUrl + "/user/games/started";

    var service = {};

    var games = [];

    service.loadingGameDetails = false;

    service.loadLanguages = function(setLanguages, block) {
	var b = block == undefined ? true : block;
	$rootScope.loading = b;
	$http.get(languagesUrl).
            success(function(data) {
		$rootScope.loading = false;
		service.decks = data;
		setLanguages(service.decks);
            }).
	    error(function(data) {
		$rootScope.loading = false;
		alert(data);
	    });
    };

    service.loadDecks = function(setDecks, languageId, block) {
	var b = block == undefined ? true : block;
	$rootScope.loading = b;
	var url = decksUrl.replace('${languageId}', languageId);
	$http.get(url).
            success(function(data) {
		$rootScope.loading = false;
		service.decks = data;
		setDecks(service.decks);
            }).
	    error(function(data) {
		$rootScope.loading = false;
		alert(data);
	    });
    };

    
    service.loadGames = function(setGames) {
	$http.get(gameUrl).
            success(function(data) {
		games = data;
		if (setGames) {
		    setGames(data);
		}
            }).
	    error(function(data) {
		$rootScope.logout();
		alert(data);
	    });
    };

    service.createGame = function(game, decks, setGames) {
	game.decks = decks;
	if ($rootScope.loading) {
	    return;
	};
	$rootScope.loading = true;
	$http.post(gameUrl, game).
            success(function(data) {
		$rootScope.loading = false;
		service.getGames(setGames);
            }).
	    error(function(data) {
		$rootScope.loading = false;
		if (data["errors"] != undefined ) {
		    var msg = "";
		    for (var i = 0;i < data.errors.length;i++) {
			msg += "message: " + data.errors[i].message + "\n"; 
			msg += "invalidValue: " + data.errors[i].invalidValue + "\n"; 
			msg += "propertyPath: " + data.errors[i].propertyPath + "\n"; 
		    }
		    alert("Error\n" + msg);
		} else {
		    alert(data);
		}
	    });
    };

    service.joinGame = function(game, user, setGames) {
	if ($rootScope.loading) {
	    return;
	};
	$rootScope.loading = true;
	var url = baseUrl + '/action/game/' + game.id + '/join';
	$http.post(url, {}).
            success(function(data) {
		$rootScope.loading = false;
		service.getGames(setGames);
            }).
	    error(function(data) {
		$rootScope.loading = false;
		alert(data);
	    });
    };

    service.startGame = function(game, user, setGames) {
	if ($rootScope.loading) {
	    return;
	};
	$rootScope.loading = true;
	var url = baseUrl + '/action/game/' + game.id + '/start';
	$http.put(url, {}).
            success(function(data) {
		$rootScope.loading = false;
		service.getGames(setGames);
            }).
	    error(function(data) {
		$rootScope.loading = false;
		alert(data);
	    });
    };

    service.getGameDetails = function(gameId, setGameDetails) {
	if (service.loadingGameDetails) {
	    setTimeout(function() {
		console.log('Simul loading of game details.. waiting');
		service.getGameDetails(gameId, setGameDetails);
	    }, 100);
	    return;
	}
	service.loadingGameDetails = true;
	var url = baseUrl + '/game/' + gameId;
	$http.get(url).
            success(function(data) {
		$rootScope.loading = false;
		service.loadingGameDetails = false;
		setGameDetails(data);
            }).
	    error(function(data) {
		$rootScope.loading = false;
		$rootScope.logout();
		alert(data);
	    });	
    };
    
    service.playCards = function(game, cards, setGame) {
	$rootScope.loading = true;
	var url = baseUrl + '/action/player/game/' + game.id + '/play-white';
	var cardIds = [];
	for (var i = 0;i < cards.length;i++) {
	    cardIds.push(cards[i].id);
	}
	$http.put(url, cardIds).
            success(function(data) {
		$rootScope.loading = false;
		game.currentPlayer.actionPerformed = true;
		service.getGameDetails(game.id, setGame);
            }).
	    error(function(data) {
		$rootScope.loading = false;
		alert(data);
	    });
    }

    service.vote = function(game, playedCard, score, setGameDetails) {
	$rootScope.loading = true;
	var url = baseUrl + '/action/player/game/' + game.id + '/vote';
	var vote = { playedCardId: playedCard.id, score: score };
	$http.put(url, vote).
            success(function(data) {
		$rootScope.loading = false;
		game.currentPlayer.actionPerformed = true;
		service.getGameDetails(game.id, setGameDetails);
            }).
	    error(function(data) {
		$rootScope.loading = false;
		alert(data);
	    });
    }

    service.nextRound = function(game, setGameDetails) {
	$rootScope.loading = true;
	var url = baseUrl + '/action/player/game/' + game.id + '/nextround';
	$http.put(url, {}).
            success(function(data) {
		$rootScope.loading = false;
		game.currentPlayer.actionPerformed = true;
		service.getGameDetails(game.id, setGameDetails);
            }).
	    error(function(data) {
		$rootScope.loading = false;
		alert(data);
	    });
    }

    service.filterGames = function(games, user, filter, inverse) {
	var res = [];
	for (var i = 0;i < games.length;i++ ) {
	    var game = games[i];
	    var accept = filter(game, user);
	    if (accept && !inverse) {
		res.push(game);
	    } else if (!accept && inverse) {
		res.push(game);
	    };
	};
	return res;
    };

    service.isUserInGame = function(game, user) {
	return game.currentPlayer && game.currentPlayer != null;
    };

    service.isUsersTurn = function(game, user) {
	if (!service.isUserInGame(game, user)) {
	    return false;
	}
	if (!game.started) {
	    return false;
	}
	return !game.currentPlayer.actionPerformed;
    };

    service.isStarted = function(game, user) {
	return game.started;
    };

    service.canStartGame = function(game, user) {
	if (game.started) {
	    return false;
	}
	if (game.players.length < 3) {
	    return false;
	}
	return game.owner.id == user.id;
    };

    return service;
}
