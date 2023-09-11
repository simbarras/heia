import type { PageLoad } from './$types';
import { API_URL } from '../constant';
import type { Beer } from '../types';

export const load = (async () => {

    let products: Beer[] = []

    products = await fetch(`${API_URL}/drinks`)
        .then((res) => res.json())
        .then((data) => {
            console.log(data);
            data.map(async (product: Beer) => {
                product.avgRating = 2;
                product.cntRating = 2;
                // try {
                //     await fetch(`${API_URL}/ratings/info/${product.id}`)
                //         .then((res) => res.json())
                //         .then((data) => {
                //             const rating = {id: Number(product.id), average: Number(data.average), count: Number(data.count)};
                //             ratings.push(rating);
                //         });
                // } catch (error) {
                //     const rating = {id: Number(product.id), average: 0, count: 0};
                //     ratings.push(rating);
                // }
            });
            return data;
        }
    );

    return { products };

}) satisfies PageLoad;