function BlackCardCtrl($scope, $http) {
    
    $scope.cards_per_page = 5;
    $scope.current_page = 1;
    var maxpagesshown = 7;

    $scope.show = "list";

    $scope.filter = "";

    $scope.current_card = undefined;

    $scope.data = { newCard : { text: "", cardsToPick: 1, cardsToDraw: 0 }, cards: [] };

    $scope.setShow = function(toShow) {
	$scope.show = toShow;
    };

    $scope.filterCards = function() {
	if ($scope.filter == "") {
	    return $scope.data.cards;
	}
	var res = [];
	for (i = 0;i < $scope.data.cards.length;i++) {
	    var card = $scope.data.cards[i];
	    if (card.text.toLowerCase().indexOf($scope.filter.toLowerCase()) >= 0) {
		res.push(card);
	    }
	}
	if ($scope.current_page > calcNumberOfPages(res.length)) {
	    $scope.current_page = calcNumberOfPages(res.length);
	}
	return res;
    };

    $scope.listCards = function() {
	var first = ($scope.current_page - 1) * $scope.cards_per_page;
	var last = first + $scope.cards_per_page;
        return $scope.filterCards().slice(first,last);
    };

    $scope.getPages = function() {
	var res = [];
	var max = $scope.getNumberOfPages();
	if (max <= maxpagesshown) {
	    for (i = 1;i <= max ;i++) {
		res.push(i);
	    }
	} else {
	    res.push(1);
	    var start = $scope.current_page - 5;
	    if (start < 1) {
		start = 1;
	    }	    
	    for (i = start;i <= start + maxpagesshown - 2;i++) {
		if (i <= 1) {
		} else if (i >= max) {

		} else {
		    res.push(i);
		}
	    }
	    res.push(max);
	}
	return res;
    };

    $scope.getPageClass = function(p) {
	if ($scope.current_page == p) {
	    return "btn-primary";
	} else {
	    return "";
	}
    };

    $scope.setPage = function(p) {
	var max = $scope.getNumberOfPages();
	if (p > max) {
	    p = max;
	}
	if (p < 1) {
	    p = 1;o
	}
	$scope.current_page = p;
    };

    var calcNumberOfPages = function(size) {
	return Math.ceil(size / $scope.cards_per_page);
    }

    $scope.getNumberOfPages = function() {
	return calcNumberOfPages($scope.filterCards().length);
    };

    $scope.prev_page = function() {
	$scope.current_page = $scope.current_page - 1;
	if ($scope.current_page < 1) {
	    $scope.current_page = $scope.getNumberOfPages();
	}
    };

    $scope.next_page = function() {
	$scope.current_page = $scope.current_page + 1;
	if ($scope.current_page > $scope.getNumberOfPages()) {
	    $scope.current_page = 1;
	}
    };

    $scope.selectCard = function(card) {
	$scope.current_card = card;
	$scope.current_card.newtext = card.text;
    };

    $scope.deselectCard = function(card) {
	$scope.current_card = undefined;
    };

    $scope.isCardSelected  = function(card) {
	return $scope.current_card === card;
    };

    $scope.getCardText = function(card) {
	var l = 30;
	if (card.text.length <= l) {
	    return card.text;
	}
	return card.text.substring(0,l - 3) + "...";
    };

    $scope.loadCards = function() {
	url = '../admin/black-cards'
	$http.get(url).
            success(function(data) {
//		if ($scope.current_card === undefined && data.length >= 1) {
//		    $scope.current_card = data[0];
//		}
                $scope.data.cards = data;
                $scope.data.cards.sort(function(a, b) {
		    if (a.text == b.text) {
			return 0;
		    }
		    return a.text < b.text ? -1 : 1;
		});
            }).
	    error(function(data) {
		alert(data);
	    });
    };

    $scope.createCard = function() {
	url = '../admin/black-cards'
	$http.post(url, $scope.data.newCard).
            success(function(data) {
		var last_page = ($scope.current_page == $scope.getNumberOfPages());
		$scope.data.cards.push($scope.data.newCard);
		$scope.data.newCard = { text: '', cardsToPick: 1, cardsToDraw: 0 };
		if (last_page) {
		    $scope.current_page = $scope.getNumberOfPages();
		}
		$scope.loadCards();
            }).
	    error(function(data) {
		alert(data);
	    });
	$scope.data.newCard.text = "";
    };

    $scope.updateCard = function(card) {
	url = '../admin/black-cards'
	$http.put(url, card).
            success(function(data) {
		$scope.loadCards();
            }).
	    error(function(data) {
		alert(data);
	    });
    };

    $scope.deleteCard = function(card) {
	url = '../admin/black-card/' + card.id;
	var tmp = $scope.current_page;
	var index = $scope.data.cards.indexOf(card);
	if (index > -1) {
		$http.delete(url).
		success(function(data) {
		    $scope.data.cards.splice(index, 1);
		    if (tmp > $scope.getNumberOfPages()) {
			$scope.prev_page();
		    }
		    //		    $scope.loadCards();
		}).
		error(function(data) {
		    alert(data);
		});
	}
    };

    $scope.validateUpdateCard = function(card) {
	return card.text != card.newtext;
    };

    $scope.validateNewCard = function() {
	return $scope.data.newCard.text != "";
    };

    $scope.login = function() {
	url = '../user';
  	var auth = btoa($scope.currentuser.name + ':' + $scope.currentuser.password);
	$http.defaults.headers.common.Authorization = 'Basic ' + auth;
	$http.get(url).
            success(function(data) {
		$scope.authenticated = true;
		$scope.currentuser.id = data.id;
		$scope.currentuser.name = data.name;
		$scope.currentuser.email = data.email;
		$scope.loadCards();
		
            }).
	    error(function(data) {
		$scope.authenticated = false;
		$scope.currentuser.password = '';
		$http.defaults.headers.common.Authorization = '';
		alert(data);
	    }); 
    };
    
    $scope.init = function() {
	$scope.currentuser = { name: 'Admin', password: '' };
	$scope.authenticated = false;
    };

    $scope.init();
}
