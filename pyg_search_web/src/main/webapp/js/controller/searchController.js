app.controller("searchController",function ($scope,$location,searchService) {

    $scope.paramMap={keyword:'小米',category:'',brand:'',price:'',order:'',spec:{},pageNo:1};

    //一进入页面的初始化方法：根据关键字查询
    $scope.initSearch=function () {
        // 获取url上的参数
        if ($location.search()['keyword']!=undefined) {
            $scope.paramMap.keyword=$location.search()['keyword'];
            $scope.keyword = $location.search()['keyword']
        }


        $scope.search();
    }

    // key:  category  brand
    // value:手机       三星
    $scope.addParamToParamMap = function (key,value) {
        $scope.paramMap[key] = value;
        $scope.search();
    }
    // key:  category  brand
    // value:手机       三星
    $scope.removeParamFromParamMap = function (key,value) {
        $scope.paramMap[key] = '';
        $scope.search();
    }

    // key:  网络        机身内存
    $scope.removeSpecParamFromParamMap = function (key) {
        delete $scope.paramMap.spec[key]; // 删除map中的一对
        $scope.search();
    }

    // 只是根据关键字查询
    $scope.searchByKeyword = function () {

        $scope.paramMap={keyword:'小米',category:'',brand:'',price:'',order:'',spec:{},pageNo:1};
        // 第一个keyword是属性名   第二个keyword是页面上搜索框输入的内容
        $scope.paramMap.keyword = $scope.keyword;
        $scope.search();
    }
    
    $scope.search = function () {
        searchService.searchByParam($scope.paramMap).success(function (response) {
            $scope.resultMap=response;

            $scope.pageLabel = [];
            // for (var i = 1; i<=response.totalPages; i++) {
            //     $scope.pageLabel.push(i)
            // }
            buildPageLabel();
        })
    }

    // key:  网络        机身内存
    // value:移动4G       32G
    $scope.addSpecParamToParamMap = function (key,value) {
        $scope.paramMap.spec[key] = value;
        $scope.search();
    }



    function buildPageLabel() {
        $scope.pageLabel = [];//新增分页栏属性
        var maxPageNo = $scope.resultMap.totalPages;//得到最后页码
        var firstPage = 1;//开始页码
        var lastPage = maxPageNo;//截止页码
        $scope.firstDot = true;//前面有点
        $scope.lastDot = true;//后边有点
        if ($scope.resultMap.totalPages > 5) { //如果总页数大于 5 页,显示部分页码
            if ($scope.paramMap.pageNo <= 3) {//如果当前页小于等于 3
                lastPage = 5; //前 5 页
                $scope.firstDot = false;//前面没点
            } else if ($scope.paramMap.pageNo >= lastPage - 2) {//如果当前页大于等于最大页码-2
                firstPage = maxPageNo - 4;  //后 5 页
                $scope.lastDot = false;//后边没点
            } else { //显示当前页为中心的 5 页
                firstPage = $scope.paramMap.pageNo - 2;
                lastPage = $scope.paramMap.pageNo + 2;
            }
        } else {
            $scope.firstDot = false;//前面无点
            $scope.lastDot = false;//后边无点
        }
        //循环产生页码标签
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    }




})