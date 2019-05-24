'use strict';
const SERVER_URL = 'http://localhost:8000'

function loadStudents(model) {
  $.ajax({
    url: SERVER_URL + '/students',
    type: 'GET',
    dataType: 'json',
    accepts: {
      contentType: 'application/json'
    }
  }).done(function(result) {
    ko.mapping.fromJS(result, {}, model.students);
    model.students.subscribe(modifyStudent, null, 'arrayChange');
  });
}

function modifyStudent(changes) {
  console.log(changes)
  changes.forEach(function(change) {
    // Student deleted from database
    if (change.status === 'deleted') {
      $.ajax({
        url: resourceUrl(change.value),
        type: 'DELETE',
        dataType: 'json',
        contentType: 'application/json',
        accepts: {
          contentType: 'application/json'
        },
      }).done(function() {
        console.log('Student removed from database');
      });
    }
    console.log(change)
    // TODO new
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
    this.index = ko.observable(data.index);
    this.firstName = ko.observable(data.firstName);
    this.lastName = ko.observable(data.lastName);
    this.dateOfBirth = ko.observable(data.dateOfBirth);
    this.links = ko.observable(data.links);
    this.i = '1';

    this.firstName.subscribe(function() {
      console.log('sdadsadasdas');
    })
  }

  var userMapping = {
    students: {
      key: function(item) {
        return ko.utils.unwrapObservable(item.index);
      },
      create: function(options) {
        console.log('222222')
        return new Student(options.data);
      }
    }
};

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
      // self.students.remove(student)
    };
    self.save = function() {
      console.log('save');
      // console.log(self.students())
    }

    $.ajax({
      url: SERVER_URL + '/students',
      type: 'GET',
      dataType: 'json',
      accepts: {
        contentType: 'application/json'
      }
    }).done(function(result) {
      ko.mapping.fromJS(result, {}, self.students);
    });

    // self.students.subscribe(modifyStudent, null, 'update');
    // self.students.subscribe(modifyStudent, null, 'arrayChange');
  }
  var model = new StateViewModel();
  ko.applyBindings(model, );

  // loadStudents(model);
});
