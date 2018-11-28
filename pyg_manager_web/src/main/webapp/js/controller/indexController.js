app.controller("indexController",function($scope,indexService){
	$scope.showName=function(){
		// indexService.showName().success(function(response){
		// 	$scope.username =JSON.parse(response) ;
		// })
		indexService.showNameMap().success(function(response){
			// {"username":'admin'}
			$scope.username =response.username ;
		})
	}
})