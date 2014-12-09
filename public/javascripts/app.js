var badCarApp = angular.module('BadCarApp', ['ngAnimate', 'ui.grid', 'ui.grid.infiniteScroll', 'ui.grid.cellNav']);

badCarApp.controller('CarCtrl', ['$scope', '$interval', 'uiGridConstants', '$http', '$log' , function ($scope, $interval, uiGridConstants, $http, $log) {
    $scope.gridOptions = {};
    $scope.itemsHash = {};
    $scope.activeCarsHash = {};
    $scope.maxsize = 100;
    $scope.pagesize = 20;
    $scope.last = 0;

    $scope.citycenter = new google.maps.LatLng(45.033333, 38.966667);
    $scope.defaultZoom = 12;
    $scope.map = new google.maps.Map(document.getElementById('map-canvas'),
        {
            zoom: $scope.defaultZoom,
            center: $scope.citycenter,
            mapTypeId: google.maps.MapTypeId.TERRAIN
        });
    $scope.cityCircle = 'undefined';

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
        { name: 'active', field: 'active', enableFiltering: false, enableSorting: false, allowCellFocus : false, visible: false},
        { name: 'mark', field: 'mark.title', enableSorting: false, allowCellFocus : false,
            filter: { condition: uiGridConstants.filter.CONTAINS }
        },
        { name: 'number', field: 'number', enableSorting: false, allowCellFocus : false,
            filter: { condition: uiGridConstants.filter.CONTAINS }
        },
        { name: 'fromplace', field: 'fromplace', enableSorting: false,
            filter: { condition: uiGridConstants.filter.CONTAINS }
        },
        { name: 'location', field: 'location', enableFiltering: false, enableSorting: false, allowCellFocus : false, visible: false},
        { name: 'date', field: 'date', enableSorting: false, allowCellFocus : false,
            filter: { condition: uiGridConstants.filter.CONTAINS }
        },
        { name: 'timestamp', field: 'timestamp', allowCellFocus : false,
            sort: { direction: uiGridConstants.DESC, priority: 1 },
            visible: false
        }
    ];

    var update = $interval(function () {
        getUpdate();
        getActiveCars();
    }, 60000);

    function manageDate(data) {
        for (var i = 0; i < data.length; i++)
            if (!$scope.itemsHash.hasOwnProperty(data[i].id)) {
                $scope.itemsHash[data[i].id] = 1;
                $scope.gridOptions.data.push(data[i]);
                //Save last timestamp
                if (data[i].timestamp > $scope.last)
                    $scope.last = data[i].timestamp
            }
    }

    function getUpdate() {
        $http.get("/get?count=1000&after=" + $scope.last)
            .success(function (data) {
                for (var i = data.length - 1; i >= 0; i--)
                    if (!$scope.itemsHash.hasOwnProperty(data[i].id)) {
                        $scope.itemsHash[data[i].id] = 1;
                        $scope.gridOptions.data.unshift(data[i]);
                        //Save last timestamp
                        if (data[i].timestamp > $scope.last)
                            $scope.last = data[i].timestamp
                    }
            });
    }

    function getActiveCars() {
        $http.get("/getActive")
            .success(function (data) {
                for (var i = data.length - 1; i >= 0; i--)
                    if (!$scope.activeCarsHash.hasOwnProperty(data[i].id)) {
                        $scope.activeCarsHash[data[i].id] = 1;
                        //TODO:create points on map
                    }
            });
    }

    //First load
    getActiveCars();

    $http.get("/get?count=" + $scope.maxsize + "&skip=0")
        .success(function (data) {
            manageDate(data);
        });

    $scope.gridOptions.onRegisterApi = function (gridApi) {
        gridApi.cellNav.on.navigate($scope, function (newRowCol, oldRowCol) {
            //Delete previous point
            if ($scope.cityCircle != 'undefined')
                $scope.cityCircle.setMap(null);

            var location = newRowCol.row.entity.location;
            //Bad geo
            if (location == null || location.lat == 0) {
                $scope.map.setCenter($scope.citycenter);
                $scope.map.setZoom($scope.defaultZoom);
                alert("Sorry, we don't know where it is.");
            }
            else {
                var point = new google.maps.LatLng(location.lat, location.lng);
                var populationOptions = {
                    strokeColor: '#FF0000',
                    strokeOpacity: 0.8,
                    strokeWeight: 2,
                    fillColor: '#FF0000',
                    fillOpacity: 0.35,
                    map: $scope.map,
                    center: point,
                    radius: 30
                };

                // Add the circle for this city to the map.
                $scope.cityCircle = new google.maps.Circle(populationOptions);
                $scope.map.setCenter(point);
                $scope.map.setZoom(15);
            }
            $log.log('navigation event' + newRowCol.row.entity.location);
        });
        $scope.gridApi = gridApi;
        gridApi.infiniteScroll.on.needLoadMoreData($scope, function () {
            var len = Object.keys($scope.gridOptions.data).length;
            var urlNext = "/get?count=" + $scope.pagesize + "&skip=" + len;
            $http.get(urlNext)
                .success(function (data) {
                    manageDate(data);
                    gridApi.infiniteScroll.dataLoaded();
                })
                .error(function () {
                    gridApi.infiniteScroll.dataLoaded();
                });
        });
    };
}]);

