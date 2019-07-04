var request = require('request');

// var options = {
  // url: 'https://api.foursquare.com/v2/venues/search?near=Darmstadt&oauth_token=OCOJ4CTOQCUUPYEOZNAFY3QMBEPQICQ5O3QMQ0UKF4LW0EIQ&v=20170731',
  // headers: {
    // 'User-Agent': 'request'
  // }
// };
 
// function callback(error, response, body) {
  // if (!error && response.statusCode == 200) {
    // var info = JSON.parse(body);
    // console.log(info.stargazers_count + " Stars");
    // console.log(info.forks_count + " Forks");
  // }
// } 
// request(options, callback);

// // Normal get request -> print it out
// request.get("http://localhost:3000/students", function (error, response, body) {
  // console.log('statusCode:', response && response.statusCode); 
  // console.log('body:', body); 
// });

// // Making a post request
// request.post("http://localhost:3000/students");

// Making a more complex request
options = {
		method: 'GET',
		uri: 'http://localhost:3000/students',
		qs: {
			amount: 10,
			sort: 'asc'
		  }
	};

callback = function(err, res, body) {
	console.log(res.statusCode);
	console.log(body);
}
request(options, callback);



// request.get("http://localhost:3001/students", function (error, response, body) {
  // console.log('statusCode:', response && response.statusCode); 
  // console.log('body:', body); 
// });
