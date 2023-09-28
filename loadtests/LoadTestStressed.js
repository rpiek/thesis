import http from 'k6/http';
import { check } from 'k6';
import exec from "k6/execution";
import { SharedArray } from "k6/data";

const tripsJson = new SharedArray('trips', function () {
    return JSON.parse(open('trips.json'));
});

const usersJson = new SharedArray('users', function () {
    return JSON.parse(open('users.json'));
});

const foodsJson = new SharedArray('foods', function () {
    return JSON.parse(open('foods.json'));
});

const contactsJson = new SharedArray('contacts', function () {
    return JSON.parse(open('contacts.json'));
});

const dates = new SharedArray('dates', function () {
    return JSON.parse(open('dates_2025.json'));
});

export const options = {
    scenarios: {
        warmup_find_ticket_order_ticket: {
            executor: 'ramping-arrival-rate',
            startRate: 1,
            preAllocatedVUs: 100,
            stages: [
                { duration: '60s', target: '12' },
                { duration: '540s', target: '12' },
            ],
            exec: 'findAndOrderTicket'
        },
        warmup_find_ticket_plan: {
            executor: 'ramping-arrival-rate',
            startRate: 1,
            preAllocatedVUs: 100,
            stages: [
                { duration: '60s', target: '12' },
                { duration: '540s', target: '12' },
            ],
            exec: 'findTicketPlan'
        },
        find_ticket_order_ticket: {
            executor: 'constant-arrival-rate',
            rate: 12,
            timeUnit: '1s',
            duration: '5m',
            preAllocatedVUs: 100,
            startTime: '600s',
            exec: 'findAndOrderTicket'

        },
        find_ticket_plan: {
            executor: 'constant-arrival-rate',
            rate: 12,
            timeUnit: '1s',
            duration: '5m',
            preAllocatedVUs: 100,
            startTime: '600s',
            exec: 'findTicketPlan'
        },
    },
};

export function findAndOrderTicket() {
    const trip = tripsJson[exec.scenario.iterationInTest % tripsJson.length];

    const requestBody = {
        startPlace: trip.start_station_name,
        endPlace: trip.stations_name,
        departureTime: '2030-04-20 02:01:00',
    };

    const res = http.post('http://localhost:12346/api/v1/travelservice/trips/left', JSON.stringify(requestBody), {
        headers: { 'Content-Type': 'application/json' },
        scenarios: ['warmup_find_ticket', 'find_ticket'], // Specify the scenario to use for this request
    });

    check(res, { 'Status is 200': (r) => r.status === 200 });

    // Parse the response body as JSON
    const responseBody = JSON.parse(res.body);

    // Check if 'data' property exists in the response body
    check(responseBody, { 'Response has data property': (json) => json.hasOwnProperty('data') });

    // Check if 'tripId' exists in the 'data' property
    check(responseBody, { 'data has tripId property': (json) => json.data[0].hasOwnProperty('tripId') });

    const food = foodsJson[exec.scenario.iterationInTest % foodsJson.length];
    const user = usersJson[exec.scenario.iterationInTest % usersJson.length];
    const contact = contactsJson[exec.scenario.iterationInTest % contactsJson.length];

    const tripId = 'D' + (trip.number || '');
    const from = trip.start_station_name || '';
    const to = trip.stations_name || '';
    const terminalStation = trip.terminal_station_name || '';
    const foodName = food.food_name || '';
    const storeName = food.store_name || '';
    const foodPrice = food.food_price || '';
    const foodType = food.food_type || '';
    const date = dates[exec.scenario.iterationInTest % dates.length]

    const requestBody2 = {
        accountId: user.user_id,
        contactsId: contact.id,
        tripId,
        seatType: (exec.scenario.iterationInTest % 2) + 1,
        date,
        from,
        to,
        assurance: '0',
        foodType,
        stationName: terminalStation,
        storeName,
        foodName,
        foodPrice,
        handleDate: '2023-13-09',
        consigneeName: user.user_name,
        consigneePhone: contact.phone_number,
        consigneeWeight: Math.floor(Math.random() * 100) + 2,
        within: (exec.scenario.iterationInTest % 2) !== 0,
    };

    const res2 = http.post('http://localhost:14568/api/v1/preserveservice/preserve', JSON.stringify(requestBody2), {
        headers: { 'Content-Type': 'application/json' },
    });

    check(res2, { 'Status is 200': (r) => r.status === 200 });

    // Parse the response body as JSON
    const responseBody2 = JSON.parse(res2.body);

    check(responseBody2, { 'Response has msg property': (json) => json.hasOwnProperty('msg') });

    check(responseBody2, { 'Response msg is "Success."': (json) => json.msg === "Success."});
}


export function findTicketPlan() {
    const {start_station_name, stations_name}= tripsJson[exec.scenario.iterationInTest % tripsJson.length];

    const requestBody = {
        startStation: start_station_name,
        endStation: stations_name,
        travelDate: '2030-04-20 02:01:00',
        num: 1
    };

    const res = http.post('http://localhost:14578/api/v1/routeplanservice/routePlan/minStopStations', JSON.stringify(requestBody), {
        headers: { 'Content-Type': 'application/json' },
        scenarios: ['warmup_find_ticket_plan', 'find_ticket_plan'], // Specify the scenario to use for this request
    });

    check(res, { 'Status is 200': (r) => r.status === 200 });

    // Parse the response body as JSON
    const responseBody = JSON.parse(res.body);

    // Check if 'data' property exists in the response body
    check(responseBody, { 'Response has data property': (json) => json.hasOwnProperty('data') });

    // Check if 'tripId' exists in the 'data' property
    check(responseBody, { 'data has tripId property': (json) => json.data[0].hasOwnProperty('tripId') });

}

