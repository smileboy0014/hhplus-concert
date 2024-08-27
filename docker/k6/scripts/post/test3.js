import http from 'k6/http';
import {check, sleep} from 'k6';
import {randomIntBetween} from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export const options = {
    summaryTrendStats: ["avg", "min", "med", "max", "p(90)", "p(95)", "p(99)", "p(99.50)"],
    stages: [
        {duration: '10s', target: 200},  // Ramp up
        {duration: '30s', target: 1000},  // Stay
        {duration: '10s', target: 0},   // Ramp down
    ],
};

function getToken() {
    const url = 'http://host.docker.internal:8080/api/v1/queues/token';
    const payload = JSON.stringify({ userId: randomIntBetween(1,35) });

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
    const token = JSON.parse(res.body).data.token;

    return token;
}

export default function() {
    // Get the token
    getToken();

    sleep(1);
}
