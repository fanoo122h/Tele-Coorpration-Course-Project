var mongo  = require('mongodb').MongoClient;
var ObjectId = require('mongodb').ObjectID;

function mongoCRUD() {
	
	that = this;
	var collection;
	
//  mongo.connect("mongodb://localhost:27017/laware", function(err, db){
	mongo.connect("mongodb://arslanyasinwattoo:Arslan-03144214002@laware-shard-00-00-xq7dc.mongodb.net:27017,laware-shard-00-01-xq7dc.mongodb.net:27017,laware-shard-00-02-xq7dc.mongodb.net:27017/Laware?ssl=true&replicaSet=Laware-shard-0&authSource=admin", function(err, db){
	if(err) { 
			return console.dir(err); 
		}
		console.log("Connected to mongodb");
		
		collection = db.collection('rating', function(err, collection) {
			if(err) {
				console.log("Error with collection:");
				console.log(err);
			}
		});
	});
	
	/**
	*	Loads the rating list from the database collection
	*/
	that.retrieveRatings = function(callback) {
		console.log("Retrieving rating list...");
		collection.find().toArray(function(err, items) {
			callback(items);
		});
	}
	
	/**
	 * Retrieves a single list of  rating related to a resturant
	 */ 
	that.retrieveRating = function(id, callback) {		
		// create ObjectId as identification criterion
		rating = {	'venueId': id };	
		collection.find(rating).toArray(function(err, items) {
			callback(items);
		});		
	}
	
	/**
	 * Retrieves a single list of  rating by user
	 */ 
	that.retrieveRatingByUser = function(id, callback) {		
		// create ObjectId as identification criterion
		rating = {	'userId': id };	
		collection.find(rating).toArray(function(err, items) {
			callback(items);
		});		
	}
	
	/**
	 * Saves a new Checkin to the database. 
	 * Returns full user object including the id!
	 */
	that.saveRating = function(rating, callback) {
		collection.insert(rating, function(err, result){
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
	 * Update name of existing rating
	 */
	that.updateRating = function(rating, callback) {
		collection.update({"_id":new ObjectId(rating._id)}, 
					      {$set:{venueId:rating.venueId,userId:rating.userId,firstName:rating.firstName,lastName:rating.lastName,rating:rating.rating}}, {w:1}, 
		function(err, result) {
			if(err) {
				console.log(err);
			}
		});
		callback(rating);
	}
	
	/**
	 * Deleting rating
	 */
	that.deleteRating = function(id, callback) {
		collection.remove({"_id":new ObjectId(id)}, function(err, result) {
			if(err) {
				console.log(err);
				callback(null);
			}
		});
		callback(true);
	}
	// liked places by user Return like of venues

	
	that.retrieveLikedVenues = function(id, callback) {		
		// create ObjectId as identification criterion
		console.log("reterving list from db",id);
		rating = {	'userId': id,'rating':{$gt:"2.0"}};	
		collection.find(rating).toArray(function(err, items) {
			callback(items);
		});		
	}
	


}

module.exports = new mongoCRUD();
