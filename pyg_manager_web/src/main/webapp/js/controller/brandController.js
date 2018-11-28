app.controller("brandController",function ($scope,$controller, brandService) { //brandService 把brandService直接注入到了brandController中

    $controller('baseController',{$scope:$scope});//继承   本质:共有一个$scope

    $scope.findPage=function (pageNo,pageSize) {
        brandService.findPage(pageNo,pageSize).success(function (response) {
            // response : 总条数  当前页的数据list  要求 response的格式{total:100,rows:[{},{},{}]}
            $scope.paginationConf.totalItems = response.total;
            $scope.list = response.rows;
        })
    }

    // 查询所有
    $scope.findAll=function () {
        brandService.findAll().success(function (response) {  //response代表通过方法返回的数据
            $scope.list=response;  //使用一个带有$scope的变量接收
        })
    }

    $scope.save=function () {
        // 构建一个对象 entity = {name:'',firstChar:''}
        // 根据是都有id判断 add还是update
        // 有id就是update
        var obj=null;
        if($scope.entity.id!=null){
            obj = brandService.update($scope.entity);
        }else{
            obj = brandService.add($scope.entity);
        }
        obj.success(function (response) {
            // response:要求格式：{success:true|false,message:"保存成功"|"保存失败"}
            if(response.success){ //表示成功
                // {success:true,message:"保存成功"}
                // 刷新列表数据
                $scope.reloadList();
            }else{
                // {success:false,message:"保存失败"}
                alert(response.message);
            }
        })
    }

    $scope.findOne=function (id) {
       brandService.findOne(id).success(function (response) {
            // response 根据id查询 的对象 {id:1,name:'sss',firstChar:'ddd'}
            $scope.entity=response;
        })
    }



    $scope.dele=function () {
        // 判断数组中是否有数据
        if($scope.selectIds.length==0){
            return;
        }
        var flag = window.confirm("确认删除您选择的数据吗?");
        if(flag){
          brandService.dele($scope.selectIds).success(function (response) {
                if(response.success){
                    $scope.reloadList();//刷新列数据
                    $scope.selectIds=[];//清空数组
                }else{
                    alert(response.message);
                }
            })
        }
    }
    $scope.searchEntity={};  //初始化searchEntity 如果不初始化 一打开页面时searchEntity是undefined
    $scope.search=function (pageNo,pageSize) {
        // $scope.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
        // 当前页码  每页显示的条数  查询条件的对象  $scope.searchEntity
       brandService.search(pageNo,pageSize,$scope.searchEntity).success(function (response) {
            // response : 总条数  当前页的数据list  要求 response的格式{total:100,rows:[{},{},{}]}
            $scope.paginationConf.totalItems = response.total;
            $scope.list = response.rows;
        })
    }



})