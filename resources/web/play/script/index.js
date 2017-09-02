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
app.factory('chat', ['$rootScope', '$http', ChatService]);
app.factory('auth', ['$rootScope', '$http', '$cookieStore', '$window', AuthService]);


// Controllers
//
app.controller('MainController', MainController);
app.controller('ListGamesController', ListGamesController);
app.controller('PlayGameController', PlayGameController);

app.filter('reverse', function() {
  return function(items) {
    return items.slice().reverse();
  };
});

app.filter('asdate', function() {
    return function(input) {
	return new Date(input);
    }
});


// 
// Route configuration
// 
app.config(function($routeProvider) {
    $routeProvider.when('/',   
			{templateUrl: 'pages/home.html', reloadOnSearch: false, controller: 'ListGamesController'});
    $routeProvider.when('/login',
			{templateUrl: 'pages/login.html', reloadOnSearch: false});
    $routeProvider.when('/play/:gameId/',
			{templateUrl: 'pages/play.html', reloadOnSearch: false, controller: 'PlayGameController'});
    $routeProvider.when('/newgame',
			{templateUrl: 'pages/games.html', reloadOnSearch: false, controller: 'ListGamesController'});
    $routeProvider.otherwise({ redirectTo: '/login' });
});

contenteditable.add(app);
