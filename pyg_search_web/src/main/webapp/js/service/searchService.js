app.service("searchService",function ($http) {


    this.searchByParam = function (paramMap) {
        return $http.post("./search/searchByParam",paramMap );
    }

})