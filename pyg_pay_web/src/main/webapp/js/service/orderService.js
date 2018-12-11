app.service("orderService",function ($http) {

    this.add=function (entity) {
        return $http.post('./order/add',entity);
    }
})