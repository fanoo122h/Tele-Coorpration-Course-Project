var mongo  = require('mongodb').MongoClient;
var ObjectId = require('mongodb').ObjectID;

function mongoCRUD() {
	
	that = this;
	var collection;
	
//	mongo.connect("mongodb://localhost:27017/laware", function(err, db){
	mongo.connect("mongodb://arslanyasinwattoo:Arslan-03144214002@laware-shard-00-00-xq7dc.mongodb.net:27017,laware-shard-00-01-xq7dc.mongodb.net:27017,laware-shard-00-02-xq7dc.mongodb.net:27017/Laware?ssl=true&replicaSet=Laware-shard-0&authSource=admin", function(err, db){
	if(err) { 
			return console.dir(err); 
		}
		console.log("Connected to mongodb");
		
		collection = db.collection('comments', function(err, collection) {
			if(err) {
				console.log("Error with collection:");
				console.log(err);
			}
		});
	});
	
	/**
	*	Loads the checkin list from the database collection
	*/
	that.retrieveComments = function(callback) {
		console.log("Retrieving comments list...");
		collection.find().toArray(function(err, items) {
			callback(items);
		});
	}
	
	/**
	 * Retrieves a single list of  Comments related to a Venue
	 */ 
	that.retrieveCommentById = function(id, callback) {		
		// create ObjectId as identification criterion
		comment = {	'venueId': id };	
		collection.find(comment).toArray(function(err, items) {
			callback(items);
		});		
	}
    
    /**
	 * Retrieves a single list of  Comments related to a users
	 */ 
	that.retrieveCommentByUserId = function(id, callback) {		
		// create ObjectId as identification criterion
		comment = {	'userId': id };	
		collection.find(comment).toArray(function(err, items) {
			callback(items);
		});		
	}
	

	/**
	 * Saves a new Comment to the database. 
	 * Returns full comment object including the id!
	 */
	that.saveComment = function(comment, callback) {
		collection.insert(comment, function(err, result){
			if(err) {
				Console.log("Error: "+err);
				callback(null);
			}
			else {
				callback(result['ops']);
			}
		});
	}
	
	/**
	 * Update name of existing comment
	 */
	that.updateComment = function(comment, callback) {
		collection.update({"_id":new ObjectId(comment._id)}, 
					      {$set:{venueId:comment.venueId,userId:comment.userId,comment:comment.comment,url:comment.url}}, {w:1}, 
		function(err, result) {
			if(err) {
				console.log(err);
			}
		});
		callback(checkin);
	}
	
	/**
	 * Deleting checkin
	 */
	that.deleteComment = function(id, callback) {
		collection.remove({"_id":new ObjectId(id)}, function(err, result) {
			if(err) {
				console.log(err);
				callback(null);
			}
		});
		callback(true);
	}

}

module.exports = new mongoCRUD();
