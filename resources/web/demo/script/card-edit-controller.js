function CardEditController($rootScope, $scope, cah) {
    $scope.menu = "CAH Card Editor";

    // TODO 
    $scope.languages = [];
    $scope.decks = [];
    if (!$rootScope.deck) {
	$rootScope.deck = { id: -1};
    }

    $scope.whiteCards = [];
    $scope.blackCards = [];

    $scope.newWhiteCard = { text: '' };
    $scope.newBlackCard = { text: '', pick: 1, draw: 0 };


    $scope.editModel = { 
	card: {}, 
	cardOld: {},
	type: '',
	edit: true
    };

    $scope.getModalName = function() {
	return 'modal_' + $scope.editModel.type + '_edit';
    };

    $scope.openEditCard = function(card, type) {
	if ($scope.ignoreOpen) {
	    $scope.ignoreOpen = false;
	    return;
	}
	$scope.editModel.showDetails = false;
	$scope.editModel.cardOld = card;
	$scope.editModel.edit = true;
	$scope.editModel.card = { text: card.text, safeForWork: card.safeForWork };
	if (type == 'black') {
	    $scope.editModel.card.cardsToPick = card.cardsToPick;
	    $scope.editModel.card.cardsToDraw = card.cardsToDraw;
	}
	$scope.editModel.type = type;
	var mname = $scope.getModalName();
	$scope.openModal(mname);
    };

    $scope.openCreateCard = function(type) {
	$scope.editModel.showDetails = false;
	$scope.editModel.edit = false;
	$scope.editModel.card.text = '';
	$scope.editModel.card.safeForWork = false;
	$scope.editModel.cardOld = {};
	if (type == 'black') {
	    $scope.editModel.card.cardsToPick = 1;
	    $scope.editModel.card.cardsToDraw = 0;
	}
	$scope.editModel.type = type;
	var mname = $scope.getModalName();
	$scope.openModal(mname);
    };

    $scope.closeEdit = function() {
	$scope.closeModal($scope.getModalName());
    };

    $scope.saveAndCloseEdit = function() {
	if ($scope.editModel.type == 'black') {
	    if ($scope.editModel.edit) {
    		cah.updateBlackCard($scope.editModel.cardOld, $scope.editModel.card, $scope.setDeck);
	    } else {
		cah.createBlackCard($scope.editModel.card, $scope.setDeck);
	    }
	} else {
	    if ($scope.editModel.edit) {
    		cah.updateWhiteCard($scope.editModel.cardOld, $scope.editModel.card, $scope.setDeck);
	    } else {
		cah.createWhiteCard($scope.editModel.card, $scope.setDeck);
	    }
	}
	$scope.closeEdit();
    };

    $scope.deleteAndCloseEdit = function() {
	var editmodel = $scope.editModel;
	var card = editmodel.cardOld;
	if (editmodel.type == 'black') {
	    $scope.deleteBlackCard(card);
	} else {
	    $scope.deleteWhiteCard(card);
	    console.log('After delete ' + card);
	}
	$scope.closeEdit();
	$scope.ignoreOpen = false;
    };

    $scope.deleteWhiteCard = function(card) {
	$scope.ignoreOpen = true;
	cah.deleteWhiteCard(card, $rootScope.deck, $scope.setDeck);
    };

    $scope.deleteBlackCard = function(card) {
	$scope.ignoreOpen = true;
	cah.deleteBlackCard(card, $rootScope.deck, $scope.setDeck);
    };

    $scope.setDeck = function(deck) {
//	console.log(deck);
	$rootScope.deck = deck;
    }

    $scope.setDecks = function(decks) {
	$scope.decks = decks;
	var t = true;
	if ($rootScope.deck) {
	    for (var i = 0;i < $scope.decks.length;i++) {
		if ($scope.decks[i].id == $rootScope.deck.id) {
		    console.log('Found deck: ' + $rootScope.deck.id);
		    t = false;
		}
	    }
	}
	if (t) {
	    $rootScope.deck = $scope.decks[0];
	}
	cah.getDeck($scope.setDeck, $rootScope.deck.id, false);
    }

    $scope.setLanguages = function(languages) {
	$scope.languages = languages;
	if (!$rootScope.selectedLanguage) {
	    $rootScope.selectedLanguage = $scope.languages[0];
	}
    }

    $scope.getWhiteCardDetails = function(card) {
	if ($scope.editModel.showDetails) {
	    $scope.editModel.showDetails = false;
	} else {
	    $scope.editModel.showDetails = true;
	    if (!card.details) {
		cah.loadWhiteCard(card, $scope.setCardOld);
	    }
	}
    };

    $scope.setCardOld = function(card) {
	$scope.editModel.cardOld = card;
    };

    $scope.getBlackCardDetails = function(card) {
	if ($scope.editModel.showDetails) {
	    $scope.editModel.showDetails = false;
	} else {
	    $scope.editModel.showDetails = true;
	    if (!card.details) {
		cah.loadBlackCard(card, $scope.setCardOld);
	    }
	}
    };

    $scope.selectDeck = function(deck) {
	cah.loadDeck($scope.setDeck, deck.id, true);
    };


    // New Deck
    $scope.newDeck = { name: '' };

    $scope.createDeck = function(newdeck) {
	var tmp = { name: newdeck.name, language: $rootScope.selectedLanguage };
	newdeck.name = '';
	setTimeout(function() {
	    cah.createDeck(tmp, $scope.setDecks, $scope.setDeckGotoEdit);
	}, 5);
    }

    $scope.setDeckGotoEdit = function(deck) {
	$rootScope.deck = deck;	
	$rootScope.gotoPage('white-card-edit');
    }


    $scope.selectLanguage = function(language) {
	$rootScope.selectedLanguage = language;
	cah.getDecks($scope.setDecks, $rootScope.selectedLanguage.id, false);
    }

    $scope.init = function() {
	cah.loadLanguages($scope.setLanguages);
	var i = 1;
	var tmp = function() {
	    if ($rootScope.selectedLanguage) {
		cah.getDecks($scope.setDecks, $rootScope.selectedLanguage.id);
	    } else if (i < 100) {
		i++;
		setTimeout(tmp, 10);
	    }
	}
	tmp();
    }

    $scope.init();
}
