app.controller("orderController",function ($scope,addressService,orderService,cartService) {

    $scope.selectedAddress=null;//记录了选中的地址
    $scope.findAddressList=function () {
        addressService.findAddressList().success(function (response) {
            $scope.addressList=response;
            for (var i = 0; i < response.length; i++) {
                if( response[i].isDefault=='1'){ //找到了默认地址
                    // 默认地址赋值给selectedAddress
                    $scope.selectedAddress=response[i];
                    break;
                }
            }
            // 如果没有默认的地址，那就取第一个
            if($scope.selectedAddress==null&&response!=null&&response.length>0){
                $scope.selectedAddress=response[0];
            }
        })
    }

    $scope.updateSelectedAddress=function (pojo) {
        $scope.selectedAddress=pojo;
    }
    $scope.isSelectedAddress=function (pojo) {
       return $scope.selectedAddress==pojo;
    }


    $scope.findCartList=function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList=response;

            $scope.totalNum=0;
            $scope.totalMoney=0.00;
            for (var i = 0; i < response.length; i++) {
                var orderItemList = response[i].orderItemList;
                for (var j = 0; j < orderItemList.length; j++) {
                    var orderItem = orderItemList[j];
                    $scope.totalNum+=orderItem.num;
                    $scope.totalMoney+=orderItem.totalFee;
                }
            }

        })
    }

    $scope.entity={sourceType:'2',paymentType:'1'};
    $scope.saveOrder=function () {
        // `payment_type` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '支付类型，1、在线支付 微信，2、货到付款',
        //     `receiver_area_name` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人地区名称(省，市，县)街道',
        //     `receiver_mobile` varchar(12) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人手机',
        //     `receiver` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人',
        //     `source_type` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端',
        $scope.entity['receiverAreaName']=$scope.selectedAddress.address;
        $scope.entity['receiverMobile']=$scope.selectedAddress.mobile;
        $scope.entity['receiver']=$scope.selectedAddress.contact;
            orderService.add($scope.entity).success(function(response){
            if(response.success){
                location.href="http://pay.pinyougou.com/pay.html";
            }else{
                alert(response.message);
            }
        })
    }
    

})