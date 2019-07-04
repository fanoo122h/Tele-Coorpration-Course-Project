var dbase = require("./locationDao");

function LocationController() {

	that = this;
	
	// Get the list of all user locations
	that.get = function(req, res, next) {
		console.log("get list of Location->"+req.params.id);
		dbase.retrieveLocations(function(locations) {
			res.send(200, locations);
		});		
		return next();
	};
    
	// Get single location
	that.getById = function(req, res, next) {
		location = dbase.retrieveLocationById(req.params.id, function(location) {
			if(location != null && location.length>0) {

				res.send(200, location);
			} else {
			res.send(404, "location not found");
		
	}
		
		});
		return next();
	};
	// Create a new location
	that.post = function(req, res, next) {
		console.log(req.body);
		if(!req.body.hasOwnProperty('userId','firstname','lastname','long','lat','date','time')) {
			res.send(500, "Insufficient parameters, ids required!"); 	// Internal Server Error
		} else {
            console.log("in location before retrieve");
            location = dbase.retrieveLocationById(req.body.userId, function(location) {
			if(location != null && location.length>0) {
                console.log("in location update");
       console.log("date- body-"+req.body.date);
       console.log("_id- body-"+location[0]._id);
       
                locations = {
             	"_id" : location[0]._id,
                "userId":location[0].userId,
                "firstname":req.body.firstname,
                "lastname":req.body.lastname,
				"long" : req.body.long,
				"lat" : req.body.lat,
				"date" : req.body.date,
                "time": req.body.time,
			}
		dbase.updateLocation(locations, function(locations) {			
			if(!locations) {
	//			res.send(404, "location not found.");
			}
			console.log("Updated location "+locations._id);
			res.send(200, locations);	// 200 ok
		});		
			} else {
			console.log("location save");
			var location = {
                userId:req.body.userId,
                firstname:req.body.firstname,
                lastname:req.body.lastname,
				long : req.body.long,
				lat : req.body.lat,
				date : req.body.date,
                time: req.body.time,
			};			

			dbase.saveLocation(location, function(location){
				console.log('location added: _id '+location._id+' userId:'+location.userId+' long:'+location.long+' Lat:'+location.lat+' Date:'+location.date+' time:'+location.time);
				res.send(201,location);	// Send "Created" code and the location object			
			});
    }
            });
		
		}
		return next();
	};
	
	// Update a location
	that.put = function(req, res, next) {
		console.log("id in updating" +req.params.id);
		//console.log("id in updating" +req.body.id);
	
		 location = {
             	"_id" : req.body.id,
                "userId":req.body.userId,
                "firstname":req.body.firstname,
                "lastname":req.body.lastname,
				"long" : req.body.long,
				"lat" : req.body.lat,
				"date" : req.body.date,
                "time": req.body.time,
			}
		dbase.updateLocation(location, function(location) {			
			if(!location) {
				res.send(404, "location not found.");
			}
			console.log("Updated location "+req.params.id);
			res.send(200, location);	// 200 ok
		});		

		
		return next();
	};
	
	// Delete a location
	that.del = function(req, res, next) {
		dbase.deleteLocation(req.params.id, function(result) {
			if(!result) {
				res.send(404, "location not found.");
			} 
			else {
				console.log("Deleted location "+req.params.id);
				res.send(200, "Deleted");	// 200 ok
			}
		});		

		return next();
	};
};

module.exports = new LocationController();