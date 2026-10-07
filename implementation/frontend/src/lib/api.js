import { PUBLIC_API_BASE } from '$env/static/public';

const fetchAPI = async (resource, options = {}) => {
    const sep = resource.startsWith("/") ? '' : '/';
    const response = await fetch(`${PUBLIC_API_BASE}${sep}${resource}`, options);

    

    const contentType = response.headers.get("content-type");

    if (!response.ok) {
        let errorData = null;
        if (contentType && contentType.includes("application/json")) {
            errorData = await response.json(); // Reads custom error message from the backend!
        } else {
            errorData = { message: await response.text() };
        }

        const error = new Error(errorData.message || response.statusText);
        error.response = { data: errorData, status: response.status }; // This will pass the full error response!
        throw error;
    }

    // Return parsed JSON if applicable
    if (contentType && contentType.includes("application/json")) {
        return await response.json();
    } else {
        return await response.text();
    }
};


export const api = {
    all: async (resource, options = {}) => fetchAPI(resource, {...options, method: 'GET'}),
    one: async (resource, slug, options = {}) => fetchAPI(`${resource}/${slug}`, {...options, method: 'GET'}),
    create: async (resource, data, options = {}) => fetchAPI(resource, {...options, method: 'POST', body: data}),
    update: async (resource, slug, data, options = {}) => fetchAPI(`${resource}/${slug}`, {...options, method: 'PUT', body: data}),
    delete: async (resource, slug, options = {}) => fetchAPI(`${resource}/${slug}`, {...options, method: 'DELETE'}),
}

/**
 * Format date to YYYY-MM-DD format
 * The input date is already in YYYY-MM-DD format from the HTML date input
 */

function formatDateForAPI(dateStr, timeStr) {
    if (!dateStr || !timeStr) return '';
    return `${dateStr}T${timeStr}:00`;
}

function transformFlightData(backendFlight) {
    const departureTime = new Date(backendFlight.departureTime);
    const arrivalTime = new Date(backendFlight.arrivalTime);
    const durationMs = arrivalTime - departureTime;
    const durationMinutes = Math.floor(durationMs / 60000);
    const hours = Math.floor(durationMinutes / 60);
    const minutes = durationMinutes % 60;
    const durationFormatted = `${hours} hour${hours !== 1 ? 's' : ''} ${minutes} minute${minutes !== 1 ? 's' : ''}`;

    
    return {
        flightNumber: backendFlight.flightNumber.toString(),
        departure: backendFlight.flightDeparture,
        arrival: backendFlight.flightDestination,
        departureTime: departureTime.toISOString(),
        arrivalTime: arrivalTime.toISOString(),
        duration: durationFormatted
    };
}

function transformFlightPath(flightPath) {
    const flights = flightPath.map(transformFlightData);
    const totalDuration = flights.reduce((sum, flight) => sum + flight.duration, 0);
    const totalTransfers = flights.length - 1;

    let totalTransferMinutes = 0;
    const transfers = [];

    for (let i = 0; i < flights.length - 1; i++) {
        const arrival = new Date(flights[i].arrivalTime);
        const nextDeparture = new Date(flights[i + 1].departureTime);
        const transferMs = nextDeparture - arrival;
        const transferMinutes = Math.floor(transferMs / 60000);
        totalTransferMinutes += transferMinutes;

        // Format transfer duration string
        const transferHours = Math.floor(transferMinutes / 60);
        const transferMins = transferMinutes % 60;
        transfers.push(`${transferHours} hour${transferHours !== 1 ? 's' : ''} ${transferMins} minute${transferMins !== 1 ? 's' : ''}`);
    }
     const totalTransferHours = Math.floor(totalTransferMinutes / 60);
    const totalTransferMins = totalTransferMinutes % 60;
    const totalTransferDuration = `${totalTransferHours} hour${totalTransferHours !== 1 ? 's' : ''} ${totalTransferMins} minute${totalTransferMins !== 1 ? 's' : ''}`;

    return {
        flights,
        totalDuration,
        totalTransfers: flights.length - 1,
        totalTransferDuration,
        transfers
    };
}

export async function searchFlights(departure, arrival, datetime) {
    if (!departure || !arrival || !datetime) {
        throw new Error('Missing required parameters');
    }

    const params = new URLSearchParams({
        departure: departure.trim(),
        arrival: arrival.trim(),
        datetime,  // Already combined string like "2025-08-01T14:30:00"
    });

    const data = await api.all(`flights/search?${params.toString()}`);
    console.log('Raw backend response:', data);

    if (!Array.isArray(data)) {
        throw new Error('Unexpected response format from server');
    }

    return data.map(transformFlightPath);
}




