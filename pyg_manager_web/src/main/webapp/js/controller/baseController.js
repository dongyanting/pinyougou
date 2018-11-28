app.controller("baseController",function ($scope) {

    // 分页组件对象
    $scope.paginationConf={
        currentPage: 1, 			 //当前页码
        totalItems: 10, 			 //总记录数 总条数 从后台查询 赋值
        itemsPerPage: 10,          //每页显示条数
        perPageOptions: [10, 20, 30, 40, 50],   //每页显示条数的选择
        onChange: function(){     // onChange事件
            $scope.reloadList();//重新加载
        }
    }

    $scope.searchEntity={};
    $scope.reloadList=function () {
        // 分页查询  向后台传人的参数：
        // 当前页码 $scope.paginationConf.currentPage  每页显示的条数 $scope.paginationConf.itemsPerPage
        // $scope.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    }

    // 准备一个数组接收即将删除的对象的id
    $scope.selectIds=[];

    $scope.updateSelection=function (event,id) {
        // 从event中获取当前对象  event.target  是一个CheckBox
        if(event.target.checked){//勾选
            // 向数组中添加数据的方法是：push
            $scope.selectIds.push(id);
        }else{
            // 取消勾选
            // 从数组中移除数据方法是：splice(需要移除数据在数组中的索引值,需要移除的数量)
            // "asdfgh" 获取g在字符串的索引  var index = "asdfgh".indexOf("x");
            var index=$scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index,1);
        }
    }
    
    $scope.ifChecked=function (id) {
        // 判断id是否在数组中，如果在返回true 如果不存在就返回false
        var index=$scope.selectIds.indexOf(id);//如果index值是-1时  表示不存在
        if(index==-1){
            return false;
        }
        return true;
    }

    // 全选当前页的所有数据
    $scope.checkAll=function (event) {
        // $scope.selectIds=[];
        if(event.target.checked){
            for (var i = 0; i < $scope.list.length; i++) {
               if(! $scope.ifChecked( $scope.list[i].id)) { //如果返回false代表没有勾选
                 $scope.selectIds.push( $scope.list[i].id);
               }
            }
        }else{  //取消勾选当前页的数据  就是把当前页数据的每个id从数组中移除
            // $scope.selectIds=[];
            for (var i = 0; i < $scope.list.length; i++) {
              var index =  $scope.selectIds.indexOf( $scope.list[i].id);
              $scope.selectIds.splice(index,1 );
            }
        }
    }
    
    $scope.ifCheckedAll=function () { //判断当前页的list数据的每个对象中的id是否都存在数组中。但凡有一个不存在 此方法直接返回false
        for (var i = 0; i < $scope.list.length; i++) {
            var index =  $scope.selectIds.indexOf( $scope.list[i].id);
           if(index==-1){
               return false;
           }
        }
        return true;
    }
    
})