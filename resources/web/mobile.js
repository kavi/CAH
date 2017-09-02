function WhiteCardCtrl($scope, $http) {
    
    $scope.cards_per_page = 5;
    $scope.current_page = 1;
    $scope.max_pages = 1;
    
    $scope.currentuser = { name: 'Admin', password: '' };

    $scope.authenticated = false;
    
    var maxpagesshown = 7;

    $scope.show = "list";

    $scope.no_filter_page = 1;
    $scope.filter = "";

    $scope.current_card = undefined;

    $scope.data = { newCard : { text: "" }, cards: [] };

    $scope.setShow = function(toShow) {
	$scope.show = toShow;
    };

    $scope.filterCards = function() {
	if ($scope.filter == "") {
	    $scope.current_page = $scope.no_filter_page;
	    return $scope.data.cards;
	}
	var res = [];
	for (i = 0;i < $scope.data.cards.length;i++) {
	    var card = $scope.data.cards[i];
	    if (card.text.toLowerCase().indexOf($scope.filter.toLowerCase()) >= 0) {
		res.push(card);
	    }
	}
	var num = calcNumberOfPages(res.length);
	if ($scope.current_page < $scope.no_filter_page) {
	    $scope.current_page = $scope.no_filter_page;
	}

	if ($scope.current_page > num && num >= 1) {
	    $scope.current_page = num;
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
	    var start = $scope.current_page - 2;
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
	$scope.no_filter_page = p;
	$scope.current_page = p;
    };

    var calcNumberOfPages = function(size) {
	return Math.ceil(size / $scope.cards_per_page);
    }

    $scope.getNumberOfPages = function() {
	$scope.max_pages = calcNumberOfPages($scope.filterCards().length);
	return $scope.max_pages;
    };

    $scope.prev_page = function() {
	$scope.current_page = $scope.current_page - 1;
	if ($scope.current_page < 1) {
	    $scope.current_page = $scope.max_pages;
	}
	$scope.no_filter_page = $scope.current_page;
    };

    $scope.next_page = function() {
	$scope.current_page = $scope.current_page + 1;
	if ($scope.current_page > $scope.max_pages) {
	    $scope.current_page = 1;
	}
	$scope.no_filter_page = $scope.current_page;
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
	if (card.text.length <= 25) {
	    return card.text;
	}
	return card.text.substring(0,17) + "...";
    };

    $scope.loadCards = function() {
	url = '../admin/white-cards';
	$http.get(url).
            success(function(data) {
                $scope.data.cards = data;
                $scope.data.cards.sort(function(a, b) {
                if (a.lastChanged == b.lastChanged) {
                	return 0;
                }
                return a.lastChanged > b.lastChanged ? -1 : 1;		   
		});
            }).
	    error(function(data) {
		alert(data);
	    });
    };

    $scope.loadCards = function() {
	url = '../admin/white-cards';
	$http.get(url).
            success(function(data) {
                $scope.data.cards = data;
                $scope.data.cards.sort(function(a, b) {
                if (a.lastChanged == b.lastChanged) {
                	return 0;
                }
                return a.lastChanged > b.lastChanged ? -1 : 1;		   
		});
            }).
	    error(function(data) {
		alert(data);
	    });
    };


    $scope.createCard = function() {
	url = '../admin/white-cards'
	$http.post(url, $scope.data.newCard).
            success(function(data) {
		var last_page = ($scope.current_page == $scope.getNumberOfPages());
		$scope.data.cards.push($scope.data.newCard);
		$scope.data.newCard = { text: '' };
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
	url = '../admin/white-cards'
	$http.put(url, card).
            success(function(data) {
		$scope.loadCards();
            }).
	    error(function(data) {
		alert(data);
	    });
    };

    $scope.deleteCard = function(card) {
	url = '../admin/white-card/' + card.id;
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
		alert(data);
	    }); 
    };
    
    $scope.init = function() {
//	$scope.login();
//	$scope.loadCards();
    };

    $scope.init();
}
