var badCarApp = angular.module('BadCarApp', []);

function CarCtrl($scope, $http) {

    $http.get('/get?count=30').
        success(function (data, status, headers, config) {
            $scope.badcars = data;
        }).
        error(function (data, status, headers, config) {
        });

    $scope.getmore = function () {
        $http.get('/get?count=30&skip=' + $scope.badcars.length).
            success(function (data, status, headers, config) {
                if (data.length > 0) {
                    $scope.badcars = $scope.badcars.concat(data);
                    $scope.$apply();
                }
            }).
            error(function (data, status, headers, config) {
            });
    }

    $scope.getnew = function () {
        if ($scope.badcars.length < 1) return 0;
        $http.get('/get?count=1000&after=' + $scope.badcars[0].timestamp).
            success(function (data, status, headers, config) {
                if (data.length > 0) {
                    $scope.badcars = data.concat($scope.badcars);
                    $scope.$apply();
                }
            }).
            error(function (data, status, headers, config) {
            });
    }

}