app.service("userService",function ($http) {


    this.sendSms = function (phone) {
        return $http.get("./user/sendSms?phone=" + phone);
    }
    
    this.register =function (user,code) {
        return $http.post("./user/register?code=" + code,user);
    }

})