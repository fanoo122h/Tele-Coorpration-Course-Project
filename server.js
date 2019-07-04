var restify = require('restify');
var users = require('./UserController');
var venue= require('./VenueController');
var friend= require('./FriendController');
var message= require('./MessageController');
var checkin= require('./CheckinController');
var rating= require('./RatingController');
var comment= require('./CommentController');
var gamification= require('./GamificationController');
var location= require('./LocationController');
var async = require('asyncawait/async');
var await = require('asyncawait/await');
// Authentication
var passport        = require('passport');
var LocalStrategy   = require('passport-local').Strategy;
var sessions        = require("client-sessions");

var port = 3000;

var server = restify.createServer({
  name: 'Laware',
});

//Defining Handlers:
// logging handler
server.use(function(req, res, next) {	
	console.log("--------------");
	console.log(req.isAuthenticated());
   console.log("Request: "+req.method + ' ' +req.url);
	return next();
});

// This is needed to use POST with body information
server.use(restify.plugins.bodyParser());
server.use(sessions({
    // cookie name dictates the key name added to the request object
    cookieName: 'session',
    // should be a large unguessable string
    secret: 'TkipIsTheBest',
    // how long the session will stay valid in ms
   duration: 15 * 86400 * 1000    // 5 days

}));

// Initialize passport
server.use(passport.initialize()); 
// Set up the passport session
server.use(passport.session());

// This is how a user gets serialized
passport.serializeUser(function(user, done) {
    console.log("in serialized");
    done(null, user.id);
});

// This is how a user gets deserialized
passport.deserializeUser(async(function(id, done) {
    // Look the user up in the database and return the user object
    // For this demo, return a static user
    console.log("in deserialize user"); 
    console.log("id "+id);
    var user =await(users.checkId(id,function(user){
        if(user!=null){
        console.log("return");
        
            return done(null, {id:user._id, username:user.emailId,user:user});
        }else{
        	return done(null, false, { error: 'Incorrect username or password.' });
        }
    
    }));
}));
// Lookup a user in our database
var lookupUser = async (function(emailId, password, done) {
console.log("in lookup"); 
   var user= await (users.checkLogin(emailId,password,function(user){
//console.log("user returned "+user._id);  
    if(user!=null){
            //return done(null, {id:user._id, username:user.emailId});
            return done(null, {id:user._id, username:user.emailId,user:user});
	}else{  
    		return done(null, false, { error: 'Incorrect username or password.' });
	         }

}));	
   	
   
});
passport.use(new LocalStrategy({ usernameField: 'emailId', session: true },lookupUser));
var loginRoute = function(req, res, next) {
     if(!req.body.hasOwnProperty('emailId','password')) {
			res.send(500, "Insufficient parameters, "); 	// Internal Server Error
     }      
    console.log("in login ");
    // The local login strategy
    console.log(req.params.emailId);
    passport.authenticate('local', function(err, user) {
    console.log("in authenticate");
        if (err) {
            console.log("in authenticate error");
            return next(err);
         }
        console.log("email id "+user.username);
        // Technically, the user should exist at this point, but if not, check
        if(!user) {
           res.send("Please check your details and try again.");
        }

        // Log the user in!
        req.logIn(user, function(err) {
            if (err) { return next(err); }
            req.session.user_id = req.user.id;

            if(user.username) { 
				//res.json({ success: 'Welcome ' + user.emailId+ "!"}); 
            res.send(user);
                return next();
            }            
            //res.json({ success: 'Welcome!'});
           res.send(user);
            return next();
        });
 })(req, res, next);
};


// GET /hello
var helloRoute =function(req, res, next) {
	
//	console.log(req["_passport"]);
//	console.log(req['headers']);
	
    console.log(req.isAuthenticated());
    if(req.isAuthenticated()) {
        res.send("Hello " + req.user.username);
    } else {
        res.send("Hello unauthenticated user");
    }

    return next();
};
function isAuthenticated(req, res, next) {
  if (req.isAuthenticated()) {
	  console.log("successs");
    return next();
  }
console.log("you fucked up bro");
res.send("login/ register in to use our services"); 
//next(new Error('You are not authenticated!.\n'));
//return next();
}

// Defining Endpoints:
server.post('/login', loginRoute);
server.get('/hello', helloRoute);
server.get('/logout', function (req, res){
  req.session.destroy();
  console.log("logout"+req.session);
  var l={"ss":"login to use our services again"};
     res.send(l);
	// res.redirect('/'); //Inside a callback… bulletproof!
});
// Complete CRUD functionality for users
server.get('api/users',isAuthenticated,  users.get);		// List of users
server.get('api/users/:id',isAuthenticated, users.getById);	// Single users
server.get('api/users/name/:name',isAuthenticated, users.getByName);
server.post('api/users',users.post);	// Create a users
server.put('api/users/:id',isAuthenticated, users.put);		// Update a users
server.del('api/users/:id',isAuthenticated, users.del);		// Delete a users
// Complete CRUD functionality for Venue
server.get('api/venues',isAuthenticated,venue.get);		// List of venue
server.get('api/venues/search',isAuthenticated,venue.getNamesTypes);// unique types and names in the system
server.get('api/venues/:id',isAuthenticated, venue.getById);	// Single venue by id
server.get('api/venues/name/:name',isAuthenticated, venue.getByName);	// Single venue by name
server.get('api/venues/type/:name',isAuthenticated, venue.getByType);
server.post('api/venues', isAuthenticated,   venue.post);	// Create a venue
server.put('api/venues/:id',isAuthenticated, venue.put);		// Update a venue
server.del('api/venues/:id',isAuthenticated, venue.del);		// Delete a venue
// Complete CRUD functionality for Friends
server.get('api/friend/:id',isAuthenticated,  friend.get);		// List of all friends
server.get('api/friend/pending/:id1',isAuthenticated,  friend.getPending);		// List of all pending friends
server.get('api/friend/:id1/id/:id2',isAuthenticated,friend.getById);// single friends data 
server.post('api/friend/',isAuthenticated, friend.post);	// Create a friend
server.put('api/friend/:id',isAuthenticated, friend.put);		// Update a friend
server.del('api/friend/:id',isAuthenticated, friend.del);	// Delete a friend
// CRUD messages  for friends based on friendship id 
server.get('api/messages/',isAuthenticated,  message.get);		// get names of headers of all friends
server.get('api/messages/:id',isAuthenticated ,message.getById);// get messages of  friend 
server.post('api/messages/',isAuthenticated, message.post);	// Create a Message-> based ofn friendship id 
server.del('api/messages/:id',isAuthenticated, message.del);	// Delete a Messages
//CRUD Checkins
server.get('api/checkin/',isAuthenticated,  checkin.get);		// get  all checkins
server.get('api/checkin/:id',isAuthenticated,checkin.getById);// get checkin by venue id 
//server.get('api/checkin/vistor/:id',checkin.getByIdTopVistors);// get checkin by venue id top vistors 
server.get('api/checkin/user/:id',isAuthenticated,checkin.getByUserId);// get rating of user 
server.post('api/checkin/',isAuthenticated, checkin.post);	// Create a checkin
server.del('api/checkin/:id',isAuthenticated, checkin.del);	// Delete a checkin
//Crud ratings
server.get('api/rating/',isAuthenticated,  rating.get);		// get all rating
server.get('api/rating/:id',isAuthenticated,rating.getById);// get rating by venue 
server.get('api/rating/user/:id',isAuthenticated,rating.getByUserId);// get rating of user 
server.post('api/rating/',isAuthenticated, rating.post);	// Create a rating
server.del('api/rating/:id', isAuthenticated,rating.del);	// Delete a rating
//Crud Comments
server.get('api/comment/',isAuthenticated,  comment.get);		// get all rating
server.get('api/comment/:id',isAuthenticated,comment.getById);// get comments by venue id
server.get('api/comment/user/:id',isAuthenticated,comment.getByUserId);// get comments by userId 
server.post('api/comment/',isAuthenticated, comment.post);	// Create a rating
server.del('api/comment/:id',isAuthenticated, comment.del);	// Delete a rating
//Crud gamification
server.get('api/gamification/',isAuthenticated,  gamification.get);		// get all rating
server.get('api/gamification/:id',isAuthenticated,gamification.getById);// get gamification by user id
//server.get('api/gamification/user/:id',isAuthenticated,gamification.getByUserId);// get gamification by userId 
server.post('api/gamification/',isAuthenticated, gamification.post);	// Create a rating
server.del('api/gamification/:id',isAuthenticated, gamification.del);	// Delete a rating
// CRUD locations for users  
server.get('api/location/',isAuthenticated,  location.get);		// get all locations
server.get('api/location/:id',isAuthenticated ,location.getById);// get location by user id 
server.post('api/location/',isAuthenticated, location.post);	// Create location 
server.del('api/location/:id',isAuthenticated, location.del);	// Delete a location


//custom endPoints
server.get('api/rating/likedplaces/:id',isAuthenticated,rating.getLikedPlaces);// gets a list of venues which the user gave more than 3 stars 
server.get('api/friend/location/:id',isAuthenticated,  friend.getlocation);		// List of all friends locations

server.listen(process.env.PORT||port, function() {
	console.log("Listening on port "+port);});