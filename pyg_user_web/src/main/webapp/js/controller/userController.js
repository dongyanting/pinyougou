app.controller("userController",function ($scope,userService) {
  
    $scope.entity = {sourceType:'1'}


    $scope.sendSms = function () {
        userService.sendSms($scope.entity.phone).success(function (response) {
            if (response.success) {
                alert("倒计时效果");
            } else {
                alert(response.message);
            }
        })
    }
    
    $scope.register = function () {
        userService.register($scope.entity,$scope.code).success(function (response) {
            if (response.success) {
                alert("跳转到登陆页面");
            } else {
                alert(response.message);
            }
        })
    }
})