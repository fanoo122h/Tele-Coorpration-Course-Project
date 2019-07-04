var mongo  = require('mongodb').MongoClient;
var ObjectId = require('mongodb').ObjectID;

function mongoCRUD() {
	
	that = this;
	var collection;
	
	//mongo.connect("mongodb://localhost:27017/laware", function(err, db){
	mongo.connect("mongodb://arslanyasinwattoo:Arslan-03144214002@laware-shard-00-00-xq7dc.mongodb.net:27017,laware-shard-00-01-xq7dc.mongodb.net:27017,laware-shard-00-02-xq7dc.mongodb.net:27017/Laware?ssl=true&replicaSet=Laware-shard-0&authSource=admin", function(err, db){
	if(err) { 
			return console.dir(err); 
		}
		console.log("Connected to mongodb");
		
		collection = db.collection('checkins', function(err, collection) {
			if(err) {
				console.log("Error with collection:");
				console.log(err);
			}
		});
	});
	
	/**
	*	Loads the checkin list from the database collection
	*/
	that.retrieveCheckins = function(callback) {
		console.log("Retrieving checkins list...");
		collection.find().toArray(function(err, items) {
			callback(items);
		});
	}
	
	/**
	 * Retrieves a single list of  Checkin related to a resturant
	 */ 
	that.retrieveCheckIn = function(id, callback) {		
		// create ObjectId as identification criterion
		checkin = {	'venueId': id };	
		collection.find(checkin).toArray(function(err, items) {
			callback(items);
		});		
	}
	
	/**
	 * Retrieves a single list of  Checkins by user
	 */ 
	that.retrieveCheckInByUser = function(id, callback) {		
		// create ObjectId as identification criterion
		checkin = {	'userId': id };	
		collection.find(checkin).toArray(function(err, items) {
			callback(items);
		});		
	}
	/**
	 * Retrieves a single list of  Checkins by user
	 */ 
/*
	 that.retrieveTopCheckIn = function(id, callback) {		
		// create ObjectId as identification criterion
		checkin = {	'venueId': id };	
		collection.find(checkin).toArray(function(err, items) {
			callback(items);
		});		
	}*/
	
	/**
	 * Saves a new Checkin to the database. 
	 * Returns full user object including the id!
	 */
	that.saveCheckIn = function(checkin, callback) {
		collection.insert(checkin, function(err, result){
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
	 * Update name of existing checkin
	 */
	that.updateCheckIn = function(checkin, callback) {
		collection.update({"_id":new ObjectId(checkin._id)}, 
					      {$set:{venueId:checkin.venueId,userId:checkin.userId,firstName:checkin.firstName,lastName:checkin.lastName}}, {w:1}, 
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
	that.deleteCheckIn = function(id, callback) {
		collection.remove({"_id":new ObjectId(id)}, function(err, result) {
			if(err) {
				console.log(err);
				callback(null);
			}
		});
		callback(true);
	}
//collection.close();
}

module.exports = new mongoCRUD();
