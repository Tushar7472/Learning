document.addEventListener('DOMContentLoaded', function ()  { 
    const form = document.querySelector('.custom-form');
    const createButton = document.getElementById('create');

	loadToolCategories();

    createButton.addEventListener('click', function (event) {
      event.preventDefault();
      if (validateForm()) {
        // Form is valid, make AJAX call
        const toolName = document.getElementById('toolName').value;
        const categoryName = document.getElementById('categoryName').value;
        const iconName = document.getElementById('iconName').value;
        const description = document.getElementById('description').value;
        const pagePath = document.getElementById('pagePath').value;
        const componentName = document.getElementById('componentName').value;
        const componentPath =  document.getElementById("coral-id-4").value;

        const url = '/bin/createTool';
        const queryParams = `?toolName=${toolName}&categoryName=${categoryName}&iconName=${iconName}&description=${description}&pagePath=${pagePath}&componentName=${componentName}&componentPath=${componentPath}`;

        const ajaxRequest = new XMLHttpRequest();
        ajaxRequest.onreadystatechange = function () {
          if (ajaxRequest.readyState === 4) {
        if (ajaxRequest.status === 200) {
             const response = ajaxRequest.responseText;
      
              // Parse the JSON response if applicable
              const responseData = JSON.parse(response);
        
              // Extract message and path from the response data
              var message = responseData.message;
         const path = responseData.pagePath;
              message = `<p>${message}</p><a href="${path}.html">Navigate to tool</a>`;


              // Show popup with message and anchor link
              showModal(message, path);
        } else if (ajaxRequest.status === 400) {
        	const response = ajaxRequest.responseText;
      
              // Parse the JSON response if applicable
              const responseData = JSON.parse(response);
        
              // Extract message and path from the response data
              var message = responseData.message;
              const path = responseData.pagePath;
				message = `<p>${message}</p>`;
         showModal(message, path);	
            }
          }
        };

        ajaxRequest.open('GET', url + queryParams, true);
        ajaxRequest.send();
      }
    });


    function showModal(message, path) {
         var dialog = new Coral.Dialog().set({
          id: "demoDialog",
          header: {
            innerHTML: "Info"
          },
          content: {
            innerHTML: message
          },
          footer: {
            innerHTML: "<button is=\"coral-button\" variant=\"primary\" coral-close=\"\">Close</button>"
          }
        });

        document.body.appendChild(dialog);
        var dialogPopup = document.querySelector('#demoDialog');
    	dialogPopup.show();
	}

    function validateForm() {
      let isValid = true;

      // Reset previous validation styles
      const formFields = form.querySelectorAll('.coral-Form-field');
      formFields.forEach(field => field.classList.remove('coral-Form-field--error'));

      // Validate each form field
      const toolName = document.getElementById('toolName').value;
      const categoryName = document.getElementById('categoryName').value;
      const iconName = document.getElementById('iconName').value;
      const description = document.getElementById('description').value;
      const componentName = document.getElementById('componentName').value;
      const componentPath = document.getElementById("coral-id-4").value;

      if (!toolName.trim()) {
        highlightField('toolName');
        return false;
      }

      if (!categoryName.trim()) {
        highlightField('categoryName');
         return false;
      }

      if (!iconName.trim()) {
        highlightField('iconName');
         return false;
      }

      if (!description.trim()) {
        highlightField('description');
         return false;
      }

         if (!componentName.trim()) {
        highlightField('componentName');
         return false;
      }

      if (!componentPath.trim()) {
        highlightField('coral-id-4');
         return false;
      }

      return isValid;
    }

    function highlightField(fieldName) {
      const field = document.getElementById(fieldName);
      field.setAttribute('required', 'true');
    }


      function loadToolCategories() {
      $.ajax({
        url: '/bin/getToolCategory',
        type: 'GET',
        dataType: 'json',
        success: function(data) {
          $('#toolCategory').empty();

          for (var i = 0; i < data.length; i++) {
            $('#toolCategory').append($('<option>', {
              value: data[i],
              text: data[i]
            }));
          }
        },
        error: function(xhr, status, error) {
          console.error('Error loading tool categories:', status, error);
        }
      });
    }
  });