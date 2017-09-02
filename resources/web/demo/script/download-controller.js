function DownloadController($rootScope, $scope, cah, $routeParams) {
    $scope.languages = [];
    $scope.selectedLanguage = { id: 1 };
    $scope.decks = [];
    $scope.selectedDecks = [];

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

    $scope.decksAsList = function() {
	var str = $scope.selectedDecks[0].id;
	for (var i = 1;i < $scope.selectedDecks.length;i++) {
	    str += "," + $scope.selectedDecks[i].id;
	}
	return str;
    }

    cah.loadLanguages($scope.setLanguages);
}
