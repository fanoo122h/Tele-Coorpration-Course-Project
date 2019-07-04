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
		
		collection = db.collection('messages', function(err, collection) {
			if(err) {
				console.log("Error with collection:");
				console.log(err);
			}
		});
	});
	/**
	*	Loads the Messages of Friends from the database collection
	*/
	that.retrieveMessages = function(callback) {
        console.log("Retrieving messages of friend list...");
       // friend = {	'id1':id,};	
        collection.find().toArray(function(err, items) {
			callback(items);
		});
	}
	
	/**
	 * Retrieves a Messages of two friends friend
	 */ 
	that.retrieveMessagesByFriendId = function(id, callback) {		
		// create ObjectId as identification criterion
		console.log("retrieveing messages");
		friend = {	'friendsId':id};	
		collection.find(friend).toArray(function(err, items) {
			console.log("items"+items);
			callback(items);
		});		
	}
	
	/**
	 * Saves a new Message to the database. 
	 * Returns full Message's object including the id!
	 */
	that.saveMessage = function(message, callback) {
		collection.insert(message, function(err, result){
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
	 * Deleting Messages
	 */
	that.deleteMessage = function(id, callback) {
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
