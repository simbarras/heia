import type { PageServerLoad } from './$types';
import type { Actions } from './$types';
import type { Beer } from '../types';
import { API_URL } from '../constant';

export const load = (async () => {
    return {};
}) satisfies PageServerLoad;

export const actions: Actions = {
    submitBeer: async ( { request } ) => {
        const formData = await request.formData();
        const beer: Beer = {
            name: formData.get('name')?.valueOf() as string,
            alcohol: Number(formData.get('alcohol')),
            capacity: Number(formData.get('capacity')),
            country: formData.get('country')?.valueOf() as string,
            price: Number(formData.get('price')),
            producer: formData.get('producer')?.valueOf() as string,
            id: formData.get('id') ? Number(formData.get('id')) : undefined,
        };

        if(beer.id == 0) {
            try {
                const response = await fetch(`${API_URL}/drink`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(beer)
                });    
                if (response.ok) {
                    console.log('Beer submitted successfully');
                }
    
                return { success: true };
            } catch (error) {
                console.error(error);
            }
        } else {
            try {
                const response = await fetch(`${API_URL}/drink/${beer.id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(beer)
                });    
                if (response.ok) {
                    console.log('Beer updated successfully');
                }
    
                return { success: true };
            } catch (error) {
                console.error(error);
            }
        }
        
    },
    editBeer: async ( { request } ) => {
        const formData = await request.formData();
        const beer: Beer = {
            id: Number(formData.get('id')),
            name: formData.get('name')?.valueOf() as string,
            alcohol: Number(formData.get('alcohol')),
            capacity: Number(formData.get('capacity')),
            country: formData.get('country')?.valueOf() as string,
            price: Number(formData.get('price')),
            producer: formData.get('producer')?.valueOf() as string,
        };

        try {
            const response = await fetch(`${API_URL}/drink`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(beer)
            });    
            if (response.ok) {
                console.log('Beer edited successfully');
            }

            return { success: true };
        } catch (error) {
            console.error(error);
        }
    },
}