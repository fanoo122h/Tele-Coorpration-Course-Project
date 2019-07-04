var dbase = require("./messageDao");

function MessageController() {

	that = this;
	
	// Get the list of all Messages
	that.get = function(req, res, next) {
		dbase.retrieveFriends(req.params.id1,function(friends) {
			res.send(200, friends);
		});		
		return next();
	};
	// Get list of messages between friends
	that.getById = function(req, res, next) {
		console.log("get by id messages"+req.params.id);
		messages = dbase.retrieveMessagesByFriendId(req.params.id,function(messages) {
			if(messages != null) {
				res.send(200, messages);
			} else {
				res.send(404, "Message not found");
			}
		});	
		return next();
	};
	// Create a new message
	that.post = function(req, res, next) {
		console.log(req.body);
		if(!req.body.hasOwnProperty('friendId','userId','firstname','lastname','message','time','date')) {
			res.send(500, "Insufficient parameters, ids required!"); 	// Internal Server Error
		} else {
			var message = {
                friendsId:req.body.friendId,
                userId:req.body.userId,
                firstname : req.body.firstname,
				lastname : req.body.lastname,
                message:req.body.message,
                time:req.body.time,
                date:req.body.date
			};			

			dbase.saveMessage(message, function(message){
				console.log('Message added: _id '+message._id+' friendId:'+message.friendsId+' user_id:'+message.userId+' firstName:'+message.firstName+' lastName:'+message.lastName+' message'+message.message+' time'+message.time+' date'+message.date);
				res.send(201,message);	// Send "Created" code and the friend object			
			});		
		}
		return next();
	};
	
	// Delete Messages of a friend
	that.del = function(req, res, next) {
		dbase.deleteMessage(req.params.id, function(result) {
			if(!result) {
				res.send(404, "Messages not found.");
			} 
			else {
				console.log("Deleted Messages "+req.params.id);
				res.send(200, "Deleted");	// 200 ok
			}
		});		

		return next();
	};
};

module.exports = new MessageController();