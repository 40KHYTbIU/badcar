/**
 * Created by Mike on 04/03/15.
 */
var badCarApp = angular.module('BadCarApp', ['ngAnimate', 'ui.grid', 'ui.grid.infiniteScroll', 'ui.grid.cellNav']);

badCarApp.controller('CarCtrl', ['$scope', '$interval', 'uiGridConstants', '$http', '$log' , function ($scope, $interval, uiGridConstants, $http, $log) {
    $scope.gridOptions = {};
    $scope.itemsHash = {};
    $scope.activeCarsHash = {};
    $scope.currentMarker = undefined;
    $scope.maxsize = 100;
    $scope.pagesize = 20;
    $scope.last = 0;

    $scope.hideGrid = false;

    $scope.citycenter = new google.maps.LatLng(45.033333, 38.966667);
    $scope.defaultZoom = 12;
    $scope.carZoom = 15;
    $scope.map = new google.maps.Map(document.getElementById('map-canvas'),
        {
            zoom: $scope.defaultZoom,
            center: $scope.citycenter,
            mapTypeControl: false,
            streetViewControl: false,
            mapTypeId: google.maps.MapTypeId.TERRAIN
        });

    $scope.gridOptions.infiniteScrollPercentage = 20;
    $scope.gridOptions.data = [];
    $scope.gridOptions.enableFiltering = true;
    $scope.gridOptions.enableColumnMenu = false;
    $scope.gridOptions.enableRowSelection = true;
    $scope.gridOptions.multiSelect = true;

    $scope.gridOptions.columnDefs = [
        { name: 'number', field: 'number', enableSorting: false, enableColumnMenu: false,
            filter: { condition: uiGridConstants.filter.CONTAINS },
            cellClass: function (grid, row, col, rowRenderIndex, colRenderIndex) {
                if (row.entity.active)
                    return 'active';
                else
                    return 'deactive';
            }
        },
        { name: 'date', field: 'date', enableSorting: false, allowCellFocus : false, enableFiltering: false, enableColumnMenu: false,
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

    function makeInfoContent(carEntity) {
        return '<div class="infoWindow">'+
            '<span>'+carEntity.mark.title+'</span></br>' +
            '<span>'+carEntity.number+'</span></br>' +
            '<span>'+carEntity.fromplace+'</span></br>' +
            '</div>'
    }

    function markerClick() {
        if (this.getAnimation() != null) {
            this.setAnimation(null);
        } else {
            this.infowindow.open($scope.map, this);
        }
    }


    function getActiveCars() {
        $http.get("/getActive")
            .success(function (data) {
                var activeCarsList = [];
                for (var i = data.length - 1; i >= 0; i--) {
                    activeCarsList.push(data[i].id);
                    if (data[i].hasOwnProperty("location") && data[i].location != null && !$scope.activeCarsHash.hasOwnProperty(data[i].id)) {
                        var point = new google.maps.LatLng(data[i].location.lat, data[i].location.lng);
                        var marker = new google.maps.Marker({
                            position: point,
                            map: $scope.map,
                            animation: google.maps.Animation.DROP
                        });
                        marker.infowindow = new google.maps.InfoWindow({
                            content: makeInfoContent(data[i])
                        });
                        google.maps.event.addListener(marker, 'click', markerClick);
                        $scope.activeCarsHash[data[i].id] = marker;
                    }
                }
                //Delete deactivated markers
                for (key in $scope.activeCarsHash) {
                    var keyInt = parseInt(key);
                    if ($scope.activeCarsHash.hasOwnProperty(keyInt) && activeCarsList.indexOf(keyInt) == -1)
                        delete $scope.activeCarsHash[keyInt];
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
            var entity = newRowCol.row.entity;
            //Delete previous point
            if ($scope.currentMarker != undefined) {
                if (oldRowCol != null && !$scope.activeCarsHash.hasOwnProperty(oldRowCol.row.entity.id))
                    $scope.currentMarker.setMap(null);
                else
                    $scope.currentMarker.setAnimation(null);
            }
            var location = entity.location;
            //Bad geo
            if (location == null || location.lat == 0) {
                $scope.map.setCenter($scope.citycenter);
                $scope.map.setZoom($scope.defaultZoom);
                alert("Sorry, we don't know where it is.");
            }
            else {
                var point = new google.maps.LatLng(location.lat, location.lng);
                if (oldRowCol == null || !$scope.activeCarsHash.hasOwnProperty(oldRowCol.row.entity.id)) {

                    var marker = new google.maps.Marker({
                        position: point,
                        animation: google.maps.Animation.BOUNCE,
                        map: $scope.map
                    });
                    //TODO: template infowindow
                    marker.infowindow = new google.maps.InfoWindow({
                        content: makeInfoContent(entity)
                    });
                    google.maps.event.addListener(marker, 'click', markerClick);
                    $scope.currentMarker = marker;
                } else {
                    $scope.currentMarker = $scope.activeCarsHash[entity.id];
                    $scope.currentMarker.setAnimation(google.maps.Animation.BOUNCE);
                }

                $scope.map.setCenter(point);
                $scope.map.setZoom($scope.carZoom);
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
