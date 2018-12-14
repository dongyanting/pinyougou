app.controller("seckillController",function ($scope,$location,$interval,seckillService) {

    $scope.findSeckillGoods=function () {
        seckillService.findSeckillGoods().success(function (response) {
             $scope.list=response;
        })
    }

    $scope.findOne=function () {
       var id = $location.search()['id'];
        seckillService.findOne(id).success(function (response) {
            $scope.entity=response;
            //结束时间和当前时间相差的秒数并且向下取整Math.floor 13376.91--->13376
            var totalSeconds = Math.floor((new Date($scope.entity.endTime).getTime() - new Date().getTime())/1000);
            $interval(function () {
                $scope.timeStr="";
                var days = Math.floor(totalSeconds/60/60/24);  //13.5233---13天
                var hours =Math.floor( (totalSeconds-days*24*60*60)/60/60); //12.234545-->12
                var minuts =Math.floor( (totalSeconds-days*24*60*60-hours*60*60)/60); //20.1322-->20
                var seconds = totalSeconds-days*24*60*60-hours*60*60-minuts*60;
                if(days<10){
                    days="0"+days;
                }
                if(hours<10){
                    hours="0"+hours;
                }
                if(minuts<10){
                    minuts="0"+minuts;
                }
                if(seconds<10){
                    seconds="0"+seconds;
                }
                if(days==0){
                    $scope.timeStr+=hours+":"+minuts+":"+seconds;
                }else{
                    $scope.timeStr+=days+"天 "+hours+":"+minuts+":"+seconds;
                }
                totalSeconds--;
            },1000,totalSeconds);

            // 距离结束： 0天 12:56:78
        })

        // $interval(函数，时间间隔，[运行次数]);



        // $scope.timesStr=10;
        // $interval(function () {
        //     $scope.timesStr--;
        // },1000,10);

        // $scope.timesStr=5;
        // var times = $interval(function () {
        //     if($scope.timesStr==0){
        //         // 取消定时器
        //         $interval.cancel(times);
        //     }else{
        //         $scope.timesStr--;
        //     }
        // },1000);
    }

    $scope.saveOrder=function () {
        seckillService.saveOrder($scope.entity.id).success(function (response) {
            if(response.success){
                location.href="pay.html";
            }else{
                alert(response.message);
            }
        })
    }
})