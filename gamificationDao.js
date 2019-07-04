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
		
		collection = db.collection('gamification', function(err, collection) {
			if(err) {
				console.log("Error with collection:");
				console.log(err);
			}
		});
	});
	
	/**
	*	Loads the gamification list from the database collection
	*/
	that.retrieveGamification = function(id,callback) {
		console.log("Retrieving gamification list...");
		var gamify={'userId':id};
		collection.find(gamify).toArray(function(err, items) {
			callback(items);
		});
	}
    
    /**
	 * Retrieves a single gamification related to a user by name 
	 */ 
	that.retrieveGamificationByBadgeId = function(id,name, callback) {		
		// create ObjectId as identification criterion
		gamification = {	'userId': id,'badgeName':name };	
		collection.find(gamification).toArray(function(err, items) {
			callback(items);
		});		
	}
	/**
	 * Retrieves a single list of  gamification related to a user
	 */ 
	that.retrieveGamification = function(id, callback) {		
		// create ObjectId as identification criterion
		gamification = {	'userId': id };	
		collection.find(gamification).toArray(function(err, items) {
			callback(items);
		});		
	}
	
	/**
	 * Retrieves a single list of  gamification by user
	 */ 
	that.retrieveGamificationByUser = function(id, callback) {		
		// create ObjectId as identification criterion
		gamification = {	'userId': id };	
		collection.find(gamification).toArray(function(err, items) {
			callback(items);
		});		
	}
	
	/**
	 * Saves a new gamification to the database. 
	 * Returns full gamification object including the id!
	 */
	that.saveGamification = function(gamification, callback) {
		collection.insert(gamification, function(err, result){
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
	 * Update name of existing gamification
	 */
	that.updateGamification = function(gamification, callback) {
		collection.update({"_id":new ObjectId(gamification._id)}, 
					      {$set:{userId:gamification.userId,badgeName:gamification.badgeName}}, {w:1}, 
		function(err, result) {
			if(err) {
				console.log(err);
			}
		});
		callback(gamification);
	}
	
	/**
	 * Deleting gamification
	 */
	that.deleteGamification = function(id, callback) {
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
