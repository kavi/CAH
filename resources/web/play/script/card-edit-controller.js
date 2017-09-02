function CardEditController($scope, cah) {
    $scope.menu = "CAH Card Editor";

    $scope.test = function(event) {
	alert(event);
    };

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
    		cah.updateBlackCard($scope.editModel.cardOld, $scope.editModel.card, $scope.setBlackCards);
	    } else {
		cah.createBlackCard($scope.editModel.card, $scope.setBlackCards);
	    }
	} else {
	    if ($scope.editModel.edit) {
    		cah.updateWhiteCard($scope.editModel.cardOld, $scope.editModel.card, $scope.setWhiteCards);
	    } else {
		cah.createWhiteCard($scope.editModel.card, $scope.setWhiteCards);
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
//	    alert('Deleting card: ' + card);
	    $scope.deleteWhiteCard(card);
	    console.log('After delete ' + card);
	}
	$scope.closeEdit();
    };

    $scope.deleteWhiteCard = function(card) {
	cah.deleteWhiteCard(card, $scope.setWhiteCards);
    };

    $scope.deleteBlackCard = function(card) {
	cah.deleteBlackCard(card, $scope.setBlackCards);
    };


    $scope.setBlackCards = function(cards) {
	$scope.blackCards = cards;
    };

    $scope.setWhiteCards = function(cards) {
	$scope.whiteCards = cards;
    };

    cah.getWhiteCards($scope.setWhiteCards);
    cah.getBlackCards($scope.setBlackCards);

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
}
