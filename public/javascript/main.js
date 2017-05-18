//this function will initialize the UI elements
$(function(){
  $('#selectClass').dropdown();
  $('#selectAttribute').dropdown();
  $('.ui.checkbox').checkbox("uncheck");
});

//These are the values that will populate the radio facets
//They are plugged in on the showForm function
var humanOptions = {
  "Has child": "P40",
  "Place of birth": "P13",
  "Political party": "P102",
  "Educated at": "P69"
};

var musicOptions = {
  "Record label": "P264",
  "Track list": "P658",
  "Producer": "P162",
  "Logo": "P154"
}

var spaceOptions = {
  "opt1": "val1",
  "opt2": "val2"
}

//This function will switch the second dropdowns attributes
//Facets and attributes are changed together
function switchAtts(dpID, opts){
  var $el = $(dpID);
  $el.empty(); // remove old options
  $.each(opts, function(key,value) {
    $el.append($("<option></option>")
      .attr("value", value).text(key));
  });
}

//This function fires up when user selects the class
//from the dropdown. It then displays the appropriate facets
var showForm = function() {
  var allForms = document.getElementsByClassName('ui form');
  var dpOptions = {};
  var dropdown = document.getElementById("selectClass");

  if (dropdown.value != "-1") {
    var form = document.getElementById(dropdown.value);
    for (var i = 0; i < allForms.length; i++) {
      allForms[i].style.display = "none";
    }
    form.style.display = "initial";
  }

  if(dropdown.value == "humanForm"){
    dpOptions = humanOptions;
  } else if(dropdown.value == "musicForm"){
    dpOptions = musicOptions;
  } else if(dropdown.value == "spaceForm"){
    dpOptions = spaceOptions;
  }
  switchAtts("#selectAttribute", dpOptions);
  $("#selectAttribute").dropdown('clear');//clear selected attribute so it doesnt look out of place when changing classes
}



function submitForm() {
  var formData = {};
  if($('#selectClass').find(':selected').val() == 'humanForm'){
    console.log("we're getting the form data for the humanForm");
    formData = {
      table: 'human',
      attribute: $('#selectAttribute').find(':selected').val(),
      occupation: $('input[name="occupation"]:checked').val(),
      nationality: $('input[name="nationality"]:checked').val(),
      centuryofbirth: $('input[name="centuryofbirth"]:checked').val(),
      gender: $('input[name="gender"]:checked').val()
    };
  }else if($('#selectClass').find(':selected').val() == 'musicForm'){
    console.log("we're getting the form data for the musicForm");
    formData = {
      table: 'album',
      attribute: $('#selectAttribute').find(':selected').val(),
      genre: $('input[name="genre"]:checked').val(),
      language: $('input[name="language"]:checked').val(),
      date: $('input[name="date"]:checked').val()
    };
  }else {
    //space stuff
    formData = {
      table: 'album',
      attribute: $('#selectAttribute').find(':selected').val(),
      genre: $('input[name="genre"]:checked').val(),
      language: $('input[name="language"]:checked').val(),
      date: $('input[name="date"]:checked').val()
    };
  }

  console.log(formData);
  $.ajax({ type: 'POST', url: '/submitted', data: formData, success: onFormSubmitted });
}

// Handle post response
//to-do specify what is reponse[0],[1] and [2]
function onFormSubmitted(response) {
  console.log("the response on the client is: "+response);
  $('#result').text((response[0] * 100).toFixed(4)+ "% " + " (" + response[2]+" complete out of "+response[1]+")");


//.replace(/,/g, '')
  if(response[3]){
    var completeInstanceArray = response[3].split(',');
  }
  if(response[4]){
    var incompleteInstanceArray = response[4].split(',');
  }
  if(response[7]){ //completeLabel
    var completeLabelArray = response[7].split(',');
  }
  if(response[8]){ //incompleteLabel
    var incompleteLabelArray = response[8].split(',');
  }
  var completeInstancesList = $.map(completeInstanceArray, function (value, i) {
    if(i == 10){

    }else if(completeLabelArray[i]){
      return '<li><a href="'+value+'">' + completeLabelArray[i].slice(0, -3); + '</a></li>'
    }else{
      return '<li><a href="#" > -- </a></li>'
    }
  }).join('');
  $('#completeinstances').html(completeInstancesList);

  var incompleteInstancesList = $.map(incompleteInstanceArray, function(value, i){
    if(i == 10){

    }else{
      return '<li><a href="'+value+'">' + incompleteLabelArray[i].slice(0, -3); + '</a></li>'
    }

  }).join('');
  $('#incompleteinstances').html(incompleteInstancesList);

  $('#completeinstances').css("list-style-type", "decimal");
  $('#incompleteinstances').css("list-style-type", "decimal");

  $('#completeQueryLink').attr("onclick", "window.open('https://query.wikidata.org/#" +encodeURI(response[5]) + "')");
  $('#incompleteQueryLink').attr("onclick", "window.open('https://query.wikidata.org/#" +encodeURI(response[6]) + "')");

}
