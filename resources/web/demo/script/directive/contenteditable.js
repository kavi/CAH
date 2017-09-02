var contenteditable = {
    add: function(angular) {
	angular.directive(this.name, this.directive);
    },
    name: 'contenteditable',

    directive: function() {
	return {
	    require: 'ngModel',
	
	    compile : function(scope, element, attrs) {
		var linkfunction = function(scope, element, attrs, ctrl) {
		    var placeholder = attrs.placeholder2 || '';
		    var clear  = placeholder != '';
		    var clicked = false;
		    // view -> model
		    element.bind('blur', function() {
			var tmp = element.html().replace(/(<br ?\/?>)*/g,"");
			tmp = tmp.replace(/&gt;/g,">");
			tmp = tmp.replace(/&lt;/g,"<");
			tmp = tmp.replace(/&amp;/g,"&");
			scope.$apply(function() {
			    ctrl.$setViewValue(tmp);
			});
		    });

		    element.bind('keyup', function(event) {
//			var position = window.getSelection();
//			console.log(position.baseOffset);
//			var range = position.getRangeAt(0).cloneRange();
//			position.removeAllRanges();
//			position.addRange(range);
//  			var tmp = element.html(); //.replace(/(<br ?\/?>)*/g,"");
//	     		scope.$apply(function() {
//	    		    ctrl.$setViewValue(tmp);
//		    	});
		    });
		    
		    element.bind('click', function() {
			if (clicked) {
			    return;
			}
			if (clear) {
			    scope.$apply(function() {
				element.html('');
				ctrl.$setViewValue('');
			    });		
			}
			clear = false;
			clicked = true;
		    });
		    
		    // model -> view
		    ctrl.$render = function() {
			var tmp;
			if (clear && placeholder != '') {
			    tmp = placeholder;
			} else {
			    tmp = ctrl.$viewValue;
			    tmp = tmp.replace(/\&/g,"&amp;");
			    tmp = tmp.replace(/\</g,"&lt;");
			    tmp = tmp.replace(/\>/g,"&gt;");		
			}
			element.html(tmp);
		    };
		} // end link-function

		return linkfunction;
	    } // end compile
	}; // end return function
    } // end directive function
}
