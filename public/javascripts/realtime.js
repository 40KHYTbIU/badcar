function makeInfoContent(carEntity) {
    return '<div class="infoWindow">'+
        '<span>'+carEntity.mark.title+'</span></br>' +
        '<span>'+carEntity.number+'</span></br>' +
        '<span>'+carEntity.fromplace+'</span></br>' +
        '<span>'+carEntity.date+'</span></br>' +
        '</div>'
}

function getActiveCars(map) {
    var markerPreset = { preset: 'islands#icon', iconColor: '#a5260a' };
    
    $.ajax({
        url: "/getActive"
    }).done(function (data) {
        map.geoObjects.removeAll(); //Clean all
        for (var i = data.length - 1; i >= 0; i--)
            if (data[i].hasOwnProperty("location") && data[i].location != null) {                
                var marker = new ymaps.Placemark([data[i].location.lat, data[i].location.lng], 
                    { balloonContent: makeInfoContent(data[i]) }, markerPreset);
                map.geoObjects.add(marker);                
            }
    });

}

$(document).ready(function () {

    ymaps.ready(function () {
        var defaultZoom = 13;
        var mapState = {center: [45.033333, 38.966667], zoom: defaultZoom, controls: ['zoomControl']};
        var mapOptions = {
            minZoom: defaultZoom - 1,
            maxZoom: 17,
            restrictMapArea: false
        };
        var myMap = new ymaps.Map("map-canvas", mapState, mapOptions);

        window.setTimeout(getActiveCars(myMap), 1000);
        var intervalID = window.setInterval(function(){getActiveCars(myMap);}, 60000);
    })

});
