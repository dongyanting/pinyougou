app.controller("sellerRegisterController",function($scope,sellerService){
	
	$scope.save=function(){
		sellerService.add($scope.entity).success(function(response){
			if(response.success){
				alert("24小时之内会完成审核！！！");
			}else{
				alert(response.message);
			}
		})
	}
	
	
})