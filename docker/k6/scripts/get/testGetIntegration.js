import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

// Define the options for the test, including the stages
export const options = {
    summaryTrendStats: ["avg", "min", "med", "max", "p(90)", "p(95)", "p(99)", "p(99.50)"],
    stages: [
        { duration: '10s', target: 200 },  // Ramp up
        { duration: '30s', target: 1000 }, // Stay
        { duration: '10s', target: 0 },    // Ramp down
    ],
};

let token;

function getToken() {
    const url = 'http://host.docker.internal:8080/api/v1/queues/token';
    const payload = JSON.stringify({ userId: 1 });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // Perform the HTTP POST request to fetch the token
    const res = http.post(url, payload, params);

    // Check if the status is 200
    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    // Extract the token from the JSON response
    token = JSON.parse(res.body).data.token;
}

// Setup function to run before the test execution
export function setup() {
    getToken();
    return { token };  // Return the token to be used in default function
}

export default function(data) {
    // Get the token
    const token = data.token;  // Use the token from setup()

    // Use the token in your test scenarios
    getConcerts(token);
    getConcertDetail(token);
    getConcertDates(token);
    getAvailableSeats(token);
    getBalance();
    getMyReservations(token);

    sleep(1);
}

// Function to get concerts list
function getConcerts(token) {
    const url = 'http://host.docker.internal:8080/api/v1/concerts';
    const params = {
        headers: {
            'Authorization': `Bearer ${token}`,
        },
    };

    const res = http.get(url, params);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });
}

// Function to get concert details
function getConcertDetail(token) {
    const concertId = randomIntBetween(1, 10000);

    const url = `http://host.docker.internal:8080/api/v1/concerts/${concertId}`;
    const params = {
        headers: {
            'Authorization': `Bearer ${token}`,
        },
    };

    const res = http.get(url, params);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });
}

function getConcertDates(token) {
    // concertId를 1부터 10000 사이에서 랜덤으로 생성
    let concertId = randomIntBetween(1, 10000);

    const url = `http://host.docker.internal:8080/api/v1/concerts/${concertId}/dates`;
    const params = {
        headers: {
            'Authorization': `Bearer ${token}`,
        },
    };

    const res = http.get(url, params);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

}

function getAvailableSeats(token) {
    // concertId를 1부터 10000 사이에서 랜덤으로 생성
    let concertDateId = randomIntBetween(1, 10000);

    const url = `http://host.docker.internal:8080/api/v1/concerts/dates/${concertDateId}/seats`;
    const params = {
        headers: {
            'Authorization': `Bearer ${token}`,
        },
    };

    const res = http.get(url, params);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

}

function getBalance() {
    // userId를 1부터 30 사이에서 랜덤으로 생성
    let userId = randomIntBetween(1, 30);

    const url = `http://host.docker.internal:8080/api/v1/users/${userId}/balance`;

    const res = http.get(url);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

}

function getMyReservations(token) {
    // userId를 1부터 30 사이에서 랜덤으로 생성
    let userId = randomIntBetween(1, 30);

    const url = `http://host.docker.internal:8080/api/v1/reservations/${userId}`;
    const params = {
        headers: {
            'Authorization': `Bearer ${token}`,
        },
    };

    const res = http.get(url, params);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

}

