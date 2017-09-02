function CahService($rootScope, $http) {
    var baseUrl = "../..";
    var languagesUrl = baseUrl + "/admin/languages"
    var languageUrl = baseUrl + "/admin/language/${languageId}";
    var getDecksUrl = baseUrl + "/admin/language/${languageId}/decks";
    var deckUrl = baseUrl + "/admin/deck";
    var whiteCardsUrl = baseUrl + "/admin/deck/${deckId}/white-cards";
    var whiteCardUrl = baseUrl + "/admin/white-card";
    var blackCardsUrl = baseUrl + "/admin/deck/${deckId}/black-cards";
    var blackCardUrl = baseUrl + "/admin/black-card";

    var service = {};

    service.cachetimeout = 10000; // ms
    service.deck = undefined;
    service.decks = undefined;
    service.language = undefined;

    service.loadLanguages = function(setLanguages, block) {
	var b = block == undefined ? true : block;
	$rootScope.loading = b;
	$http.get(languagesUrl).
            success(function(data) {
		$rootScope.loading = false;		
		service.languages = data;
		setLanguages(service.languages);
            }).
	    error(function(data) {
		$rootScope.loading = false;
		$rootScope.status.error = true;
		$rootScope.status.message = data;
	    });
    };

    service.createDeck = function(deck, setDecks, setDeck) {
	var url = languageUrl.replace('${languageId}', deck.language.id) + "/decks";
	$http.post(url, deck).
            success(function(data) {
		service.decks = undefined; // clear cache to enforce reloading of decks.
		service.loadDecks(setDecks, deck.language.id, false);
		setDeck(data);
            }).
	    error(function(data) {
		service.loadDecks(setDecks, deck.language.id);
		$rootScope.status.error = true;
		$rootScope.status.message = data;
	    });
    }

    service.loadDecks = function(setDecks, languageId, block) {
	var b = block == undefined ? true : block;
	$rootScope.loading = b;
	var url = getDecksUrl.replace('${languageId}', languageId);
	$http.get(url).
            success(function(data) {
		$rootScope.loading = false;
		service.decks = data;
		service.decks.cachetstamp = Date.now();
		service.language = { id: languageId };
		setDecks(service.decks);
            }).
	    error(function(data) {
		$rootScope.loading = false;
		$rootScope.status.error = true;
		$rootScope.status.message = data;
	    });
    };

    service.getDecks = function(setDecks, languageId, block) {
	if (service.decks && service.language && service.language.id == languageId) {
	    setDecks(service.decks);
	    if (Date.now() - service.decks.cachetstamp > service.cachetimeout) {
		console.log('re-loading decks');
		service.loadDecks(setDecks,languageId,block);
	    }
	} else {
	    service.loadDecks(setDecks,languageId,block);
	}
    };

    service.loadDeck = function(setDeck, deckId, block) {
	var b = block == undefined ? true : block;
	$rootScope.loading = b;
	$http.get(deckUrl + "/" + deckId).
            success(function(data) {
		$rootScope.loading = false;
		service.deck = data;
		console.log('loaded deck');
		service.deck.cachetstamp = Date.now();
		setDeck(service.deck);
            }).
	    error(function(data) {
		$rootScope.loading = false;
		$rootScope.status.error = true;
		$rootScope.status.message = data;
	    });
    };

    service.getDeck = function(setDeck, deckId, block) {
	if (service.deck && service.deck.id == deckId) {
	    setDeck(service.deck);
	    if (Date.now() - service.deck.cachetstamp > service.cachetimeout) {
		service.loadDeck(setDeck, deckId, true);
	    }
	} else {
	    service.loadDeck(setDeck, deckId, block);
	}
    }

    service.loadWhiteCard = function(card, setCard) {
	$http.get(whiteCardUrl + "/" + card.id).
            success(function(data) {
		setCard(data); 
            }).
	    error(function(data) {
		$rootScope.status.error = true;
		$rootScope.status.message = data;
	    });
    };

    service.loadBlackCard = function(card, setCard) {
	$http.get(blackCardUrl + "/" + card.id).
            success(function(data) {
		setCard(data);
            }).
	    error(function(data) {
		$rootScope.status.error = true;
		$rootScope.status.message = data;
	    });

    };

    service.deleteWhiteCard = function(card, deck, success) {
	var idx = deck.whiteCards.indexOf(card);
	deck.whiteCards.splice(idx, 1);
	$http.delete(whiteCardUrl + "/" + card.id).
            success(function(data) {
		service.loadDeck(success, service.deck.id, false );
            }).
	    error(function(data) {
		service.loadDeck(success, service.deck.id, true);
		$rootScope.status.error = true;
		$rootScope.status.message = data;
	    });
    };

    service.updateWhiteCard = function(card, newcard, success) {
	console.log("Update white card: " + card.text + " to " + newcard.text);
	card.text = newcard.text;
	card.safeForWork = newcard.safeForWork;
	var update = { id: card.id, newtext: card.text, safeForWork: card.safeForWork };
	var url = whiteCardsUrl.replace("${deckId}", service.deck.id);
	$http.put(url, update).
            success(function(data) {
		service.loadDeck(success, service.deck.id, false);
            }).
	    error(function(data) {
		service.loadDeck(success, service.deck.id);
		$rootScope.status.error = true;
		$rootScope.status.message = data;
	    });
    };

    service.createWhiteCard = function(card, success) {
	service.deck.whiteCards.push(card);
	var url = whiteCardsUrl.replace("${deckId}", service.deck.id);
	$http.post(url, card).
            success(function(data) {
		service.loadDeck(success, service.deck.id, false);
            }).
	    error(function(data) {
		service.loadDeck(success, service.deck.id);
		$rootScope.status.error = true;
		$rootScope.status.message = data;
	    });
    };

    service.updateBlackCard = function(card, newcard, success) {
	console.log("Update black card: " + card + " to " + newcard);
	card.text = newcard.text;
	card.safeForWork = newcard.safeForWork;
	card.cardsToDraw = newcard.cardsToDraw;
	card.cardsToPick = newcard.cardsToPick;
	var update = { id: card.id, newtext: card.text, cardsToPick: card.cardsToPick, cardsToDraw: card.cardsToDraw, safeForWork: card.safeForWork };
	var url = blackCardsUrl.replace("${deckId}", service.deck.id);
	$http.put(url, update).
            success(function(data) {
		service.loadDeck(success, service.deck.id, false);
            }).
	    error(function(data) {
		service.loadDeck(success, service.deck.id);
		$rootScope.status.error = true;
		$rootScope.status.message = data;
	    });
    };

    service.createBlackCard = function(card, success) {
	service.deck.blackCards.push(card);
	var url = blackCardsUrl.replace("${deckId}", service.deck.id);
	$http.post(url, card).
            success(function(data) {
		service.loadDeck(success, service.deck.id, false);
            }).
	    error(function(data) {
		service.loadDeck(success, service.deck.id);
		$rootScope.status.error = true;
		$rootScope.status.message = data;
	    });
    };

    service.deleteBlackCard = function(card, deck, success) {
	var idx = deck.blackCards.indexOf(card);
	deck.blackCards.splice(idx, 1);
	$http.delete(blackCardUrl + "/" + card.id).
            success(function(data) {
		service.loadDeck(success, service.deck.id, false);
            }).
	    error(function(data) {
		service.loadDeck(success, service.deck.id), true;
		$rootScope.status.error = true;
		$rootScope.status.message = data;
	    });
    };

    return service;
}
