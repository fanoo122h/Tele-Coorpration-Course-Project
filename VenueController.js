var request = require('request');
var https = require('https');
var upperCase = require('upper-case')
var dbase = require("./venueDao");

function VenueController() {
 var optionsfoursquare = {
   url: 'https://api.foursquare.com/v2/venues/search?near=Darmstadt&oauth_token=OCOJ4CTOQCUUPYEOZNAFY3QMBEPQICQ5O3QMQ0UKF4LW0EIQ&v=20170731',
   headers: {
     'User-Agent': 'request'
   }
 };

	that = this;
	
	// Get the list of all venues
	that.get = function(req, res, next) {
		dbase.retrieveVenues(function(venues) {
			if(venues!=null){
				res.send(200, venues);
			}else{
 			venues = request(options, callback);
				if(venues!=null){
					venues.forEach(function (item) {
  				dbase.saveVenue(item, function(item){
				console.log('venue added: _id '+item._id+' name:'+item.name+' address:'+item.address+' long:'+item.long+' lat:'+item.lat+' postalCode:'+item.postalCode+' city:'+item.city+' country:'+item.country+' category:'+item.category);
				});
				})
				res.send(200, venues);
				}else{
				res.send(404,"No venues found");
				}
		}
			
		});		
		return next();
	};
	
	// Get single venue
	that.getById = function(req, res, next) {
		venue = dbase.retrieveVenue(req.params.id, function(venue) {
			if(venue != null) {
				res.send(200, venue);
				 
			} else {
				res.send(404, "venue not found");
				
			}
		});
		
		return next();
	};
	//get venues by name and if not in db fetch it 
	//from google than stor it and send a copy of it to the user
	that.getByName = function(req, res, next) {
		 dbase.retrieveVenueByName(upperCase(req.params.name), function(venues) {
			if(venues != null && venues.length!=0) {
				console.log("in db");
				res.send(200, venues);
			} else {
				var optionsGoogle = {
   url: 'https://maps.googleapis.com/maps/api/place/textsearch/json?query='+req.params.name+'&location=49.883922,8.668215&radius=10000&key=AIzaSyDnL7pzp26SMbg7CUyoPtq2UOdrFusJ4VE',
   headers: {
     'User-Agent': 'request'
   }
 };
			 request(optionsGoogle,function(error, response, body) {
  				if (!error && response.statusCode == 200) {
				var info = JSON.parse(body);
				var data=info['results'];
			 //	console.log(data+"not in for loop");
				var re=[];
			 	if(data !=null){
			 	for(var i=0;i<data.length;i++){
					var set=data[i]; 
					//console.log("in for loop");
					var geo=set['geometry'];
					var location= geo['location'];
			 		var venue = {
						name : set['name'],
						address:set['formatted_address'],
						long:location['lng'],
						lat:location['lat'],
						category:set['types']
				};	
				dbase.saveVenue(venue, function(venue){
				//console.log('venue added: _id '+venue._id+' name:'+venue.name+' address:'+venue.address+' long:'+venue.long+' lat:'+venue.lat+' category:'+venue.category);
			
			});
			re.push(venue);
			console.log('venue added from request: _id '+venue._id+' name:'+venue.name+' address:'+venue.address+' long:'+venue.long+' lat:'+venue.lat+' category:'+venue.category);
			}
			}
			 if(re !=null && typeof re !== 'undefined'){
				res.send(200,re);
				}
				else{
			res.send(404, "venue not found");
			}
				
			
   }else{
	   console.log("Error:"+error);
   }
 });

			// res.send(200,callback.body);
			// res.send(200,request(optionsGoogle, callback));
			}	  
		});
		
		return next();
	};

	//get venues by name and if not in db fetch it 
	//from google than stor it and send a copy of it to the user
	that.getByType = function(req, res, next) {
		console.log("In type -------"+req.params.name);
		 dbase.retrieveVenueByType(req.params.name, function(venues) {
			if(venues != null && venues.length!=0) {
				console.log("in db");
				res.send(200, venues);
			} else {
				var optionsGoogle = {
   url: 'https://maps.googleapis.com/maps/api/place/textsearch/json?type='+req.params.name+'&location=49.883922,8.668215&radius=10000&key=AIzaSyDnL7pzp26SMbg7CUyoPtq2UOdrFusJ4VE',
   headers: {
     'User-Agent': 'request'
   }
 };
			 request(optionsGoogle,function(error, response, body) {
  				if (!error && response.statusCode == 200) {
				var info = JSON.parse(body);
				var data=info['results'];
			 //	console.log(data+"not in for loop");
				var re=[];
			 	if(data !=null){
			 	for(var i=0;i<data.length;i++){
					var set=data[i]; 
					//console.log("in for loop");
					var geo=set['geometry'];
					var location= geo['location'];
			 		var venue = {
						name : set['name'],
						address:set['formatted_address'],
						long:location['lng'],
						lat:location['lat'],
						category:set['types']
				};	
				dbase.saveVenue(venue, function(venue){
			//	console.log('venue added: _id '+venue._id+' name:'+venue.name+' address:'+venue.address+' long:'+venue.long+' lat:'+venue.lat+' category:'+venue.category);
			
			});
			re.push(venue);
		//	console.log('venue added from request: _id '+venue._id+' name:'+venue.name+' address:'+venue.address+' long:'+venue.long+' lat:'+venue.lat+' category:'+venue.category);
			}
			}
			 if(re !=null && typeof re !== 'undefined'){
				res.send(200,re);
				}
				else{
			res.send(404, "venue not found");
			}
				
			
   }else{
	   console.log("Error:"+error);
   }
 });

			// res.send(200,callback.body);
			// res.send(200,request(optionsGoogle, callback));
			}	  
		});
		
		return next();
	};


	// Create a new venue
	that.post = function(req, res, next) {

		console.log(req.body);
		if(!req.body.hasOwnProperty('name','address','long','lat','category')) {
			res.send(500, "Insufficient parameters, name , address, long, lat,category required!"); 	// Internal Server Error
		} else {
			var venue = {
				name : req.body.name,
				address:req.body.address,
				long:req.body.long,
				lat:req.body.lat,
				postalCode:req.body.postalCode,
				city:req.body.city,
				country:req.body.country,
				category:req.body.category,
				openingTime:req.body.openingTime,
				closingTime:req.body.closingTime
				
			};			

			dbase.saveVenue(venue, function(venue){
				console.log('venue added: _id '+venue._id+' name:'+venue.name+' address:'+venue.address+' long:'+venue.long+' lat:'+venue.lat+' postalCode:'+venue.postalCode+' city:'+venue.city+' country:'+venue.country+' category:'+venue.category);
				res.send(201, venue);	// Send "Created" code and the venue object			
			});		
		}
		return next();
	};
//het names and types for searching autocompletion
	that.getNamesTypes = function(req, res, next) {
		dbase.retrieveVenuesNamesTypes(function(venues) {
			if(venues!=null){
				res.send(200, venues);
			}
			
		});		
		return next();
	};



	// Update a venue
	that.put = function(req, res, next) {
		
		venue = {
			"_id" : req.params.id,
			"name" : req.body.name,
			"address":req.body.address,
			"long":req.body.long,
			"lat":req.body.lat,
			"postalCode":req.body.postalCode,
			"city":req.body.city,
			"country":req.body.country,
			"category":req.body.category		
		}
		
		dbase.updateVenue(venue, function(venue) {			
			if(!venue) {
				
				res.send(404, "venue not found.");
			}
			console.log("Updated venue "+req.params.id);
			res.send(200, venue);	// 200 ok
		});		

		
		return next();
	};
	
	// Delete a venue
	that.del = function(req, res, next) {
		dbase.deleteVenue(req.params.id, function(result) {
			if(!result) {
				res.send(404, "venue not found.");
			} 
			else {
				console.log("Deleted venue "+req.params.id);
				res.send(200, "Deleted");	// 200 ok
			}
		});		

		return next();
	};
	
 

};

module.exports = new VenueController();