<script>
  import { searchFlights } from '$lib/api';
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import FlightCapacityInfo from '$lib/FlightCapacityInfo.svelte';
  import SeatSelector from '$lib/SeatSelector.svelte';
  
  let departure = '';
  let arrival = '';
  let date = '';
  let time = '';
  let flights = [];
  let loading = false;
  let error = null;

  // Modal state
  let showBookingModal = false;

  // Hardcoded flight details for the modal
  const flight = {
    id: 1,
    departure: 'Amsterdam',
    arrival: 'London',
    date: '2025-06-01',
    duration: 2,
  };

  // Booking form state
  let customerName = '';
  let customerEmail = '';
  let numPassengers = 1;
  let passengers = [
    { seat: '', baggage: 'carry-on', meal: 'standard', classType: 'Economy' }
  ];
  let paymentDetails = { cardNumber: '', expiry: '', cvv: '' };
  let bookingConfirmed = false;
  let totalPrice = 0;
  let bookingError = "";

  // Pricing logic (simple demo: base + class + baggage + meal)
  const basePrice = 100;
  const classPrices = { Economy: 0, Business: 50, First: 100 };
  const baggagePrices = { 'carry-on': 0, checked: 30 };
  const mealPrices = { standard: 0, vegetarian: 10, halal: 10 };

  $: passengers = passengers.slice(0, numPassengers);
  $: while (passengers.length < numPassengers) passengers.push({ seat: '', baggage: 'carry-on', meal: 'standard', classType: 'Economy' });

  $: totalPrice = passengers.reduce((sum, p) =>
    sum + basePrice + classPrices[p.classType] + baggagePrices[p.baggage] + mealPrices[p.meal], 0
  );

  async function handleSearch() {
    error = null;
    loading = true;
    flights = [];

    // HEWADS NOTE: THE FORM ALREADY CHECKS THAT ALL REQUIRED FIELDS ARE FILLED, USING THE "REQUIRED" ATTRIBUTE
    // SO THIS BLOCK IS REDUNDANT AND CAN BE REMOVED!
    // try {
    //   if (!departure?.trim() || !arrival?.trim() || !date) {
    //     error = 'Please fill in all fields';
    //     return;
    //   }

      // Validate that the selected date is not in the past
      try {
      const selectedDate = new Date(date);
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      
      if (selectedDate < today) {
        error = 'Please select a future date';
        return;
      
    }

      if (!time) {
  error = 'Please select a time';
  loading = false;
  return;
}

const combinedDateTime = new Date(`${date}T${time}`);
const now = new Date();
if (combinedDateTime < now) {
  error = 'Please select a future date and time';
  loading = false;
  return;
}

// Pass combined ISO string if backend expects it, or adjust accordingly
flights = await searchFlights(departure, arrival, combinedDateTime.toISOString());


      
      // Only show error if there are no flights and no other error occurred
      if (flights.length === 0 && !error) {
        error = 'No trips found for the selected route. Please try again!';
      }
    } catch (e) {
      error = e.message || 'An error occurred while searching for trips.';
      console.error('Search error:', e);
    } finally {
      loading = false;
    }
  }
  
  // ADDED: HELPER FUNCTION TO CORRECTLY FORMAT DATE FOR DISPLAY
  const formatDateTime = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleString([], {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
};

function parseTransferDuration(str) {
  if (!str) return 0;
  const match = str.match(/(\d+)\s*hour[s]?\s*(\d+)\s*minute[s]?/);
  if (!match) return 0;
  const hours = parseInt(match[1], 10);
  const minutes = parseInt(match[2], 10);
  return hours * 60 + minutes;
}
  function openBookingModal() {
    showBookingModal = true;
    bookingConfirmed = false;
    customerName = '';
    customerEmail = '';
    numPassengers = 1;
    passengers = [
      { seat: '', baggage: 'carry-on', meal: 'standard', classType: 'Economy' }
    ];
    paymentDetails = { cardNumber: '', expiry: '', cvv: '' };
  }

  function closeBookingModal() {
    showBookingModal = false;
  }

  async function createBooking(booking) {
    const res = await fetch('/api/v1/bookings', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(booking)
    });
    if (!res.ok) {
      let msg = 'Booking failed';
      try {
        const data = await res.json();
        if (data && data.message) msg = data.message;
      } catch (e) {
        // fallback to text if not JSON
        try {
          const text = await res.text();
          if (text) msg = text;
        } catch {}
      }
      throw new Error(msg);
    }
    return await res.json();
  }

  async function handleBooking() {
    // Prepare booking data for the first passenger only (demo)
    const booking = {
      flightId: flight.id,
      price: totalPrice,
      luggage: passengers[0].baggage === 'checked',
      food: passengers[0].meal !== 'standard',
      classType: passengers[0].classType,
      seatNumber: passengers[0].seat,
      customerEmail: customerEmail,
      customerName: customerName, // Added customer name
      paid: true
    };
    try {
      await createBooking(booking);
      bookingConfirmed = true;
      bookingError = "";
    } catch (e) {
      bookingError = e.message;
    }
  }
</script>

<!--Breadcrumbs-->
<nav class="flex mt-2 mb-2" aria-label="Breadcrumb">
  <ol class="inline-flex items-center space-x-1 md:space-x-2 rtl:space-x-reverse">
      <li class="inline-flex items-center">
          <a href="/" class="inline-flex items-center text-sm font-medium text-gray-700 hover:text-blue-600 ">
              <svg class="w-3 h-3 me-2.5" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 20 20">
                  <path d="m19.707 9.293-2-2-7-7a1 1 0 0 0-1.414 0l-7 7-2 2a1 1 0 0 0 1.414 1.414L2 10.414V18a2 2 0 0 0 2 2h3a1 1 0 0 0 1-1v-4a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1v4a1 1 0 0 0 1 1h3a2 2 0 0 0 2-2v-7.586l.293.293a1 1 0 0 0 1.414-1.414Z"/>
              </svg>
              Home
          </a>
      </li>
      <li>
        <div class="flex items-center">
          <svg class="rtl:rotate-180 w-3 h-3 text-gray-400 mx-1" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 6 10">
              <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m1 9 4-4-4-4"/>
          </svg>
            <a href="/search" class="ms-1 text-sm font-medium text-gray-700 hover:text-blue-600 md:ms-2">Search for Trip</a>
        </div>
      </li>
  </ol>
</nav>

<div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
  <div class="bg-white shadow rounded-lg p-6 mb-6">
    <form on:submit|preventDefault={handleSearch} class="space-y-4">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label for="departure" class="block text-sm font-medium text-gray-700">Departure</label>
          <input
            type="text"
            id="departure"
            bind:value={departure}
            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            placeholder="e.g., Amsterdam"
            required
          />
        </div>
        <div>
          <label for="arrival" class="block text-sm font-medium text-gray-700">Arrival</label>
          <input
            type="text"
            id="arrival"
            bind:value={arrival}
            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            placeholder="e.g., London"
            required
          />
        </div>
        <div>
          <label for="date" class="block text-sm font-medium text-gray-700">Date</label>
          <input
            type="date"
            id="date"
            bind:value={date}
            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            required
          />
        </div>
        <div>
          <label for="time" class="block text-sm font-medium text-gray-700">Time</label>
          <input
            type="time"
            id="time"
            bind:value={time}
            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            required
          />
        </div>
      </div>
      <div class="flex justify-end">
        <button
          type="submit"
          class="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          disabled={loading}
        >
          {loading ? 'Searching...' : 'Search trips'} 
        </button>
      </div>
    </form>
  </div>

  {#if loading}
    <div class="text-center py-4">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
    </div>
  {:else if flights.length > 0}
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      {#each flights as path, i}
  <div class="bg-white rounded-lg shadow-md p-4">
    <h2 class="text-md font-bold mb-2 text-blue-600">Option {i + 1}</h2>
    <div class="space-y-2">
      {#each path.flights as flight, idx}
  <div class="border-b pb-2 mb-2">
    <div class="flex justify-between items-start">
      <h3 class="text-sm font-semibold">Flight {flight.flightNumber}</h3>
      <span class="text-xs text-gray-500">{flight.duration}</span>
    </div>
    <div class="text-xs">
      <div class="flex justify-between">
        <span class="text-gray-600">From:</span>
        <span class="font-medium">{flight.departure}</span>
      </div>
      <div class="flex justify-between">
        <span class="text-gray-600">To:</span>
        <span class="font-medium">{flight.arrival}</span>
      </div>
      <div class="flex justify-between">
        <span class="text-gray-600">Departure Time:</span>
        <span class="font-medium">{formatDateTime(flight.departureTime)}</span>
      </div>
      <div class="flex justify-between">
        <span class="text-gray-600">Arrival Time:</span>
        <span class="font-medium">{formatDateTime(flight.arrivalTime)}</span>
      </div>
    </div>

    {#if idx < path.flights.length - 1}
      {#if parseTransferDuration(path.transfers[idx]) > 0}
        <p class="text-sm italic text-gray-500 mt-1">
          Transfer time: {path.transfers[idx]}
        </p>
      {/if}
    {/if}
  </div>
{/each}

{#if parseTransferDuration(path.totalTransferDuration) > 0}
  <p class="mt-2 font-semibold">
    Total transfer time: {path.totalTransferDuration}
  </p>
{/if}


    </div>
  </div>
{/each}
    </div>
  {:else if error}
    <div class="bg-red-50 border border-red-200 rounded-md p-4">
      <div class="flex">
        <div class="flex-shrink-0">
          <svg class="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
          </svg>
        </div>
        <div class="ml-3">
          <h3 class="text-sm font-medium text-red-800">Error</h3>
          <div class="mt-2 text-sm text-red-700">
            <p>{error}</p>
          </div>
        </div>
      </div>
    </div>
  {:else}
    <p class="text-gray-500 text-center">Search and select trips according to customer requirements!</p>
  {/if}
</div>

<!-- DEMO FLIGHT CARD WITH CAPACITY TRACKING -->
<div class="max-w-md mx-auto mt-8">
  <div class="bg-white rounded-lg shadow-lg overflow-hidden hover:shadow-xl transition-shadow duration-300">
    <div class="p-6">
      <div class="flex justify-between items-start mb-4">
        <div>
          <h3 class="text-xl font-bold text-gray-900">Flight 1</h3>
          <p class="text-sm text-gray-500">Duration: 2h</p>
        </div>
        <span class="px-3 py-1 text-sm font-semibold text-blue-600 bg-blue-100 rounded-full">
          Available
        </span>
      </div>
      <div class="flex items-center justify-between mb-6">
        <div class="flex-1">
          <div class="flex items-center">
            <div class="text-center">
              <p class="text-lg font-semibold text-gray-900">Amsterdam</p>
              <p class="text-sm text-gray-500">Departure</p>
            </div>
            <div class="flex-1 px-4">
              <div class="relative">
                <div class="h-0.5 bg-gray-300"></div>
                <div class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2">
                  <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3" />
                  </svg>
                </div>
              </div>
            </div>
            <div class="text-center">
              <p class="text-lg font-semibold text-gray-900">London</p>
              <p class="text-sm text-gray-500">Arrival</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- NEW: Flight Capacity Info -->
      <div class="mb-6">
        <FlightCapacityInfo flightId={flight.id} />
      </div>
      
      <div class="flex items-center justify-between">
        <div class="text-sm text-gray-600">
          <p>Date: 2025-06-01</p>
        </div>
        <button
          on:click={openBookingModal}
          class="px-6 py-2 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-colors duration-200"
        >
          Book Now
        </button>
      </div>
    </div>
  </div>
</div>

{#if showBookingModal}
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-white/40 backdrop-blur-sm">
    <div class="bg-white rounded-2xl shadow-2xl max-w-lg w-full mx-4 p-6 relative animate-fade-in" style="margin: 2rem 0; max-height: 80vh; overflow-y: auto;">
      <button class="absolute top-4 right-4 text-gray-400 hover:text-gray-600 text-2xl font-bold" on:click={closeBookingModal}>&times;</button>
      <h2 class="text-2xl font-bold mb-4">Book Your Flight</h2>
      <div class="mb-6 p-4 bg-blue-50 rounded">
        <div class="flex justify-between">
          <div>
            <div class="font-semibold">Flight {flight.id}</div>
            <div>{flight.departure} → {flight.arrival}</div>
            <div>Date: {flight.date}</div>
            <div>Duration: {flight.duration}h</div>
          </div>
          <div class="text-right">
            <span class="inline-block px-3 py-1 text-sm font-semibold text-blue-600 bg-blue-100 rounded-full">Available</span>
          </div>
        </div>
      </div>
      {#if !bookingConfirmed}
        <form on:submit|preventDefault={handleBooking} class="space-y-6">
            <!-- In the booking modal form Exception appearing --> 
          <!-- In the booking modal form Exception appearing --> 
          {#if bookingError}
            <div class="text-red-600 text-center font-semibold">{bookingError}</div>
          {/if}
            <!-- In the booking modal form Exception appearing --> 
               <!-- In the booking modal form Exception appearing --> 
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700">Customer Name</label>
              <input type="text" bind:value={customerName} required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500" />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Customer Email</label>
              <input type="email" bind:value={customerEmail} required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500" />
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">Number of Passengers</label>
            <input type="number" min="1" max="6" bind:value={numPassengers} class="mt-1 w-24 rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500" />
          </div>
          <div class="space-y-4">
            {#each passengers as passenger, i}
              <div class="p-4 border rounded-md bg-gray-50">
                <div class="font-semibold mb-2">Passenger {i + 1}</div>
                <div class="grid grid-cols-1 gap-4">
                  
                  <!-- NEW: Smart Seat Selector -->
                  <SeatSelector flightId={flight.id} bind:selectedSeat={passenger.seat} />
                  
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label class="block text-sm font-medium text-gray-700">Baggage</label>
                      <select bind:value={passenger.baggage} class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
                        <option value="carry-on">Carry-on</option>
                        <option value="checked">Checked</option>
                      </select>
                    </div>
                    <div>
                      <label class="block text-sm font-medium text-gray-700">Meal Preference</label>
                      <select bind:value={passenger.meal} class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
                        <option value="standard">Standard</option>
                        <option value="vegetarian">Vegetarian</option>
                        <option value="halal">Halal</option>
                      </select>
                    </div>
                    <div>
                      <label class="block text-sm font-medium text-gray-700">Class Type</label>
                      <select bind:value={passenger.classType} class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500">
                        <option value="Economy">Economy</option>
                        <option value="Business">Business</option>
                        <option value="First">First</option>
                      </select>
                    </div>
                  </div>
                </div>
              </div>
            {/each}
          </div>
          <div class="p-4 bg-gray-50 rounded-md">
            <div class="flex justify-between text-lg font-semibold">
              <span>Total Price:</span>
              <span>${totalPrice}</span>
            </div>
          </div>
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700">Card Number</label>
              <input type="text" bind:value={paymentDetails.cardNumber} placeholder="1234 5678 9012 3456" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500" />
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700">Expiry</label>
                <input type="text" bind:value={paymentDetails.expiry} placeholder="MM/YY" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500" />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">CVV</label>
                <input type="text" bind:value={paymentDetails.cvv} placeholder="123" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500" />
              </div>
            </div>
          </div>
          <button type="submit" class="w-full bg-blue-600 text-white py-3 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 font-semibold">
            Confirm Booking
          </button>
        </form>
      {:else}
        <div class="text-center">
          <div class="mb-4">
            <svg class="mx-auto h-12 w-12 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Booking Confirmed!</h3>
          <p class="text-gray-600 mb-4">Thank you for your booking. You will receive a confirmation email shortly.</p>
          <button 
            on:click={closeBookingModal}
            class="w-full bg-blue-600 text-white py-3 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 font-semibold"
          >
            Close
          </button>
        </div>
      {/if}
    </div>
  </div>
{/if}

<style>
  .animate-fade-in {
    animation: fade-in 0.3s ease-out;
  }

  @keyframes fade-in {
    from {
      opacity: 0;
      transform: scale(0.95);
    }
    to {
      opacity: 1;
      transform: scale(1);
    }
  }

  .animate-spin {
    animation: spin 1s linear infinite;
  }

  @keyframes spin {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }
</style>
