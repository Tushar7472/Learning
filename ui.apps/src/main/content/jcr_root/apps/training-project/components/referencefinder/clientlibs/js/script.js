$(document).ready(function() {
    var max = 0;
    $('.findReference').on('click', function() {
        var assetPath = $("#coral-id-0").val();

         if(assetPath != "" && typeof assetPath != "undefined") {
            $.ajax({
                type: 'GET',
                url: '/bin/findReference?assetPath='+assetPath,
                success: function(response) {
                    console.log('AJAX call successful:', response);
                max = findLargestArraySize(response);
                 var template = Handlebars.compile(document.getElementById('handle-data').innerHTML);
                    $('.reponse-body').html(template(response));
                },
                error: function(xhr, status, error) {
                    console.error('AJAX call failed:', status, error);
                }
            });
    	} else {
			showError($('#coral-id-0'));
    	}
    });


function hideError(ele) {
  $(ele).removeClass("error");
}

 $('#coral-id-0').on('change', function() {
   hideError($(this));
 });


function showError(ele) {
			$(ele).addClass("error");
    		$(ele).addClass("vibrate");
    setTimeout(function() {
		$(ele).removeClass("vibrate");
    }, 500);
    event.preventDefault();
}

function findLargestArraySize(data) {
    let largestSize = 0;

    for(let key in data) {
        if(Array.isArray(data[key])) {
            largestSize = Math.max(largestSize, data[key].length);
        }
    }
    return largestSize;
}

Handlebars.registerHelper('times', function(block) {
    var accum = '';
    for(var i=0;i<max;++i) {
        accum += block.fn({index:i});
    }
    return accum;
});

Handlebars.registerHelper('getByIndex', function(array,index) {
    if(array && array.length > index) {
		return array[index];
    }

    return null;
});

});