var dbase = require("./ratingDao");

function RatingController() {

	that = this;
	
	// Get the list of all rating
	that.get = function(req, res, next) {
		dbase.retrieveRatings(function(rating) {
			res.send(200, rating);
		});		
		return next();
	};
	
	
	// Get single Rating
	that.getLikedPlaces = function(req, res, next) {
		rating = dbase.retrieveLikedVenues(req.params.id, function(rating) {
			if(rating != null) {
				res.send(200,rating);
			} else {
				res.send(404, "rating not found");
			}
		});
		
		return next();
	};
	// Get single Rating
	that.getById = function(req, res, next) {
		rating = dbase.retrieveRating(req.params.id, function(rating) {
			if(rating != null) {
				res.send(200,rating);
			} else {
				res.send(404, "rating not found");
			}
		});
		
		return next();
	};
// Get single Rating
	that.getByUserId = function(req, res, next) {
		rating = dbase.retrieveRatingByUser(req.params.id, function(rating) {
			if(rating != null) {
				res.send(200,rating);
			} else {
				res.send(404, "rating not found");
			}
		});
		
		return next();
	};

	// Create a new rating
	that.post = function(req, res, next) {

		console.log(req.body);
        if(!req.body.hasOwnProperty('venueId','userId','venueName','firstname','lastname','long','lat','address','rating')) {
			res.send(500, "Insufficient parameters, "); 	// Internal Server Error
		} else {
			var rating = {
                venueId:req.body.venueId,
				userId:req.body.userId,
				venueName:req.body.venueName,
                firstName : req.body.firstname,
				lastName : req.body.lastname,
				long:req.body.long,
				lat:req.body.lat,
				address:req.body.address,
                rating: req.body.rating
            };			

			dbase.saveRating(rating, function(rating){
				console.log('rating added: _id '+rating._id+' venueId:'+rating.venueId+' userId:'+rating.userId+'firstName:'+rating.firstName+' lastName:'+rating.lastName+' rating'+rating.rating);
				res.send(201, rating);	// Send "Created" code and the rating object			
			});		
		}
		return next();
	};
	
	// Update a rating
	that.put = function(req, res, next) {
		
		rating = {
			"_id" : req.params.id,
            "venueId": req.params.venueId,
			"userId": req.params.userId,
			"venueName":req.body.venueName,
            "firstName": req.params.firstName,
			"lastName": req.params.lastName,
			"long":req.params.long,
			"lat":req.params.lat,
			"address":req.params.address,
            
            "rating" : req.params.rating
        }
		
		dbase.updateRating(rating, function(rating) {			
			if(!rating) {
				res.send(404, "rating not found.");
			}
			console.log("Updated rating "+req.params.id);
			res.send(200, checkin);	// 200 ok
		});		

		
		return next();
	};
	
	// Delete a rating
	that.del = function(req, res, next) {
		dbase.deleteRating(req.params.id, function(result) {
			if(!result) {
				res.send(404, "rating not found.");
			} 
			else {
				console.log("Deleted rating "+req.params.id);
				res.send(200, "Deleted");	// 200 ok
			}
		});		

		return next();
	};
};

module.exports = new RatingController();