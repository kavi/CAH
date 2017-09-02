//  Module dependencies
// 
var app = angular.module('CahDemo', [
    'ngRoute',
    'mobile-angular-ui',
    'ngCookies',
    'mobile-angular-ui.gestures'
]);

// Services
app.factory('cah', ['$rootScope', '$http', CahService]);
app.factory('auth', ['$rootScope', '$http', '$cookieStore', '$window', AuthService]);

app.filter('asdate', function() {
    return function(input) {
	return new Date(input);
    }
});


// Controllers
//
app.controller('CardEditController', CardEditController);
app.controller('DownloadController', DownloadController);
app.controller('MainController', MainController);

// 
// Route configuration
// 
app.config(function($routeProvider) {
    $routeProvider.when('/',   
			{templateUrl: 'pages/home.html', reloadOnSearch: false, controller: 'MainController'});
    $routeProvider.when('/login',
			{templateUrl: 'pages/login.html', reloadOnSearch: false});
    $routeProvider.when('/profile',
			{templateUrl: 'pages/profile.html', reloadOnSearch: false});
    $routeProvider.when('/createuser',
			{templateUrl: 'pages/createuser.html', reloadOnSearch: false});
    $routeProvider.when('/download',
			{templateUrl: 'pages/download.html', reloadOnSearch: false, controller: 'DownloadController'});
    $routeProvider.when('/decks',
			{templateUrl: 'pages/decks.html', reloadOnSearch: false, controller: 'CardEditController'});
    $routeProvider.when('/white-card-edit',
			{templateUrl: 'pages/white-card-edit.html', reloadOnSearch: false, controller: 'CardEditController'}); 
    $routeProvider.when('/black-card-edit',
			{templateUrl: 'pages/black-card-edit.html', reloadOnSearch: false, controller: 'CardEditController'}); 
    $routeProvider.otherwise({ redirectTo: '/' });
});

contenteditable.add(app);
//app.directive(contenteditable.name, contenteditable.directive);

//
// `$drag` example: drag to dismiss
//
app.directive('dragToDismiss', function($drag, $parse, $timeout){
    return {
	restrict: 'A',
	compile: function(elem, attrs) {
	    var dismissFn = $parse(attrs.dragToDismiss);
	    return function(scope, elem, attrs){
		var dismiss = false;

		$drag.bind(elem, {
		    constraint: {
			minX: 0, 
			minY: 0, 
			maxY: 0 
		    },
		    move: function(c) {
			if( c.left >= c.width / 4) {
			    dismiss = true;
			    elem.addClass('dismiss');
			} else {
			    dismiss = false;
			    elem.removeClass('dismiss');
			}
		    },
		    cancel: function(){
			elem.removeClass('dismiss');
		    },
		    end: function(c, undo, reset) {
			if (dismiss) {
			    elem.addClass('dismitted');
			    $timeout(function() { 
				scope.$apply(function() {
				    dismissFn(scope);  
				});
			    }, 400);
			} else {
			    reset();
			}
		    }
		});
	    };
	}
    };
});

//
// Another `$drag` usage example: this is how you could create 
// a touch enabled "deck of cards" carousel. See `carousel.html` for markup.
//
app.directive('carousel', function(){
    return {
	restrict: 'C',
	scope: {},
	controller: function($scope) {
	    this.itemCount = 0;
	    this.activeItem = null;

	    this.addItem = function(){
		var newId = this.itemCount++;
		this.activeItem = this.itemCount == 1 ? newId : this.activeItem;
		return newId;
	    };

	    this.next = function(){
		this.activeItem = this.activeItem || 0;
		this.activeItem = this.activeItem == this.itemCount - 1 ? 0 : this.activeItem + 1;
	    };

	    this.prev = function(){
		this.activeItem = this.activeItem || 0;
		this.activeItem = this.activeItem === 0 ? this.itemCount - 1 : this.activeItem - 1;
	    };
	}
    };
});

app.directive('carouselItem', function($drag) {
    return {
	restrict: 'C',
	require: '^carousel',
	scope: {},
	transclude: true,
	template: '<div class="item"><div ng-transclude></div></div>',
	link: function(scope, elem, attrs, carousel) {
	    scope.carousel = carousel;
	    var id = carousel.addItem();
      
	    var zIndex = function(){
		var res = 0;
		if (id == carousel.activeItem){
		    res = 2000;
		} else if (carousel.activeItem < id) {
		    res = 2000 - (id - carousel.activeItem);
		} else {
		    res = 2000 - (carousel.itemCount - 1 - carousel.activeItem + id);
		}
		return res;
	    };

	    scope.$watch(function(){
		return carousel.activeItem;
	    }, function(n, o){
		elem[0].style['z-index']=zIndex();
	    });
      

	    $drag.bind(elem, {
		constraint: { minY: 0, maxY: 0 },
		adaptTransform: function(t, dx, dy, x, y, x0, y0) {
		    var maxAngle = 15;
		    var velocity = 0.02;
		    var r = t.getRotation();
		    var newRot = r + Math.round(dx * velocity);
		    newRot = Math.min(newRot, maxAngle);
		    newRot = Math.max(newRot, -maxAngle);
		    t.rotate(-r);
		    t.rotate(newRot);
		},
		move: function(c){
		    if(c.left >= c.width / 4 || c.left <= -(c.width / 4)) {
			elem.addClass('dismiss');  
		    } else {
			elem.removeClass('dismiss');  
		    }          
		},
		cancel: function(){
		    elem.removeClass('dismiss');
		},
		end: function(c, undo, reset) {
		    elem.removeClass('dismiss');
		    if(c.left >= c.width / 4) {
			scope.$apply(function() {
			    carousel.next();
			});
		    } else if (c.left <= -(c.width / 4)) {
			scope.$apply(function() {
			    carousel.next();
			});
		    }
		    reset();
		}
	    });
	}
    };
});


//
// For this trivial demo we have just a unique MainController 
// for everything
//

/*
app.run(['$rootScope', '$location', '$cookieStore', '$http',
	 function ($rootScope, $location, $cookieStore, $http) {
             // keep user logged in after page refresh
             $rootScope.globals = $cookieStore.get('globals') || {};
             if ($rootScope.globals.currentUser) {
		  $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata; // jshint ignore:line
             }
	     
             $rootScope.$on('$locationChangeStart', function (event, next, current) {
		 // redirect to login page if not logged in
		 if ($location.path() !== '/login' && !$rootScope.globals.currentUser) {
                     $location.path('/login');
		 }
             });
	 }]);*/
