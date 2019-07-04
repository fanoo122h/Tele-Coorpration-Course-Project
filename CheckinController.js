var dbase = require("./checkinDao");

function CheckInController() {

	that = this;
	
	// Get the list of all checkin
	that.get = function(req, res, next) {
		dbase.retrieveCheckIns(function(checkin) {
			res.send(200, checkin);
		});		
		return next();
	};
		that.getByUserId = function(req, res, next) {
		checkin = dbase.retrieveCheckInByUser(req.params.id, function(checkin) {
			if(checkin != null) {
				res.send(200, checkin);
			} else {
				res.send(404, "checkin not found");
			}
		});
		
		return next();
	};



	/*
	// Get single checkin
	that.getByIdTopVistors = function(req, res, next) {
		checkin = dbase.retrieveTopCheckIn(req.params.id, function(checkin) {
			if(checkin != null) {
				var Array=[];
				var counting=0;
				for(var i=0;i<checkin.length;i++){
					var sorting=checkin[i];
						var index = Array.indexOf(sorting.userId); // 1
			
					//console.log("condition"+arrayFound);
					if(index!=-1){
					console.log("Array[userid]:"+sorting.userId);
					console.log("Array[index]:"+index);
						counting=Array[index];
						console.log("Array[index]:"+counting);
		
						var data={
						userId:sorting.userId,
						venueId:sorting.venueId,
						venueName:sorting.venueName,
						firstName : sorting.firstName,
						lastName : sorting.lastName,
						count:counting["count"]+1					
						}
						Array[index]=data;
						//counter++
					}else{
						var data={
						userId:sorting.userId,
					    venueId:sorting.venueId,
						venueName:sorting.venueName,
						firstName : sorting.firstName,
						lastName : sorting.lastName,
						count:1					
						}
					Array.push(data);	
					}
					}
				
				res.send(200, Array);
			} else {
				res.send(404, "checkin not found");
			}
		});
		
		return next();
	};

	*/
	// Get single checkin
	that.getById = function(req, res, next) {
		checkin = dbase.retrieveCheckIn(req.params.id, function(checkin) {
			if(checkin != null) {
				res.send(200, checkin);
			} else {
				res.send(404, "checkin not found");
			}
		});
		
		return next();
	};

	// Create a new checkin
	that.post = function(req, res, next) {

		console.log(req.body);
		if(!req.body.hasOwnProperty('venueId','userId','venueName','firstName','lastName')) {
			res.send(500, "Insufficient parameters, "); 	// Internal Server Error
		} else {
			var checkin = {
                venueId:req.body.venueId,
				userId:req.body.userId,
				venueName:req.body.venueName,
                firstName :req.body.firstName,
				lastName :req.body.lastName,
			};			

			dbase.saveCheckIn(checkin, function(checkin){
				console.log('checkin added: _id '+checkin._id+' venueId:'+checkin.venueId+' userId:'+checkin.userId+'firstName:'+checkin.firstName+' lastName:'+checkin.lastName);
				res.send(201, checkin);	// Send "Created" code and the user object			
			});		
		}
		return next();
	};
	
	// Update a checkin
	that.put = function(req, res, next) {
		
		checkin = {
			"_id" : req.params.id,
            "venueId": req.params.venueId,
			"userId": req.params.userId,
			"venueName":req.body.venueName,
            "firstName": req.params.firstName,
			"lastName": req.params.lastName
		}
		
		dbase.updateCheckIn(checkin, function(checkin) {			
			if(!checkin) {
				res.send(404, "checkin not found.");
			}
			console.log("Updated checkin "+req.params.id);
			res.send(200, checkin);	// 200 ok
		});		

		
		return next();
	};
	
	// Delete a checkin
	that.del = function(req, res, next) {
		dbase.deletecheckIn(req.params.id, function(result) {
			if(!result) {
				res.send(404, "checkin not found.");
			} 
			else {
				console.log("Deleted checkin "+req.params.id);
				res.send(200, "Deleted");	// 200 ok
			}
		});		

		return next();
	};
};

module.exports = new CheckInController();