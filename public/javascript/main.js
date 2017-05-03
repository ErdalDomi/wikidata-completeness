
$(function(){
  $('#selectClass').dropdown();
  $('#selectAttribute').dropdown();
  $('.ui.checkbox').checkbox("uncheck");
});

//-----------reference for other project
// function checkDbStatus(){
//   var xhttp = new XMLHttpRequest();
//   xhttp.onreadystatechange = function(){
//     if(this.readyState == 4 && this.status == 200){
//       var status = this.responseText;
//       $('#dbConnectionStatus').text(status);
//       if(status == "ON"){
//         $('#dbConnectionStatus').css('color', 'green');
//       }
//       console.log(this.responseText);
//     }
//   }
//   xhttp.open("GET", "/checkStatus", true);
//   xhttp.send();
// }
// setInterval(checkDbStatus, 500);
//-------------------------



 function submitForm() {
    var formData = {
        class: $('#selectClass').find(':selected').val(),
        attribute: $('#selectAttribute').find(':selected').val(),
        occupation: $('input[name="occupation"]:checked').val(),
        nationality: $('input[name="nationality"]:checked').val(),
        centuryOfBirth: $('input[name="centuryOfBirth"]:checked').val(),
        gender: $('input[name="gender"]:checked').val()
    };
    console.log(formData);
    $.ajax({ type: 'POST', url: '/submitted', data: formData, success: onFormSubmitted });
 }

 // Handle post response
 function onFormSubmitted(response) {
      console.log("the response on the client is: "+response);
 }
