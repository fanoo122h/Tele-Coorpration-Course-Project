var mongo  = require('mongodb').MongoClient;
var ObjectId = require('mongodb').ObjectID;

function mongoCRUD() {
	
	that = this;
	var collection;
mongo.connect("mongodb://arslanyasinwattoo:Arslan-03144214002@laware-shard-00-00-xq7dc.mongodb.net:27017,laware-shard-00-01-xq7dc.mongodb.net:27017,laware-shard-00-02-xq7dc.mongodb.net:27017/Laware?ssl=true&replicaSet=Laware-shard-0&authSource=admin", function(err, db){
//	mongo.connect("mongodb://localhost:27017/laware", function(err, db){	
		if(err) { 
			return console.dir(err); 
		}
		console.log("Connected to mongodb");
		
		collection = db.collection('venue', function(err, collection) {
			if(err) {
				console.log("Error with collection:");
				console.log(err);
			}
		});
		});
	

	/**
	*	Loads the venue list from the database collection
	*/
	that.retrieveVenues = function(callback) {
		console.log("Retrieving venue list...");
		collection.find().toArray(function(err, items) {
			callback(items);
		});
	}
	
	/**
	 * Retrieves a single venue
	 */ 
	that.retrieveVenue = function(id, callback) {		
		// create ObjectId as identification criterion
		venue = {	'_id': new ObjectId(id) };	
		collection.findOne(venue, function(err, items) {
			callback(items);
		});		
	}
		
	/**
	 * Retrieves a single venue by name
	 */ 
	that.retrieveVenueByName = function(name, callback) {		
		// create ObjectId as identification criterion
		venue = {	'name':new RegExp(name, 'i')};	
		collection.find(venue).toArray(function(err, items) {
			callback(items);
		});		
	}
	
	/**
	 * Retrieves a single venue by type
	 */ 
	that.retrieveVenueByType = function(type, callback) {		
		// create ObjectId as identification criterion
		console.log("in venue by type");
console.log("in ype "+type);
		
		venue = {	'category':type };
		collection.find(venue).toArray(function(err, items) {
			console.log(items);
			callback(items);
		});		
	}
	//to get types and names for better searching etc
		that.retrieveVenuesNamesTypes = function(callback) {	
			
	//		collection.find().toArray(function(err, items) {
	//		callback(items);
	//	});	
			
	var results=[];
	collection.distinct('name',function(err, items){
			console.log(items);
//			for(var i=0;i<items.length;i++){
//				results.push(items[i]);
//			}
			callback(items);
			//	results=items;
		});
//collection.distinct('category',function(err, items){
//			console.log(items);
//			for(var i=0;i<items.length;i++){
//				results.push(items[i]);
//			}
			//	results= results+ items 
//			callback(results);
//		});



		//var 
		//callback(results);		
	}
	
	
	/**
	 * Saves a new venue to the database. 
	 * Returns full venue object including the id!
	 */
	that.saveVenue = function(venue, callback) {
		collection.insert(venue, function(err, result){
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
	 * Update name of existing venue
	 */
	that.updateVenue = function(venue, callback) {
		collection.update({"_id":new ObjectId(venue._id)}, 
					      {$set:{name:venue.name,address:venue.address,long:venue.long,lat:venue.lat,postalCode:venue.postalCode,city:venue.city,country:venue.country,category:venue.category}}, {w:1}, 
		function(err, result) {
			if(err) {
				console.log(err);
			}
		});
		callback(venue);
	}
	
	/**
	 * Deleting Venue
	 */
	that.deleteVenue = function(id, callback) {
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