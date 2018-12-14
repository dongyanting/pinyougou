app.service("seckillService",function ($http) {

    this.findSeckillGoods=function () {
        return $http.get("./seckill/findSeckillGoods");
    }
    this.findOne=function (id) {
        return $http.get("./seckill/findOne?id="+id);
    }
    this.saveOrder=function (id) {
        return $http.get("./seckill/saveOrder?id="+id);
    }

})