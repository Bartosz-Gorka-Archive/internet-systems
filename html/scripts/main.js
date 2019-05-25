'use strict';
const SERVER_URL = 'http://localhost:8000'

function loadStudents(model) {
  $.ajax({
    url: SERVER_URL + '/students',
    type: 'GET',
    dataType : "json",
    contentType: "application/json"
  }).done(function(result) {
    ko.mapping.fromJS(result, {}, model.students);
    model.students.subscribe(removedObjectCallback, null, 'arrayChange');
  });
}

function loadCourses(model) {
  $.ajax({
    url: SERVER_URL + '/courses',
    type: 'GET',
    dataType : "json",
    contentType: "application/json"
  }).done(function(result) {
    ko.mapping.fromJS(result, {}, model.courses);
    model.courses.subscribe(removedObjectCallback, null, 'arrayChange');
  });
}

function loadGrades(model, student) {
  $.ajax({
    url: resourceUrl(student, 'grades'),
    type: 'GET',
    dataType : "json",
    contentType: "application/json"
  }).done(function(data) {
    ko.mapping.fromJS(data, {}, model.grades);
  });
}

function removedObjectCallback(changes) {
  changes.forEach(function(change) {
    // Student / Course deleted from database
    if (change.status === 'deleted') {
      $.ajax({
        url: resourceUrl(change.value),
        type: 'DELETE',
        dataType : "json",
        contentType: "application/json"
      }).done(function() {
        console.log('Object removed from eStudent service');
      });
    }
  })
}

function resourceUrl(record, type = 'self') {
  const links = record.link();
  const resourceUrl = links.find(function(link) {
    return link.params.rel() === type
  });

  return resourceUrl.href();
}

$(document).ready(function(){
  function Student(data) {
    var self = this;
    self.index = ko.observable(data.index);
    self.firstName = ko.observable(data.firstName);
    self.lastName = ko.observable(data.lastName);
    self.dateOfBirth = ko.observable(data.dateOfBirth);
    self.link = ko.observableArray();
    ko.mapping.fromJS(data.link, {}, self.link);
  }

  function Course(data) {
    var self = this;
    self.id = ko.observable(data.id);
    self.name = ko.observable(data.name);
    self.supervisor = ko.observable(data.supervisor);
    self.link = ko.observableArray();
    ko.mapping.fromJS(data.link, {}, self.link);
  }

  function Grade(data) {
    var self = this;
    self.id = ko.observable(data.id);
    self.courseID = ko.observable(data.courseID);
    self.createdAt = ko.observable(data.createdAt);
    self.studentIndex = ko.observable(data.studentIndex);
    self.grade = ko.observable(data.grade);
    self.link = ko.observableArray();
    ko.mapping.fromJS(data.link, {}, self.link);
  }

  var StateViewModel = function () {
    var self = this;
    self.students = ko.observableArray();
    self.courses = ko.observableArray();
    self.grades = ko.observableArray();
    self.possibleGradeValue = ko.observableArray([
      {name: '2.0', value: 'NIEDOSTATECZNY'},
      {name: '3.0', value: 'DOSTATECZNY'},
      {name: '3.5', value: 'DOSTATECZNY_PLUS'},
      {name: '4.0', value: 'DOBRY'},
      {name: '4.5', value: 'DOBRY_PLUS'},
      {name: '5.0', value: 'BARDZO_DOBRY'}
    ]);
    self.newStudent = {
      firstName: ko.observable(),
      lastName: ko.observable(),
      dateOfBirth: ko.observable()
    };
    self.newCourse = {
      name: ko.observable(),
      supervisor: ko.observable()
    };
    self.newGrade = {
      studentIndex: ko.observable(),
      courseID: ko.observable(),
      createdAt: ko.observable(),
      grade: ko.observable(),
      student: ko.observable()
    };
    self.removeStudent = function(student) {
      self.students.remove(student)
    };
    self.setGrades = function(student) {
      loadGrades(self, student);
      self.newGrade.student(student);
      self.newGrade.studentIndex(student.index());

      // https://knockoutjs.com/documentation/click-binding.html#note-3-allowing-the-default-click-action
      return true;
    };
    self.removeGrade = function(grade) {
      $.ajax({
        url: resourceUrl(grade),
        type: 'DELETE',
        dataType : "json",
        contentType: "application/json"
      }).done(function() {
        console.log('Object removed from eStudent service');
        self.grades.remove(grade);
      });
    };
    self.removeCourse = function(course) {
      self.courses.remove(course);
      self.grades.removeAll();
    };
    self.saveNewStudent = function() {
      $.ajax({
        url: SERVER_URL + '/students',
        type: 'POST',
        dataType : "json",
        contentType: "application/json",
        data: ko.mapping.toJSON(self.newStudent)
      }).done(function(data) {
        self.students.push(new Student(data));
        self.newStudent.firstName('');
        self.newStudent.lastName('');
        self.newStudent.dateOfBirth('');
      });
    };
    self.saveNewCourse = function() {
      $.ajax({
        url: SERVER_URL + '/courses',
        type: 'POST',
        dataType : "json",
        contentType: "application/json",
        data: ko.mapping.toJSON(self.newCourse)
      }).done(function(data) {
        self.courses.push(new Course(data));
        self.newCourse.name('');
        self.newCourse.supervisor('');
      });
    };
    self.saveNewGrade = function() {
      $.ajax({
        url: resourceUrl(self.newGrade.student(), 'grades'),
        type: 'POST',
        dataType : "json",
        contentType: "application/json",
        data: ko.mapping.toJSON(self.newGrade)
      }).done(function(data) {
        self.grades.push(new Grade(data));
        self.newGrade.createdAt('');
      });
    };
  }
  var model = new StateViewModel();
  ko.applyBindings(model);

  loadStudents(model);
  loadCourses(model);
});
