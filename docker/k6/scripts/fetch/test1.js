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


export default function () {
    charge();
    sleep(1);
}

function charge() {
    let userId = randomIntBetween(1, 35);

    const url = `http://host.docker.internal:8080/api/v1/users/${userId}/charge`;
    const payload = JSON.stringify({
        "balance": 100,
    });
    const params = {
        headers: {
            'Content-Type': 'application/json'
        },
    };

    const res = http.patch(url, payload, params);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });
}