<script>
  import { onMount } from 'svelte';
  
  export let flightId;
  
  let capacity = null;
  let bookedSeats = [];
  let loading = true;
  let error = null;
  let showSeatMap = false;
  
  // Generate all possible seats (60 total: rows 1-10, columns A-F)
  const generateAllSeats = () => {
    const seats = [];
    for (let row = 1; row <= 10; row++) {
      for (let col of ['A', 'B', 'C', 'D', 'E', 'F']) {
        seats.push(`${row}${col}`);
      }
    }
    return seats;
  };
  
  const allSeats = generateAllSeats();
  
  async function fetchCapacityData() {
    if (!flightId) return;
    
    try {
      loading = true;
      error = null;
      
      // Fetch capacity info and booked seats in parallel
      const [capacityRes, bookedSeatsRes] = await Promise.all([
        fetch(`/api/v1/flights/${flightId}/capacity`),
        fetch(`/api/v1/flights/${flightId}/booked-seats`)
      ]);
      
      if (capacityRes.ok) {
        capacity = await capacityRes.json();
      }
      
      if (bookedSeatsRes.ok) {
        const bookedData = await bookedSeatsRes.json();
        bookedSeats = bookedData.bookedSeats || [];
      }
      
    } catch (e) {
      error = 'Failed to load capacity data';
      console.error('Capacity fetch error:', e);
    } finally {
      loading = false;
    }
  }
  
  // Check if a seat is available
  const isSeatAvailable = (seat) => !bookedSeats.includes(seat);
  
  // Get status badge color based on occupancy
  const getOccupancyColor = (percentage) => {
    if (percentage >= 90) return 'bg-red-100 text-red-800';
    if (percentage >= 70) return 'bg-yellow-100 text-yellow-800';
    return 'bg-green-100 text-green-800';
  };
  
  onMount(() => {
    fetchCapacityData();
  });
  
  // Reactive update when flightId changes
  $: if (flightId) fetchCapacityData();
</script>

{#if loading}
  <div class="flex items-center space-x-2 text-gray-500">
    <div class="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
    <span class="text-sm">Loading capacity...</span>
  </div>
{:else if error}
  <div class="text-red-600 text-sm">{error}</div>
{:else if capacity}
  <div class="bg-gray-50 rounded-lg p-4 space-y-3">
    <!-- Capacity Summary -->
    <div class="flex items-center justify-between">
      <div class="flex items-center space-x-3">
        <h3 class="font-semibold text-gray-900">Seat Availability</h3>
        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium {getOccupancyColor(capacity.occupancyPercentage)}">
          {capacity.occupancyPercentage}% Full
        </span>
      </div>
      
      <button 
        on:click={() => showSeatMap = !showSeatMap}
        class="text-blue-600 hover:text-blue-800 text-sm font-medium"
      >
        {showSeatMap ? 'Hide' : 'Show'} Seat Map
      </button>
    </div>
    
    <!-- Capacity Details -->
    <div class="grid grid-cols-3 gap-4 text-center">
      <div class="bg-white rounded-lg p-3">
        <div class="text-2xl font-bold text-green-600">{capacity.availableSeats}</div>
        <div class="text-sm text-gray-500">Available</div>
      </div>
      <div class="bg-white rounded-lg p-3">
        <div class="text-2xl font-bold text-red-600">{capacity.bookedSeats}</div>
        <div class="text-sm text-gray-500">Booked</div>
      </div>
      <div class="bg-white rounded-lg p-3">
        <div class="text-2xl font-bold text-gray-600">{capacity.totalSeats}</div>
        <div class="text-sm text-gray-500">Total</div>
      </div>
    </div>
    
    <!-- Seat Map -->
    {#if showSeatMap}
      <div class="bg-white rounded-lg p-4">
        <h4 class="font-medium text-gray-900 mb-3">Aircraft Seat Map</h4>
        
        <!-- Legend -->
        <div class="flex items-center space-x-4 mb-4 text-sm">
          <div class="flex items-center space-x-1">
            <div class="w-4 h-4 bg-green-200 border border-green-300 rounded"></div>
            <span>Available</span>
          </div>
          <div class="flex items-center space-x-1">
            <div class="w-4 h-4 bg-red-200 border border-red-300 rounded"></div>
            <span>Taken</span>
          </div>
        </div>
        
        <!-- Seat Grid -->
        <div class="grid grid-cols-6 gap-1 max-w-md">
          {#each Array(10) as _, rowIndex}
            {#each ['A', 'B', 'C', 'D', 'E', 'F'] as col}
              {@const seatNumber = `${rowIndex + 1}${col}`}
              {@const isAvailable = isSeatAvailable(seatNumber)}
              
              <div 
                class="w-8 h-8 text-xs flex items-center justify-center border rounded font-medium
                       {isAvailable 
                         ? 'bg-green-200 border-green-300 text-green-800 hover:bg-green-300' 
                         : 'bg-red-200 border-red-300 text-red-800'}"
                title="{seatNumber} - {isAvailable ? 'Available' : 'Taken'}"
              >
                {seatNumber}
              </div>
            {/each}
          {/each}
        </div>
        
        <!-- Row Labels -->
        <div class="text-xs text-gray-500 mt-2">
          Rows 1-10 • Columns A-F (Window), B-E (Middle), C-D (Aisle)
        </div>
      </div>
    {/if}
    
    <!-- Booking Status -->
    {#if capacity.isFull}
      <div class="bg-red-50 border border-red-200 rounded-lg p-3">
        <div class="flex items-center">
          <svg class="w-5 h-5 text-red-500 mr-2" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
          </svg>
          <span class="font-medium text-red-800">Flight is fully booked</span>
        </div>
      </div>
    {:else if capacity.availableSeats <= 5}
      <div class="bg-yellow-50 border border-yellow-200 rounded-lg p-3">
        <div class="flex items-center">
          <svg class="w-5 h-5 text-yellow-500 mr-2" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
          </svg>
          <span class="font-medium text-yellow-800">Only {capacity.availableSeats} seats left!</span>
        </div>
      </div>
    {/if}
  </div>
{/if}

<style>
  .animate-spin {
    animation: spin 1s linear infinite;
  }
  
  @keyframes spin {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
  }
</style> 