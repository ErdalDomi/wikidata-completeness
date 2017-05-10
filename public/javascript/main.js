//this function will initialize the UI elements
$(function(){
  $('#selectClass').dropdown();
  $('#selectAttribute').dropdown();
  $('.ui.checkbox').checkbox("uncheck");
});


 function submitForm() {
    var formData = {
        class: $('#selectClass').find(':selected').val(),
        attribute: $('#selectAttribute').find(':selected').val(),
        occupation: $('input[name="occupation"]:checked').val(),
        nationality: $('input[name="nationality"]:checked').val(),
        centuryofbirth: $('input[name="centuryofbirth"]:checked').val(),
        gender: $('input[name="gender"]:checked').val()
    };
    console.log(formData);
    $.ajax({ type: 'POST', url: '/submitted', data: formData, success: onFormSubmitted });
 }

 // Handle post response
 function onFormSubmitted(response) {
      console.log("the response on the client is: "+response);
      $('#result').text(response);
 }
