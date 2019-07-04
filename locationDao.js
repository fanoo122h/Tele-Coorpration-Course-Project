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
		
		collection = db.collection('locations', function(err, collection) {
			if(err) {
				console.log("Error with collection:");
				console.log(err);
			}
		});
	});
	
	/**
	*	Loads the location list from the database collection
	*/
	that.retrieveLocations = function(callback) {
        console.log("Retrieving friends list...");
	    collection.find().toArray(function(err, items) {
					callback(items); 			
		});
	}
	
	/**
	 * Retrieves a single friend
	 */ 
	that.retrieveLocationById = function(id, callback) {		
		// create ObjectId as identification criterion
		console.log("searching for user location");
	location = {	'userId':id};
		
		collection.find(location).toArray(function(err, items) {
			callback(items);
		});		
	}
	
	/**
	 * Saves a new friends to the database. 
	 * Returns full friend's object including the id!
	 */
	that.saveLocation = function(location, callback) {
		collection.insert(location, function(err, result){
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
	 * Update name of existing location
	 */
	that.updateLocation = function(location, callback) {
        console.log("in update");
		collection.update({"_id":new ObjectId(location._id)}, 
					      {$set:{userId:location.userId,firstname:location.firstname,lastname:location.lastname,long:location.long,lat:location.lat,date:location.date,time:location.time}}, {w:1}, 
		function(err, result) {
			if(err) {
				console.log(err);
			}
		});
		callback(location);
	}
	
	/**
	 * Deleting Friends
	 */
	that.deleteLocation = function(id, callback) {
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
