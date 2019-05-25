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
    model.students.subscribe(modifyStudent, null, 'arrayChange');
  });
}

function modifyStudent(changes) {
  changes.forEach(function(change) {
    // Student deleted from database
    if (change.status === 'deleted') {
      $.ajax({
        url: resourceUrl(change.value),
        type: 'DELETE',
        dataType : "json",
        contentType: "application/json"
      }).done(function() {
        console.log('Student removed from database');
      });
    }
  })
}

function resourceUrl(record) {
  const links = record.link();
  const resourceUrl = links.find(function(link) {
    return link.params.rel() === 'self'
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

  var StateViewModel = function () {
    var self = this;
    self.students = ko.observableArray();
    self.courses = ko.observableArray();
    self.newStudent = {
      firstName: ko.observable(),
      lastName: ko.observable(),
      dateOfBirth: ko.observable()
    };
    self.removeStudent = function(student) {
      self.students.remove(student)
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
    }
  }
  var model = new StateViewModel();
  ko.applyBindings(model);

  loadStudents(model);
});
