$(document).ready(function() {

    ymaps.ready(function(){
        var defaultZoom = 13;
        var mapState = { center: [45.033333, 38.966667], zoom: defaultZoom, controls: ['zoomControl']};
        var mapOptions = {
            minZoom: defaultZoom - 1,
            maxZoom: 17,
            restrictMapArea: false
        };
        var myMap = new ymaps.Map ("map-canvas", mapState, mapOptions);

        ymaps.modules.require(['Heatmap'], function (Heatmap) {
            //TODO: get data from db
            var data = [[45.033, 38.966642], [45.033, 38.966123]];
            var heatmap = new Heatmap(data);
            heatmap.setMap(myMap);
            //heatmap.setData(newData);
        });
    })

});
