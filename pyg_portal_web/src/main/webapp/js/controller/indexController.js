app.controller("indexController",function ($scope,indexService) {
  
    
    $scope.findByCategotyId = function (categoryId) {
        indexService.findByCategotyId(categoryId).success(function (response) {
            $scope.bannerList = response;
        })
    }

    $scope.search = function () {

        location.href="http://search.pinyougou.com/search.html#?keyword=" + $scope.keyword;
    }
})