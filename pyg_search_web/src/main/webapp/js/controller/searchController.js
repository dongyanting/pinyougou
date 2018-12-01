app.controller("searchController",function ($scope,searchService) {
  
    $scope.paramMap = {keyword:'小米'};

    $scope.initSearch = function () {
        searchService.searchByParam($scope.paramMap).success(function (response) {
            $scope.resultMap = response;
        })
    }
})