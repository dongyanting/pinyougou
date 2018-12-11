app.controller("payController",function ($scope,$location,payService) {
    
    
    $scope.createNative = function () {

        $scope.flag = false;

        payService.createNative().success(function (response) {

            $scope.resultMap = response;

            new QRious({
                element:document.getElementById('payImg'),
                size:300,
                value:response.code_url,
                level:'H'
            })
            $scope.queryOrder(response.out_trade_no)
        })
    }
    $scope.queryOrder = function (out_trade_no) {
        payService.queryOrder(out_trade_no).success(function (response) {
            if (response.success) {
                location.href = "paysuccess.html#?total_fee=" + $scope.resultMap.total_fee ;
            } else {
                // 支付失败
                if (response.message == "支付失败") {
                    location.href = "payfail.html";
                }
                if (response.message == "二维码失效") {
                    $scope.flag = true;
                }
            }
        })
    }

    $scope.showMoney = function () {
        $scope.total_fee = $location.search()['total_fee'];
    }
})