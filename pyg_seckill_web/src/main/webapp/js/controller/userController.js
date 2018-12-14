app.controller("userController",function ($scope,userService) {


    $scope.entity={sourceType:'1'};

    $scope.sendSms=function () {
        userService.sendSms($scope.entity.phone).success(function (response) {
            if(response.success){
                alert("倒计时效果");
            }else{
                alert(response.message);
            }
        })
    }
    
    $scope.register=function () {
        // 判断两个密码是否一致
        userService.register($scope.entity,$scope.code).success(function (response) {
            if(response.success){
                // alert("跳转到登录页面--使用单点登录");
                location.href="home-index.html";
            }else{
                alert(response.message);
            }
        })
    }
})