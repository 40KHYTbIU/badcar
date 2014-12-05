var badCarApp = angular.module('BadCarApp', ['ui.grid', 'ui.grid.infiniteScroll', 'ui.grid.cellNav']);

badCarApp.controller('CarCtrl', ['$scope', '$http', '$log', function ($scope, $http, $log) {
    $scope.gridOptions = {};
    $scope.itemsHash = {}
    $scope.maxsize = 50;
    $scope.pagesize = 20;
    $scope.last=0;

    /**
     * @ngdoc property
     * @name infiniteScrollPercentage
     * @propertyOf ui.grid.class:GridOptions
     * @description This setting controls at what percentage of the scroll more data
     * is requested by the infinite scroll
     */
    $scope.gridOptions.infiniteScrollPercentage = 20;
    $scope.gridOptions.data = [];
    $scope.gridOptions.enableFiltering = true;
    $scope.gridOptions.columnDefs = [
        { name: 'mark', field: 'mark.title' },
        { name: 'number', field: 'number' },
        { name: 'fromplace', field: 'fromplace'},
        { name: 'location', field: 'location', enableFiltering: false, enableSorting: false},
        { name: 'date', field: 'timestamp'}
    ];
    var page = 1;
    var getData = function(data, page) {
        var res = [];
        for (var i = 0; i < page * 100 && i < data.length; ++i) {
            res.push(data[i]);
        }
        return res;
    };

    //First load
    $http.get("/get?count="+$scope.maxsize+"&skip=0")
        .success(function(data) {
            for (var i = 0; i < data.length; i++)
                if (!$scope.itemsHash.hasOwnProperty(data[i].id)) {
                    $scope.itemsHash[data[i].id] = 1;
                    $scope.gridOptions.data.push(data[i]);
                    //Save last timestamp
                    if (data[i].timestamp > $scope.last)
                        $scope.last = data[i].timestamp
                }
        });

    $scope.gridOptions.onRegisterApi = function(gridApi){
        gridApi.infiniteScroll.on.needLoadMoreData($scope,function(){
            var len = Object.keys($scope.gridOptions.data).length;
            var urlNext = "/get?count=" + $scope.pagesize + "&skip=" + len;
            $http.get(urlNext)
                .success(function(data) {
                    for (var i = 0; i < data.length; i++)
                        if (!$scope.itemsHash.hasOwnProperty(data[i].id)) {
                            $scope.itemsHash[data[i].id] = 1;
                            $scope.gridOptions.data.push(data[i]);
                            //Save last timestamp
                            if (data[i].timestamp > $scope.last)
                                $scope.last = data[i].timestamp
                        }
                    gridApi.infiniteScroll.dataLoaded();
                })
                .error(function() {
                    gridApi.infiniteScroll.dataLoaded();
                });
        });
    };
}]);

//var citymap = {};
//citymap['krasnodar'] = {
//    center: new google.maps.LatLng(45.033333, 38.966667),
//    population: 744995
//};
//
//var cityCircle;

function initialize() {
    // Create the map.
    var mapOptions = {
        zoom: 13,
        center: new google.maps.LatLng(45.033333, 38.966667),
        mapTypeId: google.maps.MapTypeId.TERRAIN
    };

    var map = new google.maps.Map(document.getElementById('map-canvas'),
        mapOptions);

    // Construct the circle for each value in citymap.
    // Note: We scale the area of the circle based on the population.
//    for (var city in citymap) {
//        var populationOptions = {
//            strokeColor: '#FF0000',
//            strokeOpacity: 0.8,
//            strokeWeight: 2,
//            fillColor: '#FF0000',
//            fillOpacity: 0.35,
//            map: map,
//            center: citymap[city].center,
//            radius: Math.sqrt(citymap[city].population)
//        };
//        // Add the circle for this city to the map.
//        cityCircle = new google.maps.Circle(populationOptions);
//    }
}

google.maps.event.addDomListener(window, 'load', initialize);
