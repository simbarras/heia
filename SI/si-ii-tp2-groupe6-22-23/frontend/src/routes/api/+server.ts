import type { RequestHandler } from './$types';
import { API_URL } from '../../constant';

export const DELETE: RequestHandler = async ({ request }) => {
    const beerId = await request.json().then((data) => data.id);
    
    console.log(beerId);

    const res = await fetch(`${API_URL}/drink/${beerId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    })
    .then((res) => res.json()
    .then((data) => {
        console.log(data);
        return data;
    }));


    return new Response(JSON.stringify({ message: res }));
};

export const POST: RequestHandler = async ({ request }) => {
    const beerId = await request.json().then((data) => data.id);

    const ratings = await fetch(`${API_URL}/ratings/${beerId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    })
    .then((res) => res.json()
    .then((data) => {
        console.log(data);
        return data;
    }));

    return new Response(JSON.stringify({ ratings: ratings }));
}

export const PUT: RequestHandler = async ({ request }) => {
    const rating = await request.json();

    const res = await fetch(`${API_URL}/rating`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: JSON.stringify(rating)
    })
    .then((res) => res.json()
    .then((data) => {
        console.log(data);
        return data;
    }));

    return new Response(JSON.stringify({ message: res }));
}