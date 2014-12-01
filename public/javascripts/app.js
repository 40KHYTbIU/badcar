var badCarApp = angular.module('BadCarApp', []);

badCarApp.factory('BadCars', function () {
    return {list: [
        {
            "id": 50372,
            "date": "01.12.2014 17:20",
            "fromplace": "ул. Ставропольская, 137",
            "evacuator": {
                "id": 26,
                "number": "Н 547 МО 123"
            },
            "mark": {
                "id": 72,
                "title": "Toyota",
                "sort": 890
            },
            "number": "С 151 МО 123",
            "organization": {
                "id": 6,
                "title": "ООО \"АвтоДрайв\""
            },
            "parking": {
                "id": 6,
                "title": "ул. Вишняковой, 1/15"
            },
            "active": true,
            "toindex": true,
            "sort": 10
        },
        {
            "id": 50371,
            "date": "01.12.2014 17:20",
            "fromplace": "ул. Коммунаров, 45",
            "evacuator": {
                "id": 5,
                "number": "О 053 ВС 123"
            },
            "mark": {
                "id": 1,
                "title": "Ваз",
                "sort": 60
            },
            "number": "Р 342 НЕ 93",
            "organization": {
                "id": 1,
                "title": "МУП \"КГТ\""
            },
            "parking": {
                "id": 13,
                "title": "ул. Суворова, 3"
            },
            "active": true,
            "toindex": true,
            "sort": 20
        },
        {
            "id": 50370,
            "date": "01.12.2014 17:15",
            "fromplace": "ул. Фурманова, 152",
            "evacuator": {
                "id": 25,
                "number": "Н 546 МО 123"
            },
            "mark": {
                "id": 1,
                "title": "Ваз",
                "sort": 60
            },
            "number": "В 516 ХС 93",
            "organization": {
                "id": 6,
                "title": "ООО \"АвтоДрайв\""
            },
            "parking": {
                "id": 6,
                "title": "ул. Вишняковой, 1/15"
            },
            "active": true,
            "toindex": true,
            "sort": 30
        },
        {
            "id": 50369,
            "date": "01.12.2014 17:09",
            "fromplace": "ул. Орджоникидзе, 59",
            "evacuator": {
                "id": 11,
                "number": "О 641 ВС 123"
            },
            "mark": {
                "id": 52,
                "title": "Nissan",
                "sort": 670
            },
            "number": "У 101 ВН 123",
            "organization": {
                "id": 1,
                "title": "МУП \"КГТ\""
            },
            "parking": {
                "id": 13,
                "title": "ул. Суворова, 3"
            },
            "active": true,
            "toindex": true,
            "sort": 40
        },
        {
            "id": 50368,
            "date": "01.12.2014 16:57",
            "fromplace": "ул.Рашпилевская, 43",
            "evacuator": {
                "id": 19,
                "number": "Т 847 НА 123"
            },
            "mark": {
                "id": 40,
                "title": "Kia",
                "sort": 520
            },
            "number": "О 304 КВ 93",
            "organization": {
                "id": 4,
                "title": "ООО \"СВП\""
            },
            "parking": {
                "id": 4,
                "title": "ул. Тургенева, 1/5"
            },
            "active": true,
            "toindex": true,
            "sort": 50
        },
        {
            "id": 50367,
            "date": "01.12.2014 16:57",
            "fromplace": "ул. Рашпилевская, 32",
            "evacuator": {
                "id": 17,
                "number": "Т 845 НА 123"
            },
            "mark": {
                "id": 60,
                "title": "Range Rover",
                "sort": 760
            },
            "number": "Е 434 МК 123",
            "organization": {
                "id": 4,
                "title": "ООО \"СВП\""
            },
            "parking": {
                "id": 4,
                "title": "ул. Тургенева, 1/5"
            },
            "active": true,
            "toindex": true,
            "sort": 60
        },
        {
            "id": 50366,
            "date": "01.12.2014 16:47",
            "fromplace": "ул. Рашпилевская, 42",
            "evacuator": {
                "id": 15,
                "number": "А 195 КК 123"
            },
            "mark": {
                "id": 18,
                "title": "Chevrolet",
                "sort": 260
            },
            "number": "У 484 РУ 93",
            "organization": {
                "id": 4,
                "title": "ООО \"СВП\""
            },
            "parking": {
                "id": 4,
                "title": "ул. Тургенева, 1/5"
            },
            "active": true,
            "toindex": true,
            "sort": 70
        },
        {
            "id": 50365,
            "date": "01.12.2014 16:45",
            "fromplace": "ул. Орджоникидзе, 65",
            "evacuator": {
                "id": 5,
                "number": "О 053 ВС 123"
            },
            "mark": {
                "id": 52,
                "title": "Nissan",
                "sort": 670
            },
            "number": "У 531 МС 123",
            "organization": {
                "id": 1,
                "title": "МУП \"КГТ\""
            },
            "parking": {
                "id": 13,
                "title": "ул. Суворова, 3"
            },
            "active": true,
            "toindex": true,
            "sort": 80
        },
        {
            "id": 50364,
            "date": "01.12.2014 16:43",
            "fromplace": "ул. Орджоникидзе, 59",
            "evacuator": {
                "id": 41,
                "number": "В 934 ОЕ 123"
            },
            "mark": {
                "id": 1,
                "title": "Ваз",
                "sort": 60
            },
            "number": "У 859 ЕУ 123",
            "organization": {
                "id": 1,
                "title": "МУП \"КГТ\""
            },
            "parking": {
                "id": 13,
                "title": "ул. Суворова, 3"
            },
            "active": true,
            "toindex": true,
            "sort": 90
        },
        {
            "id": 50363,
            "date": "01.12.2014 16:38",
            "fromplace": "ул. Октябрьская, 40",
            "evacuator": {
                "id": 13,
                "number": "О 644 ВС 123"
            },
            "mark": {
                "id": 1,
                "title": "Ваз",
                "sort": 60
            },
            "number": "Р 492 КХ 123",
            "organization": {
                "id": 1,
                "title": "МУП \"КГТ\""
            },
            "parking": {
                "id": 13,
                "title": "ул. Суворова, 3"
            },
            "active": true,
            "toindex": true,
            "sort": 100
        }
    ]}
});

function CarCtrl($scope, BadCars) {
    $scope.badcars = BadCars;
}