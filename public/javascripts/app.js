$(document).ready(function() {
    var citycenter = new google.maps.LatLng(45.033333, 38.966667);
    var defaultZoom = 12;
    var carZoom = 15;
    var map = new google.maps.Map(document.getElementById('map-canvas'),
        {
            zoom: defaultZoom,
            center: citycenter,
            mapTypeControl: false,
            streetViewControl: false,
            mapTypeId: google.maps.MapTypeId.TERRAIN
        });
});
