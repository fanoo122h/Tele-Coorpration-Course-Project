var dbase = require("./friendDao");
var dblocation = require("./locationDao");
var async = require('asyncawait/async');
var await = require('asyncawait/await');

function FriendController() {

	that = this;
	
	// Get the list of all Friend
	that.get = function(req, res, next) {
		console.log("get list of friends->"+req.params.id);
		if(req.params.id!=null){
	dbase.retrieveFriends(req.params.id,function(friends) {
			res.send(200, friends);
		});		
		}
//	var friend=[];
//	res.send(200,friend);
		return next();
	};
	// Get the list of all Friend
	that.getlocation = function(req, res, next) {
		console.log("get list of friends->"+req.params.id);
		var items=[];
		var check=0;
		 dbase.retrieveFriends(req.params.id,function(friends) {
			if(friends!=null){
			for(var i=0;i<friends.length;i++){
				console.log("in loop");	
			if(req.params.id===friends[i].id1){
		    dblocation.retrieveLocationById(friends[i].id2, function(location) {
			check=check+1;
				if(location != null && location.length>0) {
				console.log("in setting ");
				console.log(location[0].long);
				items.push(location[0]);
				console.log(check);
				if(check===friends.length){
				res.send(200,items);
}
			}else{
				if(check===friends.length){
				res.send(200,items);
			}
			} 
		});
			}else{			
			dblocation.retrieveLocationById(friends[i].id1, function(location) {
			check=check+1;
				if(location != null && location.length>0) {
				//add into list
				items.push(location[0]);
	            console.log(check);
				if(check===friends.length){
				res.send(200,items);
			}
			}else{
				if(check===friends.length){
				res.send(200,items);
			}
			}
				});
			}
				};
		}
	});
		return next();
	};
    
	

    // Get the list of all Friend
	that.getPending = function(req, res, next) {
		dbase.retrievePendingFriends(req.params.id1,function(friends) {
			res.send(200, friends);
		});		
		return next();
	};
	
	// Get single Friend
	that.getById = function(req, res, next) {
		friend = dbase.retrieveFriend(req.params.id1,req.params.id2, function(friend) {
			if(friend != null && friend.length>0) {

				res.send(200, friend);
			} else {
		friend = dbase.retrieveFriend(req.params.id2,req.params.id1, function(friend) {
	if(friend!=null){
		res.send(200,friend);
	}else{
			res.send(404, "Friend not found");
		
	}
		});
		
				}
		});
		
		return next();
	};
	// Create a new Friend
	that.post = function(req, res, next) {
		console.log(req.body);
		if(!req.body.hasOwnProperty('id1','id2','firstname','lastname','firstname2','lastname2')) {
			res.send(500, "Insufficient parameters, ids required!"); 	// Internal Server Error
		} else {
			var friend = {
                id1:req.body.id1,
                id2:req.body.id2,
				firstname : req.body.firstname,
				lastname : req.body.lastname,
				firstname2 : req.body.firstname2,
                lastname2: req.body.lastname2,
                pending:1
			};			

			dbase.saveFriend(friend, function(friend){
				console.log('friend added: _id '+friend._id+' id1:'+friend.id1+' id2:'+friend.id2+' firstName:'+friend.firstName+' lastName:'+friend.lastName+' firstName2:'+friend.firstName2+' lastName2:'+friend.lastName2);
				res.send(201,friend);	// Send "Created" code and the friend object			
			});		
		}
		return next();
	};
	
	// Update a friend
	that.put = function(req, res, next) {
		console.log("id in updating" +req.params.id);
		//console.log("id in updating" +req.body.id);
		friend = {
			"_id" : req.body.id,
            "id1" : req.body.id1,
            "id2" : req.body.id2,
            "firstname": req.body.firstname,
			"lastname": req.body.lastname,
			"firstname2" : req.body.firstname2,
			"lastname2": req.body.lastname2,
            "pending":0
		}
		
		dbase.updateFriend(friend, function(friend) {			
			if(!friend) {
				res.send(404, "friend not found.");
			}
			console.log("Updated friend "+req.params.id);
			res.send(200, friend);	// 200 ok
		});		

		
		return next();
	};
	
	// Delete a friend
	that.del = function(req, res, next) {
		dbase.deleteFriend(req.params.id, function(result) {
			if(!result) {
				res.send(404, "frined not found.");
			} 
			else {
				console.log("Deleted Friend "+req.params.id);
				res.send(200, "Deleted");	// 200 ok
			}
		});		

		return next();
	};
};

module.exports = new FriendController();