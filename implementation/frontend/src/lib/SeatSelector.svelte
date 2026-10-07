<script>
  import { onMount } from 'svelte';
  
  export let flightId;
  export let selectedSeat = '';
  
  let bookedSeats = [];
  let suggestedSeat = null;
  let loading = false;
  
  async function fetchSeatData() {
    if (!flightId) return;
    
    try {
      loading = true;
      
      // Fetch booked seats and suggested seat
      const [bookedRes, suggestRes] = await Promise.all([
        fetch(`/api/v1/flights/${flightId}/booked-seats`),
        fetch(`/api/v1/flights/${flightId}/suggest-seat`)
      ]);
      
      if (bookedRes.ok) {
        const data = await bookedRes.json();
        bookedSeats = data.bookedSeats || [];
      }
      
      if (suggestRes.ok) {
        const data = await suggestRes.json();
        suggestedSeat = data.suggestedSeat || null;
      }
      
    } catch (e) {
      console.error('Failed to fetch seat data:', e);
    } finally {
      loading = false;
    }
  }
  
  // Check if a seat is available
  const isSeatAvailable = (seat) => !bookedSeats.includes(seat);
  
  // Auto-suggest seat when component mounts
  function useSuggestedSeat() {
    if (suggestedSeat) {
      selectedSeat = suggestedSeat;
    }
  }
  
  // Validate seat format and availability
  function validateSeat(seat) {
    if (!seat) return { valid: false, message: 'Please enter a seat number' };
    
    // Check format (1-10, A-F)
    if (!/^([1-9]|10)[A-F]$/.test(seat)) {
      return { 
        valid: false, 
        message: 'Invalid format. Use rows 1-10 and columns A-F (e.g., "5A", "10F")' 
      };
    }
    
    // Check availability
    if (!isSeatAvailable(seat)) {
      return { 
        valid: false, 
        message: 'This seat is already taken. Please choose another.' 
      };
    }
    
    return { valid: true, message: '' };
  }
  
  onMount(() => {
    fetchSeatData();
  });
  
  // Reactive validation
  $: seatValidation = validateSeat(selectedSeat);
  $: if (flightId) fetchSeatData();
</script>

<div class="space-y-3">
  <label for="seat" class="block text-sm font-medium text-gray-700">
    Seat Selection
  </label>
  
  <!-- Seat Input with Suggestion -->
  <div class="flex space-x-2">
    <div class="flex-1">
      <input
        type="text"
        id="seat"
        bind:value={selectedSeat}
        placeholder="e.g., 5A, 10F"
        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500
               {seatValidation.valid === false && selectedSeat ? 'border-red-300 bg-red-50' : ''}"
        maxlength="3"
      />
      
      <!-- Validation Message -->
      {#if selectedSeat && !seatValidation.valid}
        <p class="mt-1 text-sm text-red-600">{seatValidation.message}</p>
      {:else if selectedSeat && seatValidation.valid}
        <p class="mt-1 text-sm text-green-600">✓ Seat is available</p>
      {/if}
    </div>
    
    <!-- Auto-suggest Button -->
    {#if suggestedSeat && !loading}
      <button
        type="button"
        on:click={useSuggestedSeat}
        class="px-4 py-2 bg-blue-100 text-blue-700 rounded-md hover:bg-blue-200 text-sm font-medium whitespace-nowrap"
        title="Use suggested seat: {suggestedSeat}"
      >
        Suggest: {suggestedSeat}
      </button>
    {/if}
  </div>
  
  <!-- Seat Info -->
  {#if !loading}
    <div class="bg-gray-50 rounded-lg p-3">
      <div class="text-sm text-gray-600">
        <div class="flex items-center justify-between">
          <span><strong>Aircraft:</strong> 60 seats (10 rows × 6 columns)</span>
          {#if bookedSeats.length > 0}
            <span class="text-red-600">{bookedSeats.length} seats taken</span>
          {/if}
        </div>
        
        <div class="mt-2 text-xs">
          <strong>Seat Guide:</strong>
          A & F = Window • B & E = Middle • C & D = Aisle
        </div>
        
        {#if bookedSeats.length > 0}
          <div class="mt-2">
            <details>
              <summary class="cursor-pointer text-blue-600 hover:text-blue-800">
                View taken seats ({bookedSeats.length})
              </summary>
              <div class="mt-1 text-xs">
                {bookedSeats.join(', ')}
              </div>
            </details>
          </div>
        {/if}
      </div>
    </div>
  {:else}
    <div class="bg-gray-50 rounded-lg p-3">
      <div class="flex items-center space-x-2 text-gray-500 text-sm">
        <div class="animate-spin rounded-full h-3 w-3 border-b-2 border-blue-600"></div>
        <span>Loading seat availability...</span>
      </div>
    </div>
  {/if}
</div>

<style>
  .animate-spin {
    animation: spin 1s linear infinite;
  }
  
  @keyframes spin {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
  }
</style> 