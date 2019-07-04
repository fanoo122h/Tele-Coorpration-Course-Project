var dbase = require("./commentDao");

function CommentController() {

	that = this;
	
	// Get the list of all comments
	that.get = function(req, res, next) {
		dbase.retrieveComments(function(comment) {
			res.send(200, comment);
		});		
		return next();
	};
	
	// Get list of comments by venue id
	that.getById = function(req, res, next) {
		comment = dbase.retrieveCommentById(req.params.id, function(comment) {
			if(comment != null) {
				res.send(200, comment);
			} else {
				res.send(404, "comments not found");
			}
		});
		
		return next();
	};

	// Get list of comments by user id
	that.getByUserId = function(req, res, next) {
		comment = dbase.retrieveCommentByUserId(req.params.id, function(comment) {
			if(comment != null) {
				res.send(200, comment);
			} else {
				res.send(404, "comments not found");
			}
		});
		
		return next();
	};

	// Create a new comment
	that.post = function(req, res, next) {

		console.log(req.body);
		if(!req.body.hasOwnProperty('venueId','userId','firstname','lastname','comment','url')) {
			res.send(500, "Insufficient parameters, "); 	// Internal Server Error
		} else {
			var comment = {
                venueId:req.body.venueId,
				userId:req.body.userId,
				firstname:req.body.firstname,
				lastname:req.body.lastname,
				comment:req.body.comment,
                url:req.body.url
			};			

			dbase.saveComment(comment, function(comment){
				console.log('comment added: _id '+comment._id+' venueId:'+comment.venueId+' userId:'+comment.userId+' url:'+comment.url);
				res.send(201, comment);	// Send "Created" code and the comment object			
			});		
		}
		return next();
	};
	
	// Update a comment
	that.put = function(req, res, next) {
		
		comment = {
			"_id" : req.params.id,
            "venueId": req.params.venueId,
			"userId": req.params.userId,
			"firstname":req.params.firstname,
			"lastname":req.params.lastname,
			"comment":req.body.comment,
            "url": req.params.url
			}
		
		dbase.updateComment(comment, function(comment) {			
			if(!comment) {
				res.send(404, "comment not found.");
			}
			console.log("Updated comment "+req.params.id);
			res.send(200, comment);	// 200 ok
		});		

		
		return next();
	};
	
	// Delete a comment
	that.del = function(req, res, next) {
		dbase.deleteComment(req.params.id, function(result) {
			if(!result) {
				res.send(404, "comment not found.");
			} 
			else {
				console.log("Deleted comment "+req.params.id);
				res.send(200, "Deleted");	// 200 ok
			}
		});		

		return next();
	};
};

module.exports = new CommentController();