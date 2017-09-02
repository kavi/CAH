function MainController($rootScope, $scope, $location, cah, auth) {
    $scope.menu = "Home";

    $rootScope.status = { error: false, message: '' };

    $scope.openModal = function(name) {
	$rootScope.Ui.turnOn(name);
    };
    
    $scope.closeModal = function(name) {
	$rootScope.Ui.turnOff(name);
    };
    
    // User agent displayed in home page
    $scope.userAgent = navigator.userAgent;
    
    $scope.resetEditUser = function(user) {
	$scope.editUser = { 
	    name : '',
	    email : '',
	    password: '',
	    password2: '',
	    editPassword: true,
	    familyFilter: false
	}; 
	if (user && $rootScope.globals.authenticated) {
	    $scope.editUser.name = user.name;
	    $scope.editUser.email =  user.email;
	    $scope.editUser.editPassword = false;
	    $scope.editUser.familyFilter = user.familyFilter;
	    $scope.editUser.id = user.id;
	} 
    }

    
    $scope.user = auth.checkLogin();
    $scope.resetEditUser($scope.user);
  
    $scope.validateUser = function(user) {
	if (!user || !user.name || user.name.trim() == '') {
	    return false;
	}
	if (!$scope.validateUsername(user)) {
	    return false;
	}
	if (!user.password || user.password.length < 4) {
	    return false;
	}
	if (user.password != user.password2) {
	    return false;
	}
	return true;
    };

    $scope.validateUsername = function(user) {
	return /^([a-z0-9]{3,64})$/.test(user.name);
    }

    $scope.getUsernameInputClass = function(user) {
	if (!user || !user.name) {
	    return 'form-control';
	}
	if ($scope.validateUsername(user)) {
	    return 'form-control input-good';
	} else {
	    return 'form-control input-error';
	}
    }

    $scope.validatePassword = function(user) {
	return user.password.length > 3;
    }

    $scope.getPasswordInputClass = function(user) {
	if (!user || !user.password) {
	    return 'form-control';
	}
	if ($scope.validatePassword(user)) {
	    return 'form-control input-good';
	} else {
	    return 'form-control input-error';
	}
    }

    $scope.validatePassword2 = function(user) {
	return user.password == user.password2;
    }

    $scope.getPassword2InputClass = function(user) {
	if (!user || !user.password2) {
	    return 'form-control';
	}
	if ($scope.validatePassword2(user)) {
	    return 'form-control input-good';
	} else {
	    return 'form-control input-error';
	}
    }

  
    $rootScope.$on('$locationChangeStart', function (event, next, current) {
	// redirect to login page if not logged in
	if (!$rootScope.globals.authenticated && 
	    !($location.path() == '/' 
		|| $location.path() == '/login'
		|| $location.path() == '/createuser'
		|| $location.path() == '/download')) {	    
	    $rootScope.$evalAsync(function() {
		$location.path('/');
	    });
	}
    });
	
    $rootScope.$on('$routeChangeStart', function(){
	$rootScope.loading = true;
    });
	
    $rootScope.$on('$routeChangeSuccess', function(){
	$rootScope.loading = false;
    });
    
    $scope.login = function() {
	auth.login($scope.user, $scope.setUser)
	if ($rootScope.globals.authenticated) {
	    $location.path('/');
	} else {
	    // Authentication failed..
	}
    };
    
    $rootScope.gotoPage = function(path) {
	$location.path(path);
    };
    
    $scope.setUser = function(user) {
	$scope.user = user;
	$scope.resetEditUser(user);
    }
    
    $scope.updateUser = function(edituser) {
	if (!edituser.editPassword) {
	    edituser.password = $scope.user.password;
	}
	auth.updateUser(edituser, $scope.setUser);
    }

    $scope.createUser = function(user) {
	auth.createUser(user, $scope.setUser);
    }
    
    $scope.logout = function() {
	auth.logout();
	$scope.newuser = { name: '', email: '', familyFilter: false, password: '', password2: '' };
	$scope.user = {};
	$rootScope.globals = { authenticated: false};
	$location.path('/');
    };
}
