$(document).bind('mobileinit', function () {
    var map, bounds, overlay,
        m = $('#map'),
        b = $('#refreshbutton'),
        o = $('#offlinebutton'),
        c = $('#clearbutton'),
        d = $('#debug');

    map = new L.Map(m.get(0));
    bounds = new L.LatLngBounds(new L.LatLng(-70, -170), new L.LatLng(70, 170));
    overlay = new L.ImageOverlay("images/political_world_map.jpg", bounds);
    map.addLayer(overlay);

    b.click(function () {
        navigator.geolocation.getCurrentPosition(function (obj) {
            var pos = obj.coords, json, loc;
            d.append($('<div>').text(pos.latitude + '<->' + pos.longitude));

            if (window.localStorage) {
                json = JSON.stringify({
                    latitude: pos.latitude,
                    longitude: pos.longitude
                });
                window.localStorage.setItem('currentpos', json);
            }

            loc = new L.LatLng(pos.latitude, pos.longitude);
            map.addLayer(new L.Marker(loc));
            map.setView(loc, 4);
        }, function (e) {
            var msg = [e.code, e.message].join('-');
            if (navigator.notification) {
                navigator.notification.alert(msg);
            } else {
                alert(msg);
            }
        }, {
            enableHighAccuracy: true,
            maximumAge: 100,
            timeout: 5000
        });
    });

    o.click(function () {
        if (window.localStorage) {
            var json, pos;
            json = window.localStorage.getItem('currentpos');
            if (json) {
                pos = JSON.parse(json);
                d.append($('<div>').text(pos.latitude + '<->' + pos.longitude));
            } else {
                d.append($('<div>').text('position undefined'));
            }
        } else {
            navigator.notification.alert('localstorage unavailable');
        }
    });

    c.click(function () {
        if (window.localStorage) {
            window.localStorage.clear();
            d.empty();
        } else {
            navigator.notification.alert('localStorage unavailable');
        }
    });
});