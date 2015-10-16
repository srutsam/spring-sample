function doThis() {
    $('.greeting-id').append("Hi now", "what");
    $('.greeting-content').append("And what", "what");

    $(document).ready(function() {
        $.ajax({
            url: "https://maps.googleapis.com/maps/api/place/nearbysearch/json?sensor=false&location=45.440847,12.315515&radius=5000&key=AIzaSyDBMgy7k1P8oZcs5L3quapwkFkfnZ1Cbtc&keyword=cruises"
        }).then(function(data) {
            $('.greeting-id').append("Hi first", "what");
            $('.greeting-content').append("And then this", "what");
        });
    });
}

