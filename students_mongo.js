var dbase = require("./mongoDBConnection");

function StudentController() {

	that = this;
	
	// Get the list of all students
	that.get = function(req, res, next) {
		dbase.retrieveStudents(function(students) {
			res.send(200, students);
		});		
		return next();
	};
	
	// Get single student
	that.getById = function(req, res, next) {
		student = dbase.retrieveStudent(req.params.id, function(student) {
			if(student != null) {
				res.send(200, student);
			} else {
				res.send(404, "Student not found");
			}
		});
		
		return next();
	};

	// Create a new student
	that.post = function(req, res, next) {

		console.log(req.body);
		if(!req.body.hasOwnProperty('name')) {
			res.send(500, "Insufficient parameters, name required!"); 	// Internal Server Error
		} else {
			var student = {
				name : req.body.name
			};			

			dbase.saveStudent(student, function(student){
				console.log('Student added: _id '+student._id+' name:'+student.name);
				res.send(201, student);	// Send "Created" code and the student object			
			});		
		}
		return next();
	};
	
	// Update a student
	that.put = function(req, res, next) {
		
		student = {
			"_id" : req.params.id,
			"name": req.params.name
		}
		
		dbase.updateStudent(student, function(student) {			
			if(!student) {
				res.send(404, "Student not found.");
			}
			console.log("Updated student "+req.params.id);
			res.send(200, student);	// 200 ok
		});		

		
		return next();
	};
	
	// Delete a student
	that.del = function(req, res, next) {
		dbase.deleteStudent(req.params.id, function(result) {
			if(!result) {
				res.send(404, "Student not found.");
			} 
			else {
				console.log("Deleted student "+req.params.id);
				res.send(200, "Deleted");	// 200 ok
			}
		});		

		return next();
	};
};

module.exports = new StudentController();