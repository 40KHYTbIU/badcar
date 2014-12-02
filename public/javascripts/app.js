var badCarApp = angular.module('BadCarApp', ['infinite-scroll']);

// Reddit constructor function to encapsulate HTTP and pagination logic
badCarApp.factory('Reddit', function ($http) {
    var Reddit = function () {
        this.maxsize = 50;
        this.pagesize = 20;
        this.items = {};
        this.busy = false;
        this.last = 0;
    };

    Reddit.prototype.nextPage = function () {
        if (this.busy) return;
        this.busy = true;
        var len = Object.keys(this.items).length;
        //First time we take maxsize
        var count = (len == 0) ? this.maxsize : this.pagesize;
        var url = "/get?count=" + count + "&skip=" + len;
        $http.get(url).success(function (data) {
            for (var i = 0; i < data.length; i++)
                if (!this.items.hasOwnProperty(data[i].id)) {
                    this.items[data[i].id] = data[i];
                    //Save last timestamp
                    if (data[i].timestamp > this.last)
                        this.last = data[i].timestamp
                }
            this.busy = false;
        }.bind(this));
    };

    Reddit.prototype.checkNew = function () {
        var url = "/get?count=" + this.maxsize + "&after=" + this.last;
        $http.get(url).success(function (data) {
            for (var i = 0; i < data.length; i++) {
                this.items[data[i].id] = data[i];
                if (data[i].timestamp > this.last)
                    this.last = data[i].timestamp
            }
        }.bind(this));
    }
    return Reddit;
});

badCarApp.filter('orderObjectBy', function () {
    return function (items, field, reverse) {
        var filtered = [];
        angular.forEach(items, function (item) {
            filtered.push(item);
        });
        filtered.sort(function (a, b) {
            return (a[field] > b[field] ? 1 : -1);
        });
        if (reverse) filtered.reverse();
        return filtered;
    };
});

function CarCtrl($scope, Reddit) {
    $scope.reddit = new Reddit();
}